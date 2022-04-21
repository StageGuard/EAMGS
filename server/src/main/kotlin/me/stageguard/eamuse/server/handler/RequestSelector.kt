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

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.*
import me.stageguard.eamuse.server.SelectorType
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import java.nio.charset.Charset

@ChannelHandler.Sharable
internal object RequestSelector : SimpleChannelInboundHandler<FullHttpRequest>() {
    private val LOGGER = LoggerFactory.getLogger(RequestSelector.javaClass)

    private val eAmuseGameRequestURLRegex =
        Regex("/\\?model=[A-Z\\d:]+&f=[a-z\\d_]+\\.[a-z\\d_]+", RegexOption.IGNORE_CASE)

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

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
