package co.edu.upb.moviles.entrega_final1.repository

import co.edu.upb.moviles.entrega_final1.data.local.dao.TaskDao
import co.edu.upb.moviles.entrega_final1.data.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {
    val tasks: Flow<List<Task>> = dao.getAll()
    suspend fun insert(task: Task) = dao.insert(task)
    suspend fun update(task: Task) = dao.update(task)
    suspend fun delete(task: Task) = dao.delete(task)
}
