package me.stageguard.eamuse.game.sdvx6

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.buttongames.butterflycore.xml.kbinxml.childElements
import com.buttongames.butterflycore.xml.kbinxml.firstChild
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.config
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.game.sdvx6.model.*
import me.stageguard.eamuse.game.sdvx6.router.*
import me.stageguard.eamuse.game.sdvx6.data.*
import me.stageguard.eamuse.game.sdvx6.handler.queryBest50Play
import me.stageguard.eamuse.game.sdvx6.handler.queryProfile
import me.stageguard.eamuse.game.sdvx6.handler.queryRecentPlay
import me.stageguard.eamuse.game.sdvx6.handler.queryVolForce
import me.stageguard.eamuse.game.sdvx6.util.getResourceOrExport
import me.stageguard.eamuse.game.sdvx6.util.tryOrNull
import me.stageguard.eamuse.json
import me.stageguard.eamuse.server.APIRequestDSL
import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.uriParameters
import org.intellij.lang.annotations.Language
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.slf4j.LoggerFactory
import org.w3c.dom.Element
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend

private val LOGGER = LoggerFactory.getLogger("SDVX6")

/* Routers */
const val SDVX6_20210830 = "KFC:20210830"
const val SDVX6_20210831 = "KFC:20210831"
const val SDVX6_20211020 = "KFC:20211020"
const val SDVX6_20211124 = "KFC:20211124"


object SDVX6Routers : RouteCollection("game") {
    override val routers: Set<RouteHandler>
        get() = mutableSetOf(Common, Longue, // common
            New, HiScore, Load, LoadScore, LoadRival,  // profile
            Save, SaveScore, SaveCourse, // save
            Buy, Shop, // consume
            *defaultSDVX6Router(
                "frozen", "save_e", "save_mega", "play_e", "play_s", "entry_s", "entry_e", "exception"
            )
        )
}

/* API Handlers */
private fun APIRequestDSL.validateAndRouting(method: String, handler: KFunction<String>) {
    routing(method) {
        val cardId = try {
            json.decodeFromString<CardIdDTO>(it.content().toString(Charset.forName("utf-8"))).cardId
        } catch (ex: SerializationException) {
            uriParameters(it.uri()) ?.get("cardid")
                ?: return@routing apiError("CARDID")
        }
        val refId = Database.query { db -> db.sequenceOf(EAmuseCardTable).find { c -> c.cardNFCId eq cardId } }
            ?.refId ?: return@routing apiError("REFID")

        if (handler.isSuspend) handler.callSuspend(refId) else handler.call(refId)
    }
}
@Language("JSON")
fun apiError(reason: String) = """{"result": -1, "message": "$reason"}"""

fun APIRequestDSL.sdvx6APIHandler() {
    validateAndRouting("recent", ::queryRecentPlay)
    validateAndRouting("profile", ::queryProfile)
    validateAndRouting("vf", ::queryVolForce)
    validateAndRouting("best50", ::queryBest50Play)
}

/* Database tables */
val sdvx6DatabaseTables = arrayOf(
    CourseRecordTable, ItemTable, ParamTable, PlayRecordTable, SkillTable, UserProfileTable
)

/* Events */
val sdvx6Events = listOf(
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
val sdvx6AppealCards: Lazy<Map<Int, SDVX6AppealCard>> = lazy {
    val cards: MutableMap<Int, SDVX6AppealCard> = mutableMapOf()
    getResourceOrExport("appeal_card.xml") {
        Load::class.java.getResourceAsStream("/sdvx6/appeal_card.xml") ?: run {
            LOGGER.warn("Appeal card source data is not found either jar or data folder.")
            return@getResourceOrExport null
        }
    } ?.use { i -> i.tryOrNull { XmlUtils.byteArrayToXmlFile(readAllBytes()) } ?.childElements ?.forEach { c ->
        val cardInfo = c.firstChild("info") ?: return@forEach
        val cardId = c.getAttribute("id").toInt()
        cards[cardId] = SDVX6AppealCard(
            cardId,
            cardInfo.childNodeValue("texture") ?: return@forEach,
            cardInfo.childNodeValue("title") ?: return@forEach,
            cardInfo.childNodeValue("rarity") ?.toInt() ?: return@forEach,
            cardInfo.childNodeValue("limited") ?.toInt() ?: return@forEach
        )
    } }
    cards
}

/* Music library */
val SDVX_DIFFICULTY_VALUE = arrayOf("novice", "advanced", "exhaust", "infinite", "maximum")
val sdvx6MusicLibrary: Lazy<Map<Int, SDVX6Music>> = lazy {
    val musicLibs: MutableMap<Int, SDVX6Music> = mutableMapOf()
    config.sdvx.musicDatabase.forEach _ignore@ { file ->
        getResourceOrExport(file) {
            Common::class.java.getResourceAsStream("/sdvx6/$file") ?: run {
                LOGGER.warn("Music database $file is not found either jar or data folder, please check your config.")
                return@getResourceOrExport null
            }
        } ?.use { i -> i.tryOrNull { XmlUtils.byteArrayToXmlFile(readAllBytes()) } ?.childElements ?.forEach { m ->
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
        } }
    }
    musicLibs.also {
        if (it.isEmpty()) LOGGER.warn("Music library are empty, check if resources/sdvx6/music_db.xml music_db.merged.xml and exist in jar!")
    }
}

/* Skill course  */
val sdvx6SkillCourseSessions = lazy {
    (getResourceOrExport("course_session.json") {
        Common::class.java.getResourceAsStream("/sdvx6/course_session.json") ?: run {
            LOGGER.warn("Skill course data is not found either jar or data folder.")
            return@getResourceOrExport null
        }
    } ?.use { i ->
        i.tryOrNull { json.decodeFromString<SDVX6SkillCourse>(readAllBytes().toString(Charset.defaultCharset())) }
    } ?.sessions ?: listOf()).also {
        if (it.isEmpty()) LOGGER.warn("Skill course data is empty.")
    }
}