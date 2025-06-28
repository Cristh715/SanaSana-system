package com.goforest.sanasanasystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goforest.sanasanasystem.api.MedicoDisponible

class MedicoAdapter(private val medicos: List<MedicoDisponible>) : RecyclerView.Adapter<MedicoAdapter.MedicoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medico, parent, false)
        return MedicoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicoViewHolder, position: Int) {
        val medico = medicos[position]
        holder.tvNombre.text = medico.nombre_completo
        holder.recyclerHorarios.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerHorarios.adapter = HorarioAdapter(medico.horarios)
    }

    override fun getItemCount(): Int = medicos.size

    class MedicoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvMedicoNombre)
        val recyclerHorarios: RecyclerView = itemView.findViewById(R.id.recyclerHorarios)
    }
} 