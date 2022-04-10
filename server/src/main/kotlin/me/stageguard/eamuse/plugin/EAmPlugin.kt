package me.stageguard.eamuse.plugin

import me.stageguard.eamuse.database.AddableTable
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.router.ProfileChecker

interface EAmPlugin {
    /*
     * Game name
     */
    val name: String
    /*
     * Game routers to handle requests sent from bemani game
     */
    val routerModules: List<RouterModule>?
    /*
     * Database tables to restore game data
     */
    val tables: List<AddableTable<*>>?
    /*
     * Game profile checker, to check if user exists.
     */
    val profileChecker: ProfileChecker?
    /*
     * API Handlers
     */
    val apiHandlers: List<AbstractAPIHandler>?
}
