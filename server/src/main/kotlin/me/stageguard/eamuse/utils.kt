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

package me.stageguard.eamuse

import com.buttongames.butterflycore.xml.kbinxml.firstChild
import io.netty.handler.codec.http.FullHttpRequest
import kotlinx.coroutines.*
import org.w3c.dom.Element
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URLDecoder
import java.nio.charset.Charset
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <R> retry(
    n: Int,
    exceptionBlock: (Throwable) -> Unit = { },
    block: (Int) -> R,
): Result<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }
    require(n >= 1) { "param n for retryCatching must not be negative" }
    var timesRetried = 0
    var exception: Throwable? = null
    repeat(n) {
        try {
            return Result.success(block(timesRetried++))
        } catch (e: Throwable) {
            exceptionBlock(e)
            exception?.addSuppressed(e)
            exception = e
        }
    }
    return Result.failure(exception!!)
}

val FullHttpRequest.uriParameters
    get() = URLDecoder.decode(uri(), Charset.defaultCharset()).split("?").runCatching {
        val path = getOrNull(0)
        val parameters = getOrNull(1)
        check(path != null && (path.isNotEmpty() || path == "/") && parameters != null)

        mapOf(*parameters.split("&").map {
            it.split("=").run p@{ this@p[0].lowercase() to this@p[1] }
        }.toTypedArray())
    }.getOrElse { mapOf() }

fun Element.childNodeValue(name: String) = firstChild(name)?.firstChild?.nodeValue

@OptIn(ObsoleteCoroutinesApi::class)
private val context = newSingleThreadContext("SV6ResExport")

fun getResourceOrExport(game: String, f: String, exporter: () -> InputStream?): InputStream? {
    val path = File("data/$game/")
    if (!path.exists()) path.mkdirs()
    val res = File("${path.path}/$f")
    return if (res.exists() and res.isFile) {
        res.inputStream()
    } else {
        runBlocking(context) {
            launch(Dispatchers.IO) {
                exporter().use { i ->
                    if (i != null) {
                        res.createNewFile()
                        FileOutputStream(res).use { o -> o.write(i.readAllBytes()) }
                    }
                }
            }
        }
        exporter()
    }
}

fun <T, R> T.tryOrNull(block: T.() -> R): R? = try {
    block(this)
} catch (_: Exception) {
    null
}
