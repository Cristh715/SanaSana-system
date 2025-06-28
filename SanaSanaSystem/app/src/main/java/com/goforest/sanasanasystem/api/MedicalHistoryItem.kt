package com.goforest.sanasanasystem.api

data class MedicalHistoryItem(
    val id_cita: Int,
    val fecha: String,
    val especialidad: String,
    val medico: Map<String, String>?,
    val turno: String?,
    val sintomas: String?,
    val estado: String,
    val confirmacion_asistencia: Boolean,
    val qr_codigo: String?
) 