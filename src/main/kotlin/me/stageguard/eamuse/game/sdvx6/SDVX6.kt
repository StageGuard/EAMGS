package me.stageguard.eamuse.game.sdvx6

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.buttongames.butterflycore.xml.kbinxml.childElements
import com.buttongames.butterflycore.xml.kbinxml.firstChild
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.game.sdvx6.model.*
import me.stageguard.eamuse.game.sdvx6.router.*
import me.stageguard.eamuse.server.RouteModel
import org.w3c.dom.Element


const val SDVX6_20210830 = "KFC:20210830"
const val SDVX6_20210831 = "KFC:20210831"
val sdvx6RouteHandlers = arrayOf(
    Common, Shop, Longue, // common
    New, HiScore, Load, LoadScore, LoadRival,  // profile
    Save, SaveScore, SaveCourse, // save
    *defaultSDVX6Handler("frozen", "save_e", "save_mega", "play_e", "play_s", "entry_s", "entry_e", "exception")
)

val sdvx6DatabaseTables = listOf(
    CourseRecordTable, ItemTable, ParamTable, PlayRecordTable, SkillTable, UserProfileTable
)

val SDVX_DIFFICULTY_VALUE = arrayOf("novice", "advanced", "exhaust", "infinite", "maximum")

val sdvx6MusicLibrary: Map<Int, SDVX6Music> = run {
    val musicLibs: MutableMap<Int, SDVX6Music> = mutableMapOf()

    arrayOf("music_db.xml", "music_db.merged.xml").forEach _ignoreLabel@ { file ->
        Common::class.java.getResourceAsStream("/sdvx6/$file")?.run {
            XmlUtils.byteArrayToXmlFile(this.readAllBytes())
        } ?.childElements ?.forEach { m ->
            val musicInfo = m.firstChild("info") ?: return@forEach
            val difficulties = m.firstChild("difficulty") ?: return@forEach

            val diffDTO = mutableListOf<SDVX6MusicDifficulty>()
            SDVX_DIFFICULTY_VALUE.forEachIndexed { type, difficultyName ->
                val difficultyInfo = difficulties.firstChild(difficultyName) ?: return@forEachIndexed

                val difficultyNumber = difficultyInfo.childNodeValue("difnum") ?.toInt() ?: 0
                if (difficultyNumber < 1) return@forEachIndexed

                val difficultyLimit = difficultyInfo.childNodeValue("limited") ?.toInt() ?: 1

                diffDTO.add(SDVX6MusicDifficulty(type, difficultyNumber, difficultyLimit))
            }

            val mid = m.getAttribute("id").toInt()
            musicLibs[mid] = SDVX6Music(mid,
                musicInfo.childNodeValue("title_name") ?: "unknown title",
                musicInfo.childNodeValue("artist_name") ?: "unknown artist",
                (musicInfo.childNodeValue("bpm_min") ?.toInt() ?: 0) / 100.0
                        to ((musicInfo.childNodeValue("bpm_max") ?.toInt() ?: 0)) / 100.0,
                diffDTO)
        }
    }

    musicLibs
}

private fun defaultSDVX6Handler(vararg method: String) : Array<out SDVX6RouteHandler> {
    return method.map { m ->
        @RouteModel(SDVX6_20210831, SDVX6_20210830)
        object : SDVX6RouteHandler(m) {
            override suspend fun processGameNode(gameNode: Element): KXmlBuilder {
                return createGameResponseNode()
            }
        }
    }.toTypedArray()
}