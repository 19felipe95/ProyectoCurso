package co.edu.upb.moviles.entrega_final1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.edu.upb.moviles.entrega_final1.data.model.Task
import co.edu.upb.moviles.entrega_final1.databinding.ItemTaskBinding

class TaskAdapter(
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    private val items = mutableListOf<Task>()

    fun submit(list: List<Task>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        with(holder.b) {
            tvTitulo.text = t.titulo
            tvDescripcion.text = t.descripcion
            tvFecha.text = t.fecha
            btnEditar.setOnClickListener { onEdit(t) }
            btnEliminar.setOnClickListener { onDelete(t) }
        }
    }

    override fun getItemCount(): Int = items.size
}

