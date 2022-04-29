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

package me.stageguard.eamuse.server.api

import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpMethod
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.database.model.EAmuseCardTable
import me.stageguard.eamuse.json
import me.stageguard.eamuse.server.AbstractAPIHandler
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.nio.charset.Charset

@Serializable
internal data class VerifyCardRequest(
    val cardId: String,
    val pin: Int,
)

@Serializable
internal data class VerifyCardResponse(
    val refId: String,
    val result: Int = 0, // identifier
)

internal object VerifyCardId : AbstractAPIHandler("verify_card_id", "verify") {
    private val CARD_ID_REGEX = Regex("^[A-Z\\d]{16}\$")
    override suspend fun handle(request: FullHttpRequest): String {
        if (request.method() != HttpMethod.POST) return apiError("ILLEGAL_REQUEST")
        val data = try {
            json.decodeFromString<VerifyCardRequest>(request.content().toString(Charset.defaultCharset()))
        } catch (ex: SerializationException) {
            return apiError("ILLEGAL_PARAM")
        }

        if (!data.cardId.matches(CARD_ID_REGEX)) return apiError("INVALID_CARD_ID")
        if (data.pin > 9999) return apiError("INVALID_PIN")

        val user = Database.query { db ->
            db.sequenceOf(EAmuseCardTable).find {
                it.cardNFCId eq data.cardId and (it.pin eq data.pin)
            }
        } ?: return apiError("USER_NOT_FOUND")

        return json.encodeToString(VerifyCardResponse(user.refId))
    }
}
