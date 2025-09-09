package co.edu.upb.moviles.entrega_final1.repository

import co.edu.upb.moviles.entrega_final1.data.model.Task
import co.edu.upb.moviles.entrega_final1.data.model.local.dao.db.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    val tasks: Flow<List<Task>> = dao.getAll()
    fun getById(id: Int): Flow<Task> = dao.getById(id)
    suspend fun insert(task: Task) = dao.insert(task)
    suspend fun update(task: Task) = dao.update(task)
    suspend fun delete(task: Task) = dao.delete(task)
}

