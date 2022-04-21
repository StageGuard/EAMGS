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

package me.stageguard.eamuse.game.iidx.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object ScoreDetailTable : AddableTable<ScoreDetail>("iidx_score_detail") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val musicId = int("music_id").bindTo { it.musicId }
    val clid = int("clid").bindTo { it.clid }
    val score = int("score").bindTo { it.score }
    val clflg = int("clflg").bindTo { it.clflg }
    val miss = int("miss").bindTo { it.miss }
    val great = int("great").bindTo { it.great }
    val pGreat = int("pgreat").bindTo { it.pGreat }
    val time = long("time").bindTo { it.time }


    override fun <T : AssignmentsBuilder> T.mapElement(element: ScoreDetail) {
        set(refId, element.refId)
        set(musicId, element.musicId)
        set(clid, element.clid)
        set(score, element.score)
        set(clflg, element.clflg)
        set(miss, element.miss)
        set(great, element.great)
        set(pGreat, element.pGreat)
        set(time, element.time)
    }


    override val createStatement: String = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `music_id` int NOT NULL,
        `clid` int not NULL,
        `score` int not NULL,
        `clflg` int not NULL,
        `miss` int not NULL,
        `great` int not NULL,
        `pGreat` int not NULL,
        `time` bigint not NULL,
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface ScoreDetail : Entity<ScoreDetail> {
    companion object : Entity.Factory<ScoreDetail>()

    var __id: Int
    var refId: String

    var musicId: Int
    var clid: Int
    var score: Int
    var clflg: Int
    var miss: Int
    var great: Int
    var pGreat: Int
    var time: Long
}
