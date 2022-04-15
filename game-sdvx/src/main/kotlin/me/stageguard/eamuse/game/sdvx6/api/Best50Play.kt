package me.stageguard.eamuse.game.sdvx6.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.SDVX6APIHandler
import me.stageguard.eamuse.game.sdvx6.algorithm.calculateForce
import me.stageguard.eamuse.game.sdvx6.algorithm.toFixed
import me.stageguard.eamuse.game.sdvx6.model.PlayRecord
import me.stageguard.eamuse.game.sdvx6.model.PlayRecordTable
import me.stageguard.eamuse.game.sdvx6.sdvx6MusicLibrary
import me.stageguard.eamuse.json
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.groupBy
import org.ktorm.entity.sequenceOf

@Serializable
data class Best50PlayDTO(
    // identifier
    val result: Int = 0,
    val volForce: Double,
    val plays: List<Best50PlayItemDTO>
)

@Serializable
data class Best50PlayItemDTO(
    // music info
    val mId: Int,
    val mDiffType: Int,
    val mDiff: Int,
    // score info
    val score: Long,
    val exScore: Long,
    val clear: Int,
    val grade: Int,
    // force
    val force: Double
)

object QueryBest50Plays : SDVX6APIHandler("best50") {
    override suspend fun handle0(refId: String, request: FullHttpRequest): String {
        val b50Plays = Database.query { db -> db.sequenceOf(PlayRecordTable)
            .filter { it.refId eq refId }
            .groupBy { it.mid.times(5).plus(it.type) }
            .map { (_, v) -> PlayRecord {
                mid = v.first().mid
                type = v.first().type
                score = v.maxOf { it.score }
                clear = v.maxOf { it.clear }
            }.let { v.maxByOrNull { s -> s.score }!!.also { nr ->
                nr.clear = v.maxOf { r -> r.clear }
                nr.grade = v.maxOf { r -> r.grade }
                nr.exScore = it.exScore
            } to calculateForce(it) } }
            .sortedByDescending { (_, force) -> force }
            .map { (record, force) ->
                val music = sdvx6MusicLibrary[record.mid.toInt()] ?: return@map null
                Best50PlayItemDTO(
                    mId = music.id, mDiffType = record.type.toInt(),
                    mDiff = music.difficulties.find { it.type == record.type.toInt() }?.difficulty ?: 0,
                    score = record.score, exScore = record.exScore, clear = record.clear.toInt(),
                    grade = record.grade.toInt(), force = force
                )
            }
        } ?: return apiError("NO_SCORE")

        return json.encodeToString(Best50PlayDTO(
            volForce = (b50Plays.take(50).sumOf { it ?.force ?: 0.0 } / 100.0).toFixed(3),
            plays = b50Plays.take(50).filterNotNull()
        ))
    }
}
