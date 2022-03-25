package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.server.RouteModel
import org.w3c.dom.Element

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124, SDVX6_20220214)
object Shop : SDVX6RouteHandler("shop") {
    private const val SHOP_NXT_TIME = (1000 * 5 * 60).toLong()

    override suspend fun handle(gameNode: Element): KXmlBuilder {
        return createGameResponseNode().u32("nxt_time", SHOP_NXT_TIME)
    }
}