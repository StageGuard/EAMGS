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

package me.stageguard.eamuse.server

import io.netty.handler.codec.http.FullHttpRequest
import org.intellij.lang.annotations.Language

interface APIHandler {
    suspend fun handle(request: FullHttpRequest): String
}

abstract class AbstractAPIHandler(val name: String, val path: String) : APIHandler {
    @Language("JSON")
    protected fun apiError(reason: String) = """{"result": -1, "message": "$reason"}"""
}
