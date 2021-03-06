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

package me.stageguard.eamuse.game.sdvx6.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object SkillTable : AddableTable<Skill>("sdvx6_skill") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val baseId = int("baseId").bindTo { it.baseId }
    val level = int("level").bindTo { it.level }
    val nameId = int("nameId").bindTo { it.nameId }

    override fun <T : AssignmentsBuilder> T.mapElement(element: Skill) {
        set(refId, element.refId)
        set(baseId, element.baseId)
        set(level, element.level)
        set(nameId, element.nameId)
    }

    override val createStatement = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `baseId` int NOT NULL,
        `level` int NOT NULL,
        `nameId` int NOT NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface Skill : Entity<Skill> {
    companion object : Entity.Factory<Skill>()

    var __id: Int
    var refId: String
    var baseId: Int
    var level: Int
    var nameId: Int
}
