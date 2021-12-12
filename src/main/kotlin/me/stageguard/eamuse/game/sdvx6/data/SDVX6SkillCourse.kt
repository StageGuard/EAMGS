package me.stageguard.eamuse.game.sdvx6.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SDVX6SkillCourse(
    @SerialName("sessions")
    val sessions: List<SkillCourseSeason>
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
    val name: String
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
    val type: Int
)
@Serializable
data class Track(
    @SerialName("mid")
    val mid: Int,
    @SerialName("mty")
    val mty: Int,
    @SerialName("no")
    val no: Int
)