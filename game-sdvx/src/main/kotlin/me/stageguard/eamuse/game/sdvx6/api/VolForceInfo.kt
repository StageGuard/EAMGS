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

object QueryVolForce : SDVX6APIHandler("query_vol_force","vf") {
    override suspend fun handle0(refId: String, request: FullHttpRequest): String {
        val recordForce = Database.query { db -> db.sequenceOf(PlayRecordTable)
            .filter { it.refId eq refId }
            .groupBy { it.mid.times(5).plus(it.type) }
            .map { (_, v) -> PlayRecord {
                mid = v.first().mid
                type = v.first().type
                score = v.maxOf { it.score }
                clear = v.maxOf { it.clear }
            }.let { calculateForce(it) } }
            .sortedDescending()
        } ?: return apiError("NO_SCORE")

        return json.encodeToString(VolForceDTO(
            volForce = (recordForce.take(50).sum() / 100.0).toFixed(3),
            tailScoreForce = recordForce.take(recordForce.size.coerceAtMost(50)).last()
        ))
    }
}


