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
    val musicDatabase: List<String> = listOf("music_db.xml", "music_db.merged.xml")
)

val sdvx6Config = lazy {
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
