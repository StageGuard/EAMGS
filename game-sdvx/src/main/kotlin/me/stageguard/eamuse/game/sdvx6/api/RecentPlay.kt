/*
 * Copyright (c) 2022 StageGuard
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import org.ktorm.entity.last
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.sortedBy

@Serializable
data class RecentPlayDTO(
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
    // identifier
    val result: Int = 0,
)

object QueryRecentPlay : SDVX6APIHandler("query_recent_play", "recent") {
    override suspend fun handle0(refId: String, request: FullHttpRequest): String {
        return try {
            val record = Database.query { db ->
                db.sequenceOf(PlayRecordTable).sortedBy { it.time }.last { it.refId eq refId }
            } ?: return apiError("NO_SCORE")
            val music = sdvx6MusicLibrary[record.mid.toInt()] ?: return apiError("NO_SONG")

            json.encodeToString(RecentPlayDTO(
                mId = music.id,
                mDiffType = record.type.toInt(),
                mDiff = music.difficulties.find { it.type == record.type.toInt() }?.difficulty ?: 0,
                score = record.score,
                exScore = record.exScore,
                clear = record.clear.toInt(),
                grade = record.grade.toInt(),
                buttonRate = record.buttonRate,
                longRate = record.longRate,
                volRate = record.volRate,
                force = calculateForce(record),
                time = record.time
            ))
        } catch (ex: Exception) {
            apiError(ex.toString())
        }
    }
}
