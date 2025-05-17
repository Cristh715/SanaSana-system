package com.goforest.sanasanasystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class LoginActivity : AppCompatActivity() {

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class LoginResponse(
        val id_cuenta: Int,
        val correo: String,
        val token: String
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val inputCorreoTelefono = findViewById<EditText>(R.id.inputCorreoTelefono)
        val spinnerTipoDocumento = findViewById<Spinner>(R.id.spinnerTipoDocumento)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val textRegister = findViewById<TextView>(R.id.textRegister)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.document_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDocumento.adapter = adapter

        btnLogin.setOnClickListener {
            val correoTelefono = inputCorreoTelefono.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (correoTelefono.isNotEmpty() && password.isNotEmpty()) {
                val loginRequest = LoginRequest(email = correoTelefono, password = password)

                api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            val user = response.body()!!

                            val sharedPreferences = getSharedPreferences("sana_sana_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("auth_token", user.token)
                            editor.putInt("user_id", user.id_cuenta)
                            editor.putString("user_email", user.correo)
                            editor.apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Inicio de sesión exitoso",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Credenciales inválidas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error de red: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        textRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
