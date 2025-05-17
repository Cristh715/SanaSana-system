package com.goforest.sanasanasystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

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
        .baseUrl("http://10.0.2.2:8000/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val inputNombres = findViewById<EditText>(R.id.inputNombres)
        val inputApellidoP = findViewById<EditText>(R.id.inputApellidoP)
        val inputApellidoM = findViewById<EditText>(R.id.inputApellidoM)
        val inputDNI = findViewById<EditText>(R.id.inputDNI)
        val inputDigito = findViewById<EditText>(R.id.inputDigito)
        val inputCorreo = findViewById<EditText>(R.id.inputCorreo)
        val inputTelefono = findViewById<EditText>(R.id.inputTelefono)
        val inputFechaNacimiento = findViewById<EditText>(R.id.inputFechaNacimiento)
        val inputContrasena = findViewById<EditText>(R.id.inputContrasena)
        val inputContrasena2 = findViewById<EditText>(R.id.inputContrasena2)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnVolver = findViewById<ImageButton>(R.id.btnVolver)

        btnLogin.setOnClickListener {
            val usuario = RegistroUsuario(
                nombres = inputNombres.text.toString().trim(),
                apellidoPaterno = inputApellidoP.text.toString().trim(),
                apellidoMaterno = inputApellidoM.text.toString().trim(),
                correo = inputCorreo.text.toString().trim(),
                telefono = inputTelefono.text.toString().trim(),
                dni = inputDNI.text.toString().trim(),
                digitoVerificador = inputDigito.text.toString().trim(),
                fecha_nacimiento = inputFechaNacimiento.text.toString().trim(),
                password = inputContrasena.text.toString().trim(),
                password_confirm = inputContrasena2.text.toString().trim()
            )

            if (usuario.password != usuario.password_confirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            enviarDatosAlBackend(usuario)
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun enviarDatosAlBackend(usuario: RegistroUsuario) {
        api.registrarUsuario(usuario).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@RegisterActivity, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
