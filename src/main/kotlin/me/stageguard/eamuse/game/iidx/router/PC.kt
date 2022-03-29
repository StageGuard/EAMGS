@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.iidx.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.buttongames.butterflycore.xml.kbinxml.childElements
import com.buttongames.butterflycore.xml.kbinxml.firstChild
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.game.iidx.IIDXPCRouteHandler
import me.stageguard.eamuse.game.iidx.LDJ20211013
import me.stageguard.eamuse.game.iidx.model.*
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteModel
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import org.w3c.dom.Element
import kotlin.properties.Delegates
import kotlin.random.Random

@RouteModel(LDJ20211013)
object Common : IIDXPCRouteHandler("common") {
    override suspend fun handle(node: Element): KXmlBuilder {
        return createResponseNode()
            .u16("monthly_mranking", 65535).up()
            .u16("total_mranking", 65535).up()
            .e("internet_ranking").up()
            .e("secret_ex_course").up()
            .s32("kac_mid", 65535).up()
            .s32("kac_clid", 0).up()
            .e("ir").a("beat", "3").up()
            .e("cm").a("compo", "cm_ultimate").a("folder", "cm_ultimate").a("id", "0").up()
            .e("tdj_cm")
                .e("cm").a("filename", "cm_bn_001").a("id", "0").up()
                .e("cm").a("filename", "cm_bn_002").a("id", "1").up()
                .e("cm").a("filename", "event_bn_001").a("id", "2").up()
                .e("cm").a("filename", "event_bn_004").a("id", "3").up()
                .e("cm").a("filename", "event_bn_006").a("id", "4").up()
                .e("cm").a("filename", "fipb_001").a("id", "5").up()
                .e("cm").a("filename", "year_bn_004").a("id", "6").up()
                .e("cm").a("filename", "year_bn_005").a("id", "7").up()
                .e("cm").a("filename", "year_bn_006_2").a("id", "8").up()
                .e("cm").a("filename", "year_bn_007").a("id", "9").up()
            .up()
            .e("license")
                .str("string", "StageGuard").up()
            .up()
            .e("file_recovery").a("url", "https://example.com").up()
            .e("button_release_frame").a("frame", "0").up()
            .e("escape_package_info")
                .e("list").a("apply_file_name", "XXX").a("apply_release_code", "XXX").up()
            .up()
            .e("expert").a("phase", "1").up()
            .e("expert_random_secret").a("phase", "2").up()
            .e("boss").a("phase", "0").up()
            .e("vip_pass_black").up()
            .e("deller_bonus").a("open", "1").up()
            .e("newsong_another").a("open", "1").up()
            .e("pcb_check").a("flg", "0").up()
            .e("expert_secret_full_open").up()
            .e("eaorder_phase").a("phase", "2").up()
            .e("common_evnet").a("flg", "65535").up()
            .e("system_voice_phase").a("phase", "0").up()
            .e("extra_boss_event").a("phase", "0").up()
            .e("event1_phase").a("phase", "0").up()
            .e("premium_area_news").a("open", "1").up()
            .e("premium_area_qpro").a("open", "1").up()
            .e("ignore_button_mashing").up()
    }
}

@RouteModel(LDJ20211013)
object OldGet : IIDXPCRouteHandler("oldget") {
    override suspend fun handle(node: Element): KXmlBuilder {
//        val refId = node.getAttribute("rid")
//            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
//
//        Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
//            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        return createResponseNode()
    }
}

@RouteModel(LDJ20211013)
object GetProfileName : IIDXPCRouteHandler("getname") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val refId = node.getAttribute("rid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        return createResponseNode()
            .a("idstr", profile.iidxId.toString().run { "${slice(0..3)}-${slice(4..7)}" })
            .a("name", profile.name)
            .a("pid", profile.pid.toString())
    }
}

private suspend fun resetPCData(refId: String) = Database.query { db ->
    val pcData = db.sequenceOf(PCDataTable).find { it.refId eq refId }

    if(pcData != null) {
        pcData.deller = 0; pcData.sgid = -1; pcData.dgid = -1
        pcData.trophy = listOf("0", "0", "0", "0", "0", "0", "0", "0", "0", "0")
        pcData.spRank = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        pcData.spPoint = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        pcData.dpRank = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        pcData.dpPoint = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        pcData.spRadar = listOf(0, 0, 0, 0, 0, 0); pcData.dpRadar = listOf(0, 0, 0, 0, 0, 0)
        pcData.dpClearMissionClear = 0; pcData.dpClearMissionLevel = 0; pcData.dpDjMissionClear = 0
        pcData.dpDjMissionLevel = 0; pcData.dpLevel = 0; pcData.dpMissionPoint = 0
        pcData.dpMplay = 0; pcData.enemyDamage = 0; pcData.progress = 0; pcData.spClearMissionClear = 0
        pcData.spClearMissionLevel = 0; pcData.spDjMissionClear = 0; pcData.spDjMissionLevel = 0
        pcData.spLevel = 0; pcData.spMissionPoint = 0; pcData.spMplay = 0; pcData.tipsReadList = 0
        pcData.totalPoint = 0; pcData.enemyDefeatFlg = 0; pcData.missionClearNum = 0; pcData.dpnum = 0
        pcData.dAutoScrach = 0; pcData.dCameraLayout = 0; pcData.dDispJudge = 0; pcData.dGaugeDisp = 0
        pcData.dGhostScore = 0; pcData.dGno = 0; pcData.dGraphScore = 0; pcData.dGtype = 0
        pcData.dHispeed = 0; pcData.dJudge = 0; pcData.dJudgeAdj = 0; pcData.dLaneBrignt = 0
        pcData.dLiflen = 0; pcData.dNotes = 0; pcData.dOpstyle = 0; pcData.dPace = 0
        pcData.dSdlen = 0; pcData.dSdtype = 0; pcData.dSorttype = 0; pcData.dTiming = 0
        pcData.dTsujigiriDisp = 0; pcData.dach = 0; pcData.dSubGno = 0; pcData.dTune = 0
        pcData.gpos = 0; pcData.mode = 0; pcData.pmode = 0; pcData.rtype = 0
        pcData.ngrade = 0; pcData.spnum = 0; pcData.sAutoScrach = 0; pcData.sCameraLayout = 0
        pcData.sDispJudge = 0; pcData.sGaugeDisp = 0; pcData.sGhostScore = 0; pcData.sGno = 0
        pcData.sGraphScore = 0; pcData.sGtype = 0; pcData.sHispeed = 0; pcData.sJudge = 0
        pcData.sJudgeAdj = 0; pcData.sLaneBrignt = 0; pcData.sLiflen = 0; pcData.sNotes = 0
        pcData.sOpstyle = 0; pcData.sPace = 0; pcData.sSdlen = 0; pcData.sSdtype = 0
        pcData.sSorttype = 0; pcData.sTiming = 0; pcData.sTsujigiriDisp = 0; pcData.sach = 0
        pcData.sSubGno = 0; pcData.sTune = 0; pcData.sAutoAdjust = 0; pcData.dAutoAdjust = 0
        pcData.dpOpt = "0"; pcData.dpOpt2 = "0"; pcData.spOpt = "0"
        pcData.lastWeekly = 0; pcData.pack = 0; pcData.packComp = 0; pcData.rivalCrush = 0
        pcData.visitFlg = 0; pcData.weeklyNum = 0

        pcData.flushChanges()
    } else {
        PCDataTable.insert(PCData {
            this.refId = refId
            deller = 0; sgid = -1; dgid = -1
            trophy = listOf("0", "0", "0", "0", "0", "0", "0", "0", "0", "0")
            spRank = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            spPoint = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            dpRank = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            dpPoint = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            spRadar = listOf(0, 0, 0, 0, 0, 0); dpRadar = listOf(0, 0, 0, 0, 0, 0)
            dpClearMissionClear = 0; dpClearMissionLevel = 0; dpDjMissionClear = 0; dpDjMissionLevel = 0
            dpLevel = 0; dpMissionPoint = 0; dpMplay = 0; enemyDamage = 0; progress = 0; spClearMissionClear = 0
            spClearMissionLevel = 0; spDjMissionClear = 0; spDjMissionLevel = 0; spLevel = 0
            spMissionPoint = 0; spMplay = 0; tipsReadList = 0; totalPoint = 0; enemyDefeatFlg = 0
            missionClearNum = 0; dpnum = 0; dAutoScrach = 0; dCameraLayout = 0; dDispJudge = 0; dGaugeDisp = 0
            dGhostScore = 0; dGno = 0; dGraphScore = 0; dGtype = 0; dHispeed = 0; dJudge = 0; dJudgeAdj = 0
            dLaneBrignt = 0; dLiflen = 0; dNotes = 0; dOpstyle = 0; dPace = 0; dSdlen = 0; dSdtype = 0
            dSorttype = 0; dTiming = 0; dTsujigiriDisp = 0; dach = 0; dSubGno = 0; dTune = 0
            gpos = 0; mode = 0; pmode = 0; rtype = 0; ngrade = 0; spnum = 0; sAutoScrach = 0; sCameraLayout = 0
            sDispJudge = 0; sGaugeDisp = 0; sGhostScore = 0; sGno = 0; sGraphScore = 0; sGtype = 0
            sHispeed = 0; sJudge = 0; sJudgeAdj = 0; sLaneBrignt = 0; sLiflen = 0; sNotes = 0; sOpstyle = 0
            sPace = 0; sSdlen = 0; sSdtype = 0; sSorttype = 0; sTiming = 0; sTsujigiriDisp = 0; sach = 0
            sSubGno = 0; sTune = 0; sAutoAdjust = 0; dAutoAdjust = 0; dpOpt = "0"; dpOpt2 = "0"; spOpt = "0"
            lastWeekly = 0; pack = 0; packComp = 0; rivalCrush = 0; visitFlg = 0; weeklyNum = 0
        })
    }
}

@RouteModel(LDJ20211013)
object TakeOver : IIDXPCRouteHandler("takeover") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val refId = node.getAttribute("rid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        profile.name = node.getAttribute("name")
        profile.pid = node.getAttribute("pid").toInt()
        profile.refId = refId

        profile.flushChanges()

        resetPCData(refId)

        return createResponseNode().a("id", profile.iidxId.toString())
    }
}

@RouteModel(LDJ20211013)
object PCRegister : IIDXPCRouteHandler("reg") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val refId = node.getAttribute("rid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val generatedId = Database.query { db ->
            val ids = db.sequenceOf(UserProfileTable).map { it.iidxId }.toSet()
            var generated by Delegates.notNull<Int>()
            do {
                generated = Random.Default.nextInt(10000000, 99999999)
            } while (generated in ids)
            generated
        } ?: throw InvalidRequestException(HttpResponseStatus.INTERNAL_SERVER_ERROR)

        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }

        if (profile != null) {
            profile.name = node.getAttribute("name")
            profile.pid = node.getAttribute("pid").toInt()
            profile.iidxId = generatedId
        } else {
            UserProfileTable.insert(UserProfile {
                this.refId = refId
                name = node.getAttribute("name")
                pid = node.getAttribute("pid").toInt()
                iidxId = generatedId
            })
        }

        resetPCData(refId)

        return createResponseNode()
            .a("id", generatedId.toString())
            .a("id_str", generatedId.toString().run { "${slice(0..3)}-${slice(4..7)}" })
    }
}

@RouteModel(LDJ20211013)
object Get : IIDXPCRouteHandler("get") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val refId = node.getAttribute("rid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val pcData = Database.query { db -> db.sequenceOf(PCDataTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val sortedDArray = Database.query { db -> db.sequenceOf(GradeTable).filter { it.refId eq refId } }
            ?.map { it.dArray } ?.sortedWith comparator@ { a, b ->
                val diff1 = a[0] - b[0]
                if (diff1 != 0) return@comparator diff1
                return@comparator a[1] - b[1]
            } ?: throw InvalidRequestException(HttpResponseStatus.INTERNAL_SERVER_ERROR)

        //TODO: modify settings via api
        val settings = Database.query { db ->
            db.sequenceOf(SettingsTable).find { it.refId eq refId } ?: Settings {
                this.refId = refId
                frame = 0; menuMusic = 0; noteBurst = 0; turntable = 0; laneCover = 0; pacemakerCover = 0
                noteSkin = 0; judgeFont = 0; noteBeam = 0; fullComboSplash = 0; disableMusicpreview = 0
                vefxLock = 0; effect = 0; bombSize = 0; disableHcnColor = 0; firstNotePreview = 0
                qproHead = 0; qproHair = 0; qproHand = 0; qproFace = 0; qproBody = 0
                scoreFolders = true; clearFolders = true; difficultyFolders = true; alphabetFolders = true
                hidePlaycount = false; disableGraphcutin = false; classicHispeed = false;hideIidxid = false
            }.also { SettingsTable.insert(it) }
        } ?: throw InvalidRequestException(HttpResponseStatus.INTERNAL_SERVER_ERROR)

        val appendSetting = (if(settings.scoreFolders) 1 else 0) * 1 +
            (if(settings.clearFolders) 1 else 0) * 2 +
            (if(settings.difficultyFolders) 1 else 0) * 4 +
            (if(settings.alphabetFolders) 1 else 0) * 8 +
            (if(settings.hidePlaycount) 1 else 0) * 256 +
            (if(settings.disableGraphcutin) 1 else 0) * 512 +
            (if(settings.classicHispeed) 1 else 0) * 1024 +
            (if(settings.hideIidxid) 1 else 0) * 4196

        return createResponseNode()
            .e("pcdata")
                .a("d_auto_scrach", pcData.dAutoScrach.toString())
                .a("d_camera_layout", pcData.dCameraLayout.toString())
                .a("d_disp_judge", pcData.dDispJudge.toString())
                .a("d_gauge_disp", pcData.dGaugeDisp.toString())
                .a("d_ghost_score", pcData.dGhostScore.toString())
                .a("d_gno", pcData.dGno.toString())
                .a("d_graph_score", pcData.dGraphScore.toString())
                .a("d_gtype", pcData.dGtype.toString())
                .a("d_hispeed", pcData.dHispeed.toString())
                .a("d_judge", pcData.dJudge.toString())
                .a("d_judgeAdj", pcData.dJudgeAdj.toString())
                .a("d_lane_brignt", pcData.dLaneBrignt.toString())
                .a("d_liflen", pcData.dLiflen.toString())
                .a("d_notes", pcData.dNotes.toString())
                .a("d_opstyle", pcData.dOpstyle.toString())
                .a("d_pace", pcData.dPace.toString())
                .a("d_sdlen", pcData.dSdlen.toString())
                .a("d_sdtype", pcData.dSdtype.toString())
                .a("d_sorttype", pcData.dSorttype.toString())
                .a("d_sub_gno", pcData.dSubGno.toString())
                .a("d_timing", pcData.dTiming.toString())
                .a("d_tsujigiri_disp", pcData.dTsujigiriDisp.toString())
                .a("d_tune", pcData.dTune.toString())
                .a("dach", pcData.dach.toString())
                .a("dp_opt", pcData.dpOpt)
                .a("dp_opt2", pcData.dpOpt2)
                .a("dpnum", "0")
                .a("gpos", pcData.gpos.toString())
                .a("id", profile.iidxId.toString())
                .a("idstr", profile.iidxId.toString().run { "${slice(0..3)}-${slice(4..7)}" })
                .a("mode", pcData.mode.toString())
                .a("name", profile.name)
                .a("pid", profile.pid.toString())
                .a("pmode", pcData.pmode.toString())
                .a("rtype", pcData.rtype.toString())
                .a("s_auto_scrach", pcData.sAutoScrach.toString())
                .a("s_camera_layout", pcData.sCameraLayout.toString())
                .a("s_disp_judge", pcData.sDispJudge.toString())
                .a("s_gauge_disp", pcData.sGaugeDisp.toString())
                .a("s_ghost_score", pcData.sGhostScore.toString())
                .a("s_gno", pcData.sGno.toString())
                .a("s_graph_score", pcData.sGraphScore.toString())
                .a("s_gtype", pcData.sGtype.toString())
                .a("s_hispeed", pcData.sHispeed.toString())
                .a("s_judge", pcData.sJudge.toString())
                .a("s_judgeAdj", pcData.sJudgeAdj.toString())
                .a("s_lane_brignt", pcData.sLaneBrignt.toString())
                .a("s_liflen", pcData.sLiflen.toString())
                .a("s_notes", pcData.sNotes.toString())
                .a("s_opstyle", pcData.sOpstyle.toString())
                .a("s_pace", pcData.sPace.toString())
                .a("s_sdlen", pcData.sSdlen.toString())
                .a("s_sdtype", pcData.sSdtype.toString())
                .a("s_sorttype", pcData.sSorttype.toString())
                .a("s_sub_gno", pcData.sSubGno.toString())
                .a("s_timing", pcData.sTiming.toString())
                .a("s_tsujigiri_disp", pcData.sTsujigiriDisp.toString())
                .a("s_tune", pcData.sTune.toString())
                .a("sach", pcData.sach.toString())
                .a("sp_opt", pcData.spOpt)
                .a("spnum", pcData.spnum.toString())
                .a("ngrade", pcData.ngrade.toString())
                .a("s_auto_adjust", pcData.sAutoAdjust.toString())
                .a("d_auto_adjust", pcData.dAutoAdjust.toString()).up()
            .e("join_shop")
                .a("join_cflg", "1")
                .a("join_id", "ea")
                .a("join_name", "\uff33\uff27")
                .a("joinflg", "1").up()
            .e("grade")
                .a("sgid", pcData.sgid.toString())
                .a("dgid", pcData.dgid.toString()).let {
                    var resp = it
                    sortedDArray.forEach { arr ->
                        resp = resp.e("g").a("__type", "u8").a("__count", "4")
                            .t(arr.take(4).joinToString(" ")).up()
                    }
                    resp
            }.up()
            .e("deller").a("deller", pcData.deller.toString()).a("rate", "0").up()
            .e("rlist").up()
            .e("ir_data").up()
            .e("secret_course_data").up()
            .e("secret")
                .e("flg1").a("__type", "s64").a("__count", "3").t("0 0 0").up()
                .e("flg2").a("__type", "s64").a("__count", "3").t("0 0 0").up()
                .e("flg3").a("__type", "s64").a("__count", "3").t("0 0 0").up()
                .e("flg4").a("__type", "s64").a("__count", "3").t("0 0 0").up()
            .up()
            .e("achievements")
                .a("last_weekly", pcData.lastWeekly.toString())
                .a("pack", pcData.pack.toString())
                .a("pack_comp", pcData.packComp.toString())
                .a("rival_crush", pcData.rivalCrush.toString())
                .a("visit_flg", pcData.visitFlg.toString())
                .a("weekly_num", pcData.weeklyNum.toString())
                .e("trophy").a("__type", "s64").a("__count", pcData.trophy.take(10).size.toString()) // original: 20
                    .t(pcData.trophy.take(10).joinToString(" ")).up()
            .up()
            .e("expert_point").up()
            .e("classic_course_data").up()
            .e("qprodata").a("__type", "u32").a("__count", "5")
                .t(settings.run { "$qproHead $qproHair $qproFace $qproHand $qproBody" }).up()
            .e("step")
                .a("dp_clear_mission_clear", pcData.dpClearMissionClear.toString())
                .a("dp_clear_mission_level", pcData.dpClearMissionLevel.toString())
                .a("dp_dj_mission_clear", pcData.dpDjMissionClear.toString())
                .a("dp_dj_mission_level", pcData.dpDjMissionLevel.toString())
                .a("dp_level", pcData.dpLevel.toString())
                .a("dp_mission_point", pcData.dpMissionPoint.toString())
                .a("dp_mplay", pcData.dpMplay.toString())
                .a("enemy_damage", pcData.enemyDamage.toString())
                .a("progress", pcData.progress.toString())
                .a("sp_clear_mission_clear", pcData.spClearMissionClear.toString())
                .a("sp_clear_mission_level", pcData.spClearMissionLevel.toString())
                .a("sp_dj_mission_clear", pcData.spDjMissionClear.toString())
                .a("sp_dj_mission_level", pcData.spDjMissionLevel.toString())
                .a("sp_level", pcData.spLevel.toString())
                .a("sp_mission_point", pcData.spMissionPoint.toString())
                .a("sp_mplay", pcData.spMplay.toString())
                .a("tips_read_list", pcData.tipsReadList.toString())
                .a("total_point", pcData.totalPoint.toString())
                .a("enemy_defeat_flg", pcData.enemyDefeatFlg.toString())
                .a("mission_clear_num", pcData.missionClearNum.toString())
                .bool("is_track_ticket", true).up()
            .up()
            .e("dj_rank").a("style", "0")
                .e("rank").a("__type", "s32").a("__count", "15").t(pcData._spRank).up()
                .e("point").a("__type", "s32").a("__count", "15").t(pcData._spPoint).up()
            .up()
            .e("dj_rank").a("style", "1")
                .e("rank").a("__type", "s32").a("__count", "15").t(pcData._dpRank).up()
                .e("point").a("__type", "s32").a("__count", "15").t(pcData._dpPoint).up()
            .up()
            .e("notes_radar").a("style", "0")
                .e("radar_score").a("__type", "s32").a("__count", "6").t(pcData._spRadar).up()
            .up()
            .e("notes_radar").a("style", "1")
                .e("radar_score").a("__type", "s32").a("__count", "6").t(pcData._dpRadar).up()
            .up()
            .e("ea_premium_course").up()
            .e("bind_eaappli").up()
            .e("leggendaria_open").up()
            .e("pay_per_use").a("item_num", "99").up()
            .e("playlist")
                .a("encrypt_playlist", "0")
                .a("index", "0")
                .a("play_style", "0").up()
            .e("spdp_rival").a("flg", "0").up()
            .e("enable_qr_reward").up()
            .e("visitor")
                .a("anum", "0")
                .a("pnum", "0")
                .a("snum", "0")
                .a("vs_flg", "0").up()
            .e("konami_stytle").a("skip_flg", "0").up()
            .e("arena_penalty").up()
            .e("defeat").a("defeat_flg", "0").up()
            .e("bemani_vote").a("music_list", "-1").up()
            .e("floor_infection3").a("music_list", "-1").up()
            .e("language_setting").a("language", "-1").up()
            .e("leggendaria_semi_open").a("flg", "0").up()
            .e("kac_entry_info")
                .e("enable_kac_deller").up()
                .e("disp_kac_mark").up()
                .e("is_kac_entry").up()
                .e("is_kac_evnet_entry").up()
                .e("kac_secret_music").up()
            .up()
            .e("skin").a("__type", "s16").a("__count", "20")
                .t(settings.run {
                    "$frame $turntable $noteBurst $menuMusic $appendSetting " +
                            "$laneCover 0 0 $noteSkin $fullComboSplash $noteBeam " +
                            "$judgeFont 0 $disableMusicpreview $pacemakerCover " +
                            "$vefxLock $effect $bombSize $disableHcnColor $firstNotePreview"
                }).up()
    }
}

@RouteModel
object Save: IIDXPCRouteHandler("save") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val refId = Database.query { db -> db.sequenceOf(EAmuseCardTable).find {
            it.cardNFCId eq (node.getAttribute("cid")
                ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST))
        } ?.refId } ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val pcData = Database.query { db -> db.sequenceOf(PCDataTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        when(node.getAttribute("cid").toIntOrNull() ?: 0) {
            0 -> pcData.spnum ++
            1 -> {
                pcData.spnum ++
                pcData.dpnum ++
            }
        }

        pcData.deller += node.firstChild("deller") ?.getAttribute("deller") ?.toIntOrNull() ?: 0

        pcData.dLiflen = node.getAttribute("d_lift").toIntOrNull() ?: 0
        pcData.sLiflen = node.getAttribute("s_lift").toIntOrNull() ?: 0

        node.firstChild("step") ?.let { step ->
            pcData.dpClearMissionClear = step.getAttribute("dp_clear_mission_clear").toIntOrNull() ?: pcData.dpClearMissionClear
            pcData.dpClearMissionLevel = step.getAttribute("dp_clear_mission_level").toIntOrNull() ?: pcData.dpClearMissionLevel
            pcData.dpDjMissionClear = step.getAttribute("dp_dj_mission_clear").toIntOrNull() ?: pcData.dpDjMissionClear
            pcData.dpDjMissionLevel = step.getAttribute("dp_dj_mission_level").toIntOrNull() ?: pcData.dpDjMissionLevel
            pcData.dpLevel = step.getAttribute("dp_level").toIntOrNull() ?: pcData.dpLevel
            pcData.dpMissionPoint = step.getAttribute("dp_mission_point").toIntOrNull() ?: pcData.dpMissionPoint
            pcData.dpMplay = step.getAttribute("dp_mplay").toIntOrNull() ?: pcData.dpMplay
            pcData.enemyDamage = step.getAttribute("enemy_damage").toIntOrNull() ?: pcData.enemyDamage
            pcData.progress = step.getAttribute("progress").toIntOrNull() ?: pcData.progress
            pcData.spClearMissionClear = step.getAttribute("sp_clear_mission_clear").toIntOrNull() ?: pcData.spClearMissionClear
            pcData.spClearMissionLevel = step.getAttribute("sp_clear_mission_level").toIntOrNull() ?: pcData.spClearMissionLevel
            pcData.spDjMissionClear = step.getAttribute("sp_dj_mission_clear").toIntOrNull() ?: pcData.spDjMissionClear
            pcData.spDjMissionLevel = step.getAttribute("sp_dj_mission_level").toIntOrNull() ?: pcData.spDjMissionLevel
            pcData.spLevel = step.getAttribute("sp_level").toIntOrNull() ?: pcData.spLevel
            pcData.spMissionPoint = step.getAttribute("sp_mission_point").toIntOrNull() ?: pcData.spMissionPoint
            pcData.spMplay = step.getAttribute("sp_mplay").toIntOrNull() ?: pcData.spMplay
            pcData.tipsReadList = step.getAttribute("tips_read_list").toIntOrNull() ?: pcData.tipsReadList
            pcData.totalPoint = step.getAttribute("total_point").toIntOrNull() ?: pcData.totalPoint
            pcData.enemyDefeatFlg = step.getAttribute("enemy_defeat_flg").toIntOrNull() ?: pcData.enemyDefeatFlg
            pcData.missionClearNum = step.getAttribute("mission_clear_num").toIntOrNull() ?: pcData.missionClearNum
        }

        node.childElements.filter { it.nodeName == "dj_rank" }.forEach { djRank ->
            val rank = djRank.childNodeValue("rank") ?: return@forEach
            val point = djRank.childNodeValue("point") ?: return@forEach
            when(djRank.getAttribute("style").toIntOrNull()) {
                0 -> {
                    pcData.spRank = rank.split(" ").map { it.toInt() }
                    pcData.spPoint = point.split(" ").map { it.toInt() }
                }
                1 -> {
                    pcData.dpRank = rank.split(" ").map { it.toInt() }
                    pcData.dpPoint = point.split(" ").map { it.toInt() }
                }
            }
        }

        node.childElements.filter { it.nodeName == "notes_radar" }.forEach { notesRadar ->
            val radarScore = notesRadar.childNodeValue("radar_score") ?: return@forEach
            when(notesRadar.getAttribute("style").toIntOrNull()) {
                0 -> pcData.spRadar = radarScore.split(" ").map { it.toInt() }
                1 -> pcData.dpRadar = radarScore.split(" ").map { it.toInt() }
            }
        }

        pcData.dAutoScrach = node.getAttribute("d_auto_scrach").toIntOrNull() ?: pcData.dAutoScrach
        pcData.dCameraLayout = node.getAttribute("d_camera_layout").toIntOrNull() ?: pcData.dCameraLayout
        pcData.dDispJudge = node.getAttribute("d_disp_judge").toIntOrNull() ?: pcData.dDispJudge
        pcData.dGaugeDisp = node.getAttribute("d_gauge_disp").toIntOrNull() ?: pcData.dGaugeDisp
        pcData.dGhostScore = node.getAttribute("d_ghost_score").toIntOrNull() ?: pcData.dGhostScore
        pcData.dGno = node.getAttribute("d_gno").toIntOrNull() ?: pcData.dGno
        pcData.dGraphScore = node.getAttribute("d_graph_score").toIntOrNull() ?: pcData.dGraphScore
        pcData.dGtype = node.getAttribute("d_gtype").toIntOrNull() ?: pcData.dGtype
        pcData.dHispeed = node.getAttribute("d_hispeed").toIntOrNull() ?: pcData.dHispeed
        pcData.dJudge = node.getAttribute("d_judge").toIntOrNull() ?: pcData.dJudge
        pcData.dJudgeAdj = node.getAttribute("d_judgeAdj").toIntOrNull() ?: pcData.dJudgeAdj
        pcData.dLaneBrignt = node.getAttribute("d_lane_brignt").toIntOrNull() ?: pcData.dLaneBrignt
        pcData.dNotes = node.getAttribute("d_notes").toIntOrNull() ?: pcData.dNotes
        pcData.dOpstyle = node.getAttribute("d_opstyle").toIntOrNull() ?: pcData.dOpstyle
        pcData.dPace = node.getAttribute("d_pace").toIntOrNull() ?: pcData.dPace
        pcData.dSdlen = node.getAttribute("d_sdlen").toIntOrNull() ?: pcData.dSdlen
        pcData.dSdtype = node.getAttribute("d_sdtype").toIntOrNull() ?: pcData.dSdtype
        pcData.dSorttype = node.getAttribute("d_sorttype").toIntOrNull() ?: pcData.dSorttype
        pcData.dTiming = node.getAttribute("d_timing").toIntOrNull() ?: pcData.dTiming
        pcData.dTsujigiriDisp = node.getAttribute("d_tsujigiri_disp").toIntOrNull() ?: pcData.dTsujigiriDisp
        pcData.dach = node.getAttribute("d_achi").toIntOrNull() ?: pcData.dach
        pcData.dSubGno = node.getAttribute("d_sub_gno").toIntOrNull() ?: pcData.dSubGno
        pcData.gpos = node.getAttribute("gpos").toIntOrNull() ?: pcData.gpos
        pcData.mode = node.getAttribute("mode").toIntOrNull() ?: pcData.mode
        pcData.pmode = node.getAttribute("pmode").toIntOrNull() ?: pcData.pmode
        pcData.rtype = node.getAttribute("rtype").toIntOrNull() ?: pcData.rtype
        pcData.ngrade = node.getAttribute("ngrade").toIntOrNull() ?: pcData.ngrade
        pcData.sAutoScrach = node.getAttribute("s_auto_scrach").toIntOrNull() ?: pcData.sAutoScrach
        pcData.sCameraLayout = node.getAttribute("s_camera_layout").toIntOrNull() ?: pcData.sCameraLayout
        pcData.sDispJudge = node.getAttribute("s_disp_judge").toIntOrNull() ?: pcData.sDispJudge
        pcData.sGaugeDisp = node.getAttribute("s_gauge_disp").toIntOrNull() ?: pcData.sGaugeDisp
        pcData.sGhostScore = node.getAttribute("s_ghost_score").toIntOrNull() ?: pcData.sGhostScore
        pcData.sGno = node.getAttribute("s_gno").toIntOrNull() ?: pcData.sGno
        pcData.sGraphScore = node.getAttribute("s_graph_score").toIntOrNull() ?: pcData.sGraphScore
        pcData.sGtype = node.getAttribute("s_gtype").toIntOrNull() ?: pcData.sGtype
        pcData.sHispeed = node.getAttribute("s_hispeed").toIntOrNull() ?: pcData.sHispeed
        pcData.sJudge = node.getAttribute("s_judge").toIntOrNull() ?: pcData.sJudge
        pcData.sJudgeAdj = node.getAttribute("s_judgeAdj").toIntOrNull() ?: pcData.sJudgeAdj
        pcData.sLaneBrignt = node.getAttribute("s_lane_brignt").toIntOrNull() ?: pcData.sLaneBrignt
        pcData.sNotes = node.getAttribute("s_notes").toIntOrNull() ?: pcData.sNotes
        pcData.sOpstyle = node.getAttribute("s_opstyle").toIntOrNull() ?: pcData.sOpstyle
        pcData.sPace = node.getAttribute("s_pace").toIntOrNull() ?: pcData.sPace
        pcData.sSdlen = node.getAttribute("s_sdlen").toIntOrNull() ?: pcData.sSdlen
        pcData.sSdtype = node.getAttribute("s_sdtype").toIntOrNull() ?: pcData.sSdtype
        pcData.sSorttype = node.getAttribute("s_sorttype").toIntOrNull() ?: pcData.sSorttype
        pcData.sTiming = node.getAttribute("s_timing").toIntOrNull() ?: pcData.sTiming
        pcData.sTsujigiriDisp = node.getAttribute("s_tsujigiri_disp").toIntOrNull() ?: pcData.sTsujigiriDisp
        pcData.sach = node.getAttribute("s_achi").toIntOrNull() ?: pcData.sach
        pcData.sSubGno = node.getAttribute("s_sub_gno").toIntOrNull() ?: pcData.sSubGno
        pcData.sAutoAdjust = node.getAttribute("s_auto_adjust").toIntOrNull() ?: pcData.sAutoAdjust
        pcData.dpOpt = node.getAttribute("dp_opt") ?: pcData.dpOpt
        pcData.dpOpt2 = node.getAttribute("dp_opt2") ?: pcData.dpOpt2
        pcData.spOpt = node.getAttribute("sp_opt") ?: pcData.spOpt

        node.firstChild("achievements") ?.let { achievements ->
            pcData.lastWeekly = achievements.getAttribute("last_weekly").toIntOrNull() ?: pcData.lastWeekly
            pcData.pack = achievements.getAttribute("pack").toIntOrNull() ?: pcData.pack
            pcData.packComp = achievements.getAttribute("pack_comp").toIntOrNull() ?: pcData.packComp
            pcData.rivalCrush = achievements.getAttribute("rival_crush").toIntOrNull() ?: pcData.rivalCrush
            pcData.visitFlg = achievements.getAttribute("visit_flg").toIntOrNull() ?: pcData.visitFlg
            pcData.weeklyNum = achievements.getAttribute("weekly_num").toIntOrNull() ?: pcData.weeklyNum

            achievements.childNodeValue("trophy") ?.let { trophy ->
                pcData.trophy = trophy.split(" ").take(10)
            }
        }

        pcData.flushChanges()

        return createResponseNode()
    }
}