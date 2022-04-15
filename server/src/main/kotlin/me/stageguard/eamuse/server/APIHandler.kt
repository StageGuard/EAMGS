package me.stageguard.eamuse.server

import io.netty.handler.codec.http.FullHttpRequest
import org.intellij.lang.annotations.Language

interface APIHandler {
    suspend fun handle(request: FullHttpRequest): String
}

abstract class AbstractAPIHandler(val path: String) : APIHandler {
    @Language("JSON") protected fun apiError(reason: String) = """{"result": -1, "message": "$reason"}"""
}
