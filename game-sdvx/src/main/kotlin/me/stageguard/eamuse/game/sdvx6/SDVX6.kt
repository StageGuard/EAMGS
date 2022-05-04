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

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.childElements
import com.buttongames.butterflycore.xml.kbinxml.firstChild
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.decodeFromStream
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.database.AddableTable
import me.stageguard.eamuse.game.sdvx6.api.*
import me.stageguard.eamuse.game.sdvx6.data.*
import me.stageguard.eamuse.game.sdvx6.model.*
import me.stageguard.eamuse.game.sdvx6.router.*
import me.stageguard.eamuse.getResourceOrExport
import me.stageguard.eamuse.json
import me.stageguard.eamuse.plugin.EAmPlugin
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.router.ProfileChecker
import me.stageguard.eamuse.tryOrNull
import org.slf4j.LoggerFactory
import java.nio.charset.Charset

private val LOGGER = LoggerFactory.getLogger("SDVX6")

/* Routers */
const val SDVX6_20210830 = "KFC:20210830"
const val SDVX6_20210831 = "KFC:20210831"
const val SDVX6_20211020 = "KFC:20211020"
const val SDVX6_20211124 = "KFC:20211124"
const val SDVX6_20220214 = "KFC:20220214"
const val SDVX6_20220308 = "KFC:20220308"
const val SDVX6_20220425 = "KFC:20220425"

object SDVX6 : EAmPlugin {
    override val id: String = "sdvx6"
    override val name: String = "SOUND VOLTEX EXCEED GEAR"
    override val code: String = "KFC"

    override val routerModules: List<RouterModule>
        get() = listOf(object : RouterModule("game") {
            override val routers: Set<RouteHandler>
                get() = mutableSetOf(Common, Longue, // common
                    New, HiScore, Load, LoadScore, LoadRival,  // profile
                    Save, SaveScore, SaveCourse, // save
                    Buy, Shop, // consume
                    *defaultSDVX6Router(
                        "frozen", "save_e", "save_mega", "play_e", "play_s", "entry_s", "entry_e", "exception"
                    )
                )
        })

    override val tables: List<AddableTable<*>>
        get() = listOf(CourseRecordTable, ItemTable, ParamTable, PlayRecordTable, SkillTable, UserProfileTable)

    override val profileChecker: ProfileChecker
        get() = SDVX6ProfileChecker

    override val apiHandlers: List<AbstractAPIHandler>
        get() = listOf(
            QueryRecentPlay, QueryProfile, QueryVolForce, QueryBest50Plays,
            Customize.Get, Customize.Update,
            GameDataList.GetAppealCards, GameDataList.GetChatStamps,
            GameDataList.GetNemsys, GameDataList.GetAkaName, GameDataList.GetCrews,
        )

}

/* Events */
internal val sdvx6Events = listOf(
    //'ICON_POLICY_BREAK',
    //'ICON_FLOOR_INFECTION',
    "DEMOGAME_PLAY",
    "MATCHING_MODE",
    "MATCHING_MODE_FREE_IP",
    "LEVEL_LIMIT_EASING",
    //'EVENT_IDS_SERIALCODE_TOHO_02',
    "ACHIEVEMENT_ENABLE",
    "APICAGACHADRAW\t30",
    "VOLFORCE_ENABLE",
    "AKANAME_ENABLE",
    //'FACTORY\t10',
    "PAUSE_ONLINEUPDATE",
    "CONTINUATION",
    "TENKAICHI_MODE",
    "QC_MODE",
    "KAC_MODE",
    "APPEAL_CARD_GEN_PRICE\t100",
    "APPEAL_CARD_GEN_NEW_PRICE\t200",
    "APPEAL_CARD_UNLOCK\t0,20170914,0,20171014,0,20171116,0,20180201,0,20180607,0,20181206,0,20200326,0,20200611,4,10140732,6,10150431",
    "FAVORITE_APPEALCARD_MAX\t200",
    "FAVORITE_MUSIC_MAX\t200",
    "EVENTDATE_APRILFOOL",
    "KONAMI_50TH_LOGO",
    "OMEGA_ARS_ENABLE",
    "DISABLE_MONITOR_ID_CHECK",
    "SKILL_ANALYZER_ABLE",
    "BLASTER_ABLE",
    "STANDARD_UNLOCK_ENABLE",
    "PLAYERJUDGEADJ_ENABLE",
    "MIXID_INPUT_ENABLE",
    //'SERIALCODE_JAPAN',
    "EVENTDATE_ONIGO",
    "EVENTDATE_GOTT",
    "GENERATOR_ABLE",
    "CREW_SELECT_ABLE",
    "PREMIUM_TIME_ENABLE",
    "OMEGA_ENABLE\t1,2,3,4,5,6,7,8,9",
    "HEXA_ENABLE\t1,2,3",
    "MEGAMIX_ENABLE",
    "VALGENE_ENABLE",
    "ARENA_ENABLE",
    "DISP_PASELI_BANNER"
)

/* Appeal cards */
internal val sdvx6AppealCards by lazy {
    val cards: MutableMap<Int, SDVX6AppealCard> = mutableMapOf()
    getResourceOrExport("sdvx6", "appeal_card.xml") {
        Load::class.java.getResourceAsStream("/sdvx6/appeal_card.xml") ?: run {
            LOGGER.warn("Appeal card source data is not found either jar or data folder.")
            return@getResourceOrExport null
        }
    }?.use { i ->
        i.tryOrNull { XmlUtils.byteArrayToXmlFile(readAllBytes()) }?.childElements?.forEach { c ->
            val cardInfo = c.firstChild("info") ?: return@forEach
            val cardId = c.getAttribute("id").toInt()
            cards[cardId] = SDVX6AppealCard(
                cardId,
                cardInfo.childNodeValue("texture") ?: return@forEach,
                cardInfo.childNodeValue("title") ?: return@forEach,
                cardInfo.childNodeValue("rarity")?.toInt() ?: return@forEach,
                cardInfo.childNodeValue("limited")?.toInt() ?: return@forEach
            )
        }
    }
    cards
}

/* chat stamp */
internal val sdvx6ChatStamp by lazy {
    val chatStamps: MutableMap<Int, SDVX6ChatStamp> = mutableMapOf()
    getResourceOrExport("sdvx6", "chat_stamp.xml") {
        Load::class.java.getResourceAsStream("/sdvx6/chat_stamp.xml") ?: run {
            LOGGER.warn("Chat stamp source data is not found either jar or data folder.")
            return@getResourceOrExport null
        }
    }?.use { i ->
        i.tryOrNull { XmlUtils.byteArrayToXmlFile(readAllBytes()) }?.childElements?.forEach { n ->
            val id = n.childNodeValue("id")?.toInt() ?: return@forEach
            val path = n.childNodeValue("filename") ?: return@forEach
            chatStamps[id] = SDVX6ChatStamp(id, path)
        }
    }
    chatStamps.also {
        if (it.isEmpty()) LOGGER.warn("Chat stamp data is empty, check if resource/sdvx6/custom_nemsys.xml exists in jar file!")
    }
}

/* nemsys */
internal val sdvx6Nemsys by lazy {
    val nemsys: MutableMap<Int, SDVX6Nemsys> = mutableMapOf()
    getResourceOrExport("sdvx6", "custom_nemsys.xml") {
        Load::class.java.getResourceAsStream("/sdvx6/custom_nemsys.xml") ?: run {
            LOGGER.warn("Custom nemsys source data is not found either jar or data folder.")
            return@getResourceOrExport null
        }
    }?.use { i ->
        i.tryOrNull { XmlUtils.byteArrayToXmlFile(readAllBytes()) }?.childElements?.forEach { n ->
            val id = n.childNodeValue("id")?.toInt() ?: return@forEach
            val texture = n.childNodeValue("texture_name") ?: return@forEach
            nemsys[id] = SDVX6Nemsys(id, texture)
        }
    }
    nemsys.also {
        if (it.isEmpty()) LOGGER.warn("Custom nemsys data is empty, check if resource/sdvx6/custom_nemsys.xml exists in jar file!")
    }
}

/* aka name*/
internal val sdvx6AkaNames by lazy {
    val akaNames: MutableMap<Int, SDVX6AkaName> = mutableMapOf()
    getResourceOrExport("sdvx6", "akaname_parts.xml") {
        Load::class.java.getResourceAsStream("/sdvx6/akaname_parts.xml") ?: run {
            LOGGER.warn("Aka name source data is not found either jar or data folder.")
            return@getResourceOrExport null
        }
    }?.use { i ->
        i.tryOrNull { XmlUtils.byteArrayToXmlFile(readAllBytes()) }?.childElements?.forEach { a ->
            val id = a.getAttribute("id").toInt()
            val word = a.childNodeValue("word") ?: return@forEach
            akaNames[id] = SDVX6AkaName(id, word)
        }
    }
    akaNames.also {
        if (it.isEmpty()) LOGGER.warn("Aka name data is empty, check if resource/sdvx6/custom_nemsys.xml exists in jar file!")
    }
}

/* crew */
@OptIn(ExperimentalSerializationApi::class)
internal val sdvx6Crews by lazy {
    (getResourceOrExport("sdvx6", "crew.json") {
        Load::class.java.getResourceAsStream("/sdvx6/crew.json") ?: run {
            LOGGER.warn("Crew source data is not found either jar or data folder.")
            return@getResourceOrExport null
        }
    }?.use { i ->
        i.tryOrNull { json.decodeFromStream<SDVX6CrewList>(i) }?.crew?.associate { it.id to it }
    } ?: mapOf()).also {
        if (it.isEmpty()) LOGGER.warn("Crew data are empty, check if resources/sdvx6/crew.json and exist in jar!")
    }
}

/* Music library */
internal val SDVX_DIFFICULTY_VALUE = arrayOf("novice", "advanced", "exhaust", "infinite", "maximum")
internal val sdvx6MusicLibrary by lazy {
    val musicLibs: MutableMap<Int, SDVX6Music> = mutableMapOf()
    sdvx6Config.musicDatabase.forEach _ignore@{ file ->
        getResourceOrExport("sdvx6", file) {
            Common::class.java.getResourceAsStream("/sdvx6/$file") ?: run {
                LOGGER.warn("Music database $file is not found either jar or data folder, please check your config.")
                return@getResourceOrExport null
            }
        }?.use { i ->
            i.tryOrNull { XmlUtils.byteArrayToXmlFile(readAllBytes()) }?.childElements?.forEach { m ->
                val musicInfo = m.firstChild("info") ?: return@forEach
                val difficulties = m.firstChild("difficulty") ?: return@forEach

                val diffDTO = mutableListOf<SDVX6MusicDifficulty>()
                SDVX_DIFFICULTY_VALUE.forEachIndexed { type, difficultyName ->
                    val difficultyInfo = difficulties.firstChild(difficultyName) ?: return@forEachIndexed

                    val difficultyNumber = difficultyInfo.childNodeValue("difnum")?.toInt() ?: 0
                    if (difficultyNumber < 1) return@forEachIndexed

                    val difficultyLimit = difficultyInfo.childNodeValue("limited")?.toInt() ?: 1

                    diffDTO.add(SDVX6MusicDifficulty(type, difficultyNumber, difficultyLimit))
                }

                val mid = m.getAttribute("id").toInt()
                musicLibs[mid] = SDVX6Music(mid,
                    musicInfo.childNodeValue("title_name") ?: "unknown title",
                    musicInfo.childNodeValue("artist_name") ?: "unknown artist",
                    (musicInfo.childNodeValue("bpm_min")?.toInt() ?: 0) / 100.0
                            to ((musicInfo.childNodeValue("bpm_max")?.toInt() ?: 0)) / 100.0,
                    diffDTO)
            }
        }
    }
    musicLibs.also {
        if (it.isEmpty()) LOGGER.warn("Music library are empty, check if resources/sdvx6/music_db.xml and music_db.merged.xml exist in jar!")
    }
}

/* Skill course  */
internal val sdvx6SkillCourseSessions by lazy {
    (getResourceOrExport("sdvx6", "course_session.json") {
        Common::class.java.getResourceAsStream("/sdvx6/course_session.json") ?: run {
            LOGGER.warn("Skill course data is not found either jar or data folder.")
            return@getResourceOrExport null
        }
    }?.use { i ->
        i.tryOrNull { json.decodeFromString<SDVX6SkillCourse>(readAllBytes().toString(Charset.defaultCharset())) }
    }?.sessions ?: listOf()).also {
        if (it.isEmpty()) LOGGER.warn("Skill course data is empty.")
    }
}
