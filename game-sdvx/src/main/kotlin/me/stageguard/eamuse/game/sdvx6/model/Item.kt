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
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object ItemTable : AddableTable<Item>("sdvx6_item") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val id = long("id").bindTo { it.id }
    val type = int("type").bindTo { it.type }
    val param = long("param").bindTo { it.param }

    override fun <T : AssignmentsBuilder> T.mapElement(element: Item) {
        set(refId, element.refId)
        set(id, element.id)
        set(type, element.type)
        set(param, element.param)
    }

    override val createStatement = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `id` bigint NOT NULL,
        `type` int NOT NULL,
        `param` bigint NOT NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface Item : Entity<Item> {
    companion object : Entity.Factory<Item>()

    var __id: Int
    var refId: String
    var id: Long
    var type: Int
    var param: Long
}
