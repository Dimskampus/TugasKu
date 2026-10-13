package id.ac.mulia.tugasku.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {
    val tasks: Flow<List<Task>> = dao.observeAll()
    fun observe(id: Long): Flow<Task?> = dao.observeById(id)
    suspend fun get(id: Long): Task? = dao.getById(id)
    suspend fun save(task: Task) = dao.upsert(task)
    suspend fun delete(task: Task) = dao.delete(task)
}
