package me.stageguard.eamuse.game.iidx

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.game.iidx.router.GetConvention
import me.stageguard.eamuse.game.iidx.router.GetName
import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import org.w3c.dom.Element

class IIDXRouter(
    private val iidxModule: String,
    private val iidxRouters: Set<RouteHandler> = setOf(),
    private vararg val defaults: String
) : RouteCollection("IIDX29$iidxModule") {
    override val routers: Set<RouteHandler>
        get() = iidxRouters.plus(generate(*defaults))

    private fun generate(vararg method: String): Array<RouteHandler> {
        return method.map { m ->
            @RouteModel(LDJ20211013) object : IIDXRouteHandler(iidxModule, m) {
                override suspend fun handle(gameNode: Element): KXmlBuilder {
                    return KXmlBuilder.create("response").e(module).a("status", "0")
                }
            }
        }.toTypedArray()
    }
}

val IIDXPCRouters = IIDXRouter("pc", mutableSetOf(

))
val IIDXMusicRouters = IIDXRouter("music", mutableSetOf(

))
val IIDXGradeRouters = IIDXRouter("grade", mutableSetOf(

))
val IIDXShopRouters = IIDXRouter(
    "shop",
    mutableSetOf(GetName, GetConvention),
    "sentinfo", "savename", "sendescapepackageinfo"
)
val IIDXRankingRouters = IIDXRouter("ranking", defaults = arrayOf("getranker"))
val IIDXGameSystemRouters = IIDXRouter("gameSystem", defaults = arrayOf("systemInfo"))