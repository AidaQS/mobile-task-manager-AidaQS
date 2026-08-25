package gal.uvigo.taskmanager

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TaskDto(
    @Json(name = "_id")
    val _id: String? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: String = "",
    val category: Category = Category.OTHER,
    val isDone: Boolean = false
)
