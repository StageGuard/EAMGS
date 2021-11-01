package me.stageguard.eamuse.server

import com.jamesmurty.utils.BaseXMLBuilder
import me.stageguard.eamuse.server.packet.EAGRequestPacket

abstract class RouteHandler(val method: String) {
    abstract suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder
}