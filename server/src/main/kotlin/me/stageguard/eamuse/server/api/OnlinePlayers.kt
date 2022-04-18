package me.stageguard.eamuse.server.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable
import me.stageguard.eamuse.json
import me.stageguard.eamuse.plugin.EAmPluginLoader
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.EAmusementGameServer
import me.stageguard.eamuse.uriParameters
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext

internal object OnlinePlayersMonitor : CoroutineScope {
    private val LOGGER = LoggerFactory.getLogger(OnlinePlayersMonitor::class.java)
    override val coroutineContext: CoroutineContext
        get() = Job(EAmusementGameServer.coroutineContext.job) +
            CoroutineExceptionHandler { _, t -> LOGGER.warn("Exception in OnlinePlayersMonitor", t) }
    private val opSupervisorJob = SupervisorJob(coroutineContext.job)
    private val accessLock = Mutex(false)

    private val gameCode by lazy {
        mapOf(*EAmPluginLoader.plugins.map { it.code to it.id }.toTypedArray()).onEach { (_, id) ->
            countRecord[id] = mutableListOf()
            playerRecord[id] = hashMapOf()
        }
    }

    private val countRecord: MutableMap<String, MutableList<Int>> = mutableMapOf()
    private val playerRecord: MutableMap<String, HashMap<String, LocalDateTime>> = mutableMapOf()

    fun inquire(code: String, tag: String) = launch(opSupervisorJob) j@ {
        val gameId = gameCode[code] ?: return@j
        accessLock.withLock {
            val players = playerRecord.getValue(gameId)

            val p = players[tag]
            if (p == null) {
                val record = countRecord.getValue(gameId)
                if (record.isEmpty()) {
                    record.add(1)
                } else {
                    record[record.lastIndex] ++
                }
            }
            players[tag] = LocalDateTime.now()
        }
    }.let { }

    private fun tickerFlow(initialMilli: Long, intervalMilli: Long = 3600000) = flow {
        delay(initialMilli)
        while (isActive) {
            emit(Unit)
            delay(intervalMilli)
        }
    }

    fun getOnlinePlayerRecord(gameId: String): List<Int>? {
        return countRecord[gameId]
    }

    internal fun start() {
        val timeDiffOfNextHour = LocalDateTime.now().until(
            LocalDateTime.of(LocalDate.now(), LocalTime.now().run { LocalTime.of(hour + 1, 0) }),
            ChronoUnit.MILLIS)

        launch(coroutineContext) { tickerFlow(timeDiffOfNextHour).collect {
            accessLock.withLock {
                playerRecord.forEach { (gameId, record) ->
                    var playersEntranceWithin5Minute = 0
                    record.forEach { (tag, time) ->
                        if (time.until(LocalDateTime.now(), ChronoUnit.MILLIS) < 30 * 1000) {
                            playersEntranceWithin5Minute ++
                        } else {
                            record.remove(tag)
                        }
                    }
                    countRecord.getValue(gameId).apply {
                        add(playersEntranceWithin5Minute)
                        if (size > 24) removeFirst()
                    }
                }
                LOGGER.info("status of previous hour: { ${countRecord.map { "${it.key}: ${it.value}}" }.joinToString(", ")} }")
            }
        } }
    }
}

@Serializable
data class OnlinePlayersRecord(
    val record: List<Int>,
    val result: Int = 0 // identifier
)

internal object OnlinePlayers : AbstractAPIHandler("online") {
    override suspend fun handle(request: FullHttpRequest): String {
        val gameId = request.uriParameters["game"]
            ?: return apiError("INVALID_REQUEST:game")
        val record = OnlinePlayersMonitor.getOnlinePlayerRecord(gameId)
        return if (record != null) {
            json.encodeToString(OnlinePlayersRecord(record))
        } else {
            apiError("NOT_FOUND")
        }
    }
}
