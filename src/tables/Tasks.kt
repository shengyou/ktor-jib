package io.kraftsman.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime

object Tasks : UUIDTable() {
    val title = varchar("title", 255)
    val description = text("description")
    var completed = bool("completed")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
