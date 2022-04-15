package me.stageguard.eamuse.server.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.json
import me.stageguard.eamuse.plugin.EAmPluginLoader
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.RouteModel

@kotlinx.serialization.Serializable
internal data class ServicesList(
    val games: List<GameDTO>,
    val result: Int = 0 // identifier
)

@kotlinx.serialization.Serializable
internal data class GameDTO(
    val id: String,
    val name: String,
    val supportedVersions: List<String>,
    val api: Map<String, String>
)


internal object LoadedServices : AbstractAPIHandler("services") {
    // some api handlers which might have custom fields
    private const val STATUS_PATH = "status"
    private const val RANKING_PATH = "ranking"
    private const val PROFILE_PATH = "profile"
    private const val CUSTOMIZE_GET_PATH = "custom/get"
    private const val CUSTOMIZE_UPDATE_PATH = "custom/update"

    private val games by lazy {
        val games = mutableListOf<GameDTO>()
        EAmPluginLoader.plugins.filterNot { it.id == "common" }.forEach { p ->
            val supported = p.profileChecker ?.javaClass
                ?.getAnnotation(RouteModel::class.java) ?.name ?.toList() ?: listOf()

            val definedApis = p.apiHandlers ?.map { it.path } ?: listOf()
            val api = mutableMapOf<String, String>()

            definedApis.forEach a@ { a ->
                if (a.contains("${p.id}/$STATUS_PATH")) {
                    api["status"] = a; return@a
                }
                if (a.contains("${p.id}/$RANKING_PATH")) {
                    api["ranking"] = a; return@a
                }
                if (a.contains("${p.id}/$PROFILE_PATH")) {
                    api["profile"] = a; return@a
                }
                if (a.contains("${p.id}/$CUSTOMIZE_GET_PATH")) {
                    api["customize_get"] = a; return@a
                }
                if (a.contains("${p.id}/$CUSTOMIZE_UPDATE_PATH")) {
                    api["customize_update"] = a; return@a
                }
            }
            games.add(GameDTO(p.id, p.name, supported, api))
        }
        games
    }

    override suspend fun handle(request: FullHttpRequest): String {
        return try {
            json.encodeToString(ServicesList(games))
        } catch (ex: Exception) {
            apiError(ex.toString())
        }
    }
}
