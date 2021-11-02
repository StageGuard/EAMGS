package me.stageguard.eamuse.game.sdvx6

import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCard
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.router.ProfileChecker
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.sequenceOf

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020)
object SDVX6ProfileChecker : ProfileChecker {
    override suspend fun check(cardInfo: EAmuseCard) =
        Database.query { db -> db.sequenceOf(UserProfileTable).any { it.refId eq cardInfo.refId } } ?: false

}