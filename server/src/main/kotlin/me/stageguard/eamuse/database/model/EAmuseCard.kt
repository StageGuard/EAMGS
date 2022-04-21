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

package me.stageguard.eamuse.database.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object EAmuseCardTable : AddableTable<EAmuseCard>("eAmuseCard") {
    val id = int("id").primaryKey().bindTo { it.id }
    val cardNFCId = varchar("cardNFCId").bindTo { it.cardNFCId }
    val refId = varchar("refId").bindTo { it.refId }
    val pin = int("pin").bindTo { it.pin }
    val displayId = varchar("displayId").bindTo { it.displayId }
    val registerTime = varchar("registerTime").bindTo { it.registerTime }
    val lastPlayTime = varchar("lastPlayTime").bindTo { it.lastPlayTime }

    override fun <T : AssignmentsBuilder> T.mapElement(element: EAmuseCard) {
        set(cardNFCId, element.cardNFCId)
        set(refId, element.refId)
        set(pin, element.pin)
        set(displayId, element.displayId)
        set(registerTime, element.registerTime)
        set(lastPlayTime, element.lastPlayTime)
    }

    override val createStatement = """
        `id` int NOT NULL AUTO_INCREMENT,
        `cardNFCId` varchar(16) NOT NULL,
        `refId` varchar(16) NOT NULL,
        `pin` int NOT NULL,
        `displayId` varchar(64) NOT NULL,
        `registerTime` varchar(35) NOT NULL,
        `lastPlayTime` varchar(35) NOT NULL,
        PRIMARY KEY (`id`),
        UNIQUE KEY `cards_unique_id` (`cardNFCId`)
    """.trimIndent()
}

interface EAmuseCard : Entity<EAmuseCard> {
    companion object : Entity.Factory<EAmuseCard>()

    var id: Int
    var cardNFCId: String
    var refId: String
    var pin: Int
    var displayId: String
    var registerTime: String
    var lastPlayTime: String
}
