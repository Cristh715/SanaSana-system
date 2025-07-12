package com.goforest.sanasanasystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goforest.sanasanasystem.api.EspecialidadDisponible

class EspecialidadAdapter(private val especialidades: List<EspecialidadDisponible>) : RecyclerView.Adapter<EspecialidadAdapter.EspecialidadViewHolder>() {
    private val expandedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EspecialidadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_especialidad, parent, false)
        return EspecialidadViewHolder(view)
    }

    override fun onBindViewHolder(holder: EspecialidadViewHolder, position: Int) {
        val especialidad = especialidades[position]
        holder.tvNombre.text = especialidad.especialidad
        val isExpanded = expandedPositions.contains(position)
        holder.recyclerMedicos.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.btnExpandir.setImageResource(
            if (isExpanded) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float
        )
        holder.recyclerMedicos.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerMedicos.adapter = MedicoAdapter(especialidad.medicos)

        holder.btnExpandir.setOnClickListener {
            if (isExpanded) expandedPositions.remove(position) else expandedPositions.add(position)
            notifyItemChanged(position)
        }
        holder.itemView.setOnClickListener {
            if (isExpanded) expandedPositions.remove(position) else expandedPositions.add(position)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = especialidades.size

    class EspecialidadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvEspecialidadNombre)
        val btnExpandir: ImageView = itemView.findViewById(R.id.btnExpandirEspecialidad)
        val recyclerMedicos: RecyclerView = itemView.findViewById(R.id.recyclerMedicos)
    }
} 