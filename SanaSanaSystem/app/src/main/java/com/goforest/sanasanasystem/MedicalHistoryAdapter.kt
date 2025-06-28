package com.goforest.sanasanasystem

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.goforest.sanasanasystem.api.MedicalHistoryItem

class MedicalHistoryAdapter(private var items: List<MedicalHistoryItem>) : RecyclerView.Adapter<MedicalHistoryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fechaTextView: TextView = view.findViewById(R.id.fechaTextView)
        val especialidadTextView: TextView = view.findViewById(R.id.especialidadTextView)
        val medicoTextView: TextView = view.findViewById(R.id.medicoTextView)
        val estadoTextView: TextView = view.findViewById(R.id.estadoTextView)
        val sintomasTextView: TextView = view.findViewById(R.id.sintomasTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.fechaTextView.text = item.fecha
        holder.especialidadTextView.text = item.especialidad
        holder.medicoTextView.text = item.medico?.let { "${it["nombres"]} ${it["apellidos"]}" } ?: "-"
        holder.sintomasTextView.text = item.sintomas?.takeIf { it.isNotBlank() } ?: ""
        holder.sintomasTextView.visibility = if (item.sintomas.isNullOrBlank()) View.GONE else View.VISIBLE
        holder.estadoTextView.text = item.estado
        when (item.estado.lowercase()) {
            "pendiente" -> holder.estadoTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.principal))
            "cancelada" -> holder.estadoTextView.setTextColor(Color.RED)
            "atendida", "finalizada" -> holder.estadoTextView.setTextColor(Color.parseColor("#388E3C")) // verde
            else -> holder.estadoTextView.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MedicalHistoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }
} 