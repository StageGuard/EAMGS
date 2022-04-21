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

package me.stageguard.eamuse.server.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.config
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.json
import me.stageguard.eamuse.plugin.EAmPluginLoader
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.EAmusementGameServer
import me.stageguard.eamuse.server.RouteModel
import org.ktorm.entity.count
import org.ktorm.entity.sequenceOf

@kotlinx.serialization.Serializable
internal data class Status(
    val games: Map<String, GameDTO>,
    val profileCount: Int,
    val startupEpochSecond: Long,
    val dbStatus: Boolean,
    val serverUrl: String,
    val pcbIdRequired: Boolean,
    val result: Int = 0, // identifier
)

@kotlinx.serialization.Serializable
internal data class GameDTO(
    val name: String,
    val supportedVersions: List<String>,
    val api: Map<String, String>,
)


internal object ServerStatus : AbstractAPIHandler("server_status", "status") {
    private val games by lazy {
        val games = mutableMapOf<String, GameDTO>()
        EAmPluginLoader.plugins.filterNot { it.id == "common" }.forEach { p ->
            val supported = p.profileChecker?.javaClass
                ?.getAnnotation(RouteModel::class.java)?.name?.toList() ?: listOf()

            val definedApi = p.apiHandlers?.associate { it.name to it.path } ?: mapOf()
            games[p.id] = GameDTO(p.name, supported, definedApi)
        }
        games
    }

    override suspend fun handle(request: FullHttpRequest): String {
        return try {
            val profileCount = Database.query { db -> db.sequenceOf(EAmuseCardTable).count() } ?: -1
            json.encodeToString(Status(
                games,
                profileCount,
                startupEpochSecond = EAmusementGameServer.startupTime,
                dbStatus = Database.connected,
                serverUrl = config.server.globalDomainName + ":" + config.server.globalPort,
                pcbIdRequired = config.server.pcbIdCheck
            ))
        } catch (ex: Exception) {
            apiError("ERROR:$ex")
        }
    }
}
