package gal.uvigo.taskmanager

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

class TaskForm {
    val title = ObservableField<String>("")
    val description = ObservableField<String>("")
    val category = ObservableField<String>("Other")
    val dueDate = ObservableField<String>("")
    val isDone = ObservableBoolean(false)

    //cambio parametro id
    fun toTask(id: String? = null): Task {
        val cat = try {
            Category.valueOf(category.get()?.uppercase() ?: "OTHER")
        } catch (e: Exception) {
            Category.OTHER
        }
        return Task(
            _id = id, //cambio a id
            title = title.get() ?: "",
            description = description.get() ?: "",
            dueDate = dueDate.get() ?: "",
            category = cat,
            isDone = isDone.get()
        )
    }

    fun fromTask(task: Task) {
        title.set(task.title)
        description.set(task.description)
        category.set(task.category.name.capitalize())
        dueDate.set(task.dueDate)
        isDone.set(task.isDone)
    }
}
