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

package me.stageguard.eamuse.database

import org.ktorm.dsl.*
import org.ktorm.entity.Entity
import org.ktorm.schema.Column
import org.ktorm.schema.Table

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class AddableTable<E : Entity<E>>(name: String) : Table<E>(name) {
    abstract fun <T : AssignmentsBuilder> T.mapElement(element: E)
    abstract val createStatement: String

    suspend fun insert(item: E) {
        Database.query { db -> db.insert(this@AddableTable) { mapElement(item) } }
    }

    suspend fun <T> batchInsert(items: Collection<T>, tMapper: (T) -> E) = Database.query { db ->
        db.batchInsert(this@AddableTable) {
            items.forEach { e -> item { mapElement(tMapper(e)) } }
        }
    }

    suspend fun batchInsert(items: Collection<E>) = batchInsert(items) { it }

    suspend fun batchUpdate(statement: BatchUpdateStatementBuilder<*>.() -> Unit) = Database.query { db ->
        db.batchUpdate(this@AddableTable) {
            statement(this)
        }
    }

    suspend fun <T, C : Any> batchUpdate1(
        items: Collection<T>, columnToIdentify: Column<C>,
        whereExpr: T.() -> C, tMapper: (T) -> E,
    ) = batchUpdate {
        items.forEach { e ->
            item {
                mapElement(tMapper(e))
                where { columnToIdentify eq whereExpr(e) }
            }
        }
    }
}
