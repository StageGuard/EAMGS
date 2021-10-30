@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.sdvx6.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object CourseRecordTable : AddableTable<CourseRecord>("sdvx6_course_record") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val sid = int("sid").bindTo { it.sid }
    val cid = int("cid").bindTo { it.cid }
    val score = int("score").bindTo { it.score }
    val clear = int("clear").bindTo { it.clear }
    val grade = int("grade").bindTo { it.grade }
    val rate = int("rate").bindTo { it.rate }
    val count = int("count").bindTo { it.count }

    override fun <T : AssignmentsBuilder> T.mapElement(element: CourseRecord) {
        set(refId, element.refId)
        set(sid, element.sid)
        set(cid, element.cid)
        set(score, element.score)
        set(clear, element.clear)
        set(grade, element.grade)
        set(rate, element.rate)
        set(count, element.count)
    }

    override val createStatement = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `sid` int NOT NULL,
        `cid` int NOT NULL,
        `score` int NOT NULL,
        `clear` int NOT NULL,
        `grade` int NOT NULL,
        `rate` int NOT NULL,
        `count` int NOT NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface CourseRecord : Entity<CourseRecord> {
    companion object : Entity.Factory<CourseRecord>()
    var __id: Int
    var refId: String
    var sid: Int
    var cid: Int
    var score: Int
    var clear: Int
    var grade: Int
    var rate: Int
    var count: Int
}