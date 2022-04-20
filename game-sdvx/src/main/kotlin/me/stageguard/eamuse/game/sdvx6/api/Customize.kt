package me.stageguard.eamuse.game.sdvx6.api

import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.serialization.encodeToString
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.SDVX6APIHandler
import me.stageguard.eamuse.game.sdvx6.model.UserProfileTable
import me.stageguard.eamuse.json
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
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
    // identifier
    val result: Int = 0,
)

class Customize {
    object Get : SDVX6APIHandler("customize_get","custom/get") {
        override suspend fun handle0(refId: String, request: FullHttpRequest): String {
            val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
                ?: return apiError("USER_NOT_FOUND")
            return json.encodeToString(CustomizeData(
                profile.name, profile.appeal, profile.akaname, profile.nemsys, profile.bgm, profile.subbg,
                listOf(profile.stampA, profile.stampB, profile.stampC, profile.stampD)
            ))

        }
    }

    object Update : SDVX6APIHandler("customize_update", "custom/update") {
        override suspend fun handle0(refId: String, request: FullHttpRequest): String {
            val data = try {
                json.decodeFromString<CustomizeData>(request.content().toString(Charset.defaultCharset()))
            } catch (ex: SerializationException) { return apiError("ILLEGAL_PARAM") }
            require(data.stamp.size == 4) { return apiError("ILLEGAL_PARAM") }

            val profile = Database.query { db -> db.sequenceOf(UserProfileTable).find { it.refId eq refId } }
                ?: return apiError("USER_NOT_FOUND")

            profile.name = data.name
            profile.appeal = data.appeal
            profile.akaname = data.akaName
            profile.nemsys = data.nemsys
            profile.bgm = data.bgm
            profile.subbg = data.subbg
            data.stamp.run {
                profile.stampA = get(0)
                profile.stampB = get(1)
                profile.stampC = get(2)
                profile.stampD = get(3)
            }
            profile.flushChanges()

            return """{"result": 0}"""
        }
    }
}
