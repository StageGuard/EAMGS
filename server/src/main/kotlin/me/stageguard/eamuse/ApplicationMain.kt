package me.stageguard.eamuse

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.stageguard.eamuse.database.AddableTable
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.database.model.PcbIdTable
import me.stageguard.eamuse.plugin.EAmPlugin
import me.stageguard.eamuse.plugin.EAmPluginLoader
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.EAmusementGameServer
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.api.LoadedServices
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
    // common
    EAmPluginLoader.initializePlugin(object : EAmPlugin {
        override val name: String
            get() = "E-Amusement Common"
        override val id: String
            get() = "common"
        override val code: String
            get() = "COMMON"
        override val routerModules: List<RouterModule>
            get() = listOf(Service, PCBTracker, EACoin, Package, Message, Facility, PCBEvent, EventLog, CardManager)
        override val tables: List<AddableTable<*>>
            get() = listOf(EAmuseCardTable, PcbIdTable)
        override val profileChecker: ProfileChecker? = null
        override val apiHandlers: List<AbstractAPIHandler>
            get() = listOf(LoadedServices)

    })

    // load game plugins
    if (System.getProperty("me.stageguard.eamuse.dev") != null) {
        EAmPluginLoader.path = File(".").listFiles { f ->
            f.isDirectory && f.name.startsWith("game-")
        }?.map { File(it.path + "/build/libs/") } ?: listOf(File("plugins/"))
    }
    EAmPluginLoader.loadPlugins()

    // start
    Database.connect()
    EAmusementGameServer.start(config.server.host, config.server.port).join()
}
