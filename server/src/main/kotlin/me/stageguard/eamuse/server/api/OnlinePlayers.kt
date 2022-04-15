package me.stageguard.eamuse.server.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import me.stageguard.eamuse.plugin.EAmPluginLoader
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.EAmusementGameServer
import me.stageguard.eamuse.uriParameters
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

internal object OnlinePlayersMonitor : CoroutineScope {
    private val LOGGER = LoggerFactory.getLogger(OnlinePlayers.javaClass)
    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + EAmusementGameServer.coroutineContext +
            CoroutineExceptionHandler { _, t -> LOGGER.warn("Exception in OnlinePlayersMonitor", t) }

    /*
     * game code
     * format: GameCode -> GameId
     */
    private val gameCode by lazy { mapOf(*EAmPluginLoader.plugins.map { it.code to it.id }.toTypedArray()) }
    /*
     * represents history of count of players
     * format: GameId -> PlayersOfCurrentPeriod
     */
    private val countRecord: ConcurrentHashMap<String, ArrayBlockingQueue<Int>> = ConcurrentHashMap()
    /*
     * players of current time period (commonly, 1 hr), clears every hour
     * format: GameId -> (PlayerTag -> EntranceTimestamp)
     */
    private val playerRecord: MutableMap<String, HashMap<String, LocalDateTime>> = mutableMapOf()

    /*
     * record a player
     */
    fun inquire(game: String, tag: String) {

    }

    /*
     * helper scheduler timer flow factory
     */
    private fun tickerFlow(initialMilli: Long, intervalMilli: Long) = flow {
        delay(initialMilli)
        while (isActive) {
            emit(Unit)
            delay(intervalMilli)
        }
    }

    init {
        val timeDiffOfNextHour = LocalDateTime.now().until(LocalDateTime.now().plusHours(1), ChronoUnit.MILLIS)
        launch(coroutineContext) { tickerFlow(timeDiffOfNextHour, 3600000).collect {

        } }

    }
}

internal object OnlinePlayers : AbstractAPIHandler("online") {
    override suspend fun handle(request: FullHttpRequest): String {
        val gameId = request.uriParameters["game"]

        return ""
    }
}
