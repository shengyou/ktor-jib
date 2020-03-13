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
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

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
                completed = listOf(true, false, false).shuffled().first()
                createdAt = DateTime.now()
                updatedAt = DateTime.now()
            }
            println("Task ${task.id} created")
        }
    }

    routing {

        get("/") {

            call.respond("Hello, Auto-Jib!!!")

        }

        get("/api/tasks") {
            val tasks = transaction {
                Task.all().map {
                    TaskResponse(
                        it.id.value,
                        it.title,
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
            transaction {
                Task.new {
                    title = request.title
                    completed = false
                    createdAt = DateTime.now()
                    updatedAt = DateTime.now()
                }
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/api/tasks/{id}/complete") {
            val id = call.parameters["id"]?.toInt()
            transaction {
                if (id != null) {
                    val task = Task.findById(id)
                    task?.completed = true
                }
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/api/tasks/{id}/delete") {
            val id = call.parameters["id"]?.toInt()
            transaction {
                if (id != null) {
                    val task = Task.findById(id)
                    task?.delete()
                }
            }

            call.respond(HttpStatusCode.OK)
        }

    }

}

