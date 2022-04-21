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

internal object PCBEvent : RouterModule("pcbevent") {
    override val routers: Set<RouteHandler>
        get() = setOf(Put)

    @RouteModel
    object Put : RouteHandler("put") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            //TODO: Save pcb info

            return KXmlBuilder.create("response")
                .e("pcvevent").a("status", "0")
        }
    }

}
