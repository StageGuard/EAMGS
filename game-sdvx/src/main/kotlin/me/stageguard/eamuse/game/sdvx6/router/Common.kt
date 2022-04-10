package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.config
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.server.RouteModel
import org.w3c.dom.Element

val songsToUnlock by lazy {
    val songs: MutableList<Pair<Int, Int>> = mutableListOf()
    sdvx6MusicLibrary.value.forEach { (mid, music) ->
        music.difficulties.forEach { difficulty ->
            if (difficulty.limited != 3) {
                songs.add(mid to difficulty.type)
            }
        }
    }
    songs.toList()
}

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124, SDVX6_20220214, SDVX6_20220308)
object Common : SDVX6RouteHandler("common") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        // events
        var resp = createGameResponseNode().e("event")
        sdvx6Events.forEach { ev ->
            resp = resp.e("info").str("event_id", ev).up(2)
        }
        resp = resp.up()

        // extend
        resp = resp.e("extend").up()

        // music library
        resp = resp.e("music_limited")
        if (config.sdvx.unlockAllSongs) {
            songsToUnlock.forEach { (mid, type) ->
                resp = resp.e("info")
                    .s32("music_id", mid).up()
                    .u8("music_type", type).up()
                    .u8("limited", 3).up()
                resp = resp.up()
            }
        }
        resp = resp.up()

        // skill course
        resp = resp.e("skill_course")
        sdvx6SkillCourseSessions.value.forEach { season ->
            season.courses.forEach { course ->
                resp = resp.e("info")
                    .s32("season_id", season.id).up()
                    .str("season_name", season.name).up()
                    .bool("season_new_flg", season.isNew == 1).up()
                    .s16("course_type", course.type).up()
                    .s16("course_id", course.id).up()
                    .str("course_name", course.name).up()
                    .s16("skill_level", course.level).up()
                    .s16("skill_name_id", course.nameID).up()
                    .bool("matching_assist", course.assist == 1).up()
                    .s32("clear_rate", 5000).up()
                    .u32("avg_score", 15000000).up()
                    course.tracks.forEach { track ->
                        resp = resp.e("track")
                            .s16("track_no", track.no).up()
                            .s32("music_id", track.mid).up()
                            .s8("music_type", track.mty).up()
                        resp = resp.up()
                    }
                resp = resp.up()
            }
        }
        resp = resp.up()

        return resp
    }
}