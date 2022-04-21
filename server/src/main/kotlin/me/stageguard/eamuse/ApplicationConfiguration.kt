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

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationConfiguration(
    val server: ServerConfiguration = ServerConfiguration(),
    val database: DatabaseConfiguration = DatabaseConfiguration(),
) {
    @Serializable
    data class ServerConfiguration(
        val host: String = "0.0.0.0",
        val port: Int = 8081,
        val globalHost: String = host,
        val globalPort: Int = port,
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
        var maximumPoolSize: Int? = 10,
    )
}
