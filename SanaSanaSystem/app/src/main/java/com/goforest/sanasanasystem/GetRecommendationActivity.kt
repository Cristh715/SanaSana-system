package com.goforest.sanasanasystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET

class GetRecommendationActivity: AppCompatActivity() {

    data class RecomendationRequest (
        val prompt: String
    )

    data class RecomendatioResponse (
        val especialidad: String,
        val turno: String,
    )

    interface ApiService {
        @GET("recommendation")
        fun recommendation(@Body request: RecomendationRequest): Call<RecomendatioResponse>
    }

    //private val retrofit = Retrofit.Builder()
    //    .baseUrl("http://10.0.2.2:8000/api/ai/recommendation")
    //    .addConverterFactory(GsonConverterFactory.create())
    //    .build()

    //private val api = retrofit.create(ApiService::class.java)

    private lateinit var inputSintomas: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_recommendation)

//        inputSintomas = findViewById(R.id.inputSintomas)

        val btnAnalizar = findViewById<Button>(R.id.btnAnalizar)
        btnAnalizar.setOnClickListener {
            // TODO: Call AI recommendation api
        }

    }
}