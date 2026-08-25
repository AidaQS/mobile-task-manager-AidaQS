package gal.uvigo.taskmanager

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    // DAO → Repository (la UI NO toca Room ni Retrofit)
    private val dao = TaskDatabase.getDatabase(application).taskDao()
    private val repo = TaskRepository(dao, application)

    // Fuente única de verdad para la UI
    val tasks: LiveData<List<Task>> = repo.tasks

    // Lista con headers para el RecyclerView
    val taskListItems: LiveData<List<TaskListItem>> = tasks.map { list ->
        buildListWithHeaders(list)
    }

    // Orden de categorías (solo memoria)
    private val categoryOrder = mutableListOf<Category>()

    // Inicializar categorías cuando llegan las primeras tareas
    private val tasksObserver = Observer<List<Task>> { list ->
        if (!list.isNullOrEmpty() && categoryOrder.isEmpty()) {
            categoryOrder.addAll(list.map { it.category }.distinct())
        }
    }

    init {
        tasks.observeForever(tasksObserver)

        viewModelScope.launch {
            repo.refreshFromRemote()
        }
    }


    override fun onCleared() {
        super.onCleared()
        tasks.removeObserver(tasksObserver)
    }

    // =========================
    // OPERACIONES (delegadas)
    // =========================

    fun addTask(task: Task) = viewModelScope.launch {
        repo.addTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repo.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repo.deleteTask(task)
    }

    // =========================
    // HELPERS UI
    // =========================

    fun getTaskByRemoteId(id: String): Task? =
        tasks.value?.find { it._id == id }

    private fun buildListWithHeaders(list: List<Task>): List<TaskListItem> {
        if (list.isEmpty()) return emptyList()

        val grouped = list.groupBy { it.category }

        val sortedCategories =
            categoryOrder.filter { grouped.containsKey(it) }
                .ifEmpty { grouped.keys.toList() }

        return sortedCategories.flatMap { category ->
            val entries = grouped[category]
                ?.sortedBy { it.dueDate }
                ?.map { TaskListItem.TaskEntry(it) }
                ?: emptyList()

            listOf(TaskListItem.Header(category)) + entries
        }
    }

    // =========================
    // REORDER (UI ONLY)
    // =========================

    fun reorderTaskWithinCategory(
        category: Category,
        fromIndex: Int,
        toIndex: Int
    ) {
        val all = tasks.value ?: return
        val tasksInCategory = all.filter { it.category == category }.toMutableList()

        if (fromIndex in tasksInCategory.indices &&
            toIndex in tasksInCategory.indices
        ) {
            val task = tasksInCategory.removeAt(fromIndex)
            tasksInCategory.add(toIndex, task)
            // Solo afecta a UI, no se persiste
        }
    }

    fun reorderCategory(fromIndex: Int, toIndex: Int) {
        if (fromIndex in categoryOrder.indices &&
            toIndex in categoryOrder.indices
        ) {
            val cat = categoryOrder.removeAt(fromIndex)
            categoryOrder.add(toIndex, cat)
        }
    }
}