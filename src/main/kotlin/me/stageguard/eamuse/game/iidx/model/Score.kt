package me.stageguard.eamuse.game.iidx.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ScoreTable : AddableTable<Score>("iidx_score") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val musicId = int("music_id").bindTo { it.musicId }
    val _spmArray = varchar("spmArray").bindTo { it._spmArray }
    val _dpmArray = varchar("dpmArray").bindTo { it._dpmArray }

    override fun <T : AssignmentsBuilder> T.mapElement(element: Score) {
        set(refId, element.refId)
        set(musicId, element.musicId)
        set(_spmArray, element._spmArray)
        set(_dpmArray, element._dpmArray)
    }


    override val createStatement: String = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `music_id` int NOT NULL,
        `spmArray` varchar(1024) NOT NULL,
        `dpmArray` varchar(1024) NOT NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface Score : Entity<Score> {
    companion object : Entity.Factory<Score>()
    var __id: Int
    var refId: String

    var musicId: Int

    var _spmArray: String
    var _dpmArray: String
}

var Score.spmArray: List<Int>
    get() { return _spmArray.split(" ").map { it.toInt() } }
    set(value) { _spmArray = value.joinToString(" ") }

var Score.dpmArray: List<Int>
    get() { return _dpmArray.split(" ").map { it.toInt() } }
    set(value) { _dpmArray = value.joinToString(" ") }