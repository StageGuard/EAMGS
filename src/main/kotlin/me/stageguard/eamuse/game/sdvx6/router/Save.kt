package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.buttongames.butterflycore.xml.kbinxml.childElements
import com.buttongames.butterflycore.xml.kbinxml.firstChild
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.game.sdvx6.SDVX6RouteHandler
import me.stageguard.eamuse.game.sdvx6.SDVX6_20210830
import me.stageguard.eamuse.game.sdvx6.SDVX6_20210831
import me.stageguard.eamuse.game.sdvx6.SDVX6_20211020
import me.stageguard.eamuse.game.sdvx6.model.*
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteModel
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.w3c.dom.Element
import java.time.LocalDateTime
import java.time.ZoneId

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020)
object Save : SDVX6RouteHandler("save") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val refId = gameNode.childNodeValue("refid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        // update profile
        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        // set
        profile.appeal = gameNode.childNodeValue("appeal_id") ?.toInt() ?: profile.appeal
        profile.musicID = gameNode.childNodeValue("music_id") ?.toInt() ?: profile.musicID
        profile.musicType = gameNode.childNodeValue("music_type") ?.toInt() ?: profile.musicType
        profile.sortType = gameNode.childNodeValue("sort_type") ?.toInt() ?: profile.sortType
        profile.headphone = gameNode.childNodeValue("headphone") ?.toInt() ?: profile.headphone
        profile.blasterCount = gameNode.childNodeValue("blaster_count") ?.toLong() ?: profile.blasterCount
        profile.hiSpeed = gameNode.childNodeValue("hispeed") ?.toInt() ?: profile.hiSpeed
        profile.laneSpeed = gameNode.childNodeValue("lanespeed") ?.toLong() ?: profile.laneSpeed
        profile.gaugeOption = gameNode.childNodeValue("gauge_option") ?.toInt() ?: profile.gaugeOption
        profile.arsOption = gameNode.childNodeValue("ars_option") ?.toInt() ?: profile.arsOption
        profile.notesOption = gameNode.childNodeValue("notes_option") ?.toInt() ?: profile.notesOption
        profile.earlyLateDisp = gameNode.childNodeValue("early_late_disp") ?.toInt() ?: profile.earlyLateDisp
        profile.drawAdjust = gameNode.childNodeValue("draw_adjust") ?.toInt() ?: profile.drawAdjust
        profile.effCLeft = gameNode.childNodeValue("eff_c_left") ?.toInt() ?: profile.effCLeft
        profile.effCRight = gameNode.childNodeValue("eff_c_right") ?.toInt() ?: profile.effCRight
        profile.narrowDown = gameNode.childNodeValue("narrow_down") ?.toInt() ?: profile.narrowDown
        // increase
        profile.packets += gameNode.childNodeValue("earned_gamecoin_packet") ?.toLong() ?: 0
        profile.blocks += gameNode.childNodeValue("earned_gamecoin_block") ?.toLong() ?: 0
        profile.blasterEnergy += gameNode.childNodeValue("earned_blaster_energy") ?.toLong() ?: 0
        profile.extrackEnergy += gameNode.childNodeValue("earned_extrack_energy") ?.toInt() ?: 0

        profile.flushChanges()

        // update item
        kotlin.run {
            val itemNodes = gameNode.firstChild("item") ?.childElements ?: sequenceOf()
            val existItems = Database.query { db -> db.sequenceOf(ItemTable).filter { it.refId eq refId } }
                ?.toList() ?: return@run

            val (toUpdate, toInsert) = mutableListOf<Item>() to mutableListOf<Item>()

            itemNodes.forEach { item ->
                val itemType = item.childNodeValue("type") ?.toInt() ?: return@forEach
                val itemId = item.childNodeValue("id") ?.toLong() ?: return@forEach
                val itemParam = item.childNodeValue("param") ?.toLong() ?: return@forEach

                val find = existItems.find { it.id == itemId && it.type == itemType }
                if (find != null) {
                    find.param = itemParam
                    toUpdate.add(find)
                } else {
                    toInsert.add(Item { this.refId = refId; type = itemType; id = itemId; param = itemParam })
                }
            }

            if (toInsert.isNotEmpty()) ItemTable.batchInsert(toInsert)
            if (toUpdate.isNotEmpty()) ItemTable.batchUpdate { toUpdate.forEach { u ->
                item {
                    set(ItemTable.param, u.param)
                    where { ItemTable.refId eq u.refId and (ItemTable.type eq u.type) and (ItemTable.id eq u.id) }
                }
            } }
        }

        // update param
        kotlin.run {
            val paramNodes = gameNode.firstChild("param") ?.childElements ?: sequenceOf()
            val existParams = Database.query { db -> db.sequenceOf(ParamTable).filter { it.refId eq refId } }
                ?.toList() ?: return@run

            val (toUpdate, toInsert) = mutableListOf<Param>() to mutableListOf<Param>()

            paramNodes.forEach { param ->
                val paramType = param.childNodeValue("type") ?.toInt() ?: return@forEach
                val paramId = param.childNodeValue("id") ?.toInt() ?: return@forEach
                val paramParam = param.childNodeValue("param") ?: return@forEach

                val find = existParams.find { it.id == paramId && it.type == paramType }
                if (find != null) {
                    find.param = paramParam
                    toUpdate.add(find)
                } else {
                    toInsert.add(Param { this.refId = refId; type = paramType; id = paramId; this.param = paramParam })
                }
            }

            if (toInsert.isNotEmpty()) ParamTable.batchInsert(toInsert)
            if (toUpdate.isNotEmpty()) ParamTable.batchUpdate { toUpdate.forEach { u ->
                item {
                    set(ParamTable.param, u.param)
                    where { ParamTable.refId eq u.refId and (ParamTable.type eq u.type) and (ParamTable.id eq u.id) }
                }
            } }
        }

        // update skill
        val skill = Database.query { db -> db.sequenceOf(SkillTable).find { it.refId eq refId } }
        if (skill != null) {
            skill.baseId = gameNode.childNodeValue("skill_base_id") ?.toInt() ?: skill.baseId
            skill.level = gameNode.childNodeValue("skill_level") ?.toInt() ?: skill.level
            skill.nameId = gameNode.childNodeValue("skill_name_id") ?.toInt() ?: skill.nameId
            skill.flushChanges()
        } else {
            SkillTable.insert(Skill {
                this.refId = refId
                baseId = gameNode.childNodeValue("skill_base_id") ?.toInt() ?: 0
                level = gameNode.childNodeValue("skill_level") ?.toInt() ?: 0
                nameId = gameNode.childNodeValue("skill_name_id") ?.toInt() ?: 0
            })
        }

        return createGameResponseNode()
    }
}

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020)
object SaveScore : SDVX6RouteHandler("save_m") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val refId = gameNode.childNodeValue("refid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val tracks = gameNode.childElements.filter { it.nodeName == "track" }
        tracks.forEach { track ->
            val mid = track.childNodeValue("music_id") ?.toLong() ?: return@forEach
            val type = track.childNodeValue("music_type") ?.toLong() ?: return@forEach
            val score = track.childNodeValue("score") ?.toLong() ?: 0
            val exScore = track.childNodeValue("exscore") ?.toLong() ?: 0
            val buttonRate = track.childNodeValue("btn_rate") ?.toInt() ?: 0
            val longRate = track.childNodeValue("long_rate") ?.toInt() ?: 0
            val volRate = track.childNodeValue("vol_rate") ?.toInt() ?: 0
            val clearType = track.childNodeValue("clear_type") ?.toLong() ?: 0
            val scoreGrade = track.childNodeValue("score_grade") ?.toLong() ?: 0

            PlayRecordTable.insert(PlayRecord {
                this.refId = refId
                this.mid = mid
                this.type = type
                this.score = score
                this.exScore = exScore
                this.buttonRate = buttonRate
                this.longRate = longRate
                this.volRate = volRate
                this.clear = clearType
                this.grade = scoreGrade
                this.time = System.currentTimeMillis()
            })
        }

        return createGameResponseNode()
    }
}

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020)
object SaveCourse : SDVX6RouteHandler("save_c") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val refId = gameNode.childNodeValue("refid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val sid = gameNode.childNodeValue("ssnid") ?.toInt()
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val cid = gameNode.childNodeValue("crsid") ?.toInt()
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val sc = gameNode.childNodeValue("sc") ?.toInt() ?: 0
        val ct = gameNode.childNodeValue("ct") ?.toInt() ?: 0
        val gr = gameNode.childNodeValue("gr") ?.toInt() ?: 0
        val ar = gameNode.childNodeValue("ar") ?.toInt() ?: 0

        val existCourse = Database.query { db -> db.sequenceOf(CourseRecordTable).find {
            it.refId eq refId and (it.sid eq sid) and (it.cid eq cid)
        } }

        if (existCourse != null) {
            existCourse.score = maxOf(existCourse.score, sc)
            existCourse.clear = maxOf(existCourse.clear, ct)
            existCourse.grade = maxOf(existCourse.grade, gr)
            existCourse.rate = maxOf(existCourse.rate,   ar)
            existCourse.count += 1
            existCourse.flushChanges()
        } else {
            CourseRecordTable.insert(CourseRecord {
                this.refId = refId
                this.sid = sid
                this.cid = cid
                this.score = sc
                this.clear = ct
                this.grade = gr
                this.rate = ar
                this.count = 1
            })
        }

        return createGameResponseNode()
    }
}