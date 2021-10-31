package me.stageguard.eamuse.game.sdvx6

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.buttongames.butterflycore.xml.kbinxml.childElements
import com.buttongames.butterflycore.xml.kbinxml.firstChild
import kotlinx.serialization.decodeFromString
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.game.sdvx6.model.*
import me.stageguard.eamuse.game.sdvx6.router.*
import me.stageguard.eamuse.game.sdvx6.data.*
import me.stageguard.eamuse.json
import me.stageguard.eamuse.server.RouteModel
import org.intellij.lang.annotations.Language
import org.slf4j.LoggerFactory
import org.w3c.dom.Element

private val LOGGER = LoggerFactory.getLogger("SDVX6")

/* Routers */
const val SDVX6_20210830 = "KFC:20210830"
const val SDVX6_20210831 = "KFC:20210831"
val sdvx6RouteHandlers = arrayOf(
    Common, Shop, Longue, // common
    New, HiScore, Load, LoadScore, LoadRival,  // profile
    Save, SaveScore, SaveCourse, // save
    *defaultSDVX6Handler("frozen", "save_e", "save_mega", "play_e", "play_s", "entry_s", "entry_e", "exception")
)

private fun defaultSDVX6Handler(vararg method: String) : Array<out SDVX6RouteHandler> {
    return method.map { m ->
        @RouteModel(SDVX6_20210831, SDVX6_20210830)
        object : SDVX6RouteHandler(m) {
            override suspend fun handle(gameNode: Element): KXmlBuilder {
                return createGameResponseNode()
            }
        }
    }.toTypedArray()
}

/* Database tables */
val sdvx6DatabaseTables = listOf(
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
val sdvx6AppealCards: Map<Int, SDVX6AppealCard> = run {
    val cards: MutableMap<Int, SDVX6AppealCard> = mutableMapOf()
    Load::class.java.getResourceAsStream("/sdvx6/appeal_card.xml")?.run {
        try { XmlUtils.byteArrayToXmlFile(readAllBytes()) } catch (_: Exception) { null }
    } ?.childElements ?.forEach { c ->
        val cardInfo = c.firstChild("info") ?: return@forEach
        val cardId = c.getAttribute("id").toInt()
        cards[cardId] = SDVX6AppealCard(
            cardId,
            cardInfo.childNodeValue("texture") ?: return@forEach,
            cardInfo.childNodeValue("title") ?: return@forEach,
            cardInfo.childNodeValue("rarity") ?.toInt() ?: return@forEach,
            cardInfo.childNodeValue("limited") ?.toInt() ?: return@forEach
        )
    }

    cards.also {
        if (it.isEmpty()) LOGGER.warn("Appeal cards are empty, check if resources/sdvx6/appeal_card.xml exists in jar!")
    }
}

/* Music library */
val SDVX_DIFFICULTY_VALUE = arrayOf("novice", "advanced", "exhaust", "infinite", "maximum")
val sdvx6MusicLibrary: Map<Int, SDVX6Music> = run {
    val musicLibs: MutableMap<Int, SDVX6Music> = mutableMapOf()

    arrayOf("music_db.xml", "music_db.merged.xml").forEach _ignoreLabel@ { file ->
        Common::class.java.getResourceAsStream("/sdvx6/$file")?.run {
            try { XmlUtils.byteArrayToXmlFile(readAllBytes()) } catch (_: Exception) { null }
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

    musicLibs.also {
        if (it.isEmpty()) LOGGER.warn("Music library are empty, check if resources/sdvx6/music_db.xml music_db.merged.xml and exist in jar!")
    }
}

/* Skill course  */
val sdvx6SkillCourseSessions = lazy {
    @Language("JSON")
    val courseSeason1 = """{
        "id": 1,
        "name": "SKILL ANALYZER 第1回 Aコース",
        "isNew": 1,
        "courses": [
            {
                "id": 1,
                "type": 0,
                "name": "SKILL ANALYZER Level.01",
                "level": 1,
                "nameID": 1,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1383,
                        "mty": 0
                    },
                    {
                        "no": 1,
                        "mid": 334,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 774,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 2,
                "type": 0,
                "name": "SKILL ANALYZER Level.02",
                "level": 2,
                "nameID": 2,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 74,
                        "mty": 0
                    },
                    {
                        "no": 1,
                        "mid": 771,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 1125,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 3,
                "type": 0,
                "name": "SKILL ANALYZER Level.03",
                "level": 3,
                "nameID": 3,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 784,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 1126,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 1075,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 4,
                "type": 0,
                "name": "SKILL ANALYZER Level.04",
                "level": 4,
                "nameID": 4,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 505,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 1403,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 609,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 5,
                "type": 0,
                "name": "SKILL ANALYZER Level.05",
                "level": 5,
                "nameID": 5,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 630,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 1598,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 1475,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 6,
                "type": 0,
                "name": "SKILL ANALYZER Level.06",
                "level": 6,
                "nameID": 6,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1154,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 1238,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 590,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 7,
                "type": 0,
                "name": "SKILL ANALYZER Level.07",
                "level": 7,
                "nameID": 7,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1606,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 834,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 820,
                        "mty": 4
                    }
                ]
            },
            {
                "id": 8,
                "type": 0,
                "name": "SKILL ANALYZER Level.08",
                "level": 8,
                "nameID": 8,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 183,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 1602,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 173,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 9,
                "type": 0,
                "name": "SKILL ANALYZER Level.09",
                "level": 9,
                "nameID": 9,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1418,
                        "mty": 4
                    },
                    {
                        "no": 1,
                        "mid": 469,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 1413,
                        "mty": 4
                    }
                ]
            },
            {
                "id": 10,
                "type": 0,
                "name": "SKILL ANALYZER Level.10",
                "level": 10,
                "nameID": 10,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1596,
                        "mty": 4
                    },
                    {
                        "no": 1,
                        "mid": 1649,
                        "mty": 4
                    },
                    {
                        "no": 2,
                        "mid": 229,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 11,
                "type": 0,
                "name": "SKILL ANALYZER Level.11",
                "level": 11,
                "nameID": 11,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1651,
                        "mty": 4
                    },
                    {
                        "no": 1,
                        "mid": 1105,
                        "mty": 4
                    },
                    {
                        "no": 2,
                        "mid": 1152,
                        "mty": 4
                    }
                ]
            },
            {
                "id": 12,
                "type": 0,
                "name": "SKILL ANALYZER Level.∞",
                "level": 12,
                "nameID": 12,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1664,
                        "mty": 4
                    },
                    {
                        "no": 1,
                        "mid": 1528,
                        "mty": 4
                    },
                    {
                        "no": 2,
                        "mid": 1185,
                        "mty": 4
                    }
                ]
            }
        ]
    }"""
    @Language("JSON")
    val courseSeason2 = """{
        "id": 2,
        "name": "SKILL ANALYZER 第1回 Bコース",
        "isNew": 1,
        "courses": [
            {
                "id": 1,
                "type": 0,
                "name": "SKILL ANALYZER Level.01",
                "level": 1,
                "nameID": 1,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1066,
                        "mty": 0
                    },
                    {
                        "no": 1,
                        "mid": 1054,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 1055,
                        "mty": 0
                    }
                ]
            },
            {
                "id": 2,
                "type": 0,
                "name": "SKILL ANALYZER Level.02",
                "level": 2,
                "nameID": 2,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 768,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 948,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 755,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 3,
                "type": 0,
                "name": "SKILL ANALYZER Level.03",
                "level": 3,
                "nameID": 3,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 401,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 1320,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 485,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 4,
                "type": 0,
                "name": "SKILL ANALYZER Level.04",
                "level": 4,
                "nameID": 4,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 295,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 255,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 1029,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 5,
                "type": 0,
                "name": "SKILL ANALYZER Level.05",
                "level": 5,
                "nameID": 5,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1420,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 1001,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 1611,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 6,
                "type": 0,
                "name": "SKILL ANALYZER Level.06",
                "level": 6,
                "nameID": 6,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1338,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 79,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 1151,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 7,
                "type": 0,
                "name": "SKILL ANALYZER Level.07",
                "level": 7,
                "nameID": 7,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1047,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 982,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 1042,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 8,
                "type": 0,
                "name": "SKILL ANALYZER Level.08",
                "level": 8,
                "nameID": 8,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 664,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 1370,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 838,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 9,
                "type": 0,
                "name": "SKILL ANALYZER Level.09",
                "level": 9,
                "nameID": 9,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 624,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 1113,
                        "mty": 4
                    },
                    {
                        "no": 2,
                        "mid": 1629,
                        "mty": 4
                    }
                ]
            },
            {
                "id": 10,
                "type": 0,
                "name": "SKILL ANALYZER Level.10",
                "level": 10,
                "nameID": 10,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1595,
                        "mty": 4
                    },
                    {
                        "no": 1,
                        "mid": 1657,
                        "mty": 4
                    },
                    {
                        "no": 2,
                        "mid": 658,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 11,
                "type": 0,
                "name": "SKILL ANALYZER Level.11",
                "level": 11,
                "nameID": 11,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1647,
                        "mty": 4
                    },
                    {
                        "no": 1,
                        "mid": 1587,
                        "mty": 4
                    },
                    {
                        "no": 2,
                        "mid": 333,
                        "mty": 3
                    }
                ]
            },
            {
                "id": 12,
                "type": 0,
                "name": "SKILL ANALYZER Level.∞",
                "level": 12,
                "nameID": 12,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1363,
                        "mty": 4
                    },
                    {
                        "no": 1,
                        "mid": 692,
                        "mty": 3
                    },
                    {
                        "no": 2,
                        "mid": 1270,
                        "mty": 4
                    }
                ]
            }
        ]
    }"""
    @Language("JSON")
    val courseSeason3 = """{
        "id": 3,
        "name": "SKILL ANALYZER 第1回 Cコース",
        "isNew": 1,
        "courses": [
            {
                "id": 1,
                "type": 0,
                "name": "SKILL ANALYZER Level.01",
                "level": 1,
                "nameID": 1,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1376,
                        "mty": 0
                    },
                    {
                        "no": 1,
                        "mid": 564,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 87,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 2,
                "type": 0,
                "name": "SKILL ANALYZER Level.02",
                "level": 2,
                "nameID": 2,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 34,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 932,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 945,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 3,
                "type": 0,
                "name": "SKILL ANALYZER Level.03",
                "level": 3,
                "nameID": 3,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1132,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 1549,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 380,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 4,
                "type": 0,
                "name": "SKILL ANALYZER Level.04",
                "level": 4,
                "nameID": 4,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 130,
                        "mty": 1
                    },
                    {
                        "no": 1,
                        "mid": 1204,
                        "mty": 1
                    },
                    {
                        "no": 2,
                        "mid": 1424,
                        "mty": 1
                    }
                ]
            },
            {
                "id": 5,
                "type": 0,
                "name": "SKILL ANALYZER Level.05",
                "level": 5,
                "nameID": 5,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 48,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 565,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 1109,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 6,
                "type": 0,
                "name": "SKILL ANALYZER Level.06",
                "level": 6,
                "nameID": 6,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1534,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 1398,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 1312,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 7,
                "type": 0,
                "name": "SKILL ANALYZER Level.07",
                "level": 7,
                "nameID": 7,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 962,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 1560,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 357,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 8,
                "type": 0,
                "name": "SKILL ANALYZER Level.08",
                "level": 8,
                "nameID": 8,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 965,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 906,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 579,
                        "mty": 2
                    }
                ]
            },
            {
                "id": 9,
                "type": 0,
                "name": "SKILL ANALYZER Level.09",
                "level": 9,
                "nameID": 9,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 332,
                        "mty": 2
                    },
                    {
                        "no": 1,
                        "mid": 36,
                        "mty": 2
                    },
                    {
                        "no": 2,
                        "mid": 1476,
                        "mty": 4
                    }
                ]
            },
            {
                "id": 10,
                "type": 0,
                "name": "SKILL ANALYZER Level.10",
                "level": 10,
                "nameID": 10,
                "assist": 0,
                "tracks": [
                    {
                        "no": 0,
                        "mid": 1533,
                        "mty": 4
                    },
                    {
                        "no": 1,
                        "mid": 1597,
                        "mty": 4
                    },
                    {
                        "no": 2,
                        "mid": 1541,
                        "mty": 4
                    }
                ]
            }
        ]
    }"""

    listOf(courseSeason1, courseSeason2, courseSeason3).map {
        json.decodeFromString<SkillCourseSeason>(it)
    }
}