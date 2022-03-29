package me.stageguard.eamuse.game.iidx.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ScoreTable : AddableTable<Score>("iidx_score") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val musicId = int("music_id").bindTo { it.musicId }
    val spPlayed = boolean("sp_played").bindTo { it.spPlayed }
    val dpPlayed = boolean("dp_played").bindTo { it.dpPlayed }
    val _spmArray = varchar("spmArray").bindTo { it._spmArray }
    val _dpmArray = varchar("dpmArray").bindTo { it._dpmArray }
    val clidO = varchar("clid0").bindTo { it.clid0 }
    val clid1 = varchar("clid1").bindTo { it.clid1 }
    val clid2 = varchar("clid2").bindTo { it.clid2 }

    override fun <T : AssignmentsBuilder> T.mapElement(element: Score) {
        set(refId, element.refId)
        set(musicId, element.musicId)
        set(spPlayed, element.spPlayed)
        set(dpPlayed, element.dpPlayed)
        set(_spmArray, element._spmArray)
        set(_dpmArray, element._dpmArray)
        set(clidO, element.clid0)
        set(clid1, element.clid1)
        set(clid2, element.clid2)
    }


    override val createStatement: String = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `music_id` int NOT NULL,
        `sp_played` boolean NOT NULL,
        `dp_played` boolean NOT NULL,
        `spmArray` varchar(1024) NOT NULL,
        `dpmArray` varchar(1024) NOT NULL,
        `clid0` varchar(1024) NOT NULL,
        `clid1` varchar(1024) NOT NULL,
        `clid2` varchar(1024) NOT NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface Score : Entity<Score> {
    companion object : Entity.Factory<Score>()
    var __id: Int
    var refId: String

    var musicId: Int
    var spPlayed: Boolean
    var dpPlayed: Boolean

    var _spmArray: String
    var _dpmArray: String

    var clid0: String
    var clid1: String
    var clid2: String
}

var Score.spmArray: List<Int>
    get() { return _spmArray.split(" ").map { it.toInt() } }
    set(value) { _spmArray = value.joinToString(" ") }

var Score.dpmArray: List<Int>
    get() { return _dpmArray.split(" ").map { it.toInt() } }
    set(value) { _dpmArray = value.joinToString(" ") }