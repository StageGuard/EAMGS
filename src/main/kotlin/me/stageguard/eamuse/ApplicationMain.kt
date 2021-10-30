package me.stageguard.eamuse

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.game.sdvx6.model.*
import me.stageguard.eamuse.server.EAmusementGameServer
import me.stageguard.eamuse.server.router.*
import java.io.File

@Serializable
data class ApplicationConfiguration(
    val server: ServerConfiguration = ServerConfiguration(),
    val database: DatabaseConfiguration = DatabaseConfiguration(),
    val sdvx: SDVXConfiguration = SDVXConfiguration()
) {
    @Serializable
    data class ServerConfiguration(
        val host: String = "0.0.0.0",
        val port: Int = 8081,
        val globalHost: String = host,
        val matchingPort: Int = 5700,
        //other configurations
        val isMaintenanceMode: Boolean = false,
        val isPaseliEnabled: Boolean = true,
    )

    @Serializable
    data class DatabaseConfiguration(
        val address: String = "localhost",
        val port: Int = 3306,
        val user: String = "root",
        val password: String = "this is password.",
        var table: String = "eamuse",
        var maximumPoolSize: Int? = 10
    )

    @Serializable
    data class SDVXConfiguration(
        val unlockAllSongs: Boolean = false,
        val unlockAllNavigators: Boolean = false,
        val unlockAllAppealCards: Boolean = true,
        val customEntryInformation: String = "",
        val customGameOverInformation: String = "",
        val useBlasterPass: Boolean = true,
        val newMusicLimitType: Int = 2
    )
}

val json = Json {
    prettyPrint = true
    encodeDefaults = true
    ignoreUnknownKeys = true
}

val config = run {
    val file = File("config.json")
    try {
        if (file.exists() && !file.isDirectory) {
            json.decodeFromString(file.readText())
        } else {
            ApplicationConfiguration().also {
                file.createNewFile()
                file.writeText(json.encodeToString(it))
            }
        }
    } catch (_: Exception) {
        file.delete()
        ApplicationConfiguration().also {
            file.createNewFile()
            file.writeText(json.encodeToString(it))
        }
    }
}

fun main() = runBlocking {

    Database.connect {
        //base table
        this + EAmuseCardTable + sdvx6DatabaseTables
    }
    EAmusementGameServer.start(config.server.host, config.server.port) {
        //bae router handlers
        this + Service + PCBTracker + EACoin +
                Package + Message + Facility +
                PCBEvent + EventLog  +
                CardManager(SDVX6ProfileChecker) +
                // sdvx6 router handlers
                Game(*sdvx6RouteHandlers)
    }.join()
}