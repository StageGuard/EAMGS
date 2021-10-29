package me.stageguard.eamuse.server

abstract class RouteCollection(val module: String) {
    abstract val routers: Set<RouteHandler>
}