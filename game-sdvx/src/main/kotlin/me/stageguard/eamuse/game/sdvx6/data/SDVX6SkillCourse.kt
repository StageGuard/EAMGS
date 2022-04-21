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

package me.stageguard.eamuse.game.sdvx6.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SDVX6SkillCourse(
    @SerialName("sessions")
    val sessions: List<SkillCourseSeason>,
)

@Serializable
data class SkillCourseSeason(
    @SerialName("courses")
    val courses: List<Course>,
    @SerialName("id")
    val id: Int,
    @SerialName("isNew")
    val isNew: Int,
    @SerialName("name")
    val name: String,
)

@Serializable
data class Course(
    @SerialName("assist")
    val assist: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("level")
    val level: Int,
    @SerialName("name")
    val name: String,
    @SerialName("nameID")
    val nameID: Int,
    @SerialName("tracks")
    val tracks: List<Track>,
    @SerialName("type")
    val type: Int,
)

@Serializable
data class Track(
    @SerialName("mid")
    val mid: Int,
    @SerialName("mty")
    val mty: Int,
    @SerialName("no")
    val no: Int,
)
