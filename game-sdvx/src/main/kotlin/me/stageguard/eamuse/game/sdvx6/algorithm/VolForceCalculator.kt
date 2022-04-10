package me.stageguard.eamuse.game.sdvx6.algorithm

import me.stageguard.eamuse.game.sdvx6.model.PlayRecord
import me.stageguard.eamuse.game.sdvx6.sdvx6MusicLibrary
import kotlin.math.round

fun calculateForce(record: PlayRecord, fixed: Boolean = true) = run {
    val music = sdvx6MusicLibrary.value[record.mid.toInt()] ?: return@run 0.0
    val level = music.difficulties.find { it.type == record.type.toInt() } ?.difficulty ?: return@run 0.0
    (level * (record.score / 10000000.0) * grade(record.score) * clear(record.clear.toInt()) * 2.0).toFixed(if (fixed) 1 else 3)
}

fun Double.toFixed(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

private fun clear(type: Int) = when (type) {
    1 -> 0.5
    2 -> 1.0
    3 -> 1.02
    4 -> 1.05
    else -> 1.10
}

private fun grade(score: Long) = when (score) {
    in 9900000..10000000 -> 1.05
    in 9800000..9899999 -> 1.02
    in 9700000..9799999 -> 1.00
    in 9500000..9699999 -> 0.97
    in 9300000..9499999 -> 0.94
    in 9000000..9299999 -> 0.91
    in 8700000..8999999 -> 0.88
    in 7500000..8699999 -> 0.85
    in 6500000..7499999 -> 0.82
    else -> 0.80
}