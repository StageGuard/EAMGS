@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.sdvx6.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object CourseRecordTable : AddableTable<CourseRecord>("sdvx6_course_record") {
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
        `refId` varchar(16) NOT NULL,
        `sid` int NOT NULL,
        `cid` int NOT NULL,
        `score` int NOT NULL,
        `clear` int NOT NULL,
        `grade` int NOT NULL,
        `rate` int NOT NULL,
        `count` int NOT NULL,
        UNIQUE KEY `ref_unique_id` (`refId`)
    """.trimIndent()
}

interface CourseRecord : Entity<CourseRecord> {
    companion object : Entity.Factory<CourseRecord>()
    var refId: String
    val sid: Int
    val cid: Int
    val score: Int
    val clear: Int
    val grade: Int
    val rate: Int
    val count: Int
}