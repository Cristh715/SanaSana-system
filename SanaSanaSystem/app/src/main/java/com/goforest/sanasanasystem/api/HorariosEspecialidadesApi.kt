package com.goforest.sanasanasystem.api

import retrofit2.http.GET

interface HorariosEspecialidadesApi {
    @GET("disponibilidad/horarios-disponibles")
    suspend fun getHorariosEspecialidades(): List<EspecialidadDisponible>
} 