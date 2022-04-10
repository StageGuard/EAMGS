package me.stageguard.eamuse.plugin

import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.server.EAmusementGameServer
import me.stageguard.eamuse.server.handler.APIRequestHandler
import me.stageguard.eamuse.server.handler.EAmGameRequestHandler
import me.stageguard.eamuse.server.router.CardManager
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader

object EAmPluginLoader {
    private val LOGGER = LoggerFactory.getLogger(EAmPluginLoader::class.java)
    var path: List<File> = listOf(File("plugins/"))

    fun loadPlugins() {
        val jars = path.flatMap { it.listFiles { f -> f.isFile && f.extension == "jar" } ?.toList() ?: listOf() }

        jars.forEach {
            val classLoader = URLClassLoader(
                "Loader of ${it.nameWithoutExtension}",
                arrayOf(it.toURI().toURL()),
                EAmPluginLoader.javaClass.classLoader
            )
            val entryPointResource = classLoader.findResource("EamPluginEntryPoint.txt")

            val entryClass = Class.forName(entryPointResource.readText().trim(), true, classLoader)

            val pluginInstance = (entryClass.kotlin.objectInstance ?: entryClass.newInstance()) as EAmPlugin
            initializePlugin(pluginInstance)
        }
    }

    fun initializePlugin(plugin: EAmPlugin) {
        LOGGER.info("Loading plugin ${plugin.name}...")
        plugin.tables ?.forEach { Database.addTable(it) }
        plugin.routerModules ?.forEach { EAmGameRequestHandler.addRouter(it) }
        plugin.profileChecker?.let { CardManager.addChecker(it) }
        plugin.apiHandlers ?.forEach { APIRequestHandler.addHandler(it) }
    }
}
