package io.kraftsman.entities

import io.kraftsman.tables.Tasks
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Task(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Task>(Tasks)

    var title by Tasks.title
    var completed by Tasks.completed
    var createdAt by Tasks.createdAt
    var updatedAt by Tasks.updatedAt
}
