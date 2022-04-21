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
     * Game id
     */
    val id: String

    /*
     * Game code
     */
    val code: String

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
