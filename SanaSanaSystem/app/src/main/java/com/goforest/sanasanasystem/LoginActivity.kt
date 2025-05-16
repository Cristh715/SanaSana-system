package com.goforest.sanasanasystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val inputCorreoTelefono = findViewById<EditText>(R.id.inputCorreoTelefono)
        val spinnerTipoDocumento = findViewById<Spinner>(R.id.spinnerTipoDocumento)
        val inputDocumento = findViewById<EditText>(R.id.inputDocumento)
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
            val correoTelefono = inputCorreoTelefono.text.toString()
            val tipoDocumento = spinnerTipoDocumento.selectedItem.toString()
            val documento = inputDocumento.text.toString()
            val password = inputPassword.text.toString()

            if (correoTelefono.isNotEmpty() && documento.isNotEmpty() && password.isNotEmpty()) {
                Toast.makeText(this, "Iniciaste sesión", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        textRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

    }
}
