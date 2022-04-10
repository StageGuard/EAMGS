package me.stageguard.eamuse.game.iidx

import me.stageguard.eamuse.plugin.EAmPlugin
import me.stageguard.eamuse.database.AddableTable
import me.stageguard.eamuse.game.iidx.model.*
import me.stageguard.eamuse.game.iidx.router.*
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.router.ProfileChecker
import org.slf4j.LoggerFactory

internal val LOGGER = LoggerFactory.getLogger("IIDX")

/* Routers */
const val LDJ20211013 = "LDJ:2021101300"

object IIDX : EAmPlugin {
    override val name: String
        get() = "Beatmania IIDX CastHour"

    override val routerModules: List<RouterModule>
        get() = listOf(
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

    override val tables: List<AddableTable<*>>
        get() = listOf(UserProfileTable, PCDataTable, GradeTable, ScoreTable, ScoreDetailTable, SettingsTable)

    override val profileChecker: ProfileChecker
        get() = IIDXProfileChecker

    override val apiHandlers: List<AbstractAPIHandler>
        get() = listOf()
}
