package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.game.sdvx6.model.PlayRecordTable
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.game.sdvx6.SDVX6RouteHandler
import me.stageguard.eamuse.game.sdvx6.SDVX6_20210830
import me.stageguard.eamuse.game.sdvx6.SDVX6_20210831
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.w3c.dom.Element

@RouteModel(SDVX6_20210831, SDVX6_20210830)
object HiScore : SDVX6RouteHandler("hiscore") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val allRecords = Database.query { db -> db.sequenceOf(PlayRecordTable).toList() } ?: listOf()

        val profiles = (Database.query { db -> db.sequenceOf(UserProfileTable).toList() } ?: listOf()).groupBy { it.refId }
        val recordMaxScore = allRecords.groupBy { "${it.mid}:${it.type}" }.map {
            it.value.maxByOrNull { s -> s.score }
        }.filterNotNull()

        var resp = createGameResponseNode().e("sc")

        recordMaxScore.forEach { r ->
            val profileIdCode = profiles[r.refId]?.first()?.id?.toString().run {
                if (this != null) "${take(4)}-${takeLast(4)}" else "0000-0000"
            }
            val profileName = profiles[r.refId]?.first()?.name ?: "DELETED"

            resp = resp.e("d")
                .u32("id", r.mid).up()
                .u32("ty", r.type).up()
                .str("a_sq", profileIdCode).up()
                .str("a_nm", profileName).up()
                .u32("a_sc", r.score).up()
                .str("l_sq", profileIdCode).up()
                .str("l_nm", profileName).up()
                .u32("l_sc", r.score).up()
            resp = resp.up()
        }

        return resp
    }
}