package me.stageguard.eamuse.server

class APIRequestDSL(private val module: String) {
    private val routers: MutableMap<String, suspend (String) -> String> = mutableMapOf()

    fun routing(method: String, block: suspend (String) -> String) {
        routers["$module/$method"] = block
    }

    operator fun invoke() = routers
}