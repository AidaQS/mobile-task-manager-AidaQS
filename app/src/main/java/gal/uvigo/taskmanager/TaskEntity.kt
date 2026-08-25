package gal.uvigo.taskmanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val remoteId: String? = null,
    val title: String,
    val description: String,
    val dueDate: String,
    val category: Category,
    val isDone: Boolean,
    val syncState: SyncState = SyncState.SYNCED
)
