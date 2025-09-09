package co.edu.upb.moviles.entrega_final1.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.upb.moviles.entrega_final1.adapter.TaskAdapter
import co.edu.upb.moviles.entrega_final1.data.model.Task
import co.edu.upb.moviles.entrega_final1.databinding.ActivityMainBinding
import co.edu.upb.moviles.entrega_final1.ui.detail.TaskDetailActivity
import co.edu.upb.moviles.entrega_final1.viewmodel.TaskViewModel



class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private val vm: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        val adapter = TaskAdapter(
            onEdit = { t -> openEdit(t) },
            onDelete = { t -> vm.delete(t) }
        )

        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter

        vm.tasks.observe(this) { tasks: List<Task>? ->
            // 'tasks' puede venir nulo, mandamos lista vacía en ese caso
            adapter.submit(tasks ?: emptyList())
        }


        b.btnAgregar.setOnClickListener {
            startActivity(Intent(this, TaskDetailActivity::class.java))
        }
    }

    private fun openEdit(t: Task) {
        val i = Intent(this, TaskDetailActivity::class.java)
        i.putExtra(TaskDetailActivity.EXTRA_TASK_ID, t.id)
        startActivity(i)
    }
}



