package me.stageguard.eamuse

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationConfiguration(
    val server: ServerConfiguration = ServerConfiguration(),
    val database: DatabaseConfiguration = DatabaseConfiguration(),
    val sdvx: SDVXConfiguration = SDVXConfiguration()
) {
    @Serializable
    data class ServerConfiguration(
        val host: String = "0.0.0.0",
        val port: Int = 8081,
        val globalHost: String = host,
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

    @Serializable
    data class SDVXConfiguration(
        val unlockAllSongs: Boolean = false,
        val unlockAllNavigators: Boolean = false,
        val unlockAllAppealCards: Boolean = true,
        val customEntryInformation: String = "",
        val customGameOverInformation: String = "",
        val useBlasterPass: Boolean = true
    )
}