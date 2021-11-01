package me.stageguard.eamuse

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.server.EAmusementGameServer
import me.stageguard.eamuse.server.router.*
import java.io.File



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
    Database.addTables(EAmuseCardTable, *sdvx6DatabaseTables) // sdvx6
    Database.connect()

    EAmusementGameServer.addRouters(Service, PCBTracker, EACoin, Package, Message, Facility, PCBEvent, EventLog)
    EAmusementGameServer.addRouters(CardManager(SDVX6ProfileChecker), Game(*sdvx6RouteHandlers)) // sdvx6
    EAmusementGameServer.start(config.server.host, config.server.port).join()
}