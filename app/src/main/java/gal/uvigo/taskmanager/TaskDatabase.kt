package gal.uvigo.taskmanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskEntity::class],
    version = 3,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    . fallbackToDestructiveMigration(false)
                    .addCallback(PrepopulateCallback(context))
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class PrepopulateCallback(
        private val context: Context
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                val dao = getDatabase(context).taskDao()

                val initialTasks = listOf(
                    TaskEntity(
                        title = "Buy groceries",
                        description = "Milk, eggs, bread",
                        dueDate = "2025-10-20",
                        category = Category.PERSONAL,
                        isDone = false
                    ),
                    TaskEntity(
                        title = "Project meeting",
                        description = "Review UI architecture",
                        dueDate = "2025-10-21",
                        category = Category.WORK,
                        isDone = true
                    ),
                    TaskEntity(
                        title = "Call Mom",
                        description = "Weekly call",
                        dueDate = "2025-10-22",
                        category = Category.FAMILY,
                        isDone = false
                    )
                )

                initialTasks.forEach { dao.insert(it) }
            }
        }
    }
}
