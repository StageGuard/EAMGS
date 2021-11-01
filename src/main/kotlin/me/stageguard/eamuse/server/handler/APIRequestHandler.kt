package me.stageguard.eamuse.server.handler

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.*
import me.stageguard.eamuse.server.SelectorType
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import java.nio.charset.Charset

@ChannelHandler.Sharable
object APIRequestHandler : SimpleChannelInboundHandler<SelectorType.APIRequest>() {
    private val LOGGER = LoggerFactory.getLogger(APIRequestHandler.javaClass)

    override fun channelReadComplete(ctx: ChannelHandlerContext) { ctx.flush() }

    override fun channelRead0(
        ctx: ChannelHandlerContext,
        msg: SelectorType.APIRequest
    ) {
        LOGGER.info("Handle request: ${URLDecoder.decode(msg.request.uri(), Charset.defaultCharset())}")
        ctx.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK).also {
            it.content().writeBytes("Handle api request".toByteArray())
        })
        ctx.close()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        LOGGER.error("Exception in APIRequestHandler", cause)
        ctx?.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR))
        ctx?.close()
    }
}