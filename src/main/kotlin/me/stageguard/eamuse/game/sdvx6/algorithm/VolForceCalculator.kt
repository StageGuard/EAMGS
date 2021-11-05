package me.stageguard.eamuse.game.sdvx6.algorithm

import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.sdvx6.model.PlayRecordTable
import me.stageguard.eamuse.game.sdvx6.sdvx6MusicLibrary
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import kotlin.math.round

suspend fun calculateVolForce(refId: String) =
    Database.query { db -> db.sequenceOf(PlayRecordTable).filter { it.refId eq refId }.map { r ->
        val music = sdvx6MusicLibrary[r.mid.toInt()] ?: return@map 0.0
        val level = music.difficulties.find { it.type == r.type.toInt() } ?.difficulty ?: return@map 0.0

        (level * (r.score / 10000000.0) * grade(r.score) * clear(r.clear.toInt()) * 2.0).toFixed(1)
    }.sortedByDescending { it }.take(50).sum() / 100.0 } ?.toFixed(3) ?: 0.0

private fun Double.toFixed(decimals: Int): Double {
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