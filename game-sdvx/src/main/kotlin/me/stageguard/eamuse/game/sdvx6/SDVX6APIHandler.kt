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

package me.stageguard.eamuse.game.sdvx6

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.game.sdvx6.data.CardIdDTO
import me.stageguard.eamuse.json
import me.stageguard.eamuse.server.AbstractAPIHandler
import me.stageguard.eamuse.uriParameters
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.nio.charset.Charset

abstract class SDVX6APIHandler(name: String, path: String) : AbstractAPIHandler(name, "${SDVX6.id}/$path") {
    abstract suspend fun handle0(refId: String, request: FullHttpRequest): String

    final override suspend fun handle(request: FullHttpRequest): String {
        val refId = validate(request)
        return handle0(refId.getOrElse { return it.message!! }, request)
    }

    private suspend fun validate(request: FullHttpRequest): Result<String> {
        val cardId = try {
            json.decodeFromString<CardIdDTO>(
                request.content().toString(Charset.forName("utf-8"))
            ).cardId
        } catch (ex: SerializationException) {
            request.uriParameters["cardid"]
                ?: return Result.failure(Exception(apiError("CARDID")))
        }
        val refId = Database.query { db -> db.sequenceOf(EAmuseCardTable).find { c -> c.cardNFCId eq cardId } }
            ?.refId ?: return Result.failure(Exception(apiError("REFID")))

        return Result.success(refId)
    }
}
