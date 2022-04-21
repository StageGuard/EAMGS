/*
 * Copyright (c) 2022 StageGuard
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.stageguard.eamuse.server.handler

import com.buttongames.butterflycore.encryption.Rc4
import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.kbinEncode
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.ReferenceCountUtil
import me.stageguard.eamuse.server.packet.EAGResponsePacket
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@ChannelHandler.Sharable
internal object EAmGameResponseEncoder : SimpleChannelInboundHandler<EAGResponsePacket>() {

    private val LOGGER = LoggerFactory.getLogger(EAmGameResponseEncoder.javaClass)

    override fun channelRead0(ctx: ChannelHandlerContext, msg: EAGResponsePacket) {
        try {
            val bos = ByteArrayOutputStream().also {
                val transformerFactory = TransformerFactory.newInstance()
                val transformer = transformerFactory.newTransformer()
                transformer.setOutputProperty(OutputKeys.METHOD, "xml")
                val source = DOMSource(msg.body.document)
                val result = StreamResult(it)
                transformer.transform(source, result)
            }

            val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)

            val content = kotlin.run {
                var respBytes = bos.toByteArray()
                bos.flush()
                bos.close()

                if (!XmlUtils.isBinaryXML(respBytes)) respBytes = kbinEncode(String(respBytes), "UTF-8")
                if (msg.srcReqPackage.eAmuseInfo != null) respBytes =
                    Rc4.encrypt(respBytes, msg.srcReqPackage.eAmuseInfo)
                //if (msg.srcReqPackage.compressScheme == "lz77") respBytes = Lz77.compress(respBytes)

                respBytes
            }.also { response.content().writeBytes(it) }

            response.headers().run {
                add("X-Powered-By", "StageGuard")
                //add("X-Compress", msg.srcReqPackage.compressScheme ?: "none")
                if (msg.srcReqPackage.eAmuseInfo != null) {
                    add("X-Eamuse-Info", msg.srcReqPackage.eAmuseInfo)
                }
                add("Content-Type", "application/octet-stream")
                add("Content-Length", content.size)
                add("Connection", "close")
            }

            ctx.writeAndFlush(response)
            LOGGER.info(
                "Send response: ${msg.srcReqPackage.model} -> <${msg.srcReqPackage.module}.${msg.srcReqPackage.method}> " +
                        "to ${
                            (ctx.channel() as SocketChannel).run {
                                "${
                                    remoteAddress().toString().drop(1)
                                } at 0x${id()}"
                            }
                        }."
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            ctx.writeAndFlush(internalError())
        }
        ctx.close()
        ReferenceCountUtil.release(msg)
    }

    private fun internalError() = DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR
    ).also {
        it.headers().run {
            add(HttpHeaderNames.CONTENT_LENGTH, 0)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        LOGGER.error("Exception in EAmGameResponseEncoder", cause)
        ctx?.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR))
        ctx?.close()
    }
}
