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

package me.stageguard.eamuse.game.sdvx6

import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCard
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.router.ProfileChecker
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.sequenceOf

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124, SDVX6_20220214, SDVX6_20220308)
object SDVX6ProfileChecker : ProfileChecker {
    override suspend fun check(cardInfo: EAmuseCard) =
        Database.query { db -> db.sequenceOf(UserProfileTable).any { it.refId eq cardInfo.refId } } ?: false

}
