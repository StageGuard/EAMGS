package me.stageguard.eamuse.server.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.packet.EAGRequestPacket

object EventLog : RouteCollection("eventlog") {
    override val routers: Set<RouteHandler>
        get() = setOf(Write)

    @RouteModel
    object Write : RouteHandler("write") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            //TODO: save event log

            return KXmlBuilder.create("response")
                .e("eventlog").a("status", "0")
                    .s64("gamesession", 1).up()
                    .s32("logsendflg", 0).up()
                    .s32("logerrlevel", 0).up()
                    .s32("evtidnosendflg", 0)
        }
    }
}