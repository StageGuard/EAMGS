package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.server.RouteModel
import org.w3c.dom.Element

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124)
object Longue : SDVX6RouteHandler("lounge") {
    private const val INTERVAL = 30.toLong()

    override suspend fun handle(gameNode: Element): KXmlBuilder {
        return createGameResponseNode().u32("interval", INTERVAL)
    }
}