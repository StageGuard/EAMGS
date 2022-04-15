package me.stageguard.eamuse.game.sdvx6.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.SDVX6APIHandler
import me.stageguard.eamuse.game.sdvx6.algorithm.calculateForce
import me.stageguard.eamuse.game.sdvx6.model.PlayRecordTable
import me.stageguard.eamuse.game.sdvx6.sdvx6MusicLibrary
import me.stageguard.eamuse.json
import org.ktorm.dsl.eq
import org.ktorm.entity.*

@Serializable
data class RecentPlayDTO(
    // identifier
    val result: Int = 0,
    // music info
    val mId: Int,
    val mDiffType: Int,
    val mDiff: Int,
    // score info
    val score: Long,
    val exScore: Long,
    val clear: Int,
    val grade: Int,
    // obj rate
    val buttonRate: Int,
    val longRate: Int,
    val volRate: Int,
    // force
    val force: Double,
    val time: Long,
)

object QueryRecentPlay : SDVX6APIHandler("recent") {
    override suspend fun handle0(refId: String, request: FullHttpRequest): String {
       return  try {
            val record = Database.query { db ->
                db.sequenceOf(PlayRecordTable).sortedBy { it.time }.last { it.refId eq refId }
            } ?: return apiError("NO_SCORE")
            val music = sdvx6MusicLibrary[record.mid.toInt()] ?: return apiError("NO_SONG")

            json.encodeToString(RecentPlayDTO(
                mId = music.id, mDiffType = record.type.toInt(), mDiff = music.difficulties.find { it.type == record.type.toInt() }?.difficulty ?: 0,
                score = record.score, exScore = record.exScore, clear = record.clear.toInt(), grade = record.grade.toInt(),
                buttonRate = record.buttonRate, longRate = record.longRate, volRate = record.volRate, force = calculateForce(record),
                time = record.time
            ))
        } catch (ex: Exception) { apiError(ex.toString()) }
    }
}
