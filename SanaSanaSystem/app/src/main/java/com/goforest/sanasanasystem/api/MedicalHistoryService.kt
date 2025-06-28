package com.goforest.sanasanasystem.api

import retrofit2.Call
import retrofit2.http.GET

interface MedicalHistoryService {
    @GET("/api/citas/historial")
    fun getMedicalHistory(): Call<List<MedicalHistoryItem>>
} 