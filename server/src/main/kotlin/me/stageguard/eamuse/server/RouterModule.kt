package me.stageguard.eamuse.server

abstract class RouterModule(val module: String) {
    abstract val routers: Set<RouteHandler>
}
