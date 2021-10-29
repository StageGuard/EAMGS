package me.stageguard.eamuse.server.packet

import com.jamesmurty.utils.BaseXMLBuilder
import io.netty.handler.codec.http.HttpResponseStatus

data class ResponsePacket(
    val body: BaseXMLBuilder,
    val srcReqPackage: RequestPacket
)
