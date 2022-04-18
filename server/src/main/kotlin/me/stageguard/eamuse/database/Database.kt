package me.stageguard.eamuse.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.*
import me.stageguard.eamuse.config
import me.stageguard.eamuse.retry
import org.ktorm.database.Database
import org.ktorm.database.Transaction
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.sql.SQLException
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

object Database : CoroutineScope {
    private val LOGGER = LoggerFactory.getLogger(Database::class.java)

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    private val threadCounter = AtomicInteger(0)

    sealed class ConnectionStatus {
        object CONNECTED : ConnectionStatus()
        object DISCONNECTED : ConnectionStatus()
    }

    private val tables: MutableList<AddableTable<*>> = mutableListOf()

    private lateinit var db : Database
    private lateinit var hikariSource : HikariDataSource
    private var connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED

    suspend fun <T> query(block: suspend Transaction.(Database) -> T) : T? = if(connectionStatus == ConnectionStatus.DISCONNECTED) {
        LOGGER.error("Database is disconnected and the query operation will not be completed.")
        null
    } else withContext(coroutineContext) {
        db.useTransaction { t ->
            retry(3, exceptionBlock = {
                if(it is SQLException && it.toString().contains("Connection is closed")) {
                    LOGGER.warn("Database connection is closed, reconnecting...")
                    close()
                    connect()
                } else throw it
            }) {
                if(connected) {
                    block(t, db)
                } else {
                    connectionStatus = ConnectionStatus.DISCONNECTED
                    throw SQLException("Connection is closed")
                }
            }.getOrThrow()
        }
    }

    fun addTable(t: AddableTable<*>) = tables.add(t)

    @OptIn(ObsoleteCoroutinesApi::class)
     fun connect() = launch(newSingleThreadContext("DatabaseInitializer")) {
        db = Database.connect(hikariDataSourceProvider().also { hikariSource = it })
        connectionStatus = ConnectionStatus.CONNECTED
        initDatabase()
        LOGGER.info("Database ${hikariSource.jdbcUrl} is connected.")
    }

    val connected get() = connectionStatus == ConnectionStatus.CONNECTED

    @Suppress("SqlNoDataSourceInspection")
    private fun initDatabase() {
        // ktorm doesn't support creating database schema.
        db.useConnection { connection ->
            val statement = connection.createStatement()
            tables.forEach { t ->
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `${t.tableName}` (${t.createStatement});")
            }
        }
    }

    fun close() {
        connectionStatus = ConnectionStatus.DISCONNECTED
        kotlin.runCatching { hikariSource.close() }
    }

    private fun hikariDataSourceProvider() : HikariDataSource = HikariDataSource(HikariConfig().apply {
        when {
            config.database.address == "" -> throw IllegalArgumentException("Database address is not set in config file.")
            config.database.table == "" -> {
                LOGGER.warn("Database table is not set in config file and now it will be default value \"eamuse\".")
                config.database.table = "eamuse"
            }
            config.database.port !in 1024..65535 -> throw IllegalArgumentException("Database port is invalid.")
            config.database.user == "" -> throw IllegalArgumentException("Database user is not set in config file.")
            config.database.password == "" -> throw IllegalArgumentException("Database password is not set in config file.")
            config.database.maximumPoolSize == null -> {
                LOGGER.warn("Database maximumPoolSize is not set in config file and now it will be default value 10.")
                config.database.maximumPoolSize = 10
            }
        }
        jdbcUrl         = "jdbc:mysql://${config.database.address}:${config.database.port}/${config.database.table}"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username        = config.database.user
        password        = config.database.password
        maximumPoolSize = config.database.maximumPoolSize!!
        poolName        = "EAMUSE Pool"

        threadFactory = ThreadFactory { runnable ->
            thread(start = false, name = "DatabaseOperation#${threadCounter.getAndIncrement()}") {
                try {
                    runBlocking(this@Database.coroutineContext) { runnable.run() }
                } catch (_: InterruptedException) { /* shutdown hook triggered. */ }
                catch (e: Exception) {
                    LOGGER.error("Database operation is failed.", e)
                }
            }
        }
    })
}
