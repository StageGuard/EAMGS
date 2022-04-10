@file:Suppress("DuplicatedCode")

package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.buttongames.butterflycore.xml.kbinxml.firstChild
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.game.sdvx6.model.UserProfile
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteModel
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import org.w3c.dom.Element
import kotlin.properties.Delegates
import kotlin.random.Random

@RouteModel(SDVX6_20210831, SDVX6_20210830, SDVX6_20211020, SDVX6_20211124, SDVX6_20220214, SDVX6_20220308)
object New : SDVX6RouteHandler("new") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val refId = gameNode.childNodeValue("refid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val name = gameNode.childNodeValue("name") ?: "GUEST"

        Database.query { db ->
            db.sequenceOf(UserProfileTable).find { it.refId eq refId }.run {
                if (this != null) throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
            }
        } ?: throw InvalidRequestException(HttpResponseStatus.INTERNAL_SERVER_ERROR)

        val generatedId = Database.query { db ->
            val ids = db.sequenceOf(UserProfileTable).map { it.id }.toSet()
            var generated by Delegates.notNull<Int>()
            do {
                generated = Random.Default.nextInt(10000000, 99999999)
            } while (generated in ids)
            generated
        } ?: throw InvalidRequestException(HttpResponseStatus.INTERNAL_SERVER_ERROR)

        UserProfileTable.insert(UserProfile {
            this.refId = refId
            id = generatedId
            this.name = name
            appeal = 0
            akaname = 0
            blocks = 0
            packets = 0
            arsOption = 0
            drawAdjust = 0
            earlyLateDisp = 0
            effCLeft = 0
            effCRight = 1
            gaugeOption = 0
            hiSpeed = 0
            laneSpeed = 0
            narrowDown = 0
            notesOption = 0
            blasterCount = 0
            blasterEnergy = 0
            extrackEnergy = 0
            bgm = 0
            subbg = 0
            nemsys = 0
            stampA = 0
            stampB = 0
            stampC = 0
            stampD = 0
            headphone = 0
            musicID = 0
            musicType = 0
            sortType = 0
            expPoint = 0
            mUserCnt = 0
        })

        return createGameResponseNode().u8("result", 0)
    }
}