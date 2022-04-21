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

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import me.stageguard.eamuse.server.RouteHandler
import me.stageguard.eamuse.server.RouteModel
import me.stageguard.eamuse.server.RouterModule
import org.w3c.dom.Element

class IIDXRouter(
    private val iidxModule: String,
    private val iidxRouters: Set<RouteHandler> = setOf(),
    private vararg val defaults: String,
) : RouterModule("IIDX29$iidxModule") {
    override val routers: Set<RouteHandler>
        get() = iidxRouters.plus(generate(*defaults))

    private fun generate(vararg method: String): Array<RouteHandler> {
        return method.map { m ->
            @RouteModel(LDJ20211013) object : IIDXRouteHandler(iidxModule, m) {
                override suspend fun handle(node: Element): KXmlBuilder {
                    return KXmlBuilder.create("response").e(module).a("status", "0")
                }
            }
        }.toTypedArray()
    }
}
