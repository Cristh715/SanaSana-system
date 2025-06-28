package com.goforest.sanasanasystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goforest.sanasanasystem.api.HorarioDisponible

class HorarioAdapter(private val horarios: List<HorarioDisponible>) : RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horario, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val horario = horarios[position]
        holder.tvTurno.text = horario.turno
    }

    override fun getItemCount(): Int = horarios.size

    class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTurno: TextView = itemView.findViewById(R.id.tvHorarioTurno)
    }
} 