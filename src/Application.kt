package io.kraftsman

import io.kraftsman.entities.Task
import io.kraftsman.requests.TaskRequest
import io.kraftsman.responses.TaskResponse
import io.kraftsman.tables.Tasks
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {

        }
    }

    Database.connect(
        url = "jdbc:h2:mem:todo_api;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver"
    )

    transaction {
        SchemaUtils.create(Tasks)
    }

    transaction {
        for (index in 1..10) {
            val task = Task.new {
                title = "Task $index"
                description = "Task description $index"
                completed = listOf(true, false, false).shuffled().first()
                createdAt = DateTime.now()
                updatedAt = DateTime.now()
            }
            println("Task ${task.id} created")
        }
    }

    routing {

        get("/") {

            call.respond("Hello, Ktor!")

        }

        get("/api/tasks") {
            val tasks = transaction {
                Task.all().map {
                    TaskResponse(
                        it.id.value,
                        it.title,
                        it.description,
                        it.completed,
                        it.createdAt.toString("yyyy-MM-dd HH:mm:ss"),
                        it.updatedAt.toString("yyyy-MM-dd HH:mm:ss")
                    )
                }
            }

            call.respond(mapOf("data" to tasks))
        }

        post("/api/tasks") {
            val request = call.receive<TaskRequest>()
            val task = transaction {
                val task = Task.new {
                    title = request.title
                    description = request.description
                    completed = false
                    createdAt = DateTime.now()
                    updatedAt = DateTime.now()
                }

                return@transaction TaskResponse(
                    id = task.id.value,
                    title = task.title,
                    description = task.description,
                    completed = task.completed,
                    createdAt = task.createdAt.toString("yyyy-MM-dd HH:mm:ss"),
                    updatedAt = task.updatedAt.toString("yyyy-MM-dd HH:mm:ss")
                )
            }

            call.respond(mapOf("data" to task))
        }

        get("/api/tasks/{id}") {
            val id = UUID.fromString(call.parameters["id"]) ?: throw IllegalArgumentException("Parameter id not found")
            val task = transaction {
                val task = Task.findById(id)

                if (task != null) {
                    return@transaction TaskResponse(
                        id = task.id.value,
                        title = task.title,
                        description = task.description,
                        completed = task.completed,
                        createdAt = task.createdAt.toString("yyyy-MM-dd HH:mm:ss"),
                        updatedAt = task.updatedAt.toString("yyyy-MM-dd HH:mm:ss")
                    )
                }

                return@transaction task
            }

            call.respond(mapOf("data" to task))
        }

        patch("/api/tasks/{id}/complete") {
            val id = UUID.fromString(call.parameters["id"]) ?: throw IllegalArgumentException("Parameter id not found")
            val task = transaction {
                val task = Task.findById(id)

                if (task != null) {
                    task.completed = true

                    return@transaction TaskResponse(
                        id = task.id.value,
                        title = task.title,
                        description = task.description,
                        completed = task.completed,
                        createdAt = task.createdAt.toString("yyyy-MM-dd HH:mm:ss"),
                        updatedAt = task.updatedAt.toString("yyyy-MM-dd HH:mm:ss")
                    )
                }

                return@transaction task
            }

            call.respond(mapOf("data" to task))
        }

        delete("/api/tasks/{id}/delete") {
            val id = UUID.fromString(call.parameters["id"]) ?: throw IllegalArgumentException("Parameter id not found")
            transaction {
                Task.findById(id)?.delete()
            }

            call.respond(HttpStatusCode.NoContent)
        }

    }

}
