package me.stageguard.eamuse.server.handler

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import kotlinx.coroutines.runBlocking
import me.stageguard.eamuse.server.*
import me.stageguard.eamuse.server.packet.EAGRequestPacket
import me.stageguard.eamuse.server.packet.EAGResponsePacket
import org.slf4j.LoggerFactory
import kotlin.reflect.full.findAnnotation

@ChannelHandler.Sharable
object EAmGameRequestHandler : SimpleChannelInboundHandler<EAGRequestPacket>() {

    private val LOGGER = LoggerFactory.getLogger(EAmGameRequestHandler.javaClass)

    private val routers: MutableMap<String, RouteHandler> = mutableMapOf()

    fun routing(block: RoutingDSL.() -> Unit) {
        block(RoutingDSL)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) { ctx.flush() }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: EAGRequestPacket) {
        routers.forEach { (command, handler) ->
            val modelAnno = handler::class.findAnnotation<RouteModel>()
            if (modelAnno == null) {
                LOGGER.warn(
                    "Router ${handler::class.simpleName} " +
                    "which implements ${RouteHandler::class.simpleName} " +
                    "doesn't have annotation ${RouteModel::class.simpleName}"
                )
            } else {
                if ("${msg.module}.${msg.method}" != command) return@forEach
                // general model
                if (modelAnno.name.contains("*")) {
                    val handled = runBlocking(EAmusementGameServer.coroutineContext) { handler.handle(msg) }
                    ctx.fireChannelRead(EAGResponsePacket(handled, msg))
                    return
                }
                // game specific model
                modelAnno.name.forEach { singleModel ->
                    val (reqModel, reqVersion) = msg.model.split(":").run { first() to last() }
                    val (routeModel, routeVersion) = singleModel.split(":").run { first() to last() }

                    if (reqModel == routeModel && reqVersion.startsWith(routeVersion)) {
                        val handled = runBlocking(EAmusementGameServer.coroutineContext) { handler.handle(msg) }
                        ctx.fireChannelRead(EAGResponsePacket(handled, msg))
                        return
                    }
                }
            }
        }
        LOGGER.info(
            "Skip unknown package: ${msg.model} <- <${msg.module}.${msg.method}> " +
            "from ${(ctx.channel() as SocketChannel).run { "${remoteAddress().toString().drop(1)} at 0x${id()}" }}."
        )
        ctx.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
        ctx.close()
    }

    object RoutingDSL {
        infix operator fun plus(rc: RouteCollection) : RoutingDSL {
            rc.routers.forEach { routers["${rc.module}.${it.method}"] = it }
            return this
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        if (cause is InvalidRequestException) {
            ctx?.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, cause.status))
        } else {
            LOGGER.error("Exception in EAmGameRequestProcessor", cause)
            ctx?.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR))
        }
        ctx?.close()
    }
}