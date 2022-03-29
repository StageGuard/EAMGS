package me.stageguard.eamuse.game.iidx

import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCard
import me.stageguard.eamuse.game.iidx.model.UserProfileTable
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.router.ProfileChecker
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.sequenceOf

@RouteModel(LDJ20211013)
object IIDXProfileChecker : ProfileChecker {
    override suspend fun check(cardInfo: EAmuseCard) =
        Database.query { db -> db.sequenceOf(UserProfileTable).any { it.refId eq cardInfo.refId } } ?: false
}