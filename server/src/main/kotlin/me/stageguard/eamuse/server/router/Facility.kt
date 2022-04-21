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

internal object Facility : RouterModule("facility") {
    override val routers: Set<RouteHandler>
        get() = setOf(Get)

    @RouteModel
    object Get : RouteHandler("get") {
        override suspend fun handle(packet: EAGRequestPacket): BaseXMLBuilder {
            return KXmlBuilder.create("response")
                .e("facility")
                .e("location")
                .str("id", "ea").up()
                .str("country", "AX").up()
                .str("region", "1").up()
                .str("name", "StageGodEAM").up()
                .u8("type", 0).up()
                .str("countryname", "Japan").up()
                .str("countryjname", "不明").up()
                .str("regionname", "Asia").up()
                .str("regionjname", "下北泽").up()
                .str("customercode", "AXUSR").up()
                .str("customercode", "AXCPY").up()
                .s32("latitude", 114).up()
                .s32("longitude", 514).up()
                .u8("accuracy", 0).up(2)
                .e("line")
                .str("id", ".").up()
                .u8("class", 0).up(2)
                .e("portfw")
                .ip("globalip", config.server.globalHost).up()
                .u16("globalport", config.server.matchingPort).up()
                .u16("privateport", config.server.matchingPort).up(2)
                .e("public")
                .u8("flag", 1).up()
                .str("name", "StageGodEAMPublic").up()
                .str("latitude", "0").up()
                .str("longitude", "0").up(2)
                .e("share")
                .e("eacoin")
                .s32("notchamount", 0).up()
                .s32("notchcount", 0).up()
                .s32("supplylimit", 114514).up(2)
                .e("url")
                .str("eapass", "https://eagate.573.jp/").up()
                .str("arcadefan", "https://eagate.573.jp/").up()
                .str("konaminetdx", "https://eagate.573.jp/").up()
                .str("konamiid", "https://eagate.573.jp/").up()
                .str("eagate", "https://eagate.573.jp/")
        }
    }
}
