package co.edu.upb.moviles.entrega_final1.ui.detail

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.edu.upb.moviles.entrega_final1.databinding.ActivityTaskDetailBinding
import co.edu.upb.moviles.entrega_final1.data.model.Task
import co.edu.upb.moviles.entrega_final1.viewmodel.TaskViewModel
import co.edu.upb.moviles.entrega_final1.notify.TaskReminderWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class TaskDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "task_id"
    }

    private lateinit var b: ActivityTaskDetailBinding
    private val vm: TaskViewModel by viewModels()

    private var editingId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(b.root)

        // DatePicker para la fecha
        b.etFecha.setOnClickListener { showDatePicker() }

        // ¿Entró para editar?
        val id = intent.getIntExtra(EXTRA_TASK_ID, -1)
        if (id != -1) {
            editingId = id
            b.tvTitle.text = "Editar Tarea"
            vm.getById(id).observe(this) { t ->
                t?.let {
                    b.etTitulo.setText(it.titulo)
                    b.etDescripcion.setText(it.descripcion)
                    b.etFecha.setText(it.fecha)
                }
            }
        } else {
            b.tvTitle.text = "Nueva Tarea"
        }

        // Guardar
        b.btnGuardar.setOnClickListener {
            val titulo = b.etTitulo.text?.toString()?.trim().orEmpty()
            val desc   = b.etDescripcion.text?.toString()?.trim().orEmpty()
            val fecha  = b.etFecha.text?.toString()?.trim().orEmpty()

            var hasError = false
            if (titulo.isEmpty()) { b.etTitulo.error = "Requerido"; hasError = true }
            if (desc.isEmpty())   { b.etDescripcion.error = "Requerido"; hasError = true }
            if (fecha.isEmpty())  { b.etFecha.error = "Requerido"; hasError = true }
            if (hasError) return@setOnClickListener

            if (editingId == null) {
                vm.insert(Task(titulo = titulo, descripcion = desc, fecha = fecha))
            } else {
                vm.update(Task(id = editingId!!, titulo = titulo, descripcion = desc, fecha = fecha))
            }

            // Programa / reprograma recordatorio
            scheduleReminder(editingId, titulo, desc, fecha)

            finish()
        }

        // Cancelar
        b.btnCancelar.setOnClickListener { finish() }
    }

    // ---------- Helpers ----------

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, yy, mm, dd ->
            val s = "%02d/%02d/%04d".format(dd, mm + 1, yy)
            b.etFecha.setText(s)
        }, y, m, d).show()
    }

    /**
     * Programa un recordatorio con WorkManager:
     * - Si la fecha es válida: dispara a las 09:00 de ese día (mínimo 10s desde ahora).
     * - Si la fecha no se puede parsear: dispara en 10 segundos (modo prueba).
     */
    private fun scheduleReminder(taskId: Int?, titulo: String, desc: String, fecha: String) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val now = System.currentTimeMillis()

        val targetMillis = runCatching {
            val date = sdf.parse(fecha)!!
            Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }.getOrNull()

        // Si la fecha ya pasó o es inválida, dispara en 10s (útil para pruebas)
        val delayMs = if (targetMillis != null)
            (targetMillis - now).coerceAtLeast(10_000L)
        else
            10_000L

        val data = Data.Builder()
            .putString("title", "Tarea: $titulo")
            .putString("desc", if (desc.isBlank()) "Revisa tu tarea" else desc)
            .build()

        val req = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag(taskId?.let { "task_id_$it" } ?: "task_new")
            .build()

        val wm = WorkManager.getInstance(this)

        if (taskId != null) {
            // trabajo único por tarea: REPLACE = reprograma si ya existía
            wm.enqueueUniqueWork(
                "task_reminder_$taskId",
                ExistingWorkPolicy.REPLACE,
                req
            )
        } else {
            wm.enqueue(req)
        }
    }
}


