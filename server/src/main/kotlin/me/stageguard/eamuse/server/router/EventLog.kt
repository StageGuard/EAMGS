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
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.packet.EAGRequestPacket

internal object EventLog : RouterModule("eventlog") {
    override val routers: Set<RouteHandler>
        get() = setOf(Write)

    @RouteModel
    object Write : RouteHandler("write") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            //TODO: save event log

            return KXmlBuilder.create("response")
                .e("eventlog").a("status", "0")
                .s64("gamesession", 1).up()
                .s32("logsendflg", 0).up()
                .s32("logerrlevel", 0).up()
                .s32("evtidnosendflg", 0)
        }
    }
}
