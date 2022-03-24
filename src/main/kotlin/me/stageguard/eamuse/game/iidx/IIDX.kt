package me.stageguard.eamuse.game.iidx

import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler
import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger("IIDX")

/* Routers */
const val LDJ20211013 = "LDJ:2021101300"

val IIDXRouters = arrayOf(
    IIDXPCRouters, IIDXMusicRouters, IIDXGradeRouters, IIDXRankingRouters, IIDXShopRouters, IIDXGameSystemRouters
)