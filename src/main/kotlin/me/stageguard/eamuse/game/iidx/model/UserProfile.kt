package me.stageguard.eamuse.game.iidx.model

import me.stageguard.eamuse.database.AddableTable
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.entity.Entity
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object UserProfileTable : AddableTable<UserProfile>("iidx_profile") {
    val __id = int("__id").primaryKey().bindTo { it.__id }
    val refId = varchar("refId").bindTo { it.refId }
    val name = varchar("name").bindTo { it.name }
    val pid = int("pid").bindTo { it.pid }
    val iidxId = long("iidxId").bindTo { it.iidxId }

    override fun <T : AssignmentsBuilder> T.mapElement(element: UserProfile) {
        set(refId, element.refId)
        set(name, element.name)
        set(pid, element.pid)
        set(iidxId, element.iidxId)
    }

    override val createStatement: String = """
        `__id` INT NOT NULL AUTO_INCREMENT,
        `refId` varchar(16) NOT NULL,
        `name` varchar(8) NOT NULL,
        `pid` int NOT NULL,
        `iidxId` bigint NOT NULL,
        UNIQUE KEY `ref_unique_id` (`refId`),
        PRIMARY KEY (`__id`)
    """.trimIndent()
}

interface UserProfile : Entity<UserProfile> {
    companion object : Entity.Factory<UserProfile>()

    var __id: Int
    var refId: String
    var name: String
    var pid: Int
    var iidxId: Long
}