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

object PcbIdTable : AddableTable<PcbId>("pcbId") {
    val id = int("id").primaryKey().bindTo { it.id }
    val pcbId = varchar("pcbId").bindTo { it.pcbId }

    override fun <T : AssignmentsBuilder> T.mapElement(element: PcbId) {
        set(pcbId, element.pcbId)
    }

    override val createStatement = """
        `id` int NOT NULL AUTO_INCREMENT,
        `pcbId` varchar(16) NOT NULL,
        PRIMARY KEY (`id`),
        UNIQUE KEY `pcb_unique_id` (`pcbId`)
    """.trimIndent()
}

interface PcbId : Entity<PcbId> {
    companion object : Entity.Factory<PcbId>()

    var id: Int
    var pcbId: String
}
