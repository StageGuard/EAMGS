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
import me.stageguard.eamuse.config
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.RouterModule
import me.stageguard.eamuse.server.packet.EAGRequestPacket

internal object Service : RouterModule("services") {
    override val routers: Set<RouteHandler>
        get() = setOf(Get)

    @Suppress("HttpUrlsUsage")
    val globalServerHost by lazy {
        if (config.server.globalPort == 80) {
            "http://${config.server.globalHost}"
        } else {
            "http://${config.server.globalHost}:${config.server.globalPort}"
        }
    }
    val serviceUrls = mapOf(
        "cardmng" to globalServerHost,
        "facility" to globalServerHost,
        "message" to globalServerHost,
        "numbering" to globalServerHost,
        "package" to globalServerHost,
        "pcbevent" to globalServerHost,
        "pcbtracker" to globalServerHost,
        "pkglist" to globalServerHost,
        "posevent" to globalServerHost,
        "userdata" to globalServerHost,
        "userid" to globalServerHost,
        "eacoin" to globalServerHost,
        "local" to globalServerHost,
        "local2" to globalServerHost,
        "lobby" to globalServerHost,
        "lobby2" to globalServerHost,
        "dlstatus" to globalServerHost,
        "netlog" to globalServerHost,
        "sidmgr" to globalServerHost,
        "globby" to globalServerHost,
        "ntp" to "ntp://pool.ntp.org/",
        "keepalive" to "http://127.0.0.1/core/keepalive?pa=127.0.0.1&ia=127.0.0.1&ga=127.0.0.1&ma=127.0.0.1&t1=2&t2=10"
    )

    @RouteModel
    object Get : RouteHandler("get") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            var resp = KXmlBuilder.create("response")
                .e("services")
                .a("expire", "10800")
                .a("method", "get")
                .a("mode", "operation")
                .a("status", "0")

            for ((key, value) in serviceUrls) {
                resp = resp.e("item").a("name", key).a("url", value).up()
            }

            return resp
        }
    }
}
