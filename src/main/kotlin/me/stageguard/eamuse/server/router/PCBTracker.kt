package me.stageguard.eamuse.server.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import me.stageguard.eamuse.config
import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.packet.EAGRequestPacket

object PCBTracker : RouteCollection("pcbtracker") {
    override val routers: Set<RouteHandler>
        get() = setOf(Alive)

    @RouteModel
    object Alive : RouteHandler("alive") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            var resp = KXmlBuilder.create("response")
                .e("pcbtracker")
                .a("ecenable", if (config.server.isPaseliEnabled) "1" else "0")

            if (config.server.isMaintenanceMode) {
                resp = resp
                    .a("eclimit", "0")
                    .a("expire", "1200")
                    .a("limit", "0")
                    .a("status", "0")
                    .a("time", (System.currentTimeMillis() / 1000).toString())
            }

            return resp
        }

    }
}