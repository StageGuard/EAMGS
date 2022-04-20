package me.stageguard.eamuse.server.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.serialization.encodeToString
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
    val result: Int = 0 // identifier
)

@kotlinx.serialization.Serializable
internal data class GameDTO(
    val name: String,
    val supportedVersions: List<String>,
    val api: Map<String, String>
)


internal object ServerStatus : AbstractAPIHandler("server_status", "status") {
    private val games by lazy {
        val games = mutableMapOf<String, GameDTO>()
        EAmPluginLoader.plugins.filterNot { it.id == "common" }.forEach { p ->
            val supported = p.profileChecker ?.javaClass
                ?.getAnnotation(RouteModel::class.java) ?.name ?.toList() ?: listOf()

            val definedApi = p.apiHandlers ?.associate { it.name to it.path } ?: mapOf()
            games[p.id] = GameDTO(p.name, supported, definedApi)
        }
        games
    }

    override suspend fun handle(request: FullHttpRequest): String {
        return try {
            val profileCount = Database.query { db -> db.sequenceOf(EAmuseCardTable).count() } ?: -1
            json.encodeToString(Status(games, profileCount, EAmusementGameServer.startupTime, Database.connected))
        } catch (ex: Exception) {
            apiError("ERROR:$ex")
        }
    }
}
