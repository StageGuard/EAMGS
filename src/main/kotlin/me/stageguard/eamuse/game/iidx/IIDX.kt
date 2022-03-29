package me.stageguard.eamuse.game.iidx

import me.stageguard.eamuse.game.iidx.model.*
import me.stageguard.eamuse.game.iidx.router.*
import org.slf4j.LoggerFactory

internal val LOGGER = LoggerFactory.getLogger("IIDX")

/* Routers */
const val LDJ20211013 = "LDJ:2021101300"

val IIDXRouters = arrayOf(
    IIDXRouter("pc", setOf(
        Common, OldGet, GetProfileName, TakeOver, PCRegister, Get, Save
    ), "playstart", "delete", "eaappliresult", "eaappliexpert", "logout"),
    IIDXRouter("music", setOf(
        MusicRegister, GetRank, APPoint, Crate
    ), "play"),
    IIDXRouter("grade", setOf(Raised)),
    IIDXRouter("shop", setOf(GetShopName, GetConvention),
        "sentinfo", "savename", "sendescapepackageinfo"),
    IIDXRouter("ranking", defaults = arrayOf("getranker")),
    IIDXRouter("gameSystem", defaults = arrayOf("systemInfo"))
)

val iidxDatabaseTables = arrayOf(
    UserProfileTable, PCDataTable, GradeTable, ScoreTable, ScoreDetailTable, SettingsTable
)