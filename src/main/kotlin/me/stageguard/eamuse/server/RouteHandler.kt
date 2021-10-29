package me.stageguard.eamuse.server

import com.jamesmurty.utils.BaseXMLBuilder
import me.stageguard.eamuse.server.packet.RequestPacket
import org.w3c.dom.Element

abstract class RouteHandler(val method: String) {
    abstract suspend fun handle(packet: RequestPacket): BaseXMLBuilder
}