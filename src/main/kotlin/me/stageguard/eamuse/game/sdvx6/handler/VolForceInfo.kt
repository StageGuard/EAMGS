package me.stageguard.eamuse.game.sdvx6.handler

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.algorithm.calculateForce
import me.stageguard.eamuse.game.sdvx6.algorithm.toFixed
import me.stageguard.eamuse.game.sdvx6.apiError
import me.stageguard.eamuse.game.sdvx6.model.PlayRecordTable
import me.stageguard.eamuse.json
import org.ktorm.dsl.eq
import org.ktorm.entity.*

@Serializable
data class VolForceDTO(
    // identifier
    val result: Int = 0,
    // vf data
    val volForce: Double,
    val tailScoreForce: Double,
)

suspend fun queryVolForce(refId: String) = run {
    val recordForce = Database.query { db -> db.sequenceOf(PlayRecordTable)
        .filter { it.refId eq refId }
        .groupBy { it.mid.times(5).plus(it.type) }
        .map { (_, v) -> v.maxOf { e -> calculateForce(e) } }
        .sortedDescending()
    } ?: return@run apiError("NO_SCORE")

    json.encodeToString(VolForceDTO(
        volForce = (recordForce.take(50).sum() / 100.0).toFixed(3),
        tailScoreForce = recordForce.take(recordForce.size.coerceAtMost(50)).last()
    ))
}



