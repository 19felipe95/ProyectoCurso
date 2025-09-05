package co.edu.upb.moviles.entrega_final1.ui.main

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.upb.moviles.entrega_final1.adapter.TaskAdapter
import co.edu.upb.moviles.entrega_final1.databinding.ActivityMainBinding
import co.edu.upb.moviles.entrega_final1.viewmodel.TaskViewModel
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private val vm: TaskViewModel by viewModels { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        // DatePicker para la fecha
        b.etFecha.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d -> b.etFecha.setText("$d/${m + 1}/$y") },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // RecyclerView + Adapter
        val adapter = TaskAdapter(
            onEdit = { t ->
                b.etTitulo.setText(t.titulo)
                b.etDescripcion.setText(t.descripcion)
                b.etFecha.setText(t.fecha)
                // Para actualizar de verdad, guarda el id y llama vm.update(...)
            },
            onDelete = { vm.delete(it) }
        )
        b.rvTasks.layoutManager = LinearLayoutManager(this)
        b.rvTasks.adapter = adapter

        // ae oservan los cambios de la bae de dtos y muestra/oculta "Sin tareas aún"
        vm.tasks.observe(this) { list ->
            adapter.submit(list)
            b.tvVacio.isVisible = list.isEmpty()
        }

        // Botón Agregar
        b.btnAgregar.setOnClickListener {
            val titulo = b.etTitulo.text?.toString()?.trim().orEmpty()
            val desc   = b.etDescripcion.text?.toString()?.trim().orEmpty()
            val fecha  = b.etFecha.text?.toString()?.trim().orEmpty()

            if (titulo.isNotBlank() && desc.isNotBlank() && fecha.isNotBlank()) {
                vm.add(titulo, desc, fecha)
                b.etTitulo.text?.clear()
                b.etDescripcion.text?.clear()
                b.etFecha.setText("")
            } else {
                if (titulo.isBlank()) b.etTitulo.error = "Requerido"
                if (desc.isBlank())   b.etDescripcion.error = "Requerido"
                if (fecha.isBlank())  b.etFecha.error = "Requerido"
            }
        }

        if (savedInstanceState == null) {
            vm.add("Gestor", "Tarea", "01/01/2026")
        }
    }
}
