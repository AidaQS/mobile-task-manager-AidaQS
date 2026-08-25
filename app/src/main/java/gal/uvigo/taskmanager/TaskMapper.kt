package gal.uvigo.taskmanager

object TaskMapper {

    fun entityToDomain(entity: TaskEntity): Task =
        Task(
            localId = entity.localId,
            _id = entity.remoteId,
            title = entity.title,
            description = entity.description,
            dueDate = entity.dueDate,
            category = entity.category,
            isDone = entity.isDone,
            syncState = entity.syncState
        )

    fun domainToEntity(task: Task): TaskEntity =
        TaskEntity(
            localId = task.localId,
            remoteId = task._id,
            title = task.title,
            description = task.description,
            dueDate = task.dueDate,
            category = task.category,
            isDone = task.isDone,
            syncState = task.syncState
        )

    fun dtoToEntity(dto: TaskDto): TaskEntity =
        TaskEntity(
            title = dto.title,
            description = dto.description,
            dueDate = dto.dueDate,
            category = dto.category,
            isDone = dto.isDone,
            remoteId = dto._id,
            syncState = SyncState.SYNCED
        )

    fun domainToDto(task: Task): TaskDto =
        TaskDto(
            _id = task._id,
            title = task.title,
            description = task.description,
            dueDate = task.dueDate,
            category = task.category,
            isDone = task.isDone
        )
}
