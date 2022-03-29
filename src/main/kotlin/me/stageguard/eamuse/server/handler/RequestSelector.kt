package me.stageguard.eamuse.server.handler

import io.netty.channel.*
import io.netty.handler.codec.http.*
import me.stageguard.eamuse.server.SelectorType
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import java.nio.charset.Charset

@ChannelHandler.Sharable
object RequestSelector : SimpleChannelInboundHandler<FullHttpRequest>() {
    private val LOGGER = LoggerFactory.getLogger(RequestSelector.javaClass)

    private val eAmuseGameRequestURLRegex =
        Regex("/\\?model=[A-Z\\d:]+&f=[a-z\\d_]+\\.[a-z\\d_]+", RegexOption.IGNORE_CASE)

    override fun channelReadComplete(ctx: ChannelHandlerContext) { ctx.flush() }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
        val url = URLDecoder.decode(msg.uri(), Charset.defaultCharset())
        when {
            msg.method() == HttpMethod.POST && url.matches(eAmuseGameRequestURLRegex) ->
                ctx.fireChannelRead(SelectorType.EAGameClientRequest(msg))
            url.startsWith("/api/") ->
                ctx.fireChannelRead(SelectorType.APIRequest(msg))
            else -> {
                ctx.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND))
                ctx.close()
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        LOGGER.error("Exception in RequestSelector", cause)
        ctx?.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR))
        ctx?.close()
    }
}