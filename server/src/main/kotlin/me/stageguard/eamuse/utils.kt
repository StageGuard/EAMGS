package me.stageguard.eamuse

import com.buttongames.butterflycore.xml.kbinxml.firstChild
import io.netty.handler.codec.http.FullHttpRequest
import org.w3c.dom.Element
import java.net.URLDecoder
import java.nio.charset.Charset
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalContracts::class)
inline fun <R> retry(
    n: Int,
    exceptionBlock: (Throwable) -> Unit = { },
    block: (Int) -> R
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
            it.split("=").run p@ { this@p[0].lowercase() to this@p[1] }
        }.toTypedArray())
    }.getOrNull()

fun Element.childNodeValue(name: String) = firstChild(name) ?.firstChild ?.nodeValue

@OptIn(ObsoleteCoroutinesApi::class)
private val context = newSingleThreadContext("SV6ResExport")

fun getResourceOrExport(game: String, f: String, exporter: () -> InputStream?): InputStream? {
    val path = File("data/$game/")
    if (!path.exists()) path.mkdirs()
    val res = File("${path.path}/$f")
    return if (res.exists() and res.isFile) {
        res.inputStream()
    } else {
        runBlocking(context) { launch(Dispatchers.IO) {
            exporter().use { i -> if (i != null) {
                res.createNewFile()
                FileOutputStream(res).use { o -> o.write(i.readAllBytes()) }
            } }
        } }
        exporter()
    }
}

fun <T, R> T.tryOrNull(block: T.() -> R): R? = try {
    block(this)
} catch (_: Exception) { null }
