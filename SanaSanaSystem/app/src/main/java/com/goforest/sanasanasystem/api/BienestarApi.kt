package com.goforest.sanasanasystem.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BienestarApi {
    @POST("api/bienestar")
    suspend fun registrarBienestar(@Body request: BienestarRequest): Response<BienestarResponse>
}