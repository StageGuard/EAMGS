package me.stageguard.eamuse.server.router

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.packet.RequestPacket
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

object EACoin : RouteCollection("eacoin") {
    override val routers: Set<RouteHandler>
        get() = setOf(Checkin, Consume, Checkout)

    @RouteModel
    object Checkin : RouteHandler("checkin") {
        override suspend fun handle(packet: RequestPacket): BaseXMLBuilder {
            val cardId = XmlUtils.strAtPath(packet.content, "/eacoin/cardid")
            val pin = XmlUtils.strAtPath(packet.content, "/eacoin/passwd")

            val cardInfo = Database.query { db -> db.sequenceOf(EAmuseCardTable).find { it.cardNFCId eq cardId } }
                ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

            if (cardInfo.pin != pin.toInt()) throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

            return KXmlBuilder.create("response")
                .e("eacoin")
                    .s16("sequence", 1).up()
                    .u8("acstatus", 0).up()
                    .str("acid", cardId).up()
                    .str("acname", cardId).up()
                    .s32("balance", 114514).up()
                    .str("sessid", cardId)
        }
    }

    @RouteModel
    object Consume : RouteHandler("consume") {
        override suspend fun handle(packet: RequestPacket): BaseXMLBuilder {
            return KXmlBuilder.create("response")
                .e("eacoin")
                .u8("acstatus", 0).up()
                .u8("autocharge", 0).up()
                .s32("balance", 114514)
        }
    }

    @RouteModel
    object Checkout : RouteHandler("checkout") {
        override suspend fun handle(packet: RequestPacket): BaseXMLBuilder {
            return KXmlBuilder.create("response").e("eacoin")
        }
    }
}