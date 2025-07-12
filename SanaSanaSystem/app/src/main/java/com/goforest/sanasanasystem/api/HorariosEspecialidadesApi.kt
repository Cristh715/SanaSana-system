package com.goforest.sanasanasystem.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HorariosEspecialidadesApi {
    @GET("disponibilidad/horarios-disponibles")
    suspend fun getHorariosEspecialidades(): List<EspecialidadDisponible>

    @GET("disponibilidad/especialidades")
    suspend fun getEspecialidades(): Response<List<EspecialidadResponse>>

    @GET("disponibilidad/medicos")
    suspend fun getMedicosPorEspecialidadYTurno(
        @Query("especialidad") especialidad: String,
        @Query("turno") turno: String
    ): Response<MedicosDisponiblesResponse>
    @POST("api/citas")
    suspend fun agendarCita(@Body cita: CitaRequest): Response<CitaResponse>
} 