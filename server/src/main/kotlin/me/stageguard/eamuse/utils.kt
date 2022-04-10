package me.stageguard.eamuse

import com.buttongames.butterflycore.xml.kbinxml.firstChild
import org.w3c.dom.Element
import java.net.URLDecoder
import java.nio.charset.Charset
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

fun uriParameters(uri: String) =
    URLDecoder.decode(uri, Charset.defaultCharset()).split("?").runCatching {
        val path = getOrNull(0)
        val parameters = getOrNull(1)
        check(path != null && (path.isNotEmpty() || path == "/") && parameters != null)

        mapOf(*parameters.split("&").map {
            it.split("=").run p@ { this@p[0].lowercase() to this@p[1] }
        }.toTypedArray())
    }.getOrNull()

fun Element.childNodeValue(name: String) = firstChild(name) ?.firstChild ?.nodeValue