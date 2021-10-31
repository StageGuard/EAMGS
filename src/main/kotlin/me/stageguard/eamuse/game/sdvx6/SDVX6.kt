package me.stageguard.eamuse.game.sdvx6

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
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