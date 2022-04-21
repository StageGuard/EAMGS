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

package me.stageguard.eamuse.server.router

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.packet.EAGRequestPacket
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

internal object EACoin : RouterModule("eacoin") {
    override val routers: Set<RouteHandler>
        get() = setOf(Checkin, Consume, Checkout)

    @RouteModel
    object Checkin : RouteHandler("checkin") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
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
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            return KXmlBuilder.create("response")
                .e("eacoin")
                .u8("acstatus", 0).up()
                .u8("autocharge", 0).up()
                .s32("balance", 114514)
        }
    }

    @RouteModel
    object Checkout : RouteHandler("checkout") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            return KXmlBuilder.create("response").e("eacoin")
        }
    }
}
