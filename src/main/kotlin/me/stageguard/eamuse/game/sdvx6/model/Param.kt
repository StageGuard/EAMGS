package me.stageguard.eamuse.game.sdvx6.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ParamTable: AddableTable<Param>("sdvx6_param") {
    val refId = varchar("refId").bindTo { it.refId }
    val id = int("id").bindTo { it.id }
    val type = int("type").bindTo { it.type }
    val param = varchar("param").bindTo { it.param }

    override fun <T : AssignmentsBuilder> T.mapElement(element: Param) {
        set(refId, element.refId)
        set(id, element.id)
        set(type, element.type)
        set(param, element.param)
    }

    override val createStatement = """
        `refId` varchar(16) NOT NULL,
        `id` int NOT NULL,
        `type` int NOT NULL,
        `param` varchar(64) NOT NULL,
        UNIQUE KEY `ref_unique_id` (`refId`)
    """.trimIndent()
}

interface Param : Entity<Param> {
    companion object : Entity.Factory<Param>()
    var refId: String
    var id: Int
    var type: Int
    var param: String

    fun param() = param.split(" ").map { it.toInt() }
    fun setParam(paramList: List<Int>) { param = paramList.joinToString(" ") }
}