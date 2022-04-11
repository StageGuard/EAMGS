package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.config
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.game.sdvx6.model.*
import org.ktorm.dsl.eq
import org.ktorm.dsl.notEq
import org.ktorm.entity.*
import org.w3c.dom.Element
import java.util.concurrent.atomic.AtomicInteger

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124, SDVX6_20220214, SDVX6_20220308)
object Load : SDVX6RouteHandler("load") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val refId = gameNode.childNodeValue("refid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: return createGameResponseNode().u8("result", 1)

        val skill = Database.query { db -> db.sequenceOf(SkillTable).find { it.refId eq refId } }
            ?: Skill { baseId = 0; level = 0; nameId = 0 }

        val courses = Database.query { db -> db.sequenceOf(CourseRecordTable).filter { it.refId eq refId }.toList() } ?: listOf()
        val items = Database.query { db -> db.sequenceOf(ItemTable).filter { it.refId eq refId }.toMutableList() } ?: mutableListOf()
        val params = Database.query { db -> db.sequenceOf(ParamTable).filter { it.refId eq refId }.toMutableList() } ?: mutableListOf()

        val timestampAfterOneAndAHalfDay = System.currentTimeMillis() + 1000 * 60 * 60 * 36

        val customize = listOf(
            profile.bgm, profile.subbg, profile.nemsys,
            profile.stampA, profile.stampB, profile.stampC, profile.stampD
        )

        var customizeParamIndex = params.indexOfFirst { it.type == 2 && it.id == 2 }
        if (customizeParamIndex == -1) {
            params.add(Param { type = 2; id = 2; _param = "" })
            customizeParamIndex = params.indexOfFirst { it.type == 2 && it.id == 2 }
        }
        params.getOrNull(customizeParamIndex)?.param = customize

        if (config.sdvx.unlockAllNavigators) {
            repeat(300) { items.add(Item { type = 11; id = it.toLong(); param = 15 }) }
            // 10 genesis card for MITSURU's voice
            items.add(Item { type = 4; id = 599; param = 10; })
        }

        if (config.sdvx.unlockAllAppealCards) {
            sdvx6AppealCards.value.forEach { (id, _) -> items.add(Item { type = 1; this.id = id.toLong(); param = 1 }) }
        }

        // make generator always power 100%
        repeat(50) { items.add(Item { type = 7; id = it.toLong(); param = 10 }) }

        var resp = createGameResponseNode()
            .u8("result", 0).up()
            .str("name", profile.name).up()
            .str("code", profile.id.toString().run {
                "${take(4)}-${takeLast(4)}"
            }).up()
            .u32("gamecoin_packet", profile.packets).up()
            .u32("gamecoin_block", profile.blocks).up()
            .u16("appeal_id", profile.appeal).up()

            .s32("last_music_id", profile.musicID).up()
            .u8("last_music_type", profile.musicType).up()
            .u8("sort_type", profile.sortType).up()
            .u8("headphone", profile.headphone).up()
            .u32("blaster_energy", profile.blasterEnergy).up()
            .u32("blaster_count", profile.blasterCount).up()
            .u16("extrack_energy", profile.extrackEnergy).up()

            .s32("hispeed", profile.hiSpeed).up()
            .u32("lanespeed", profile.laneSpeed).up()
            .u8("gauge_option", profile.gaugeOption).up()
            .u8("ars_option", profile.arsOption).up()
            .u8("notes_option", profile.notesOption).up()
            .u8("early_late_disp", profile.earlyLateDisp).up()
            .s32("draw_adjust", profile.drawAdjust).up()
            .u8("eff_c_left", profile.effCLeft).up()
            .u8("eff_c_right", profile.effCRight).up()
            .u8("narrow_down", profile.narrowDown).up()

            .str("kac_id", profile.name).up()

            .s16("skill_level", skill.level).up()
            .s16("skill_base_id", skill.baseId).up()
            .s16("skill_name_id", skill.nameId).up()

            .e("ea_shop")
                .s32("packet_booster", 1).up()
                .bool("blaster_pass_enable", config.sdvx.useBlasterPass).up()
                .u64("blaster_pass_limit_date", timestampAfterOneAndAHalfDay).up()
            .up()

            .e("eaappli")
                .s8("relation", 1).up()
            .up()
            .e("cloud")
                .s8("relation", 1).up()
            .up()
            .s32("block_no", 0).up()

            .e("skill")
            courses.forEach { c ->
                resp = resp.e("course")
                    .s16("ssnid", c.sid).up()
                    .s16("crsid", c.cid).up()
                    .s32("sc", c.score).up()
                    .s32("ex", 0).up()
                    .s16("ct", c.clear).up()
                    .s16("gr", c.grade).up()
                    .s16("ar", c.rate).up()
                    .s16("cnt", c.count).up()
                resp = resp.up()
            }
        resp = resp.up()

            .e("item")
            items.forEach { i ->
                resp = resp.e("info")
                    .u8("type", i.type).up()
                    .u32("id", i.id).up()
                    .u32("param", i.param).up()
                resp = resp.up()
            }
        resp = resp.up()

            .e("param")
            params.forEach { i ->
                resp = resp.e("info")
                    .s32("type", i.type).up()
                    .s32("id", i.id).up()
                    .e("param")
                        .a("__type", "s32")
                        .a("__count", i.param.size.toString())
                        .t(i._param).up()
                resp = resp.up()
            }
            //- Akaname
            repeat(3) { id ->
                resp = resp.e("info")
                    .s32("type", 6).up()
                    .s32("id", id).up()
                        .e("param")
                        .a("__type", "s32")
                        .a("__count", "1")
                        .t(profile.akaname.toString()).up()
                resp = resp.up()
            }
        resp = resp.up()

            .u32("play_count", 1001).up()
            .u32("day_count", 301).up()
            .u32("today_count", 21).up()
            .u32("play_chain", 31).up()
            .u32("max_play_chain", 31).up()
            .u32("week_count", 9).up()
            .u32("week_play_count", 101).up()
            .u32("week_chain", 31).up()
            .u32("max_week_chain", 31).up()

        return resp
    }

}

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124, SDVX6_20220214, SDVX6_20220308)
object LoadScore : SDVX6RouteHandler("load_m") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val refId = gameNode.childNodeValue("refid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val playRecords = Database.query { db ->
            db.sequenceOf(PlayRecordTable)
                .filter { it.refId eq refId }
                .groupBy { it.mid * 5 + it.type }
                .map { (_, v) -> PlayRecord {
                    mid = v.first().mid
                    type = v.first().type
                    score = v.maxOf { it.score }
                    exScore = v.last().exScore
                    clear = v.maxOf { it.clear }
                    grade = v.maxOf { it.grade }
                    buttonRate = v.maxOf { it.buttonRate }
                    longRate = v.maxOf { it.longRate }
                    volRate = v.maxOf { it.volRate }
                } }.toList()
        } ?: listOf()

        var resp = createGameResponseNode().e("music")
        playRecords.forEach { r ->
            resp = resp.e("info")
                .e("param")
                    .a("__type", "u32")
                    .a("__count", "21")
                    .t(listOf(
                        r.mid, r.type, r.score, r.exScore, r.clear, r.grade,
                        0, 0, r.buttonRate, r.longRate, r.volRate, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                    ).joinToString(" ")).up()
            resp = resp.up()
        }

        return resp
    }
}

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124, SDVX6_20220214, SDVX6_20220308)
object LoadRival : SDVX6RouteHandler("load_r") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val refId = gameNode.childNodeValue("refid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val rivalProfiles = Database.query { db -> db.sequenceOf(UserProfileTable).filter { it.refId notEq refId }.toList() } ?: listOf()

        var resp = createGameResponseNode()

        val rivalProfileIndex = AtomicInteger(0)
        rivalProfiles.forEach { p ->
            val rpr = Database.query { db ->
                db.sequenceOf(PlayRecordTable)
                    .filter { it.refId eq p.refId }
                    .groupBy { it.mid to it.type }
                    .map { it.value.maxByOrNull { s -> s.score }!! }
            } ?.toList() ?: return@forEach
            resp = resp.e("rival")
                .s16("no", rivalProfileIndex.getAndIncrement()).up()
                .str("seq", p.id.toString().run {
                    "${take(4)}-${takeLast(4)}"
                }).up()
                .str("name", p.name).up()
                .e("music")
                    rpr.forEach { rp ->
                        resp = resp.e("param")
                            .a("__type", "u32")
                            .a("__count", "5")
                            .t(listOf(rp.mid, rp.type, rp.score, rp.clear, rp.grade).joinToString(" "))
                        resp = resp.up()
                    }
                resp = resp.up()
            resp = resp.up()
        }
        return resp
    }
}
