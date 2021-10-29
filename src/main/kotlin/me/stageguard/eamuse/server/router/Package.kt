package me.stageguard.eamuse.server.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.packet.RequestPacket

object Package : RouteCollection("package") {
    override val routers: Set<RouteHandler>
        get() = setOf(List)

    @RouteModel
    object List : RouteHandler("list") {
        override suspend fun handle(packet: RequestPacket): BaseXMLBuilder {
            return KXmlBuilder.create("response")
                .e("package")
                .a("expire", "1200")
                .a("status", "0")
        }
    }

}