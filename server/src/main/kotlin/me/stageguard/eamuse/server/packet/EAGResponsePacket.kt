package me.stageguard.eamuse.server.packet

import com.jamesmurty.utils.BaseXMLBuilder

internal data class EAGResponsePacket(
    val body: BaseXMLBuilder,
    val srcReqPackage: EAGRequestPacket
)
