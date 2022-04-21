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

package me.stageguard.eamuse.game.iidx.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object UserProfileTable : AddableTable<UserProfile>("iidx_profile") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val name = varchar("name").bindTo { it.name }
    val pid = int("pid").bindTo { it.pid }
    val iidxId = int("iidxId").bindTo { it.iidxId }

    override fun <T : AssignmentsBuilder> T.mapElement(element: UserProfile) {
        set(refId, element.refId)
        set(name, element.name)
        set(pid, element.pid)
        set(iidxId, element.iidxId)
    }

    override val createStatement: String = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `name` varchar(8) NOT NULL,
        `pid` int NOT NULL,
        `iidxId` int NOT NULL,
        UNIQUE KEY `ref_unique_id` (`refId`),
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface UserProfile : Entity<UserProfile> {
    companion object : Entity.Factory<UserProfile>()

    var __id: Int
    var refId: String
    var name: String
    var pid: Int
    var iidxId: Int
}
