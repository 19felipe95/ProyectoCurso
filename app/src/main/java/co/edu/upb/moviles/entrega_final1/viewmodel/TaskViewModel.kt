package co.edu.upb.moviles.entrega_final1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import co.edu.upb.moviles.entrega_final1.data.local.db.AppDatabase
import co.edu.upb.moviles.entrega_final1.data.model.Task
import co.edu.upb.moviles.entrega_final1.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: TaskRepository by lazy {
        val dao = AppDatabase.getInstance(app).taskDao()
        TaskRepository(dao)
    }

    val tasks = repo.tasks.asLiveData()

    fun add(titulo: String, descripcion: String, fecha: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(Task(titulo = titulo, descripcion = descripcion, fecha = fecha))
        }
    }

    fun update(task: Task) {
        viewModelScope.launch(Dispatchers.IO) { repo.update(task) }
    }

    fun delete(task: Task) {
        viewModelScope.launch(Dispatchers.IO) { repo.delete(task) }
    }
}
