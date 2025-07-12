package com.goforest.sanasanasystem

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goforest.sanasanasystem.api.ApiClient
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST

class GetRecommendationActivity: AppCompatActivity() {

    data class RecomendationRequest (
        val prompt: String
    )

    data class RecomendationResponse (
        val especialidad: String,
        val detalle: String,
    )

    interface RecommendationService {
        @POST("api/ai/recommendation")
        fun getAIRecommendation(@Body request: RecomendationRequest): Call<RecomendationResponse>
    }

    private lateinit var inputSintomas: EditText
    private lateinit var outputEspecialidad: TextView
    private lateinit var outputDetalle: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_recommendation)

        inputSintomas = findViewById(R.id.inputSintomas)
        outputEspecialidad =  findViewById(R.id.outputEspecialidad)
        outputDetalle =  findViewById(R.id.outputDetalle)
        progressBar = findViewById(R.id.progressBar)

        val btnAnalizar = findViewById<Button>(R.id.btnAnalizar)
        btnAnalizar.setOnClickListener {
            getRecommendation()
        }

        val btnVolver = findViewById<ImageButton>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun getRecommendation() {
        inputSintomas.error = null

        val sintomas = inputSintomas.text.toString().trim()
        var isValid = true

        if (sintomas.isEmpty()) {
            inputSintomas.error = "Debe ingresar una descripción de sus sintomas"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Por favor, corrige los campos marcados.", Toast.LENGTH_SHORT).show()
            return
        }

        fetchAIRecommendation(sintomas)

    }

    private fun fetchAIRecommendation(sintomas: String) {
        progressBar.visibility = View.VISIBLE

        val request = RecomendationRequest(prompt = sintomas)
        val service = ApiClient.getClient(this).create(RecommendationService::class.java)
        service.getAIRecommendation(request).enqueue(object : Callback<RecomendationResponse> {
            override fun onResponse(call: Call<RecomendationResponse>, response: Response<RecomendationResponse>) {
                progressBar.visibility = View.GONE
                val data = response.body()
                outputEspecialidad.text = data?.especialidad
                outputDetalle.text = "Debido a que " + data?.detalle
            }
            override fun onFailure(call: Call<RecomendationResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@GetRecommendationActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}