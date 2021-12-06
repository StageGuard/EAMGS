package me.stageguard.eamuse.game.sdvx6.handler

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.algorithm.calculateForce
import me.stageguard.eamuse.game.sdvx6.algorithm.toFixed
import me.stageguard.eamuse.game.sdvx6.apiError
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

suspend fun queryBest50Play(refId: String) = run {
    val b50Plays = Database.query { db -> db.sequenceOf(PlayRecordTable)
        .filter { it.refId eq refId }
        .groupBy { it.mid.times(5).plus(it.type) }
        .map { (_, v) -> v.map { e -> e to calculateForce(e, false) }.maxByOrNull { it.second }!! }
        .sortedByDescending { it.second }
        .map { (record, force) ->
            val music = sdvx6MusicLibrary[record.mid.toInt()] ?: return@map null
            Best50PlayItemDTO(
                mId = music.id, mDiffType = record.type.toInt(),
                mDiff = music.difficulties.find { it.type == record.type.toInt() }?.difficulty ?: 0,
                score = record.score, exScore = record.exScore, clear = record.clear.toInt(),
                grade = record.grade.toInt(), force = force
            )
        }
    } ?: return@run apiError("NO_SCORE")

    json.encodeToString(Best50PlayDTO(
        volForce = (b50Plays.take(50).sumOf { it ?.force ?: 0.0 } / 100.0).toFixed(3),
        plays = b50Plays.take(50).filterNotNull()
    ))
}