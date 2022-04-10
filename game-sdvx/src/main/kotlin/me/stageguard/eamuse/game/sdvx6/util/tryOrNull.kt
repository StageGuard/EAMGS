package me.stageguard.eamuse.game.sdvx6.util

fun <T : Any, R> T.tryOrNull(block: T.(T) -> R) : R? = try {
    block(this)
} catch (ignored: Exception) {
    ignored.printStackTrace()
    null
}