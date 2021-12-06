package me.stageguard.eamuse.game.sdvx6.handler

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.apiError
import me.stageguard.eamuse.game.sdvx6.model.CourseRecordTable
import me.stageguard.eamuse.game.sdvx6.model.ParamTable
import me.stageguard.eamuse.game.sdvx6.model.SkillTable
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.json
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*

@Serializable
data class ProfileDTO (
    // identifier
    val result: Int = 0,
    // profile
    val name: String,
    val appealId: Int,
    val akaNameIndex: Int,
    val skill: Int,
    val crewId: Int
)

suspend fun queryProfile(refId: String) = run {
    val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
        ?: return@run apiError("USER_NOT_FOUND")
    val skill = Database.query { db -> db.sequenceOf(SkillTable).find { it.refId eq refId } ?.baseId ?: 0 } ?: 0
    val crewId = Database.query { db -> db.sequenceOf(ParamTable).find {
        it.refId eq refId and (it.type eq 2) and (it.id eq 1)
    } ?.param()?.get(24) ?: 113 } ?: 113 // 113 = default crew for generation 6 Rasis
    json.encodeToString(ProfileDTO(
        name = profile.name, appealId = profile.appeal, akaNameIndex = profile.akaname,
        skill = skill, crewId = crewId
    ))
}