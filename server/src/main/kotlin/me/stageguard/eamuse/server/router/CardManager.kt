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

import com.buttongames.butterflycore.cardconv.encodeCardId
import com.buttongames.butterflycore.util.StringUtils
import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCard
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.packet.EAGRequestPacket
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.properties.Delegates
import kotlin.reflect.full.findAnnotation

internal object CardManager : RouterModule("cardmng") {
    private val checkers: MutableList<ProfileChecker> = mutableListOf()

    override val routers: Set<RouteHandler> =
        setOf(Inquire, GetRefId, AuthPass, BindModel)

    fun addChecker(c: ProfileChecker) {
        checkers.add(c)
    }

    val currentTimeStamp
        get() = LocalDateTime.now().let {
            it.atZone(ZoneId.systemDefault()).run {
                it.toString() + offset.toString()
            }
        }

    @RouteModel
    internal object Inquire : RouteHandler("inquire") {
        private val LOGGER = LoggerFactory.getLogger(Inquire::class.java)

        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            val cardnmgNode = XmlUtils.nodeAtPath(packet.content, "/cardmng")
            val cardNfcId = cardnmgNode.attributes.getNamedItem("cardid").nodeValue

            val cardInfo = Database.query { it.sequenceOf(EAmuseCardTable).find { c -> c.cardNFCId eq cardNfcId } }

            val resp = KXmlBuilder.create("response").e("cardmng")
            return if (cardInfo != null) {
                val profileExists = checkers.run {
                    forEach { c ->
                        val modelAnno = c::class.findAnnotation<RouteModel>()
                        if (modelAnno == null) {
                            LOGGER.warn(
                                "RouteHandler ${c::class.simpleName} " +
                                        "doesn't have annotation ${RouteModel::class.simpleName}"
                            )
                        } else {
                            // game specific model
                            modelAnno.name.forEach { singleModel ->
                                val (reqModel, reqVersion) = packet.model.split(":").run { first() to last() }
                                val (routeModel, routeVersion) = singleModel.split(":").run { first() to last() }

                                if (reqModel == routeModel && reqVersion.startsWith(routeVersion)) {
                                    return@run c.check(cardInfo).also {
                                        if (it) {
                                            cardInfo.lastPlayTime = currentTimeStamp
                                            cardInfo.flushChanges()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    false
                }

                resp.a("binded", if (profileExists) "1" else "0")
                    .a("dataid", cardInfo.refId)
                    .a("ecflag", "1")
                    .a("expired", "0")
                    .a("newflag", "0")
                    .a("refid", cardInfo.refId)
                    .a("status", "0")
            } else {
                resp.a("status", "112")
            }
        }
    }

    @RouteModel
    object GetRefId : RouteHandler("getrefid") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            val cardmngNode = XmlUtils.nodeAtPath(packet.content, "/cardmng")
            val cardNfcId = cardmngNode.attributes.getNamedItem("cardid").nodeValue
            val pin = cardmngNode.attributes.getNamedItem("passwd").nodeValue

            val queryCardInfo = Database.query { it.sequenceOf(EAmuseCardTable).find { c -> c.cardNFCId eq cardNfcId } }
            if (queryCardInfo != null) throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

            val generatedRefId = Database.query { db ->
                var generated by Delegates.notNull<String>()
                do {
                    generated = StringUtils.getRandomHexString(16)
                } while (db.sequenceOf(EAmuseCardTable).find { c -> c.cardNFCId eq cardNfcId } != null)
                generated
            } ?: throw InvalidRequestException(HttpResponseStatus.INTERNAL_SERVER_ERROR)

            EAmuseCardTable.insert(EAmuseCard {
                this.cardNFCId = cardNfcId
                refId = generatedRefId
                this.pin = pin.toInt()
                displayId = encodeCardId(cardNfcId)
                registerTime = currentTimeStamp
                lastPlayTime = currentTimeStamp
            })

            return KXmlBuilder.create("response").e("cardmng")
                .a("dataid", generatedRefId)
                .a("refid", generatedRefId)
                .a("status", "0")
        }
    }

    @RouteModel
    object AuthPass : RouteHandler("authpass") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            val cardmngNode = XmlUtils.nodeAtPath(packet.content, "/cardmng")
            val pin = cardmngNode.attributes.getNamedItem("pass").nodeValue
            val refId = cardmngNode.attributes.getNamedItem("refid").nodeValue

            val cardInfo = Database.query { it.sequenceOf(EAmuseCardTable).find { c -> c.refId eq refId } }

            val status = if (cardInfo == null) 112 else if (cardInfo.pin == pin.toInt()) 0 else 116

            return KXmlBuilder.create("response")
                .e("cardmng")
                .a("status", status.toString())
        }
    }

    @RouteModel
    object BindModel : RouteHandler("bindmodel") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            val cardmngNode = XmlUtils.nodeAtPath(packet.content, "/cardmng")
            val refid = cardmngNode.attributes.getNamedItem("refid").nodeValue

            return KXmlBuilder.create("response")
                .e("cardmng").a("dataid", refid)
        }

    }
}

interface ProfileChecker {
    suspend fun check(cardInfo: EAmuseCard): Boolean
}
