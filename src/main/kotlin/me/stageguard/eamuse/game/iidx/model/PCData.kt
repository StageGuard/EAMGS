@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.iidx.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object PCDataTable : AddableTable<PCData>("iidx_pc_data") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val deller = int("deller").bindTo { it.deller }
    val sgid = int("sgid").bindTo { it.sgid }
    val dgid = int("dgid").bindTo { it.dgid }
    val _trophy = varchar("trophy").bindTo { it._trophy }
    val _spRank = varchar("sprank").bindTo { it._spRank }
    val _spPoint = varchar("sppoint").bindTo { it._spPoint }
    val _dpRank = varchar("dprank").bindTo { it._dpRank }
    val _dpPoint = varchar("dppoint").bindTo { it._dpPoint }
    val _spRadar = varchar("spradar").bindTo { it._spRadar }
    val _dpRadar = varchar("dpradar").bindTo { it._dpRadar }
    val dpClearMissionClear = int("dp_clear_mission_clear").bindTo { it.dpClearMissionClear }
    val dpClearMissionLevel = int("dp_clear_mission_level").bindTo { it.dpClearMissionLevel }
    val dpDjMissionClear = int("dp_dj_mission_clear").bindTo { it.dpDjMissionClear }
    val dpDjMissionLevel = int("dp_dj_mission_level").bindTo { it.dpDjMissionLevel }
    val dpLevel = int("dp_level").bindTo { it.dpLevel }
    val dpMissionPoint = int("dp_mission_point").bindTo { it.dpMissionPoint }
    val dpMplay = int("dp_mplay").bindTo { it.dpMplay }
    val enemyDamage = int("enemy_damage").bindTo { it.enemyDamage }
    val progress = int("progress").bindTo { it.progress }
    val spClearMissionClear = int("sp_clear_mission_clear").bindTo { it.spClearMissionClear }
    val spClearMissionLevel = int("sp_clear_mission_level").bindTo { it.spClearMissionLevel }
    val spDjMissionClear = int("sp_dj_mission_clear").bindTo { it.spDjMissionClear }
    val spDjMissionLevel = int("sp_dj_mission_level").bindTo { it.spDjMissionLevel }
    val spLevel = int("sp_level").bindTo { it.spLevel }
    val spMissionPoint = int("sp_mission_point").bindTo { it.spMissionPoint }
    val spMplay = int("sp_mplay").bindTo { it.spMplay }
    val tipsReadList = int("tips_read_list").bindTo { it.tipsReadList }
    val totalPoint = int("total_point").bindTo { it.totalPoint }
    val enemyDefeatFlg = int("enemy_defeat_flg").bindTo { it.enemyDefeatFlg }
    val missionClearNum = int("mission_clear_num").bindTo { it.missionClearNum }
    val dpnum = int("dpnum").bindTo { it.dpnum }
    val dAutoScrach = int("d_auto_scrach").bindTo { it.dAutoScrach }
    val dCameraLayout = int("d_camera_layout").bindTo { it.dCameraLayout }
    val dDispJudge = int("d_disp_judge").bindTo { it.dDispJudge }
    val dGaugeDisp = int("d_gauge_disp").bindTo { it.dGaugeDisp }
    val dGhostScore = int("d_ghost_score").bindTo { it.dGhostScore }
    val dGno = int("d_gno").bindTo { it.dGno }
    val dGraphScore = int("d_graph_score").bindTo { it.dGraphScore }
    val dGtype = int("d_gtype").bindTo { it.dGtype }
    val dHispeed = int("d_hispeed").bindTo { it.dHispeed }
    val dJudge = int("d_judge").bindTo { it.dJudge }
    val dJudgeAdj = int("d_judgeAdj").bindTo { it.dJudgeAdj }
    val dLaneBrignt = int("d_lane_brignt").bindTo { it.dLaneBrignt }
    val dLiflen = int("d_liflen").bindTo { it.dLiflen }
    val dNotes = int("d_notes").bindTo { it.dNotes }
    val dOpstyle = int("d_opstyle").bindTo { it.dOpstyle }
    val dPace = int("d_pace").bindTo { it.dPace }
    val dSdlen = int("d_sdlen").bindTo { it.dSdlen }
    val dSdtype = int("d_sdtype").bindTo { it.dSdtype }
    val dSorttype = int("d_sorttype").bindTo { it.dSorttype }
    val dTiming = int("d_timing").bindTo { it.dTiming }
    val dTsujigiriDisp = int("d_tsujigiri_disp").bindTo { it.dTsujigiriDisp }
    val dach = int("dach").bindTo { it.dach }
    val dSubGno = int("d_sub_gno").bindTo { it.dSubGno }
    val dTune = int("d_tune").bindTo { it.dTune }
    val gpos = int("gpos").bindTo { it.gpos }
    val mode = int("mode").bindTo { it.mode }
    val pmode = int("pmode").bindTo { it.pmode }
    val rtype = int("rtype").bindTo { it.rtype }
    val ngrade = int("ngrade").bindTo { it.ngrade }
    val spnum = int("spnum").bindTo { it.spnum }
    val sAutoScrach = int("s_auto_scrach").bindTo { it.sAutoScrach }
    val sCameraLayout = int("s_camera_layout").bindTo { it.sCameraLayout }
    val sDispJudge = int("s_disp_judge").bindTo { it.sDispJudge }
    val sGaugeDisp = int("s_gauge_disp").bindTo { it.sGaugeDisp }
    val sGhostScore = int("s_ghost_score").bindTo { it.sGhostScore }
    val sGno = int("s_gno").bindTo { it.sGno }
    val sGraphScore = int("s_graph_score").bindTo { it.sGraphScore }
    val sGtype = int("s_gtype").bindTo { it.sGtype }
    val sHispeed = int("s_hispeed").bindTo { it.sHispeed }
    val sJudge = int("s_judge").bindTo { it.sJudge }
    val sJudgeAdj = int("s_judgeAdj").bindTo { it.sJudgeAdj }
    val sLaneBrignt = int("s_lane_brignt").bindTo { it.sLaneBrignt }
    val sLiflen = int("s_liflen").bindTo { it.sLiflen }
    val sNotes = int("s_notes").bindTo { it.sNotes }
    val sOpstyle = int("s_opstyle").bindTo { it.sOpstyle }
    val sPace = int("s_pace").bindTo { it.sPace }
    val sSdlen = int("s_sdlen").bindTo { it.sSdlen }
    val sSdtype = int("s_sdtype").bindTo { it.sSdtype }
    val sSorttype = int("s_sorttype").bindTo { it.sSorttype }
    val sTiming = int("s_timing").bindTo { it.sTiming }
    val sTsujigiriDisp = int("s_tsujigiri_disp").bindTo { it.sTsujigiriDisp }
    val sach = int("sach").bindTo { it.sach }
    val sSubGno = int("s_sub_gno").bindTo { it.sSubGno }
    val sTune = int("s_tune").bindTo { it.sTune }
    val sAutoAdjust = int("s_auto_adjust").bindTo { it.sAutoAdjust }
    val dAutoAdjust = int("d_auto_adjust").bindTo { it.dAutoAdjust }
    val dpOpt = varchar("dp_opt").bindTo { it.dpOpt }
    val dpOpt2 = varchar("dp_opt2").bindTo { it.dpOpt2 }
    val spOpt = varchar("sp_opt").bindTo { it.spOpt }
    val lastWeekly = int("last_weekly").bindTo { it.lastWeekly }
    val pack = int("pack").bindTo { it.pack }
    val packComp = int("pack_comp").bindTo { it.packComp }
    val rivalCrush = int("rival_crush").bindTo { it.rivalCrush }
    val visitFlg = int("visit_flg").bindTo { it.visitFlg }
    val weeklyNum = int("weekly_num").bindTo { it.weeklyNum }


    override fun <T : AssignmentsBuilder> T.mapElement(element: PCData) {
        set(refId, element.refId)
        set(deller, element.deller)
        set(sgid, element.sgid)
        set(dgid, element.dgid)
        set(_trophy, element._trophy)
        set(_spRank, element._spRank)
        set(_spPoint, element._spPoint)
        set(_dpRank, element._dpRank)
        set(_dpPoint, element._dpPoint)
        set(_spRadar, element._spRadar)
        set(_dpRadar, element._dpRadar)
        set(dpClearMissionClear, element.dpClearMissionClear)
        set(dpClearMissionLevel, element.dpClearMissionLevel)
        set(dpDjMissionClear, element.dpDjMissionClear)
        set(dpDjMissionLevel, element.dpDjMissionLevel)
        set(dpLevel, element.dpLevel)
        set(dpMissionPoint, element.dpMissionPoint)
        set(dpMplay, element.dpMplay)
        set(enemyDamage, element.enemyDamage)
        set(progress, element.progress)
        set(spClearMissionClear, element.spClearMissionClear)
        set(spClearMissionLevel, element.spClearMissionLevel)
        set(spDjMissionClear, element.spDjMissionClear)
        set(spDjMissionLevel, element.spDjMissionLevel)
        set(spLevel, element.spLevel)
        set(spMissionPoint, element.spMissionPoint)
        set(spMplay, element.spMplay)
        set(tipsReadList, element.tipsReadList)
        set(totalPoint, element.totalPoint)
        set(enemyDefeatFlg, element.enemyDefeatFlg)
        set(missionClearNum, element.missionClearNum)
        set(dpnum, element.dpnum)
        set(dAutoScrach, element.dAutoScrach)
        set(dCameraLayout, element.dCameraLayout)
        set(dDispJudge, element.dDispJudge)
        set(dGaugeDisp, element.dGaugeDisp)
        set(dGhostScore, element.dGhostScore)
        set(dGno, element.dGno)
        set(dGraphScore, element.dGraphScore)
        set(dGtype, element.dGtype)
        set(dHispeed, element.dHispeed)
        set(dJudge, element.dJudge)
        set(dJudgeAdj, element.dJudgeAdj)
        set(dLaneBrignt, element.dLaneBrignt)
        set(dLiflen, element.dLiflen)
        set(dNotes, element.dNotes)
        set(dOpstyle, element.dOpstyle)
        set(dPace, element.dPace)
        set(dSdlen, element.dSdlen)
        set(dSdtype, element.dSdtype)
        set(dSorttype, element.dSorttype)
        set(dTiming, element.dTiming)
        set(dTsujigiriDisp, element.dTsujigiriDisp)
        set(dach, element.dach)
        set(dSubGno, element.dSubGno)
        set(dTune, element.dTune)
        set(gpos, element.gpos)
        set(mode, element.mode)
        set(pmode, element.pmode)
        set(rtype, element.rtype)
        set(ngrade, element.ngrade)
        set(spnum, element.spnum)
        set(sAutoScrach, element.sAutoScrach)
        set(sCameraLayout, element.sCameraLayout)
        set(sDispJudge, element.sDispJudge)
        set(sGaugeDisp, element.sGaugeDisp)
        set(sGhostScore, element.sGhostScore)
        set(sGno, element.sGno)
        set(sGraphScore, element.sGraphScore)
        set(sGtype, element.sGtype)
        set(sHispeed, element.sHispeed)
        set(sJudge, element.sJudge)
        set(sJudgeAdj, element.sJudgeAdj)
        set(sLaneBrignt, element.sLaneBrignt)
        set(sLiflen, element.sLiflen)
        set(sNotes, element.sNotes)
        set(sOpstyle, element.sOpstyle)
        set(sPace, element.sPace)
        set(sSdlen, element.sSdlen)
        set(sSdtype, element.sSdtype)
        set(sSorttype, element.sSorttype)
        set(sTiming, element.sTiming)
        set(sTsujigiriDisp, element.sTsujigiriDisp)
        set(sach, element.sach)
        set(sSubGno, element.sSubGno)
        set(sTune, element.sTune)
        set(sAutoAdjust, element.sAutoAdjust)
        set(dAutoAdjust, element.dAutoAdjust)
        set(dpOpt, element.dpOpt)
        set(dpOpt2, element.dpOpt2)
        set(spOpt, element.spOpt)
        set(lastWeekly, element.lastWeekly)
        set(pack, element.pack)
        set(packComp, element.packComp)
        set(rivalCrush, element.rivalCrush)
        set(visitFlg, element.visitFlg)
        set(weeklyNum, element.weeklyNum)
    }
    override val createStatement: String = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `deller` int NOT NULL, 
        `sgid` int NOT NULL, 
        `dgid` int NOT NULL, 
        `trophy` varchar(1024) NOT NULL, 
        `sprank` varchar(1024) NOT NULL, 
        `sppoint` varchar(1024) NOT NULL, 
        `dprank` varchar(1024) NOT NULL, 
        `dppoint` varchar(1024) NOT NULL, 
        `spradar` varchar(1024) NOT NULL, 
        `dpradar` varchar(1024) NOT NULL, 
        `dp_clear_mission_clear` int NOT NULL, 
        `dp_clear_mission_level` int NOT NULL, 
        `dp_dj_mission_clear` int NOT NULL, 
        `dp_dj_mission_level` int NOT NULL, 
        `dp_level` int NOT NULL, 
        `dp_mission_point` int NOT NULL, 
        `dp_mplay` int NOT NULL, 
        `enemy_damage` int NOT NULL, 
        `progress` int NOT NULL, 
        `sp_clear_mission_clear` int NOT NULL, 
        `sp_clear_mission_level` int NOT NULL, 
        `sp_dj_mission_clear` int NOT NULL, 
        `sp_dj_mission_level` int NOT NULL, 
        `sp_level` int NOT NULL, 
        `sp_mission_point` int NOT NULL, 
        `sp_mplay` int NOT NULL, 
        `tips_read_list` int NOT NULL, 
        `total_point` int NOT NULL, 
        `enemy_defeat_flg` int NOT NULL, 
        `mission_clear_num` int NOT NULL, 
        `dpnum` int NOT NULL, 
        `d_auto_scrach` int NOT NULL, 
        `d_camera_layout` int NOT NULL, 
        `d_disp_judge` int NOT NULL, 
        `d_gauge_disp` int NOT NULL, 
        `d_ghost_score` int NOT NULL, 
        `d_gno` int NOT NULL, 
        `d_graph_score` int NOT NULL, 
        `d_gtype` int NOT NULL, 
        `d_hispeed` int NOT NULL, 
        `d_judge` int NOT NULL, 
        `d_judgeAdj` int NOT NULL, 
        `d_lane_brignt` int NOT NULL, 
        `d_liflen` int NOT NULL, 
        `d_notes` int NOT NULL, 
        `d_opstyle` int NOT NULL, 
        `d_pace` int NOT NULL, 
        `d_sdlen` int NOT NULL, 
        `d_sdtype` int NOT NULL, 
        `d_sorttype` int NOT NULL, 
        `d_timing` int NOT NULL, 
        `d_tsujigiri_disp` int NOT NULL, 
        `dach` int NOT NULL, 
        `d_sub_gno` int NOT NULL, 
        `d_tune` int NOT NULL, 
        `gpos` int NOT NULL, 
        `mode` int NOT NULL, 
        `pmode` int NOT NULL, 
        `rtype` int NOT NULL, 
        `ngrade` int NOT NULL, 
        `spnum` int NOT NULL, 
        `s_auto_scrach` int NOT NULL, 
        `s_camera_layout` int NOT NULL, 
        `s_disp_judge` int NOT NULL, 
        `s_gauge_disp` int NOT NULL, 
        `s_ghost_score` int NOT NULL, 
        `s_gno` int NOT NULL, 
        `s_graph_score` int NOT NULL, 
        `s_gtype` int NOT NULL, 
        `s_hispeed` int NOT NULL, 
        `s_judge` int NOT NULL, 
        `s_judgeAdj` int NOT NULL, 
        `s_lane_brignt` int NOT NULL, 
        `s_liflen` int NOT NULL, 
        `s_notes` int NOT NULL, 
        `s_opstyle` int NOT NULL, 
        `s_pace` int NOT NULL, 
        `s_sdlen` int NOT NULL, 
        `s_sdtype` int NOT NULL, 
        `s_sorttype` int NOT NULL, 
        `s_timing` int NOT NULL, 
        `s_tsujigiri_disp` int NOT NULL, 
        `sach` int NOT NULL, 
        `s_sub_gno` int NOT NULL, 
        `s_tune` int NOT NULL, 
        `s_auto_adjust` int NOT NULL, 
        `d_auto_adjust` int NOT NULL, 
        `dp_opt` varchar(16) NOT NULL, 
        `dp_opt2` varchar(16) NOT NULL, 
        `sp_opt` varchar(16) NOT NULL, 
        `last_weekly` int NOT NULL,
        `pack` int NOT NULL,
        `pack_comp` int NOT NULL,
        `rival_crush` int NOT NULL,
        `visit_flg` int NOT NULL,
        `weekly_num` int NOT NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface PCData : Entity<PCData> {
    companion object : Entity.Factory<PCData>()

    var __id: Int
    var refId: String

    var deller: Int
    var sgid: Int
    var dgid: Int
    var _trophy: String // string array
    var _spRank: String // int array
    var _spPoint: String // int array
    var _dpRank: String // int array
    var _dpPoint: String // int array
    var _spRadar: String // int array
    var _dpRadar: String // int array
    var dpClearMissionClear: Int
    var dpClearMissionLevel: Int
    var dpDjMissionClear: Int
    var dpDjMissionLevel: Int
    var dpLevel: Int
    var dpMissionPoint: Int
    var dpMplay: Int
    var enemyDamage: Int
    var progress: Int
    var spClearMissionClear: Int
    var spClearMissionLevel: Int
    var spDjMissionClear: Int
    var spDjMissionLevel: Int
    var spLevel: Int
    var spMissionPoint: Int
    var spMplay: Int
    var tipsReadList: Int
    var totalPoint: Int
    var enemyDefeatFlg: Int
    var missionClearNum: Int
    var dpnum: Int
    var dAutoScrach: Int
    var dCameraLayout: Int
    var dDispJudge: Int
    var dGaugeDisp: Int
    var dGhostScore: Int
    var dGno: Int
    var dGraphScore: Int
    var dGtype: Int
    var dHispeed: Int
    var dJudge: Int
    var dJudgeAdj: Int
    var dLaneBrignt: Int
    var dLiflen: Int
    var dNotes: Int
    var dOpstyle: Int
    var dPace: Int
    var dSdlen: Int
    var dSdtype: Int
    var dSorttype: Int
    var dTiming: Int
    var dTsujigiriDisp: Int
    var dach: Int
    var dSubGno: Int
    var dTune: Int
    var gpos: Int
    var mode: Int
    var pmode: Int
    var rtype: Int
    var ngrade: Int
    var spnum: Int
    var sAutoScrach: Int
    var sCameraLayout: Int
    var sDispJudge: Int
    var sGaugeDisp: Int
    var sGhostScore: Int
    var sGno: Int
    var sGraphScore: Int
    var sGtype: Int
    var sHispeed: Int
    var sJudge: Int
    var sJudgeAdj: Int
    var sLaneBrignt: Int
    var sLiflen: Int
    var sNotes: Int
    var sOpstyle: Int
    var sPace: Int
    var sSdlen: Int
    var sSdtype: Int
    var sSorttype: Int
    var sTiming: Int
    var sTsujigiriDisp: Int
    var sach: Int
    var sSubGno: Int
    var sTune: Int
    var sAutoAdjust: Int
    var dAutoAdjust: Int
    var dpOpt: String
    var dpOpt2: String
    var spOpt: String
    var lastWeekly: Int
    var pack: Int
    var packComp: Int
    var rivalCrush: Int
    var visitFlg: Int
    var weeklyNum: Int
}

var PCData.trophy: List<String>
    get() { return _trophy.split(" ") }
    set(value) { _trophy = value.joinToString(" ") }
var PCData.spRank: List<Int>
    get() { return _spRank.split(" ").map { it.toInt() } }
    set(value) { _spRank = value.joinToString(" ") }
var PCData.spPoint: List<Int>
    get() { return _spPoint.split(" ").map { it.toInt() } }
    set(value) { _spPoint = value.joinToString(" ") }
var PCData.dpRank: List<Int>
    get() { return _dpRank.split(" ").map { it.toInt() } }
    set(value) { _dpRank = value.joinToString(" ") }
var PCData.dpPoint: List<Int>
    get() { return _dpPoint.split(" ").map { it.toInt() } }
    set(value) { _dpPoint = value.joinToString(" ") }
var PCData.spRadar: List<Int>
    get() { return _spRadar.split(" ").map { it.toInt() } }
    set(value) { _spRadar = value.joinToString(" ") }
var PCData.dpRadar: List<Int>
    get() { return _dpRadar.split(" ").map { it.toInt() } }
    set(value) { _dpRadar = value.joinToString(" ") }