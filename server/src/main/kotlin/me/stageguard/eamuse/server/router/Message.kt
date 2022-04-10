package me.stageguard.eamuse.server.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.packet.EAGRequestPacket

internal object Message : RouterModule("message") {
    override val routers: Set<RouteHandler>
        get() = setOf(Get)

    @RouteModel
    object Get : RouteHandler("get") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            return KXmlBuilder.create("response")
                .e("message")
                .a("expire", "300")
                .a("status", "0")
        }
    }

}
