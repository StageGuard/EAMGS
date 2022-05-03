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
import me.stageguard.eamuse.game.sdvx6.model.*
import me.stageguard.eamuse.game.sdvx6.sdvx6SkillCourseSessions
import me.stageguard.eamuse.json
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*

@Serializable
data class ProfileDTO(
    // profile
    val name: String,
    val appeal: Int,
    val akaName: Int,
    val crewId: Int,
    val skillLevel: Int,
    val passedAllSkillSeason: Boolean,
    // identifier
    val result: Int = 0,
)

object QueryProfile : SDVX6APIHandler("profile", "profile") {
    override suspend fun handle0(refId: String, request: FullHttpRequest): String {
        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: return apiError("USER_NOT_FOUND")
        val crewId = Database.query { db ->
            db.sequenceOf(ParamTable).find {
                it.refId eq refId and (it.type eq 2) and (it.id eq 1)
            }?.param?.get(24) ?: 113
        } ?: 113 // 113 = default crew for generation 6 Rasis
        val skillLevel =
            Database.query { db -> db.sequenceOf(SkillTable).filter { it.refId eq refId }.maxBy { it.level } ?: 0 }
                ?: return apiError("USER_NOT_FOUND")

        val passedAllSkillSeason = if (skillLevel == 0) false else Database.query { db ->
            db.sequenceOf(CourseRecordTable).filter {
                it.refId eq refId and (it.cid eq skillLevel) and (it.clear eq 2)
            }.count() >= sdvx6SkillCourseSessions.foldRight(0) { s, acc ->
                acc + s.courses.count { it.id == skillLevel }
            }
        } ?: false

        return json.encodeToString(ProfileDTO(
            name = profile.name, appeal = profile.appeal, akaName = profile.akaname,
            skillLevel = skillLevel, passedAllSkillSeason = passedAllSkillSeason, crewId = crewId
        ))
    }
}
