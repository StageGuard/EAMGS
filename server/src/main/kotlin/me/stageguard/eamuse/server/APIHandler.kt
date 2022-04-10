package me.stageguard.eamuse.server

import io.netty.handler.codec.http.FullHttpRequest

interface APIHandler {
    suspend fun handle(request: FullHttpRequest): String
}

abstract class AbstractAPIHandler(val path: String) : APIHandler
