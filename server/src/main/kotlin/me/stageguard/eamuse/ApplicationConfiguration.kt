package me.stageguard.eamuse

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationConfiguration(
    val server: ServerConfiguration = ServerConfiguration(),
    val database: DatabaseConfiguration = DatabaseConfiguration()
) {
    @Serializable
    data class ServerConfiguration(
        val host: String = "0.0.0.0",
        val port: Int = 8081,
        val globalHost: String = host,
        val globalPort: Int = port,
        val matchingPort: Int = 5700,
        //other configurations
        val isMaintenanceMode: Boolean = false,
        val isPaseliEnabled: Boolean = true,
    )

    @Serializable
    data class DatabaseConfiguration(
        val address: String = "localhost",
        val port: Int = 3306,
        val user: String = "root",
        val password: String = "this is password.",
        var table: String = "eamuse",
        var maximumPoolSize: Int? = 10
    )
}
