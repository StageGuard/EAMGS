package me.stageguard.eamuse.game.iidx

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.packet.EAGRequestPacket
import org.w3c.dom.Element

abstract class IIDXRouteHandler(private val module: String, override val method: String) : RouteHandler(method) {
    abstract suspend fun handle(node: Element) : KXmlBuilder

    override suspend fun handle(packet: EAGRequestPacket): KXmlBuilder {
        return handle(XmlUtils.nodeAtPath(packet.content, "/IIDX29$module") as Element)
    }

    fun createResponseNode(): KXmlBuilder = KXmlBuilder.create("response")
        .e("IIDX29$module").a("status", "0")
}

abstract class IIDXPCRouteHandler(method: String) : IIDXRouteHandler("pc", method)
abstract class IIDXMusicRouteHandler(method: String) : IIDXRouteHandler("music", method)
abstract class IIDXGradeRouteHandler(method: String) : IIDXRouteHandler("grade", method)
abstract class IIDXShopRouteHandler(method: String) : IIDXRouteHandler("shop", method)
abstract class IIDXRankingRouteHandler(method: String) : IIDXRouteHandler("ranking", method)
abstract class IIDXGameSystemRouteHandler(method: String) : IIDXRouteHandler("gameSystem", method)