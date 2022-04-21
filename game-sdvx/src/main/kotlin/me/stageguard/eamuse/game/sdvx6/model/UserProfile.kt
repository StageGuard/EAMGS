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

@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.sdvx6.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object UserProfileTable : AddableTable<UserProfile>("sdvx6_profile") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val id = int("id").bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val appeal = int("appeal").bindTo { it.appeal }
    val akaname = int("akaname").bindTo { it.akaname }
    val blocks = long("blocks").bindTo { it.blocks }
    val packets = long("packets").bindTo { it.packets }
    val arsOption = int("arsOption").bindTo { it.arsOption }
    val drawAdjust = int("drawAdjust").bindTo { it.drawAdjust }
    val earlyLateDisp = int("earlyLateDisp").bindTo { it.earlyLateDisp }
    val effCLeft = int("effCLeft").bindTo { it.effCLeft }
    val effCRight = int("effCRight").bindTo { it.effCRight }
    val gaugeOption = int("gaugeOption").bindTo { it.gaugeOption }
    val hiSpeed = int("hiSpeed").bindTo { it.hiSpeed }
    val laneSpeed = long("laneSpeed").bindTo { it.laneSpeed }
    val narrowDown = int("narrowDown").bindTo { it.narrowDown }
    val notesOption = int("notesOption").bindTo { it.notesOption }
    val blasterCount = long("blasterCount").bindTo { it.blasterCount }
    val blasterEnergy = long("blasterEnergy").bindTo { it.blasterEnergy }
    val extrackEnergy = int("extrackEnergy").bindTo { it.extrackEnergy }
    val bgm = int("bgm").bindTo { it.bgm }
    val subbg = int("subbg").bindTo { it.subbg }
    val nemsys = int("nemsys").bindTo { it.nemsys }
    val stampA = int("stampA").bindTo { it.stampA }
    val stampB = int("stampB").bindTo { it.stampB }
    val stampC = int("stampC").bindTo { it.stampC }
    val stampD = int("stampD").bindTo { it.stampD }
    val headphone = int("headphone").bindTo { it.headphone }
    val musicID = int("musicID").bindTo { it.musicID }
    val musicType = int("musicType").bindTo { it.musicType }
    val sortType = int("sortType").bindTo { it.sortType }
    val expPoint = int("expPoint").bindTo { it.expPoint }
    val mUserCnt = int("mUserCnt").bindTo { it.mUserCnt }

    override fun <T : AssignmentsBuilder> T.mapElement(element: UserProfile) {
        set(refId, element.refId)
        set(id, element.id)
        set(name, element.name)
        set(appeal, element.appeal)
        set(akaname, element.akaname)
        set(blocks, element.blocks)
        set(packets, element.packets)
        set(arsOption, element.arsOption)
        set(drawAdjust, element.drawAdjust)
        set(earlyLateDisp, element.earlyLateDisp)
        set(effCLeft, element.effCLeft)
        set(effCRight, element.effCRight)
        set(gaugeOption, element.gaugeOption)
        set(hiSpeed, element.hiSpeed)
        set(laneSpeed, element.laneSpeed)
        set(narrowDown, element.narrowDown)
        set(notesOption, element.notesOption)
        set(blasterCount, element.blasterCount)
        set(blasterEnergy, element.blasterEnergy)
        set(extrackEnergy, element.extrackEnergy)
        set(bgm, element.bgm)
        set(subbg, element.subbg)
        set(nemsys, element.nemsys)
        set(stampA, element.stampA)
        set(stampB, element.stampB)
        set(stampC, element.stampC)
        set(stampD, element.stampD)
        set(headphone, element.headphone)
        set(musicID, element.musicID)
        set(musicType, element.musicType)
        set(sortType, element.sortType)
        set(expPoint, element.expPoint)
        set(mUserCnt, element.mUserCnt)
    }

    override val createStatement = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `id` int NOT NULL,
        `name` varchar(10) NOT NULL,
        `appeal` int NOT NULL,
        `akaname` int NOT NULL,
        `blocks` bigint NOT NULL,
        `packets` bigint NOT NULL,
        `arsOption` int NOT NULL,
        `drawAdjust` int NOT NULL,
        `earlyLateDisp` int NOT NULL,
        `effCLeft` int NOT NULL,
        `effCRight` int NOT NULL,
        `gaugeOption` int NOT NULL,
        `hiSpeed` int NOT NULL,
        `laneSpeed` bigint NOT NULL,
        `narrowDown` int NOT NULL,
        `notesOption` int NOT NULL,
        `blasterCount` bigint NOT NULL,
        `blasterEnergy` bigint NOT NULL,
        `extrackEnergy` int NOT NULL,
        `bgm` int NOT NULL,
        `subbg` int NOT NULL,
        `nemsys` int NOT NULL,
        `stampA` int NOT NULL,
        `stampB` int NOT NULL,
        `stampC` int NOT NULL,
        `stampD` int NOT NULL,
        `headphone` int NOT NULL,
        `musicID` int NOT NULL,
        `musicType` int NOT NULL,
        `sortType` int NOT NULL,
        `expPoint` int NOT NULL,
        `mUserCnt` int NOT NULL,
        UNIQUE KEY `ref_unique_id` (`refId`),
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface UserProfile : Entity<UserProfile> {
    companion object : Entity.Factory<UserProfile>()

    var refId: String
    var __id: Int
    var id: Int
    var name: String
    var appeal: Int
    var akaname: Int
    var blocks: Long
    var packets: Long
    var arsOption: Int
    var drawAdjust: Int
    var earlyLateDisp: Int
    var effCLeft: Int
    var effCRight: Int
    var gaugeOption: Int
    var hiSpeed: Int
    var laneSpeed: Long
    var narrowDown: Int
    var notesOption: Int
    var blasterCount: Long
    var blasterEnergy: Long
    var extrackEnergy: Int
    var bgm: Int
    var subbg: Int
    var nemsys: Int
    var stampA: Int
    var stampB: Int
    var stampC: Int
    var stampD: Int
    var headphone: Int
    var musicID: Int
    var musicType: Int
    var sortType: Int
    var expPoint: Int
    var mUserCnt: Int
}


/*

id
name
appeal
akaname
blocks
packets
arsOption
drawAdjust
earlyLateDisp
effCLeft
effCRight
gaugeOption
hiSpeed
laneSpeed
narrowDown
notesOption
blasterCount
blasterEnergy
extrackEnergy
bgm
subbg
nemsys
stampA
stampB
stampC
stampD
headphone
musicID
musicType
sortType
expPoint
mUserCnt
        */
