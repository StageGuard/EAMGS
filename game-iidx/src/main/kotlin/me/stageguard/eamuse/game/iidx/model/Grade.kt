package me.stageguard.eamuse.game.iidx.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object GradeTable : AddableTable<Grade>("iidx_grade"){
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val style = int("style").bindTo { it.style }
    val grade = int("grade").bindTo { it.grade }
    val _dArray = varchar("dArray").bindTo { it._dArray }

    override fun <T : AssignmentsBuilder> T.mapElement(element: Grade) {
        set(refId, element.refId)
        set(style, element.style)
        set(grade, element.grade)
        set(_dArray, element._dArray)
    }

    override val createStatement: String = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `style` int NOT NULL,
        `grade` int NOT NULL,
        `dArray` varchar(1024) NOT NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface Grade : Entity<Grade> {
    companion object : Entity.Factory<Grade>()
    var __id: Int
    var refId: String
    var style: Int
    var grade: Int
    var _dArray: String
}

var Grade.dArray: List<Int>
    get() { return _dArray.split(" ").map { it.toInt() } }
    set(value) { _dArray = value.joinToString(" ") }