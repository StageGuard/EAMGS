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
import org.intellij.lang.annotations.Language
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.nio.charset.Charset

abstract class SDVX6APIHandler(path: String) : AbstractAPIHandler(path) {
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
            uriParameters(request.uri()) ?.get("cardid")
                ?: return Result.failure(Exception(apiError("CARDID")))
        }
        val refId = Database.query { db -> db.sequenceOf(EAmuseCardTable).find { c -> c.cardNFCId eq cardId } }
            ?.refId ?: return Result.failure(Exception(apiError("REFID")))

        return Result.success(refId)
    }

    @Language("JSON")
    fun apiError(reason: String) = """{"result": -1, "message": "$reason"}"""
}
