package io.kraftsman.entities

import io.kraftsman.tables.Tasks
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Task(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Task>(Tasks)

    var title by Tasks.title
    var description by Tasks.description
    var completed by Tasks.completed
    var createdAt by Tasks.createdAt
    var updatedAt by Tasks.updatedAt
}
