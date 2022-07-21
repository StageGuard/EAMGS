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

package me.stageguard.eamuse.game.sdvx6.router

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.childNodeValue
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.game.sdvx6.model.Item
import me.stageguard.eamuse.game.sdvx6.model.ItemTable
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteModel
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.w3c.dom.Element

@RouteModel(SDVX6_20210831,
    SDVX6_20210830,
    SDVX6_20211020,
    SDVX6_20211124,
    SDVX6_20220214,
    SDVX6_20220308,
    SDVX6_20220425, SDVX6_20220628)
object Buy : SDVX6RouteHandler("buy") {
    override suspend fun handle(gameNode: Element): KXmlBuilder {
        val refId = gameNode.childNodeValue("refid")
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
            ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val currencyType = gameNode.childNodeValue("currency_type")?.toInt().run {
            if (this != null) {
                if (this == 1) "blocks" else "packets"
            } else throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        }
        val cost = XmlUtils.strAtPath(gameNode, "/item/price")?.split(" ")?.sumOf { it.toInt() } ?: 0

        var consumed = false
        if (currencyType == "blocks") {
            val earnedBlocks = gameNode.childNodeValue("earned_gamecoin_block")?.toLong() ?: 0
            if (profile.blocks - (earnedBlocks - cost) > 0) {
                profile.blocks -= earnedBlocks - cost
                profile.flushChanges()
                consumed = true
            }
        } else {
            val earnedPackets = gameNode.childNodeValue("earned_gamecoin_packet")?.toLong() ?: 0
            if (profile.packets - (earnedPackets - cost) > 0) {
                profile.packets -= earnedPackets - cost
                profile.flushChanges()
                consumed = true
            }
        }

        return if (consumed) {
            val itemType = XmlUtils.strAtPath(gameNode, "/item/item_type")?.split(" ")?.map { it.toInt() } ?: listOf()
            val itemId = XmlUtils.strAtPath(gameNode, "/item/item_id")?.split(" ")?.map { it.toLong() } ?: listOf()
            val itemParam = XmlUtils.strAtPath(gameNode, "/item/param")?.split(" ")?.map { it.toLong() } ?: listOf()

            val items = itemType.zip(itemId).zip(itemParam).map {
                Item { this.refId = refId; type = it.first.first; id = it.first.second; param = it.second }
            }

            val existItems = Database.query { db -> db.sequenceOf(ItemTable).filter { it.refId eq refId } }
                ?.toList() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

            val (toUpdate, toInsert) = mutableListOf<Item>() to mutableListOf<Item>()

            items.forEach { item ->

                val find = existItems.find { it.id == item.id && it.type == item.type }
                if (find != null) {
                    find.param = item.param
                    toUpdate.add(find)
                } else {
                    toInsert.add(item)
                }
            }

            if (toInsert.isNotEmpty()) ItemTable.batchInsert(toInsert)
            if (toUpdate.isNotEmpty()) ItemTable.batchUpdate {
                toUpdate.forEach { u ->
                    item {
                        set(ItemTable.param, u.param)
                        where { ItemTable.refId eq u.refId and (ItemTable.type eq u.type) and (ItemTable.id eq u.id) }
                    }
                }
            }

            createGameResponseNode()
                .u32("gamecoin_packet", profile.packets).up()
                .u32("gamecoin_block", profile.blocks)
        } else {
            createGameResponseNode()
        }
    }
}
