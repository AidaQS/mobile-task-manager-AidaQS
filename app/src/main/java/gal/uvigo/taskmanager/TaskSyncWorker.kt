package gal.uvigo.taskmanager

import android.content.Context
import android.content.Entity
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TaskSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val dao =
        TaskDatabase.getDatabase(context).taskDao()
    private val api = RetrofitInstance.api

    override suspend fun doWork(): Result {
        val pending = dao.getPending()

        pending.forEach { entity ->
            try {
                when (entity.syncState) {

                    SyncState.PENDING_CREATE -> {
                        val response = api.createTask(TaskMapper.domainToDto(
                            TaskMapper.entityToDomain(entity)
                        ))
                        if (response.isSuccessful) {
                            dao.update(
                                entity.copy(
                                    remoteId = response.body()?._id,
                                    syncState = SyncState.SYNCED
                                )
                            )
                        }
                    }

                    SyncState.PENDING_UPDATE -> {
                        entity.remoteId?.let {
                            api.updateTask(it, TaskMapper.domainToDto(
                                TaskMapper.entityToDomain(entity)
                            ))
                            dao.update(entity.copy(syncState = SyncState.SYNCED))
                        }
                    }

                    SyncState.PENDING_DELETE -> {
                        entity.remoteId?.let { api.deleteTask(it) }
                        dao.delete(entity)
                    }

                    else -> Unit
                }

            } catch (e: Exception) {
                dao.update(entity.copy(syncState = SyncState.FAILED))
                return Result.retry()
            }
        }


        return Result.success()
    }
}
