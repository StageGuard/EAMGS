package me.stageguard.eamuse.server.packet

import org.w3c.dom.Element

data class RequestPacket(
    val model: String,
    val module: String,
    val method: String,
    val eAmuseInfo: String?,
    val compressScheme: String?,
    val content: Element
)