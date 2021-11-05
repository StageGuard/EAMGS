package me.stageguard.eamuse.game.sdvx6.handler

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.algorithm.calculateVolForce
import me.stageguard.eamuse.game.sdvx6.data.ReferenceIDDTO
import me.stageguard.eamuse.game.sdvx6.model.CourseRecordTable
import me.stageguard.eamuse.game.sdvx6.model.PlayRecordTable
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.game.sdvx6.sdvx6MusicLibrary
import me.stageguard.eamuse.json
import org.intellij.lang.annotations.Language
import org.ktorm.dsl.eq
import org.ktorm.entity.*

@Serializable
data class QueryRecentPlayResponseDTO(
    // identifier
    val result: Int = 0,
    // music info
    val mName: String,
    val mArtist: String,
    val mBpmMin: Double,
    val mBpmMax: Double,
    val mDiffType: Int,
    val mDiff: Int,
    // score info
    val score: Long,
    val exScore: Long,
    val clear: Int,
    val grade: Int,
    val buttonRate: Int,
    val longRate: Int,
    val volRate: Int,
    // player info
    val pName: String,
    val pVF: Double,
    val pSkill: Int,
)

@Language("JSON")
private fun error(reason: String) = """{"result": -1, "message": "$reason"}"""

suspend fun queryRecentPlay(content: String) : String = run {
    try {
        val refId = json.decodeFromString<ReferenceIDDTO>(content).refId
        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: return@run error("USER_NOT_FOUND")
        val record = Database.query { db -> db.sequenceOf(PlayRecordTable).last { it.refId eq refId } }
            ?: return@run error("NO_SCORE")
        val music = sdvx6MusicLibrary[record.mid.toInt()]
        val skill = Database.query { db ->
            db.sequenceOf(CourseRecordTable).filter { it.refId eq refId }
                .sortedByDescending { it.cid }.firstOrNull { it.clear eq 2 } ?.cid ?: 0
        } ?: 0

        json.encodeToString(QueryRecentPlayResponseDTO(
            mName = music ?.title ?: "Unknown", mArtist = music ?.artist ?: "Unknown",
            mBpmMin = music ?.bpm ?.first ?: 0.0, mBpmMax = music ?.bpm ?.second ?: 0.0,
            mDiffType = record.type.toInt(), mDiff = music ?.difficulties ?.find { it.type == record.type.toInt() } ?.difficulty ?: 0,
            score = record.score, exScore = record.exScore, clear = record.clear.toInt(), grade = record.grade.toInt(),
            buttonRate = record.buttonRate, longRate = record.longRate, volRate = record.volRate,
            pName = profile.name, pVF = calculateVolForce(refId), pSkill = skill
        ))
    } catch (ex: Exception) { error(ex.toString()) }
}
