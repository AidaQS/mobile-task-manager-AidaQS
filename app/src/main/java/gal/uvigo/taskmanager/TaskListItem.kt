package gal.uvigo.taskmanager

sealed class TaskListItem {
    data class Header(val category: Category) : TaskListItem()
    data class TaskEntry(val task: Task) : TaskListItem()
}


