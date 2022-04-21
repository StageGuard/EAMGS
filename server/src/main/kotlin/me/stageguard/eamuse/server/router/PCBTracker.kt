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

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import com.jamesmurty.utils.BaseXMLBuilder
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.config
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.PcbIdTable
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.packet.EAGRequestPacket
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

internal object PCBTracker : RouterModule("pcbtracker") {
    override val routers: Set<RouteHandler>
        get() = setOf(Alive)

    @RouteModel
    object Alive : RouteHandler("alive") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            if (Database.query { db ->
                    db.sequenceOf(PcbIdTable).find { it.pcbId eq packet.pcbId }?.run { true } == true
                } != true)
                throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

            var resp = KXmlBuilder.create("response")
                .e("pcbtracker")
                .a("ecenable", if (config.server.isPaseliEnabled) "1" else "0")

            if (config.server.isMaintenanceMode) {
                resp = resp
                    .a("eclimit", "0")
                    .a("expire", "1200")
                    .a("limit", "0")
                    .a("status", "0")
                    .a("time", (System.currentTimeMillis() / 1000).toString())
            }

            return resp
        }

    }
}
