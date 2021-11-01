package me.stageguard.eamuse.server

import io.netty.handler.codec.http.FullHttpRequest

sealed class SelectorType(val request: FullHttpRequest) {
    class EAGameClientRequest(val r: FullHttpRequest) : SelectorType(r)
    class APIRequest(val r: FullHttpRequest) : SelectorType(r)
}