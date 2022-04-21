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
import me.stageguard.eamuse.server.api.OnlinePlayers
import me.stageguard.eamuse.server.api.OnlinePlayersMonitor
import me.stageguard.eamuse.server.api.ServerStatus
import me.stageguard.eamuse.server.router.*
import java.io.File
import java.util.logging.Logger
import kotlin.concurrent.thread


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
        Logger.getAnonymousLogger().warning("Failed to load config.json, reverting to default.")
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
            get() = listOf(ServerStatus, OnlinePlayers)
    })

    // load game plugins
    if (System.getProperty("me.stageguard.eamuse.dev") != null) {
        EAmPluginLoader.path = File(".").listFiles { f ->
            f.isDirectory && f.name.startsWith("game-")
        }?.map { File(it.path + "/build/libs/") } ?: listOf(File("plugins/"))
    }
    EAmPluginLoader.loadExternalPlugins()

    // start
    OnlinePlayersMonitor.start()

    Database.connect()
    val serverJob = EAmusementGameServer.start(config.server.host, config.server.port)

    Runtime.getRuntime().addShutdownHook(thread(start = false, name = "ShutdownHook") {
        EAmusementGameServer.stop()
        Database.close()
    })

    serverJob.join()
}
