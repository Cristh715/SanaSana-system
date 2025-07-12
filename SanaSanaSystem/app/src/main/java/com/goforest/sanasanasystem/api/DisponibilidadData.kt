package com.goforest.sanasanasystem.api

import com.google.gson.annotations.SerializedName

data class EspecialidadResponse(
    @SerializedName("especialidad")
    val especialidad: String
)

data class MedicoPorTurnoResponse(
    @SerializedName("id_medico")
    val idMedico: Int,
    @SerializedName("id_turno")
    val idTurno: Int,
    @SerializedName("nombres")
    val nombres: String,
    @SerializedName("apellidos")
    val apellidos: String,
    @SerializedName("especialidad")
    val especialidad: String,
    @SerializedName("nombre_completo")
    val nombreCompleto: String,
    @SerializedName("correo")
    val correo: String?
)

data class MedicosDisponiblesResponse(
    @SerializedName("medicos")
    val medicos: List<MedicoPorTurnoResponse>
)

data class CitaRequest(
    @SerializedName("id_turno") val idTurno: Int,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("sintomas") val sintomas: String
)

data class CitaResponse(
    @SerializedName("id_cita") val idCita: Int,
    @SerializedName("id_paciente") val idPaciente: Int,
    @SerializedName("id_turno") val idTurno: Int,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("sintomas") val sintomas: String,
    @SerializedName("especialidad") val especialidad: String,
    @SerializedName("turno") val turno: String,
    @SerializedName("numero_turno") val numeroTurno: Int,
    @SerializedName("qr_codigo") val qrCodigo: String?,
    @SerializedName("fecha_creacion") val fechaCreacion: String?,
    @SerializedName("prioridad_emergencia") val prioridadEmergencia: String
)