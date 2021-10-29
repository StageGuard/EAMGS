package me.stageguard.eamuse.server

import io.netty.handler.codec.http.HttpResponseStatus

class InvalidRequestException(val status: HttpResponseStatus) : Exception()