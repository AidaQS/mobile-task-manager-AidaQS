package gal.uvigo.taskmanager

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(
    private val dao: TaskDao,
    private val context: Context
) {

    private val api = RetrofitInstance.api

    val tasks: LiveData<List<Task>> =
        dao.getAll().map { list -> list.map { TaskMapper.entityToDomain(it) } }

    suspend fun addTask(task: Task) = withContext(Dispatchers.IO) {
        dao.insert(
            TaskMapper.domainToEntity(task).copy(
                localId = 0,
                remoteId = null,
                syncState = SyncState.PENDING_CREATE
            )
        )
        enqueueSync()
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        dao.update(
            TaskMapper.domainToEntity(task).copy(
                syncState = SyncState.PENDING_UPDATE
            )
        )
        enqueueSync()
    }

    suspend fun deleteTask(task: Task) = withContext(Dispatchers.IO) {
        dao.update(
            TaskMapper.domainToEntity(task).copy(
                syncState = SyncState.PENDING_DELETE
            )
        )
        enqueueSync()
    }

    suspend fun refreshFromRemote() = withContext(Dispatchers.IO) {
        val response = api.getTasks()
        if (!response.isSuccessful) return@withContext

        val remoteTasks = response.body() ?: return@withContext

        remoteTasks.forEach { dto ->
            val existing = dto._id?.let { dao.getByRemoteId(it) }

            if (existing == null) {
                // No existe localmente → insert
                dao.insert(
                    TaskMapper.dtoToEntity(dto)
                )
            } else {
                // Existe → update conservando localId
                dao.update(
                    existing.copy(
                        title = dto.title,
                        description = dto.description,
                        dueDate = dto.dueDate,
                        category = dto.category,
                        isDone = dto.isDone,
                        syncState = SyncState.SYNCED
                    )
                )
            }
        }
    }

    fun enqueueSync() {
        val request = OneTimeWorkRequestBuilder<TaskSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance().enqueue(request)
    }
}
