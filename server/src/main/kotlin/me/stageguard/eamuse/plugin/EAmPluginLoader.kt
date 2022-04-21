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

package me.stageguard.eamuse.plugin

import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.server.handler.APIRequestHandler
import me.stageguard.eamuse.server.handler.EAmGameRequestHandler
import me.stageguard.eamuse.server.router.CardManager
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

internal object EAmPluginLoader {
    private val LOGGER = LoggerFactory.getLogger(EAmPluginLoader::class.java)
    var path: List<File> = listOf(File("plugins/"))

    private val loadedPlugins: ConcurrentHashMap<String, EAmPlugin> = ConcurrentHashMap()
    val plugins get() = loadedPlugins.values

    fun loadExternalPlugins() {
        val jars = path.flatMap { it.listFiles { f -> f.isFile && f.extension == "jar" }?.toList() ?: listOf() }

        jars.forEach {
            val classLoader = URLClassLoader(
                "Loader of ${it.nameWithoutExtension}",
                arrayOf(it.toURI().toURL()),
                EAmPluginLoader.javaClass.classLoader
            )
            val entryPointResource = classLoader.findResource("EamPluginEntryPoint.txt")
            val entryClass = Class.forName(entryPointResource.readText().trim(), true, classLoader)
            val pluginInstance = (entryClass.kotlin.objectInstance ?: entryClass.newInstance()) as EAmPlugin

            if (loadedPlugins[pluginInstance.id] != null) {
                LOGGER.warn("Plugin ${pluginInstance.id} is already loaded. Skipping...")
                return
            } else {
                loadedPlugins[pluginInstance.id] = pluginInstance
                initializePlugin(pluginInstance)
            }
        }
    }

    internal fun initializePlugin(plugin: EAmPlugin) {
        LOGGER.info("Loading plugin ${plugin.name}...")
        plugin.tables?.forEach { Database.addTable(it) }
        plugin.routerModules?.forEach { EAmGameRequestHandler.addRouter(it) }
        plugin.profileChecker?.let { CardManager.addChecker(it) }
        plugin.apiHandlers?.forEach { APIRequestHandler.addHandler(it) }
    }
}
