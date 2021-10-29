package me.stageguard.eamuse.game.sdvx6.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object ItemTable: AddableTable<Item>("sdvx6_item") {
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
        `refId` varchar(16) NOT NULL,
        `id` bigint NOT NULL,
        `type` int NOT NULL,
        `param` bigint NOT NULL,
        UNIQUE KEY `ref_unique_id` (`refId`)
    """.trimIndent()
}

interface Item : Entity<Item> {
    companion object : Entity.Factory<Item>()
    var refId: String
    var id: Long
    var type: Int
    var param: Long
}