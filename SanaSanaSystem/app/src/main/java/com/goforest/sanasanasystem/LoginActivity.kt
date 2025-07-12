package com.goforest.sanasanasystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class LoginActivity : AppCompatActivity() {

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class TokenResponse(
        val access_token: String,
        val token_type: String
    )
    data class LoginResponse(
        val dni: String,
        val email: String,
        val nombres: String,
        val apellidos: String,
        val token: TokenResponse
    )

    interface ApiService {
        @POST("login")
        fun login(@Body request: LoginRequest): Call<LoginResponse>
    }

private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/api/auth/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    private lateinit var inputCorreoTelefono: EditText
    private lateinit var spinnerTipoDocumento: Spinner
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var textRegister: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputCorreoTelefono = findViewById(R.id.inputCorreoTelefono)
        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento)
        inputPassword = findViewById(R.id.inputPassword)
        btnLogin = findViewById(R.id.btnLogin)
        textRegister = findViewById(R.id.textRegister)
        progressBar = findViewById(R.id.progressBar)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.document_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDocumento.adapter = adapter

        btnLogin.setOnClickListener {
            performLogin()
        }

        textRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        inputCorreoTelefono.error = null
        inputPassword.error = null

        val correoTelefono = inputCorreoTelefono.text.toString().trim()
        val password = inputPassword.text.toString().trim()

        var isValid = true

        if (correoTelefono.isEmpty()) {
            inputCorreoTelefono.error = "El correo o teléfono es requerido."
            isValid = false
        }
        else if (!correoTelefono.contains("@") && correoTelefono.length < 9) {
            inputCorreoTelefono.error = "Ingresa un correo válido o un número de teléfono completo."
            isValid = false
        }

        if (password.isEmpty()) {
            inputPassword.error = "La contraseña es requerida."
            isValid = false
        } else if (password.length < 6) { // Ejemplo: La contraseña debe tener al menos 6 caracteres
            inputPassword.error = "La contraseña debe tener al menos 6 caracteres."
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Por favor, corrige los campos marcados.", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val loginRequest = LoginRequest(email = correoTelefono, password = password)

        api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showLoading(false)

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    val sharedPreferences = getSharedPreferences("sana_sana_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("auth_token", user.token.access_token)
                    editor.putString("user_email", user.email)

                    val firstName = user.nombres.split(" ").firstOrNull() ?: "Usuario"
                    editor.putString("user_first_name", firstName)

                    editor.apply()

                    Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseApiError(response.code(), errorBody)
                    showErrorDialog("Error de Inicio de Sesión", errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                showErrorDialog("Error de Conexión", "No se pudo conectar al servidor. Verifica tu conexión a internet o inténtalo más tarde.")
                t.printStackTrace()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !isLoading
        inputCorreoTelefono.isEnabled = !isLoading
        inputPassword.isEnabled = !isLoading
        textRegister.isEnabled = !isLoading
        spinnerTipoDocumento.isEnabled = !isLoading
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    data class ErrorResponse(val detail: String)

    private fun parseApiError(statusCode: Int, errorBody: String?): String {
        return try {
            if (errorBody != null) {
                val error = Gson().fromJson(errorBody, ErrorResponse::class.java)
                when (statusCode) {
                    401 -> {
                        when (error.detail) {
                            "Account not found" -> "Usuario no encontrado. Por favor, verifica tu correo."
                            "Incorrect Password" -> "Contraseña incorrecta. Intenta de nuevo."
                            "Account is blocked" -> "Tu cuenta está bloqueada. Contacta a soporte."
                            else -> "Credenciales inválidas: ${error.detail}"
                        }
                    }
                    404 -> "Recurso no encontrado: ${error.detail}"
                    500 -> "Error interno del servidor: ${error.detail}. Intenta de nuevo más tarde."
                    else -> "Error: ${error.detail} (Código: $statusCode)"
                }
            } else {
                "Error desconocido (Código: $statusCode). No se recibió mensaje del servidor."
            }
        } catch (e: Exception) {
            "Error al procesar la respuesta del servidor (Código: $statusCode). Intenta de nuevo más tarde."
        }
    }

}