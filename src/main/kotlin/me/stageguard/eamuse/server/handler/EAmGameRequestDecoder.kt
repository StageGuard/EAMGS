package me.stageguard.eamuse.server.handler

import com.buttongames.butterflycore.compression.Lz77
import com.buttongames.butterflycore.encryption.Rc4
import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.kbinDecodeToString
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.*
import me.stageguard.eamuse.server.packet.RequestPacket
import org.slf4j.LoggerFactory
import org.w3c.dom.Element
import java.net.URLDecoder
import java.nio.charset.Charset

@ChannelHandler.Sharable
object EAmGameRequestDecoder : SimpleChannelInboundHandler<FullHttpRequest>() {
    private val LOGGER = LoggerFactory.getLogger(EAmGameRequestDecoder.javaClass)

    override fun channelReadComplete(ctx: ChannelHandlerContext) { ctx.flush() }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
        if(msg.method().name() != "POST") {
            ctx.writeAndFlush(badRequest())
            ctx.close()
            return
        }

        val parameters = URLDecoder.decode(msg.uri(), Charset.defaultCharset()).split("?").runCatching {
            val path = getOrNull(0)
            val parameters = getOrNull(1)
            check(path != null && (path.isEmpty() || path == "/") && parameters != null)

            mapOf(*parameters.split("&").map {
                it.split("=").run p@ { this@p[0] to this@p[1] }
            }.toTypedArray())
        }.getOrNull()
        if (parameters == null) {
            ctx.writeAndFlush(badRequest())
            ctx.close()
            return
        }

        val (requestModel, requestFunction) = parameters.run { listOf(get("model"), get("f")) }

        if(requestModel == null) {
            ctx.writeAndFlush(badRequest())
            ctx.close()
            return
        }

        val requestModule = parameters["module"] ?: requestFunction ?.split(".")?.get(0)
        val requestMethod = parameters["method"] ?: requestFunction ?.split(".")?.get(1)

        if (requestModule == null || requestMethod == null) {
            ctx.writeAndFlush(badRequest())
            ctx.close()
            return
        }

        val eAmuseInfo: String? = msg.headers()["X-Eamuse-Info"]
        val compressScheme: String? = msg.headers()["X-Compress"]
        var requestBody = kotlin.run {
            val contentLength = msg.content().readableBytes()
            val unpooled = Unpooled.wrappedBuffer(ByteArray(contentLength))
            val originByteBuf = msg.content()
            originByteBuf.readBytes(unpooled, 0, contentLength)
            unpooled.array().also { unpooled.release() }
        }

        if (eAmuseInfo != null) requestBody = Rc4.decrypt(requestBody, eAmuseInfo)
        if (compressScheme == "lz77") requestBody = Lz77.decompress(requestBody)

        LOGGER.info(
            "Handle request: $requestModel <- <$requestModule.$requestMethod> " +
            "from ${(ctx.channel() as SocketChannel).run { "${remoteAddress().toString().drop(1)} at 0x${id()}" }}."
        )

        val rootNode = if (XmlUtils.isBinaryXML(requestBody)) {
            XmlUtils.stringToXmlFile(kbinDecodeToString(requestBody))
        } else {
            XmlUtils.byteArrayToXmlFile(requestBody)
        }.also {
            if (it.nodeName != "call") {
                ctx.writeAndFlush(badRequest())
                ctx.close()
                return
            }
        }

        val moduleNode = rootNode.firstChild as Element

        val requestBodyModel = rootNode.getAttribute("model")
        //val requestBodyPcbId = rootNode.getAttribute("srcid")
        val requestBodyModule = moduleNode.nodeName
        val requestBodyMethod = moduleNode.getAttribute("method")

        if (requestBodyMethod != requestMethod ||
                requestBodyModel != requestModel ||
                requestBodyModule != requestModule
        ) {
            ctx.writeAndFlush(badRequest())
            ctx.close()
            return
        }

        ctx.fireChannelRead(RequestPacket(requestModel, requestModule, requestMethod, eAmuseInfo, compressScheme, moduleNode))
    }

    private fun badRequest() = DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST
    ).also {
        it.headers().run {
            add(HttpHeaderNames.CONTENT_LENGTH, 0)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        LOGGER.error("Exception in EAmGameRequestDecoder", cause)
        ctx?.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR))
        ctx?.close()
    }
}