package co.edu.upb.moviles.entrega_final1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import co.edu.upb.moviles.entrega_final1.data.model.Task
import co.edu.upb.moviles.entrega_final1.data.model.local.dao.db.AppDatabase
import co.edu.upb.moviles.entrega_final1.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val repo by lazy {
        val dao = AppDatabase.getInstance(app.applicationContext).taskDao()
        TaskRepository(dao)
    }

    // Lista para la pantalla principal
    val tasks = repo.tasks.asLiveData()

    // ------ API que usa la UI ------
    fun getById(id: Int) = repo.getById(id).asLiveData()

    fun insert(task: Task) = viewModelScope.launch {
        repo.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repo.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repo.delete(task)
    }
}

