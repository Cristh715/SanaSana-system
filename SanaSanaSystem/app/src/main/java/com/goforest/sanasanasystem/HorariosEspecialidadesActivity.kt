package com.goforest.sanasanasystem

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goforest.sanasanasystem.api.EspecialidadDisponible
import com.goforest.sanasanasystem.api.HorariosEspecialidadesApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import android.widget.ImageButton

class HorariosEspecialidadesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMensaje: TextView
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios_especialidades)

        recyclerView = findViewById(R.id.recyclerEspecialidades)
        progressBar = ProgressBar(this)
        tvMensaje = TextView(this)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Agregar el ProgressBar y mensaje al layout si no existen
        val root = findViewById<View>(android.R.id.content) as View
        if (progressBar.parent == null) {
            (root as? ViewGroup)?.addView(progressBar)
        }
        if (tvMensaje.parent == null) {
            (root as? ViewGroup)?.addView(tvMensaje)
        }
        progressBar.visibility = View.VISIBLE
        tvMensaje.visibility = View.GONE

        obtenerHorariosEspecialidades()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun obtenerHorariosEspecialidades() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // Emulador Android accede a backend local
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(HorariosEspecialidadesApi::class.java)

        scope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                tvMensaje.visibility = View.GONE
                val especialidades = api.getHorariosEspecialidades()
                progressBar.visibility = View.GONE
                if (especialidades.isEmpty()) {
                    tvMensaje.text = "No hay horarios disponibles."
                    tvMensaje.visibility = View.VISIBLE
                } else {
                    recyclerView.adapter = EspecialidadAdapter(especialidades)
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                tvMensaje.text = "Error al cargar los datos."
                tvMensaje.visibility = View.VISIBLE
                Log.e("API_ERROR", "Error al cargar horarios", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
} 