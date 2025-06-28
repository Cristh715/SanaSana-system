package com.goforest.sanasanasystem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.goforest.sanasanasystem.api.ApiClient
import com.goforest.sanasanasystem.api.MedicalHistoryItem
import com.goforest.sanasanasystem.api.MedicalHistoryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageView

class MedicalHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicalHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)

        recyclerView = findViewById(R.id.medicalHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MedicalHistoryAdapter(listOf())
        recyclerView.adapter = adapter

        fetchMedicalHistory()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchMedicalHistory() {
        val service = ApiClient.getClient(this).create(MedicalHistoryService::class.java)
        service.getMedicalHistory().enqueue(object : Callback<List<MedicalHistoryItem>> {
            override fun onResponse(call: Call<List<MedicalHistoryItem>>, response: Response<List<MedicalHistoryItem>>) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateData(response.body()!!)
                } else {
                    Toast.makeText(this@MedicalHistoryActivity, "No se pudo obtener el historial médico", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<MedicalHistoryItem>>, t: Throwable) {
                Toast.makeText(this@MedicalHistoryActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 