package com.goforest.sanasanasystem

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("SanaSanaPrefs", MODE_PRIVATE)
        val isFirstTime = sharedPref.getBoolean("isFirstTime", true)
        val isLogged = sharedPref.getBoolean("isLogged", false)

        if (isFirstTime) {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)

            sharedPref.edit().putBoolean("isFirstTime", false).apply()

            finish()
            return
        }

        if (!isLogged) {
            val intent = Intent(this, LoginActivity::class.java)
            sharedPref.edit().putBoolean("isLogged", true).apply()
            startActivity(intent)
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        val agendarItemView: View = findViewById(R.id.agendarCita)
        val misCitasItemView: View = findViewById(R.id.misCitas)
        val horariosItemView: View = findViewById(R.id.horarios)
        val historyItemView: View = findViewById(R.id.historial)
        val item: View = findViewById(R.id.item)

        // Agendar cita
        val icAgendarCita: ImageView = agendarItemView.findViewById(R.id.itemIcon)
        val txtAgendarCita: TextView = agendarItemView.findViewById(R.id.itemText)
        agendarItemView.setBackgroundResource(R.drawable.bg_rounded)
        icAgendarCita.setImageResource(R.drawable.ic_agendar_cita_light)
        txtAgendarCita.text = "Agendar Cita"
        txtAgendarCita.setTextColor(ContextCompat.getColor(this, R.color.white))

        // Mis citas
        val icMisCitas: ImageView = misCitasItemView.findViewById(R.id.itemIcon)
        val txtMisCitas: TextView = misCitasItemView.findViewById(R.id.itemText)
        icMisCitas.setImageResource(R.drawable.ic_mis_citas)
        txtMisCitas.text = "Mis citas"

        // Horarios
        val icHorarios: ImageView = horariosItemView.findViewById(R.id.itemIcon)
        val txtHorarios: TextView = horariosItemView.findViewById(R.id.itemText)
        icHorarios.setImageResource(R.drawable.ic_horarios)
        txtHorarios.text = "Turnos médicos"

        // Historial médico
        val icHistorial: ImageView = historyItemView.findViewById(R.id.itemIcon)
        val txtHistorial: TextView = historyItemView.findViewById(R.id.itemText)
        icHistorial.setImageResource(R.drawable.ic_historial)
        txtHistorial.text = "Historial Médico"

        // Signos vitales
        val icItem: ImageView = item.findViewById(R.id.itemIcon)
        val txtItem: TextView = item.findViewById(R.id.itemText)
        icItem.setImageResource(R.drawable.ic_signos)
        txtItem.text = "Signos vitales"

        agendarItemView.setOnClickListener {
            startActivity(Intent(this, CreateAppointmentActivity::class.java))
        }

        misCitasItemView.setOnClickListener {
            startActivity(Intent(this, MyAppointmentsActivity::class.java))
        }

        horariosItemView.setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }

        historyItemView.setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }

        item.setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }
    }
}
