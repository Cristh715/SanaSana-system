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
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.goforest.sanasanasystem.api.ApiClient
import com.goforest.sanasanasystem.api.HorariosEspecialidadesApi
import com.goforest.sanasanasystem.api.EspecialidadResponse
import com.goforest.sanasanasystem.api.MedicoPorTurnoResponse
import com.goforest.sanasanasystem.api.CitaRequest // Importa CitaRequest
import com.goforest.sanasanasystem.api.CitaResponse // Importa CitaResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Date
import android.app.DatePickerDialog

class CreateAppointmentActivity : AppCompatActivity() {

    private lateinit var spinnerEspecialidad: Spinner
    private lateinit var spinnerTurno: Spinner
    private lateinit var inputFechaCita: EditText
    private lateinit var inputMedico: EditText
    private lateinit var ancoreRecomendacion: TextView
    private lateinit var btnSolicitar: Button
    private lateinit var btnVolver: ImageButton

    private lateinit var disponibilidadApi: HorariosEspecialidadesApi

    private var selectedEspecialidad: String? = null
    private var selectedTurno: String? = null
    private var selectedMedicoId: Int? = null
    private var selectedMedicoNombreCompleto: String? = null
    private var selectedTurnoId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        spinnerEspecialidad = findViewById(R.id.spinnerEspecialidad)
        spinnerTurno = findViewById(R.id.spinnerTurno)
        inputFechaCita = findViewById(R.id.inputFechaCita)
        inputMedico = findViewById(R.id.inputMedico)
        ancoreRecomendacion = findViewById(R.id.ancoreRecomendacion)
        btnSolicitar = findViewById(R.id.btnLogin)
        btnVolver = findViewById(R.id.btnVolver)

        disponibilidadApi = ApiClient.getClient(this).create(HorariosEspecialidadesApi::class.java)

        btnVolver.setOnClickListener {
            finish()
        }

        ancoreRecomendacion.setOnClickListener {
            startActivity(Intent(this, GetRecommendationActivity::class.java))
        }

        inputFechaCita.setOnClickListener {
            showDatePickerDialog()
        }

        loadEspecialidades()

        spinnerEspecialidad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEspecialidad = parent?.getItemAtPosition(position).toString()
                Log.d("CreateAppointment", "Especialidad seleccionada: $selectedEspecialidad")
                inputMedico.setText("")
                selectedMedicoId = null
                selectedMedicoNombreCompleto = null
                selectedTurnoId = null // Reiniciar también el ID del turno
                if (!selectedTurno.isNullOrEmpty()) {
                    loadMedicoForSelectedOptions()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedEspecialidad = null
            }
        }

        spinnerTurno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTurno = parent?.getItemAtPosition(position).toString()
                Log.d("CreateAppointment", "Turno seleccionado: $selectedTurno")
                inputMedico.setText("")
                selectedMedicoId = null
                selectedMedicoNombreCompleto = null
                selectedTurnoId = null // Reiniciar también el ID del turno
                if (!selectedEspecialidad.isNullOrEmpty()) {
                    loadMedicoForSelectedOptions()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedTurno = null
            }
        }

        btnSolicitar.setOnClickListener {
            solicitarCita()
        }

        val turnos = arrayOf("Mañana", "Tarde", "Noche")
        spinnerTurno.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, turnos)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            inputFechaCita.setText(dateFormat.format(selectedDate.time))
            Log.d("CreateAppointment", "Fecha seleccionada: ${dateFormat.format(selectedDate.time)}")
        }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun loadEspecialidades() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = disponibilidadApi.getEspecialidades()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val especialidadesList = response.body()
                        if (especialidadesList != null && especialidadesList.isNotEmpty()) {
                            val especialidadesNombres = especialidadesList.map { it.especialidad }.toTypedArray()
                            val adapter = ArrayAdapter(
                                this@CreateAppointmentActivity,
                                android.R.layout.simple_spinner_dropdown_item,
                                especialidadesNombres
                            )
                            spinnerEspecialidad.adapter = adapter
                            Log.d("CreateAppointment", "Especialidades cargadas: $especialidadesNombres")
                        } else {
                            Toast.makeText(this@CreateAppointmentActivity, "No se encontraron especialidades.", Toast.LENGTH_SHORT).show()
                            Log.w("CreateAppointment", "Respuesta de especialidades vacía o nula.")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@CreateAppointmentActivity, "Error al cargar especialidades: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("CreateAppointment", "Error API especialidades: ${response.code()}, Cuerpo: $errorBody")
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error HTTP: ${e.code()} - ${e.message()}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "HttpException al cargar especialidades", e)
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error de red al cargar especialidades: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "IOException al cargar especialidades", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error inesperado al cargar especialidades: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "Excepción al cargar especialidades", e)
                }
            }
        }
    }

    private fun loadMedicoForSelectedOptions() {
        val especialidad = selectedEspecialidad
        val turno = selectedTurno

        if (especialidad.isNullOrEmpty() || turno.isNullOrEmpty()) {
            Log.d("CreateAppointment", "Especialidad o turno no seleccionados aún.")
            inputMedico.setText("")
            return
        }

        Log.d("CreateAppointment", "Cargando médico para Especialidad: $especialidad, Turno: $turno")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = disponibilidadApi.getMedicosPorEspecialidadYTurno(especialidad, turno)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val medicosResponse = response.body()
                        val medicosList = medicosResponse?.medicos
                        if (!medicosList.isNullOrEmpty()) {
                            val medico = medicosList[0]
                            inputMedico.setText(medico.nombreCompleto)
                            selectedMedicoId = medico.idMedico
                            selectedMedicoNombreCompleto = medico.nombreCompleto
                            selectedTurnoId = medico.idTurno
                            Log.d("CreateAppointment", "Médico cargado: ${medico.nombreCompleto} (ID Médico: ${medico.idMedico}, ID Turno: ${medico.idTurno})")
                        } else {
                            inputMedico.setText("No hay médicos disponibles")
                            selectedMedicoId = null
                            selectedMedicoNombreCompleto = null
                            selectedTurnoId = null
                            Toast.makeText(this@CreateAppointmentActivity, "No se encontraron médicos para la especialidad y turno seleccionados.", Toast.LENGTH_SHORT).show()
                            Log.w("CreateAppointment", "No hay médicos disponibles para $especialidad en turno $turno.")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@CreateAppointmentActivity, "Error al cargar médico: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("CreateAppointment", "Error API médico: ${response.code()}, Cuerpo: $errorBody")
                        inputMedico.setText("Error al cargar")
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error HTTP: ${e.code()} - ${e.message()}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "HttpException al cargar médico", e)
                    inputMedico.setText("Error")
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error de red al cargar médico: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "IOException al cargar médico", e)
                    inputMedico.setText("Error de red")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error inesperado al cargar médico: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "Excepción al cargar médico", e)
                    inputMedico.setText("Error inesperado")
                }
            }
        }
    }

    private fun solicitarCita() {
        val fecha = inputFechaCita.text.toString()
        val sintomas = findViewById<EditText>(R.id.inputSintomas).text.toString()

        // ¡Validar que selectedTurnoId no sea nulo!
        if (fecha.isBlank() || sintomas.isBlank() || selectedEspecialidad.isNullOrEmpty() || selectedTurno.isNullOrEmpty() || selectedMedicoId == null || selectedTurnoId == null) {
            Toast.makeText(this, "Por favor completa todos los campos y selecciona un médico con un turno válido.", Toast.LENGTH_SHORT).show()
            return
        }

        val citaRequest = CitaRequest(
            idTurno = selectedTurnoId!!,
            fecha = fecha,
            sintomas = sintomas
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = disponibilidadApi.agendarCita(citaRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val citaAgendada = response.body()
                        Toast.makeText(this@CreateAppointmentActivity, "Cita agendada con éxito: Cita ID ${citaAgendada?.idCita}", Toast.LENGTH_LONG).show()
                        Log.d("CreateAppointment", "Cita agendada: $citaAgendada")
                        programarNotificacion(fecha, selectedTurno!!)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@CreateAppointmentActivity, "Error al agendar cita: ${response.code()} - ${errorBody ?: "Error desconocido"}", Toast.LENGTH_LONG).show()
                        Log.e("CreateAppointment", "Error API agendar cita: ${response.code()}, Cuerpo: $errorBody")
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error HTTP al agendar cita: ${e.code()} - ${e.message()}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "HttpException al agendar cita", e)
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error de red al agendar cita: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "IOException al agendar cita", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateAppointmentActivity, "Error inesperado al agendar cita: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("CreateAppointment", "Excepción al agendar cita", e)
                }
            }
        }
    }

    private fun programarNotificacion(fechaCita: String, turno: String) {
        Log.d("CreateAppointment", "Iniciando programación de notificación")

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            Log.d("CreateAppointment", "Handler ejecutado - enviando broadcast")

            val intent = Intent(this, NotificationReceiver::class.java)
            intent.putExtra("title", "¡Recuerda tu cita médica!")
            intent.putExtra("message", "Tu cita médica empezará dentro de 1 hora. ¡Prepárate para llegar a tiempo!")

            sendBroadcast(intent)
            Log.d("CreateAppointment", "Broadcast enviado")

        }, 5000)

        Log.d("CreateAppointment", "Handler programado para 5 segundos")
    }
}