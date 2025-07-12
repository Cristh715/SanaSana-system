package com.goforest.sanasanasystem

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    data class RegistroUsuario(
        val nombres: String,
        val apellidoPaterno: String,
        val apellidoMaterno: String,
        val dni: String,
        val digitoVerificador: String,
        val correo: String,
        val telefono: String,
        val fecha_nacimiento: String,
        val password: String,
        val password_confirm: String
    )

    interface ApiService {
        @POST("register")
        fun registrarUsuario(@Body usuario: RegistroUsuario): Call<Void>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/api/auth/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    private lateinit var inputNombres: EditText
    private lateinit var inputApellidoP: EditText
    private lateinit var inputApellidoM: EditText
    private lateinit var inputDNI: EditText
    private lateinit var inputDigito: EditText
    private lateinit var inputCorreo: EditText
    private lateinit var inputTelefono: EditText
    private lateinit var inputFechaNacimiento: EditText
    private lateinit var inputContrasena: EditText
    private lateinit var inputContrasena2: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnVolver: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        inputNombres = findViewById(R.id.inputNombres)
        inputApellidoP = findViewById(R.id.inputApellidoP)
        inputApellidoM = findViewById(R.id.inputApellidoM)
        inputDNI = findViewById(R.id.inputDNI)
        inputDigito = findViewById(R.id.inputDigito)
        inputCorreo = findViewById(R.id.inputCorreo)
        inputTelefono = findViewById(R.id.inputTelefono)
        inputFechaNacimiento = findViewById(R.id.inputFechaNacimiento)
        inputContrasena = findViewById(R.id.inputContrasena)
        inputContrasena2 = findViewById(R.id.inputContrasena2)
        btnRegister = findViewById(R.id.btnLogin)
        btnVolver = findViewById(R.id.btnVolver)
        progressBar = findViewById(R.id.progressBarRegister)

        inputFechaNacimiento.setOnClickListener {
            showDatePickerDialog()
        }

        btnRegister.setOnClickListener {
            attemptRegister()
        }

        btnVolver.setOnClickListener {
            onBackPressed()
        }
    }

    private fun attemptRegister() {
        clearErrors()

        val nombres = inputNombres.text.toString().trim()
        val apellidoPaterno = inputApellidoP.text.toString().trim()
        val apellidoMaterno = inputApellidoM.text.toString().trim()
        val dni = inputDNI.text.toString().trim()
        val digitoVerificador = inputDigito.text.toString().trim()
        val correo = inputCorreo.text.toString().trim()
        val telefono = inputTelefono.text.toString().trim()
        val fechaNacimiento = inputFechaNacimiento.text.toString().trim()
        val password = inputContrasena.text.toString().trim()
        val passwordConfirm = inputContrasena2.text.toString().trim()

        var isValid = true

        if (nombres.isEmpty()) {
            inputNombres.error = "Tus nombres son requeridos."
            isValid = false
        }
        if (apellidoPaterno.isEmpty()) {
            inputApellidoP.error = "El apellido paterno es requerido."
            isValid = false
        }
        if (apellidoMaterno.isEmpty()) {
            inputApellidoM.error = "El apellido materno es requerido."
            isValid = false
        }
        if (dni.isEmpty() || dni.length != 8) {
            inputDNI.error = "El DNI debe tener 8 dígitos."
            isValid = false
        }
        if (digitoVerificador.isEmpty() || digitoVerificador.length != 1) {
            inputDigito.error = "El dígito verificador es 1 solo número."
            isValid = false
        }
        if (correo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            inputCorreo.error = "Ingresa un correo electrónico válido."
            isValid = false
        }
        if (telefono.isEmpty() || telefono.length < 9) {
            inputTelefono.error = "El número de teléfono debe tener al menos 9 dígitos."
            isValid = false
        }
        if (fechaNacimiento.isEmpty()) {
            inputFechaNacimiento.error = "La fecha de nacimiento es requerida."
            isValid = false
        }
        if (password.isEmpty() || password.length < 6) {
            inputContrasena.error = "La contraseña debe tener al menos 6 caracteres."
            isValid = false
        }
        if (passwordConfirm.isEmpty()) {
            inputContrasena2.error = "Por favor, repite tu contraseña."
            isValid = false
        }
        if (password != passwordConfirm) {
            inputContrasena2.error = "Las contraseñas no coinciden."
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Por favor, corrige los campos marcados.", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = RegistroUsuario(
            nombres = nombres,
            apellidoPaterno = apellidoPaterno,
            apellidoMaterno = apellidoMaterno,
            correo = correo,
            telefono = telefono,
            dni = dni,
            digitoVerificador = digitoVerificador,
            fecha_nacimiento = fechaNacimiento,
            password = password,
            password_confirm = passwordConfirm
        )

        enviarDatosAlBackend(usuario)
    }

    private fun enviarDatosAlBackend(usuario: RegistroUsuario) {
        showLoading(true)

        api.registrarUsuario(usuario).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                showLoading(false)

                if (response.isSuccessful) {
                    showSuccessDialog("Registro Exitoso", "¡Tu cuenta ha sido creada con éxito! Ahora puedes iniciar sesión.") {
                        finish()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseApiError(response.code(), errorBody)
                    showErrorDialog("Error de Registro", errorMessage)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Ocultar loader y habilitar botón
                showLoading(false)
                showErrorDialog("Error de Conexión", "No se pudo conectar al servidor. Por favor, verifica tu conexión a internet o inténtalo más tarde.")
                t.printStackTrace()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !isLoading
        btnVolver.isEnabled = !isLoading
        inputNombres.isEnabled = !isLoading
        inputApellidoP.isEnabled = !isLoading
        inputApellidoM.isEnabled = !isLoading
        inputDNI.isEnabled = !isLoading
        inputDigito.isEnabled = !isLoading
        inputCorreo.isEnabled = !isLoading
        inputTelefono.isEnabled = !isLoading
        inputFechaNacimiento.isEnabled = !isLoading
        inputContrasena.isEnabled = !isLoading
        inputContrasena2.isEnabled = !isLoading
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSuccessDialog(title: String, message: String, onDismiss: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                onDismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                inputFechaNacimiento.setText(sdf.format(selectedDate.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun clearErrors() {
        inputNombres.error = null
        inputApellidoP.error = null
        inputApellidoM.error = null
        inputDNI.error = null
        inputDigito.error = null
        inputCorreo.error = null
        inputTelefono.error = null
        inputFechaNacimiento.error = null
        inputContrasena.error = null
        inputContrasena2.error = null
    }

    private fun parseApiError(statusCode: Int, errorBody: String?): String {
        return try {
            if (errorBody != null && errorBody.contains("message")) {
                "Error del servidor (Código: $statusCode): ${errorBody}"
            } else {
                "Error del servidor (Código: $statusCode). No se pudo obtener un mensaje detallado."
            }
        } catch (e: Exception) {
            "Error del servidor (Código: $statusCode). Problema al procesar la respuesta de error."
        }
    }
}