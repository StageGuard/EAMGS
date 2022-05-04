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

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.server.RouteModel
import org.w3c.dom.Element

@RouteModel(SDVX6_20210831,
    SDVX6_20210830,
    SDVX6_20211020,
    SDVX6_20211124,
    SDVX6_20220214,
    SDVX6_20220308,
    SDVX6_20220425)
object Longue : SDVX6RouteHandler("lounge") {
    private const val INTERVAL = 30.toLong()

    override suspend fun handle(gameNode: Element): KXmlBuilder {
        return createGameResponseNode().u32("interval", INTERVAL)
    }
}
