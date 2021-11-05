package me.stageguard.eamuse.server

import io.netty.handler.codec.http.FullHttpRequest

class APIRequestDSL(private val module: String) {
    private val routers: MutableMap<String, suspend (FullHttpRequest) -> String> = mutableMapOf()

    fun routing(method: String, block: suspend (FullHttpRequest) -> String) {
        routers["$module/$method"] = block
    }

    operator fun invoke() = routers
}