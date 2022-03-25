package me.stageguard.eamuse.game.sdvx6

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.packet.EAGRequestPacket
import org.w3c.dom.Element

abstract class SDVX6RouteHandler(method: String) : RouteHandler("sv6_$method") {
    abstract suspend fun handle(gameNode: Element) : KXmlBuilder

    override suspend fun handle(packet: EAGRequestPacket): KXmlBuilder {
        return handle(XmlUtils.nodeAtPath(packet.content, "/game") as Element)
    }

    fun createGameResponseNode(): KXmlBuilder = KXmlBuilder.create("response")
        .e("game").a("status", "0")
}

fun defaultSDVX6Router(vararg method: String) : Array<out SDVX6RouteHandler> {
        return method.map { m ->
            @RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124, SDVX6_20220214)
            object : SDVX6RouteHandler(m) {
                override suspend fun handle(gameNode: Element): KXmlBuilder {
                    return createGameResponseNode()
                }
            }
        }.toTypedArray()
}