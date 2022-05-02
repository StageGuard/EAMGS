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

package me.stageguard.eamuse.game.sdvx6.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.json

@Serializable
data class AppealCardListDTO(
    val data: Map<Int, AppealCard>,
    // identifier
    val result: Int = 0,
)

@Serializable
data class AppealCard(
    val name: String,
    val texture: String,
)

@Serializable
data class ChatStampListDTO(
    val data: Map<Int, String>,
    // identifier
    val result: Int = 0,
)

@Serializable
data class NemsysListDTO(
    val data: Map<Int, String>,
    // identifier
    val result: Int = 0,
)

@Serializable
data class AkaNameListDTO(
    val data: Map<Int, String>,
    // identifier
    val result: Int = 0,
)


object GameDataList {
    object GetAppealCards : SDVX6APIHandler("get_appeal_cards", "data/ap_card") {
        private val lazyEvaluated by lazy {
            sdvx6AppealCards.map { (id, card) -> id to AppealCard(card.title, card.texture) }.toMap()
        }

        override suspend fun handle0(refId: String, request: FullHttpRequest): String {
            return try {
                return json.encodeToString(AppealCardListDTO(lazyEvaluated))
            } catch (ex: Exception) {
                apiError("ERR:$ex")
            }
        }
    }

    object GetChatStamps : SDVX6APIHandler("get_chat_stamps", "data/chat_stamp") {
        private val lazyEvaluated by lazy {
            sdvx6ChatStamp.map { (id, s) -> id to s.path }.toMap()
        }

        override suspend fun handle0(refId: String, request: FullHttpRequest): String {
            return try {
                return json.encodeToString(ChatStampListDTO(lazyEvaluated))
            } catch (ex: Exception) {
                apiError("ERR:$ex")
            }
        }
    }

    object GetNemsys : SDVX6APIHandler("get_nemsys", "data/nemsys") {
        private val lazyEvaluated by lazy {
            sdvx6Nemsys.map { (id, n) -> id to n.texture }.toMap()
        }

        override suspend fun handle0(refId: String, request: FullHttpRequest): String {
            return try {
                return json.encodeToString(NemsysListDTO(lazyEvaluated))
            } catch (ex: Exception) {
                apiError("ERR:$ex")
            }
        }
    }

    object GetAkaName : SDVX6APIHandler("get_akaname", "data/akaname") {
        private val lazyEvaluated by lazy {
            sdvx6AkaNames.map { (id, a) -> id to a.word }.toMap()
        }

        override suspend fun handle0(refId: String, request: FullHttpRequest): String {
            return try {
                return json.encodeToString(AkaNameListDTO(lazyEvaluated))
            } catch (ex: Exception) {
                apiError("ERR:$ex")
            }
        }
    }
}
