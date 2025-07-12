package com.goforest.sanasanasystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val agendarItemView: View = findViewById(R.id.agendarCita)
        val misCitasItemView: View = findViewById(R.id.misCitas)
        val horariosItemView: View = findViewById(R.id.horarios)
        val historyItemView: View = findViewById(R.id.historial)
        val bienestarDiarioItemView: View = findViewById(R.id.item)

        // Agendar Cita
        val icAgendarCita: ImageView = agendarItemView.findViewById(R.id.itemIcon)
        val txtAgendarCita: TextView = agendarItemView.findViewById(R.id.itemText)
        agendarItemView.setBackgroundResource(R.drawable.bg_rounded) // Este ya tiene un background
        icAgendarCita.setImageResource(R.drawable.ic_agendar_cita_light)
        txtAgendarCita.text = "Agendar Cita"
        txtAgendarCita.setTextColor(ContextCompat.getColor(this, R.color.white))

        // Mis Citas
        val icMisCitas: ImageView = misCitasItemView.findViewById(R.id.itemIcon)
        val txtMisCitas: TextView = misCitasItemView.findViewById(R.id.itemText)
        // Asegúrate de tener un background para los demás items (bg_grid_item o similar)
        misCitasItemView.setBackgroundResource(R.drawable.bg_grid_item)
        icMisCitas.setImageResource(R.drawable.ic_mis_citas)
        txtMisCitas.text = "Mis citas"
        txtMisCitas.setTextColor(ContextCompat.getColor(this, R.color.black)) // Asegúrate que el color sea visible

        // Horarios
        val icHorarios: ImageView = horariosItemView.findViewById(R.id.itemIcon)
        val txtHorarios: TextView = horariosItemView.findViewById(R.id.itemText)
        horariosItemView.setBackgroundResource(R.drawable.bg_grid_item)
        icHorarios.setImageResource(R.drawable.ic_horarios)
        txtHorarios.text = "Turnos médicos"
        txtHorarios.setTextColor(ContextCompat.getColor(this, R.color.black))

        // Historial Médico
        val icHistorial: ImageView = historyItemView.findViewById(R.id.itemIcon)
        val txtHistorial: TextView = historyItemView.findViewById(R.id.itemText)
        historyItemView.setBackgroundResource(R.drawable.bg_grid_item)
        icHistorial.setImageResource(R.drawable.ic_historial)
        txtHistorial.text = "Historial Médico"
        txtHistorial.setTextColor(ContextCompat.getColor(this, R.color.black))

        // Bienestar diario
        val icBienestarDiario: ImageView = bienestarDiarioItemView.findViewById(R.id.itemIcon)
        val txtBienestarDiario: TextView = bienestarDiarioItemView.findViewById(R.id.itemText)
        bienestarDiarioItemView.setBackgroundResource(R.drawable.bg_grid_item)
        icBienestarDiario.setImageResource(R.drawable.ic_signos)
        txtBienestarDiario.text = "Bienestar diario"
        txtBienestarDiario.setTextColor(ContextCompat.getColor(this, R.color.black))

        agendarItemView.setOnClickListener {
            startActivity(Intent(this, CreateAppointmentActivity::class.java))
        }

        misCitasItemView.setOnClickListener {
            startActivity(Intent(this, MyAppointmentsActivity::class.java))
        }

        horariosItemView.setOnClickListener {
            startActivity(Intent(this, HorariosEspecialidadesActivity::class.java))
        }

        historyItemView.setOnClickListener {
            startActivity(Intent(this, MedicalHistoryActivity::class.java))
        }

        bienestarDiarioItemView.setOnClickListener {
            startActivity(Intent(this, BienestarActivity::class.java))
        }

        val sharedPref = getSharedPreferences("sana_sana_prefs", Context.MODE_PRIVATE)
        val firstName = sharedPref.getString("user_first_name", "Usuario")
        findViewById<TextView>(R.id.nameTextView).text = "$firstName!"

        findViewById<View>(R.id.stayHomeCardView).setOnClickListener {
            Toast.makeText(this, "Redirigiendo a la próxima cita...", Toast.LENGTH_SHORT).show()
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("sana_sana_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().remove("auth_token").apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("sana_sana_prefs", Context.MODE_PRIVATE)
        val authToken = sharedPref.getString("auth_token", null)

        if (authToken.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}