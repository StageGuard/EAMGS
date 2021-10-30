package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.config
import me.stageguard.eamuse.game.sdvx6.SDVX6SkillCourseSessions
import me.stageguard.eamuse.game.sdvx6.SDVX6Events
import me.stageguard.eamuse.game.sdvx6.SDVX6_SONG_COUNT
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.game.sdvx6.SDVX6RouteHandler
import me.stageguard.eamuse.game.sdvx6.SDVX6_20210830
import org.w3c.dom.Element

@RouteModel(SDVX6_20210830)
object Common : SDVX6RouteHandler("common") {
    override suspend fun processGameNode(gameNode: Element): KXmlBuilder {

        // events
        var resp = createGameResponseNode().e("event")
        SDVX6Events.forEach { ev ->
            resp = resp.e("info").str("event_id", ev).up(2)
        }
        resp = resp.up()

        // extend
        resp = resp.e("extend").up()

        // unlock all songs
        resp = resp.e("music_limited")
        if (config.sdvx.unlockAllSongs) {
            repeat(SDVX6_SONG_COUNT) { song ->
                repeat(5) { type ->
                    resp = resp.e("info")
                        .s32("music_id", song + 1).up()
                        .u8("music_type", type).up()
                        .u8("limited", 3).up()
                    resp = resp.up()
                }
            }
        }
        resp = resp.up()

        // skill course
        resp = resp.e("skill_course")
        SDVX6SkillCourseSessions.value.forEach { season ->
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