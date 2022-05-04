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
import io.netty.handler.codec.http.HttpMethod
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.*
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.json
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.nio.charset.Charset

@Serializable
data class CustomizeData(
    val name: String,
    val appeal: Int,
    val akaName: Int,
    val nemsys: Int,
    val bgm: Int,
    val subbg: Int,
    val stamp: List<Int>,
    val crew: Int,
    // identifier
    val result: Int = 0,
)

class Customize {
    object Get : SDVX6APIHandler("customize_get", "custom/get") {
        override suspend fun handle0(refId: String, request: FullHttpRequest): String {
            val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
                ?: return apiError("USER_NOT_FOUND")
            return json.encodeToString(CustomizeData(
                profile.name, profile.appeal, profile.akaname, profile.nemsys, profile.bgm, profile.subbg,
                listOf(profile.stampA, profile.stampB, profile.stampC, profile.stampD), profile.crew
            ))

        }
    }

    object Update : SDVX6APIHandler("customize_update", "custom/update") {
        private val NAME_REGEX_REV = Regex("[^A-Za-z\\d!\\?#\\\$&\\*-\\.\\s]{1,8}")

        override suspend fun handle0(refId: String, request: FullHttpRequest): String {
            if (request.method() != HttpMethod.POST) return apiError("ILLEGAL_PARAM")
            val data = try {
                json.decodeFromString<CustomizeData>(request.content().toString(Charset.defaultCharset()))
            } catch (ex: SerializationException) {
                return apiError("ILLEGAL_PARAM:$ex")
            }
            require(data.stamp.size == 4) { return apiError("ILLEGAL_PARAM") }

            val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
                ?: return apiError("USER_NOT_FOUND")

            if (data.name.matches(NAME_REGEX_REV)) return apiError("ILLEGAL_NAME")
            profile.name = data.name.run { if (isBlank() || isEmpty()) "SDVX6" else this }
            if (sdvx6AppealCards[data.appeal] == null) return apiError("ILLEGAL_APPEAL")
            profile.appeal = data.appeal
            if (sdvx6AkaNames[data.akaName] == null) return apiError("ILLEGAL_AKANAME")
            profile.akaname = data.akaName
            if (sdvx6Nemsys[data.nemsys] == null) return apiError("ILLEGAL_NEMSYS")
            profile.nemsys = data.nemsys
            profile.bgm = data.bgm
            profile.subbg = data.subbg
            data.stamp.run {
                forEach { if (sdvx6ChatStamp[it] == null) return apiError("ILLEGAL_CHAT_STAMP") }
                profile.stampA = get(0)
                profile.stampB = get(1)
                profile.stampC = get(2)
                profile.stampD = get(3)
            }
            if (sdvx6Crews[data.crew] == null) return apiError("ILLEGAL_CREW")
            profile.crew = data.crew
            profile.flushChanges()

            return ok()
        }
    }
}
