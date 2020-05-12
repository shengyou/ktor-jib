package io.kraftsman.responses

import java.util.*

data class TaskResponse (
    val id:UUID,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String
)