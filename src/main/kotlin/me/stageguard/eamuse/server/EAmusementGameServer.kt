package me.stageguard.eamuse.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import kotlinx.coroutines.*
import me.stageguard.eamuse.config
import me.stageguard.eamuse.server.handler.*
import me.stageguard.eamuse.server.router.*
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

object EAmusementGameServer : CoroutineScope {
    private val LOGGER = LoggerFactory.getLogger(EAmusementGameServer.javaClass)

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob()

    private val threadCounter = AtomicInteger(0)

    private lateinit var bootstrap: ServerBootstrap

    fun addRouters(vararg rcs: RouteCollection) = EAmGameRequestHandler.addRouters(*rcs)

    @Suppress("HttpUrlsUsage")
    @OptIn(ObsoleteCoroutinesApi::class)
    fun start(host: String = "0.0.0.0", port: Int) = launch(newSingleThreadContext("EAmusementGameServer")) {
        LOGGER.info("Starting E-Amusement game server...")

        bootstrap = ServerBootstrap()
            .group(
                NioEventLoopGroup(1, newSingleThreadContext("GameClientReqParentGroup").asExecutor()),
                NioEventLoopGroup { runnable ->
                    thread(start = false, name = "GameClientReqChildGroup#${threadCounter.getAndIncrement()}") {
                        runBlocking(this@EAmusementGameServer.coroutineContext) { runnable.run() }
                    }
                }
            )
            .channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                        .addLast("decoder", HttpRequestDecoder())
                        .addLast("encoder", HttpResponseEncoder())
                        .addLast("aggregator", HttpObjectAggregator(8 * 1024 * 1024))
                        .addLast("selector", RequestSelector)
                        .addLast("eamuseDecoder", EAmGameRequestDecoder)
                        .addLast("eamuseProcessor", EAmGameRequestHandler)
                        .addLast("eamuseEncoder", EAmGameResponseEncoder)
                        .addLast("apiHandler", APIRequestHandler)
                }
            })
        val channelFuture = bootstrap.bind(InetAddress.getByName(host), port).syncUninterruptibly().channel()

        LOGGER.info("Server started at http://${config.server.host}:$port")
        channelFuture.closeFuture().syncUninterruptibly()
    }
}