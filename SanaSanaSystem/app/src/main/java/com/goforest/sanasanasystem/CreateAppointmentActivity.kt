package com.goforest.sanasanasystem

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.app.AlarmManager
import android.app.PendingIntent
import java.text.SimpleDateFormat
import java.util.Calendar
import android.util.Log
import android.content.Context
import android.os.Handler
import android.os.Looper

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

        val ancoreRecomendacion = findViewById<TextView>(R.id.ancoreRecomendacion)
        ancoreRecomendacion.setOnClickListener {
            startActivity(Intent(this, GetRecommendationActivity::class.java))
        }

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

            programarNotificacion(fecha, turno)

            finish()
        }
    }

    private fun programarNotificacion(fechaCita: String, turno: String) {
        Log.d("CreateAppointment", "Iniciando programación de notificación")
        
        // Para pruebas: usar Handler en lugar de AlarmManager
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            Log.d("CreateAppointment", "Handler ejecutado - enviando broadcast")
            
            val intent = Intent(this, NotificationReceiver::class.java)
            intent.putExtra("title", "¡Recuerda tu cita médica!")
            intent.putExtra("message", "Tu cita médica empezará dentro de 1 hora. ¡Prepárate para llegar a tiempo!")
            
            sendBroadcast(intent)
            Log.d("CreateAppointment", "Broadcast enviado")
            
        }, 5000) // 5 segundos
        
        Log.d("CreateAppointment", "Handler programado para 5 segundos")
    }
}
