@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.sdvx6.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object MixTable: AddableTable<Mix>("sdvx6_mix") {
    val refId = varchar("refId").bindTo { it.refId }
    val id = int("id").bindTo { it.id }
    val code = varchar("code").bindTo { it.code }
    val name = varchar("name").bindTo { it.name }
    val creator = varchar("creator").bindTo { it.creator }
    val param = varchar("param").bindTo { it.param }
    val jacket = int("jacket").bindTo { it.jacket }
    val tag = int("tag").bindTo { it.tag }

    override fun <T : AssignmentsBuilder> T.mapElement(element: Mix) {
        set(refId, element.refId)
        set(id, element.id)
        set(code, element.code)
        set(name, element.name)
        set(creator, element.creator)
        set(param, element.param)
        set(jacket, element.jacket)
        set(tag, element.tag)
    }

    override val createStatement = """
        `refId` varchar(16) NOT NULL,
        `id` int NOT NULL,
        `code` varchar(32) NOT NULL,
        `name` varchar(32) NOT NULL,
        `creator` varchar(32) NOT NULL,
        `param` varchar(32) NOT NULL,
        `jacket` int NOT NULL,
        `tag` int NOT NULL,
        UNIQUE KEY `ref_unique_id` (`refId`)
    """.trimIndent()
}

interface Mix : Entity<Mix> {
    companion object : Entity.Factory<Mix>()
    var refId: String
    var id: Int
    var code: String
    var name: String
    var creator: String
    var param: String
    var jacket: Int
    var tag: Int
}