package io.kraftsman.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object Tasks : IntIdTable() {
    val title = varchar("title", 255)
    var completed = bool("completed")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
