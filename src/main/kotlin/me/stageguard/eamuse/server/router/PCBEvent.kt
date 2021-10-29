package me.stageguard.eamuse.server.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.packet.RequestPacket

object PCBEvent : RouteCollection("pcbevent") {
    override val routers: Set<RouteHandler>
        get() = setOf(Put)

    @RouteModel
    object Put : RouteHandler("put") {
        override suspend fun handle(packet: RequestPacket): BaseXMLBuilder {
            //TODO: Save pcb info

            return KXmlBuilder.create("response")
                .e("pcvevent").a("status", "0")
        }
    }

}