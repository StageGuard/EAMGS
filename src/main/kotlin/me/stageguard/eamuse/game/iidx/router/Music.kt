@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.iidx.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.buttongames.butterflycore.xml.kbinxml.firstChild
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.iidx.IIDXMusicRouteHandler
import me.stageguard.eamuse.game.iidx.LDJ20211013
import me.stageguard.eamuse.game.iidx.LOGGER
import me.stageguard.eamuse.game.iidx.model.*
import me.stageguard.eamuse.game.sdvx6.router.Load
import me.stageguard.eamuse.game.sdvx6.util.getResourceOrExport
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteModel
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.notEq
import org.ktorm.entity.*
import org.w3c.dom.Element
import java.nio.charset.Charset
import java.util.Base64
import kotlin.math.max
import kotlin.math.min

@RouteModel(LDJ20211013)
object MusicRegister : IIDXMusicRouteHandler("reg") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val refId = Database.query { db -> db.sequenceOf(UserProfileTable).find {
            it.iidxId eq (node.getAttribute("iidxid").toIntOrNull()
                ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST))
        } ?.refId } ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val pcData = Database.query { db -> db.sequenceOf(PCDataTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        // 0 是 SP 初学者, 4 是 SP 传奇, 5 是 DP 初学者（无适用歌曲）, 9 是 DP 传奇
        val clid = node.getAttribute("clid").toIntOrNull()
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val playLog = node.firstChild("music_play_log")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val musicId = playLog.getAttribute("music_id").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        // paly mode: sp: 0, dp: 1
        val style = playLog.getAttribute("play_style").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        // rank
        val rank = playLog.getAttribute("note_id").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        // clear state, 0: noplay, 1: failed, 2: assisted, 3: easy, 4: normal, 5: hard, 6: exhard, 7: fc
        val clearFlag = playLog.getAttribute("clear_flg").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val exScore = playLog.getAttribute("ex_score").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        // note count
        val missCount = playLog.getAttribute("miss_num").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val greatCount = playLog.getAttribute("great_num").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val pGreatCount = playLog.getAttribute("pgreat_num").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        // 是否中途坠落（中途坠落：0 完结：1）
        val isSuddenDeath = playLog.childNodeValue("is_sudden_death") ?.toIntOrNull() == 0
        val ghost = node.childNodeValue("ghost") ?.let { Base64.getEncoder().encodeToString(it.toByteArray()) }
            ?: throw throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        var mArray = mutableListOf(-1, musicId, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1)
        var dbFlg = false

        val musicScore = Database.query { db -> db.sequenceOf(ScoreTable).find {
            it.refId eq refId and (it.musicId eq musicId)
        } }
        val scoreDetail = Database.query { db -> db.sequenceOf(ScoreDetailTable).find {
            it.refId eq refId and (it.musicId eq musicId) and (it.clid eq clid)
        } }

        if (musicScore != null) {
            if (style == 0 && musicScore.spPlayed) {
                mArray = musicScore.spmArray.toMutableList()
                dbFlg = true
                if (scoreDetail == null && (musicScore.spmArray[rank + 2] != 0 || musicScore.spmArray[rank + 7] != 0)) {
                    ScoreDetailTable.insert(ScoreDetail {
                        this.refId = refId
                        this.musicId = musicId
                        this.clid = clid
                        this.score = musicScore.spmArray[rank + 7]
                        this.clflg = musicScore.spmArray[rank + 2]
                        this.miss = musicScore.spmArray[rank + 12]
                        this.great = greatCount
                        this.pGreat = pGreatCount
                        this.time = System.currentTimeMillis()
                    })
                }
            } else if (style == 1 && musicScore.dpPlayed) {
                mArray = musicScore.dpmArray.toMutableList()
                dbFlg = true
                if (scoreDetail == null && (musicScore.dpmArray[rank + 2] != 0 || musicScore.dpmArray[rank + 7] != 0)) {
                    ScoreDetailTable.insert(ScoreDetail {
                        this.refId = refId
                        this.musicId = musicId
                        this.clid = clid
                        this.score = musicScore.dpmArray[rank + 7]
                        this.clflg = musicScore.dpmArray[rank + 2]
                        this.miss = musicScore.dpmArray[rank + 12]
                        this.great = greatCount
                        this.pGreat = pGreatCount
                        this.time = System.currentTimeMillis()
                    })
                }
            }
        }

        mArray[rank + 2] = max(clearFlag, mArray[rank + 2])
        if (isSuddenDeath) {
            if (mArray[rank + 12] == -1) {
                mArray[rank + 12] = missCount
            } else {
                mArray[rank + 12] = min(mArray[rank + 12], missCount)
            }
        }

        val update = (mArray[rank + 7] < exScore || !dbFlg).also {
            mArray[rank + 7] = max(exScore, mArray[rank + 7])
        }
        if (musicScore == null) {
            ScoreTable.insert(Score {
                this.refId = refId
                this.musicId = musicId
                this.spPlayed = style == 0
                this.dpPlayed = style == 1
                spmArray = if (style == 0) mArray else listOf(-1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1)
                dpmArray = if (style == 1) mArray else listOf(-1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1)
                if (update) when(clid) {
                    0 -> { this.clid0 = ghost; this.clid1 = ""; this.clid2 = ""; }
                    1 -> { this.clid1 = ghost; this.clid0 = ""; this.clid2 = ""; }
                    2 -> { this.clid2 = ghost; this.clid0 = ""; this.clid1 = ""; }
                    else -> {
                        LOGGER.warn("unknown clid: $clid")
                        this.clid2 = ""; this.clid0 = ""; this.clid1 = ""
                    }
                }
            })
        } else {
            if (style == 0) {
                musicScore.spmArray = mArray
                musicScore.spPlayed = true
            }
            if (style == 1) {
                musicScore.dpmArray = mArray
                musicScore.dpPlayed = true
            }
            if (update) when(clid) {
                0 -> musicScore.clid0 = ghost
                1 -> musicScore.clid1 = ghost
                2 -> musicScore.clid2 = ghost
                else -> LOGGER.warn("unknown clid: $clid")
            }

            musicScore.flushChanges()
        }

        ScoreDetailTable.insert(ScoreDetail {
            this.refId = refId
            this.musicId = musicId
            this.clid = clid
            this.score = exScore
            this.clflg = clearFlag
            this.miss = missCount
            this.great = greatCount
            this.pGreat = pGreatCount
            this.time = System.currentTimeMillis()
        })

        var response = createResponseNode()
            .e("shopdata").a("rank", "1").up()
            .e("LDJ")
                .a("clid", "1")
                .a("crate", "1000")
                .a("frate", "0")
                .a("mid", musicId.toString())
            .up()
            .e("ranklist")

        // shop ranking by default
        val sortedScores = (Database.query { db -> db.sequenceOf(ScoreTable).filter {
            it.musicId eq musicId and (if (style == 0) it.spPlayed else it.dpPlayed)
        }.toList() } ?: listOf()).sortedWith { a, b ->
            if (style == 0) {
                b.spmArray[rank + 7] - a.spmArray[rank + 7]
            } else {
                b.dpmArray[rank + 7] - a.dpmArray[rank + 7]
            }
        }

        val currentRank = sortedScores.indexOfFirst { it.refId == refId }

        sortedScores.forEachIndexed { index, score ->
            val scoreNum = score.run { if (style == 0) spmArray[rank + 7] else dpmArray[rank + 7] }
            val clFlag = score.run { if (style == 0) spmArray[rank + 2] else dpmArray[rank + 2] }

            response = response.e("data")
                .a("body", "0")
                .a("clflg", clFlag.toString())
                .a("dgrade", pcData.dgid.toString())
                .a("face", "0")
                .a("hair", "0")
                .a("hand", "0")
                .a("head", "0")

            if (index == currentRank) {
                response = response
                    .a("iidx_id", profile.iidxId.toString())
                    .a("name", profile.name)
                    .a("pid", profile.pid.toString())
                    .a("update", if(update) "1" else "0")
                    .a("myFlg", "1")
            } else if(scoreNum != 0 || clFlag != 0) {
                val rivalProfile = Database.query { db ->
                    db.sequenceOf(UserProfileTable).find { it.refId eq score.refId }
                } ?: return@forEachIndexed
                response = response
                    .a("iidx_id", rivalProfile.iidxId.toString())
                    .a("name", rivalProfile.name)
                    .a("pid", rivalProfile.pid.toString())
                    .a("update", "0")
                    .a("myFlg", "0")
            } else return@forEachIndexed

            response = response
                .a("opname", "\uff33\uff27")
                .a("rnum", (index + 1).toString())
                .a("score", scoreNum.toString())
                .a("sgrade", pcData.sgid.toString())
                .up()
        }
        response = response.up()

        return response
    }
}

@RouteModel(LDJ20211013)
object GetRank : IIDXMusicRouteHandler("getrank") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val clType = node.getAttribute("cltype").toIntOrNull()
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val refId = Database.query { db -> db.sequenceOf(UserProfileTable).find {
            it.iidxId eq (node.getAttribute("iidxid").toIntOrNull()
                ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST))
        } ?.refId } ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val spmArray = (Database.query { db ->
            db.sequenceOf(ScoreTable).filter { it.refId eq refId and it.spPlayed }.map { it.spmArray }
        } ?: listOf()).sortedWith { a, b -> a[1] - b[1] }
        val dpmArray = (Database.query { db ->
            db.sequenceOf(ScoreTable).filter { it.refId eq refId and it.dpPlayed }.map { it.dpmArray }
        } ?: listOf()).sortedWith { a, b -> a[1] - b[1] }

        var response = createResponseNode()
            .e("style").a("type", clType.toString()).up()
            .e("best").a("__type", "u16").a("__count", "20").a("rno", "-1")
                .t("65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535 65535")
            .up()

        if (clType == 0) {
            spmArray.forEach { spm ->
                response = response.e("m").a("__type", "s16").a("__count", spm.size.toString())
                    .t(spm.joinToString(" ")).up()
            }
        } else if (clType == 1) {
            dpmArray.forEach { dpm ->
                response = response.e("m").a("__type", "s16").a("__count", dpm.size.toString())
                    .t(dpm.joinToString(" ")).up()
            }
        }
        response = response.up()

        return response
    }
}

@RouteModel(LDJ20211013)
object APPoint : IIDXMusicRouteHandler("appoint") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val refId = Database.query { db -> db.sequenceOf(UserProfileTable).find {
            it.iidxId eq (node.getAttribute("iidxid").toIntOrNull()
                ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST))
        } ?.refId } ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val musicId = node.getAttribute("mid").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val clId = node.getAttribute("clId").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val style = if(clId >= 5) 1 else 0
        val rank = if(clId >= 5) clId - 5 else clId

        val musicDataSP = Database.query { db -> db.sequenceOf(ScoreTable).find {
            it.refId eq refId and (it.musicId eq musicId) and (it.spPlayed) and (when(clId) {
                0 -> it.clidO notEq ""
                1 -> it.clid1 notEq ""
                2 -> it.clid2 notEq ""
                else -> return@query null
            })
        } }

        val musicDataDP = Database.query { db -> db.sequenceOf(ScoreTable).find {
            it.refId eq refId and (it.musicId eq musicId) and (it.dpPlayed) and (when(clId) {
                0 -> it.clidO notEq ""
                1 -> it.clid1 notEq ""
                2 -> it.clid2 notEq ""
                else -> return@query null
            })
        } }

        return if(style == 0 && musicDataSP != null) {
            createResponseNode().e("mydata")
                .a("__type", "bin").a("score", musicDataSP.spmArray[rank + 7].toString())
                .t(Base64.getDecoder().decode(when(clId) {
                    0 -> musicDataSP.clid0
                    1 -> musicDataSP.clid1
                    2 -> musicDataSP.clid2
                    else -> throw InvalidRequestException(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                }).toString(Charset.defaultCharset()))
        } else if(style == 1 && musicDataDP != null) {
            createResponseNode().e("mydata")
                .a("__type", "bin").a("score", musicDataDP.spmArray[rank + 7].toString())
                .t(Base64.getDecoder().decode(when(clId) {
                    0 -> musicDataDP.clid0
                    1 -> musicDataDP.clid1
                    2 -> musicDataDP.clid2
                    else -> throw InvalidRequestException(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                }).toString(Charset.defaultCharset()))
        } else createResponseNode()
    }
}

@RouteModel(LDJ20211013)
object Crate : IIDXMusicRouteHandler("crate") {
    private val crate by lazy {
        getResourceOrExport("iidx", "music_crate.txt") {
            Load::class.java.getResourceAsStream("/iidx/music_crate.txt") ?: run {
                LOGGER.warn("Music crate is not found either jar or data folder.")
                return@getResourceOrExport null
            }
        } ?.use { stream ->
            val reader = stream.bufferedReader()
            reader.readLines().map { m ->
                val (mid, arr) = m.trim().split("|")
                mid.trim() to arr.trim()
            }.also { reader.close() }
        }
    }
    override suspend fun handle(node: Element): KXmlBuilder {
        var response = createResponseNode()
        crate ?.forEach { (mid, arr) ->
            response = response.e("c")
                .a("__type", "s32").a("__count", "20").a("mid", mid).t(arr).up()
        }

        return response
    }
}