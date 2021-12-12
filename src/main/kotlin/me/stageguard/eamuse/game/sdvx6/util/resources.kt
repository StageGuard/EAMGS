package me.stageguard.eamuse.game.sdvx6.util

import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path

@OptIn(ObsoleteCoroutinesApi::class)
private val context = newSingleThreadContext("SV6ResExport")

fun getResourceOrExport(f: String, exporter: () -> InputStream?): InputStream? {
    val sdvx6Path = File("data/sdvx6/")
    if (!sdvx6Path.exists()) sdvx6Path.mkdirs()
    val res = File("${sdvx6Path.path}/$f")
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