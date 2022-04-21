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
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object SettingsTable : AddableTable<Settings>("iidx_settings") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }

    val frame = int("frame").bindTo { it.frame }
    val menuMusic = int("menu_music").bindTo { it.menuMusic }
    val noteBurst = int("note_burst").bindTo { it.noteBurst }
    val turntable = int("turntable").bindTo { it.turntable }
    val laneCover = int("lane_cover").bindTo { it.laneCover }
    val pacemakerCover = int("pacemaker_cover").bindTo { it.pacemakerCover }
    val noteSkin = int("note_skin").bindTo { it.noteSkin }
    val judgeFont = int("judge_font").bindTo { it.judgeFont }
    val noteBeam = int("note_beam").bindTo { it.noteBeam }
    val fullComboSplash = int("full_combo_splash").bindTo { it.fullComboSplash }
    val disableMusicpreview = int("disable_musicpreview").bindTo { it.disableMusicpreview }
    val vefxLock = int("vefx_lock").bindTo { it.vefxLock }
    val effect = int("effect").bindTo { it.effect }
    val bombSize = int("bomb_size").bindTo { it.bombSize }
    val disableHcnColor = int("disable_hcn_color").bindTo { it.disableHcnColor }
    val firstNotePreview = int("first_note_preview").bindTo { it.firstNotePreview }
    val qproHead = int("qpro_head").bindTo { it.qproHead }
    val qproHair = int("qpro_hair").bindTo { it.qproHair }
    val qproHand = int("qpro_hand").bindTo { it.qproHand }
    val qproFace = int("qpro_face").bindTo { it.qproFace }
    val qproBody = int("qpro_body").bindTo { it.qproBody }
    val scoreFolders = boolean("score_folders").bindTo { it.scoreFolders }
    val clearFolders = boolean("clear_folders").bindTo { it.clearFolders }
    val difficultyFolders = boolean("difficulty_folders").bindTo { it.difficultyFolders }
    val alphabetFolders = boolean("alphabet_folders").bindTo { it.alphabetFolders }
    val hidePlaycount = boolean("hide_playcount").bindTo { it.hidePlaycount }
    val disableGraphcutin = boolean("disable_graphcutin").bindTo { it.disableGraphcutin }
    val classicHispeed = boolean("classic_hispeed").bindTo { it.classicHispeed }
    val hideIidxid = boolean("hide_iidxid").bindTo { it.hideIidxid }

    override fun <T : AssignmentsBuilder> T.mapElement(element: Settings) {
        set(refId, element.refId)
        set(frame, element.frame)
        set(menuMusic, element.menuMusic)
        set(noteBurst, element.noteBurst)
        set(turntable, element.turntable)
        set(laneCover, element.laneCover)
        set(pacemakerCover, element.pacemakerCover)
        set(noteSkin, element.noteSkin)
        set(judgeFont, element.judgeFont)
        set(noteBeam, element.noteBeam)
        set(fullComboSplash, element.fullComboSplash)
        set(disableMusicpreview, element.disableMusicpreview)
        set(vefxLock, element.vefxLock)
        set(effect, element.effect)
        set(bombSize, element.bombSize)
        set(disableHcnColor, element.disableHcnColor)
        set(firstNotePreview, element.firstNotePreview)
        set(qproHead, element.qproHead)
        set(qproHair, element.qproHair)
        set(qproHand, element.qproHand)
        set(qproFace, element.qproFace)
        set(qproBody, element.qproBody)
        set(scoreFolders, element.scoreFolders)
        set(clearFolders, element.clearFolders)
        set(difficultyFolders, element.difficultyFolders)
        set(alphabetFolders, element.alphabetFolders)
        set(hidePlaycount, element.hidePlaycount)
        set(disableGraphcutin, element.disableGraphcutin)
        set(classicHispeed, element.classicHispeed)
        set(hideIidxid, element.hideIidxid)
    }

    override val createStatement: String = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `frame` int NOT NULL,
        `menu_music` int NOT NULL,
        `note_burst` int NOT NULL,
        `turntable` int NOT NULL,
        `lane_cover` int NOT NULL,
        `pacemaker_cover` int NOT NULL,
        `note_skin` int NOT NULL,
        `judge_font` int NOT NULL,
        `note_beam` int NOT NULL,
        `full_combo_splash` int NOT NULL,
        `disable_musicpreview` int NOT NULL,
        `vefx_lock` int NOT NULL,
        `effect` int NOT NULL,
        `bomb_size` int NOT NULL,
        `disable_hcn_color` int NOT NULL,
        `first_note_preview` int NOT NULL,
        `qpro_head` int NOT NULL,
        `qpro_hair` int NOT NULL,
        `qpro_hand` int NOT NULL,
        `qpro_face` int NOT NULL,
        `qpro_body` int NOT NULL,
        `score_folders` boolean NOT NULL,
        `clear_folders` boolean NOT NULL,
        `difficulty_folders` boolean NOT NULL,
        `alphabet_folders` boolean NOT NULL,
        `hide_playcount` boolean NOT NULL,
        `disable_graphcutin` boolean NOT NULL,
        `classic_hispeed` boolean NOT NULL,
        `hide_iidxid` boolean NOT NULL,
        UNIQUE KEY `ref_unique_id` (`refId`),
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface Settings : Entity<Settings> {
    companion object : Entity.Factory<Settings>()

    var __id: Int
    var refId: String

    var frame: Int
    var menuMusic: Int
    var noteBurst: Int
    var turntable: Int
    var laneCover: Int
    var pacemakerCover: Int
    var noteSkin: Int
    var judgeFont: Int
    var noteBeam: Int
    var fullComboSplash: Int
    var disableMusicpreview: Int
    var vefxLock: Int
    var effect: Int
    var bombSize: Int
    var disableHcnColor: Int
    var firstNotePreview: Int
    var qproHead: Int
    var qproHair: Int
    var qproHand: Int
    var qproFace: Int
    var qproBody: Int
    var scoreFolders: Boolean
    var clearFolders: Boolean
    var difficultyFolders: Boolean
    var alphabetFolders: Boolean
    var hidePlaycount: Boolean
    var disableGraphcutin: Boolean
    var classicHispeed: Boolean
    var hideIidxid: Boolean

}
