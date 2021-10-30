@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.sdvx6.model

import me.stageguard.eamuse.database.AddableTable
import me.stageguard.eamuse.game.sdvx6.model.CourseRecordTable.bindTo
import me.stageguard.eamuse.game.sdvx6.model.CourseRecordTable.primaryKey
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object PlayRecordTable : AddableTable<PlayRecord>("sdvx6_play_record") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val mid = long("mid").bindTo { it.mid }
    val type = long("type").bindTo { it.type }
    val score = long("score").bindTo { it.score }
    val exScore = long("exScore").bindTo { it.exScore }
    val clear = long("clear").bindTo { it.clear }
    val grade = long("grade").bindTo { it.grade }
    val buttonRate = int("buttonRate").bindTo { it.buttonRate }
    val longRate = int("longRate").bindTo { it.longRate }
    val volRate = int("volRate").bindTo { it.volRate }

    override fun <T : AssignmentsBuilder> T.mapElement(element: PlayRecord) {
        set(refId, element.refId)
        set(mid, element.mid)
        set(type, element.type)
        set(score, element.score)
        set(exScore, element.exScore)
        set(clear, element.clear)
        set(grade, element.grade)
        set(buttonRate, element.buttonRate)
        set(longRate, element.longRate)
        set(volRate, element.volRate)
    }

    override val createStatement = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `mid` bigint NOT NULL,
        `type` bigint NOT NULL,
        `score` bigint NOT NULL,
        `exScore` bigint NOT NULL,
        `clear` bigint NOT NULL,
        `grade` bigint NOT NULL,
        `buttonRate` int NOT NULL,
        `longRate` int NOT NULL,
        `volRate` int NOT NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface PlayRecord : Entity<PlayRecord> {
    companion object : Entity.Factory<PlayRecord>()
    var __id: Int
    var refId: String
    var mid: Long
    var type: Long
    var score: Long
    var exScore: Long
    var clear: Long
    var grade: Long
    var buttonRate: Int
    var longRate: Int
    var volRate: Int
}