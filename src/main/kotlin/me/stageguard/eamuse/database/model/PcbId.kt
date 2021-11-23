package me.stageguard.eamuse.database.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object PcbIdTable : AddableTable<PcbId>("pcbId") {
    val id = int("id").primaryKey().bindTo { it.id }
    val pcbId = varchar("pcbId").bindTo { it.pcbId }

    override fun <T : AssignmentsBuilder> T.mapElement(element: PcbId) {
        set(pcbId, element.pcbId)
    }
    override val createStatement = """
        `id` int NOT NULL AUTO_INCREMENT,
        `pcbId` varchar(16) NOT NULL,
        PRIMARY KEY (`id`),
        UNIQUE KEY `pcb_unique_id` (`pcbId`)
    """.trimIndent()
}

interface PcbId : Entity<PcbId> {
    companion object : Entity.Factory<PcbId>()
    var id: Int
    var pcbId: String
}