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

package me.stageguard.eamuse.game.iidx

import com.buttongames.butterflycore.xml.XmlUtils
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.packet.EAGRequestPacket
import org.w3c.dom.Element

abstract class IIDXRouteHandler(private val module: String, override val method: String) : RouteHandler(method) {
    abstract suspend fun handle(node: Element): KXmlBuilder

    override suspend fun handle(packet: EAGRequestPacket): KXmlBuilder {
        return handle(XmlUtils.nodeAtPath(packet.content, "/IIDX29$module") as Element)
    }

    fun createResponseNode(): KXmlBuilder = KXmlBuilder.create("response")
        .e("IIDX29$module").a("status", "0")
}

abstract class IIDXPCRouteHandler(method: String) : IIDXRouteHandler("pc", method)
abstract class IIDXMusicRouteHandler(method: String) : IIDXRouteHandler("music", method)
abstract class IIDXGradeRouteHandler(method: String) : IIDXRouteHandler("grade", method)
abstract class IIDXShopRouteHandler(method: String) : IIDXRouteHandler("shop", method)
abstract class IIDXRankingRouteHandler(method: String) : IIDXRouteHandler("ranking", method)
abstract class IIDXGameSystemRouteHandler(method: String) : IIDXRouteHandler("gameSystem", method)
