/*
 * Copyright (c) 2022 StageGuard
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.stageguard.eamuse.game.iidx

import me.stageguard.eamuse.database.AddableTable
import me.stageguard.eamuse.game.iidx.model.*
import me.stageguard.eamuse.game.iidx.router.*
import me.stageguard.eamuse.plugin.EAmPlugin
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.router.ProfileChecker
import org.slf4j.LoggerFactory

internal val LOGGER = LoggerFactory.getLogger("IIDX")

/* Routers */
const val LDJ20211013 = "LDJ:2021101300"

object IIDX : EAmPlugin {
    override val id: String = "iidx"
    override val name: String = "Beatmania IIDX CastHour"
    override val code: String = "LDJ"

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
