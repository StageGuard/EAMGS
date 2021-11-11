package me.stageguard.eamuse.game.sdvx6.handler

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.game.sdvx6.algorithm.calculateVolForce
import me.stageguard.eamuse.game.sdvx6.model.CourseRecordTable
import me.stageguard.eamuse.game.sdvx6.model.PlayRecordTable
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.game.sdvx6.sdvx6AppealCards
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
    val mId: Int,
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
    val pAppealId: Int,
    val pAppealTex: String,
    val pAppealTitle: String,
    val time: Long,
)

@Language("JSON")
private fun error(reason: String) = """{"result": -1, "message": "$reason"}"""

suspend fun queryRecentPlay(cardId: String) : String = run {
    try {
        val refId = Database.query { db -> db.sequenceOf(EAmuseCardTable).find { it.cardNFCId eq cardId } }
            ?.refId ?: return@run error("REFID")
        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: return@run error("USER_NOT_FOUND")
        val record = Database.query { db ->
            db.sequenceOf(PlayRecordTable).sortedBy { it.time }.last { it.refId eq refId }
        } ?: return@run error("NO_SCORE")
        val music = sdvx6MusicLibrary[record.mid.toInt()]
        val skill = Database.query { db ->
            db.sequenceOf(CourseRecordTable).filter { it.refId eq refId }
                .sortedByDescending { it.cid }.firstOrNull { it.clear eq 2 } ?.cid ?: 0
        } ?: 0
        val appeal = sdvx6AppealCards[profile.appeal]

        json.encodeToString(QueryRecentPlayResponseDTO(
            mId = music ?.id ?: record.mid.toInt(), mName = music ?.title ?: "Unknown", mArtist = music ?.artist ?: "Unknown",
            mBpmMin = music ?.bpm ?.first ?: 0.0, mBpmMax = music ?.bpm ?.second ?: 0.0,
            mDiffType = record.type.toInt(), mDiff = music ?.difficulties ?.find { it.type == record.type.toInt() } ?.difficulty ?: 0,
            score = record.score, exScore = record.exScore, clear = record.clear.toInt(), grade = record.grade.toInt(),
            buttonRate = record.buttonRate, longRate = record.longRate, volRate = record.volRate,
            pName = profile.name, pVF = calculateVolForce(refId), pSkill = skill,
            pAppealId = appeal ?.id ?: profile.appeal, pAppealTex = appeal ?.texture ?: "", pAppealTitle = appeal ?.title ?: "Unknown",
            time = record.time
        ))
    } catch (ex: Exception) { error(ex.toString()) }
}
