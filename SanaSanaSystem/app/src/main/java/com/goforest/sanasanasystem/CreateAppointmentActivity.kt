package com.goforest.sanasanasystem

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateAppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        val btnVolver = findViewById<ImageButton>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish()
        }

        val spinnerEspecialidad = findViewById<Spinner>(R.id.spinnerEspecialidad)
        val spinnerTurno = findViewById<Spinner>(R.id.spinnerTurno)

        val especialidades = arrayOf("Pediatría", "Cardiología", "Dermatología")
        val turnos = arrayOf("Mañana", "Tarde", "Noche")

        spinnerEspecialidad.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            especialidades
        )

        spinnerTurno.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            turnos
        )

        val btnSolicitar = findViewById<Button>(R.id.btnLogin)
        btnSolicitar.setOnClickListener {
            val fecha = findViewById<EditText>(R.id.inputFechaCita).text.toString()
            val medico = findViewById<EditText>(R.id.inputMedico).text.toString()
            val sintomas = findViewById<EditText>(R.id.inputSintomas).text.toString()
            val especialidad = spinnerEspecialidad.selectedItem.toString()
            val turno = spinnerTurno.selectedItem.toString()

            if (fecha.isBlank() || medico.isBlank() || sintomas.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Cita solicitada con éxito", Toast.LENGTH_LONG).show()

            finish()
        }
    }
}
