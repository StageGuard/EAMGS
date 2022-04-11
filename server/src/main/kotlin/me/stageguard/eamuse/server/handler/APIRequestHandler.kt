package me.stageguard.eamuse.server.handler

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.*
import kotlinx.coroutines.runBlocking
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.SelectorType
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import java.nio.charset.Charset

@ChannelHandler.Sharable
internal object APIRequestHandler : SimpleChannelInboundHandler<SelectorType.APIRequest>() {
    private val LOGGER = LoggerFactory.getLogger(APIRequestHandler.javaClass)

    // key: module/method
    private val routers: MutableList<AbstractAPIHandler> = mutableListOf()

    fun addHandler(h: AbstractAPIHandler) {
        routers.add(h)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) { ctx.flush() }

    override fun channelRead0(
        ctx: ChannelHandlerContext,
        msg: SelectorType.APIRequest
    ) {
        val url = URLDecoder.decode(msg.request.uri(), Charset.defaultCharset())
        val sp = url.lowercase().split("?")[0].split("/")

        if (sp[1] != "api") {
            ctx.writeAndFlush(createResponse("Unhandled API request: $url", HttpResponseStatus.NOT_FOUND))
            ctx.close()
        }

        var handled = false
        var response = "Unhandled API request: $url"
        routers.forEach { handler ->
            if (handler.path == sp.drop(2).joinToString("/")) {
                LOGGER.info(
                    "Handle request: $url " +
                    "from ${(ctx.channel() as SocketChannel).run { "${remoteAddress().toString().drop(1)} at 0x${id()}" }}."
                )
                response = try {
                    runBlocking { handler.handle(msg.r) }
                } catch (ex: Exception) { "{\"result\": -1, \"message\": $ex}" }
                handled = true
                return@forEach
            }
        }

        if (!handled) LOGGER.info(
            "Unhandled request: $url " +
            "from ${(ctx.channel() as SocketChannel).run { "${remoteAddress().toString().drop(1)} at 0x${id()}" }}."
        )
        ctx.writeAndFlush(createResponse(response, if (handled) HttpResponseStatus.OK else HttpResponseStatus.NOT_FOUND))
        ctx.close()
    }

    private fun createResponse(text: String, status: HttpResponseStatus = HttpResponseStatus.OK) = text.toByteArray().run {
        DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status).also {
            it.content().writeBytes(this)
            it.headers().run h@{
                add("Content-Type", if (status == HttpResponseStatus.OK) "application/json" else "text/plain")
                add("Content-Length", this@run.size)
            }
        }
    }


    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        LOGGER.error("Exception in APIRequestHandler", cause)
        ctx?.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR))
        ctx?.close()
    }
}
