package me.stageguard.eamuse.game.sdvx6

data class SDVX6Music(
    val id: Int,
    val title: String,
    val artist: String,
    val bpm: Pair<Double, Double>,
    val difficulties: List<SDVX6MusicDifficulty>
)

data class SDVX6MusicDifficulty(
    val type: Int, // 0: novice, 1: advanced, 2: exhaust, 3: infinite, 4: maximum
    val difficulty: Int, // 1 - 20
    val limited: Int
)