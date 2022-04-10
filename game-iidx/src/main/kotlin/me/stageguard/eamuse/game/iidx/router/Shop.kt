package me.stageguard.eamuse.game.iidx.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.game.iidx.IIDXShopRouteHandler
import me.stageguard.eamuse.game.iidx.LDJ20211013
import me.stageguard.eamuse.server.RouteModel
import org.w3c.dom.Element

@RouteModel(LDJ20211013)
object GetShopName : IIDXShopRouteHandler("getname") {
    override suspend fun handle(node: Element): KXmlBuilder {
        return createResponseNode()
            .a("cls_opt", "0")
            .a("hr", "0")
            .a("mi", "0")
            .a("opname", "\uff33\uff27")
            .a("pid", "57")
    }
}

@RouteModel(LDJ20211013)
object GetConvention : IIDXShopRouteHandler("getconvention") {
    override suspend fun handle(node: Element): KXmlBuilder {
        return createResponseNode()
            .a("end_time", "0")
            .a("music_0", "23051")
            .a("music_1", "24010")
            .a("music_2", "12004")
            .a("music_3", "26007")
            .a("start_time", "0")
            .bool("valid", false)
    }
}