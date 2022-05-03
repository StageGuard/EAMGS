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

package me.stageguard.eamuse.game.sdvx6

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.json
import java.io.File

@Serializable
data class SDVX6Configuration(
    val unlockAllSongs: Boolean = false,
    val unlockAllNavigators: Boolean = false,
    val unlockAllAppealCards: Boolean = true,
    val customEntryInformation: String = "",
    val customGameOverInformation: String = "",
    val useBlasterPass: Boolean = true,
    val musicDatabase: List<String> = listOf("music_db.xml", "music_db.merged.xml"),
)

val sdvx6Config by lazy {
    val file = File("data/sdvx6/config.json")
    try {
        if (file.exists() && !file.isDirectory) {
            json.decodeFromString(file.readText())
        } else {
            SDVX6Configuration().also {
                file.createNewFile()
                file.writeText(json.encodeToString(it))
            }
        }
    } catch (_: Exception) {
        file.delete()
        SDVX6Configuration().also {
            file.createNewFile()
            file.writeText(json.encodeToString(it))
        }
    }
}
