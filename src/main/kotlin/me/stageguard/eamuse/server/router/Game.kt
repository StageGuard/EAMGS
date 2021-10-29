package me.stageguard.eamuse.server.router

import me.stageguard.eamuse.server.RouteCollection
import me.stageguard.eamuse.server.RouteHandler

class Game(private vararg val handlers: RouteHandler) : RouteCollection("game") {
    override val routers: Set<RouteHandler>
        get() = mutableSetOf(*handlers)
}