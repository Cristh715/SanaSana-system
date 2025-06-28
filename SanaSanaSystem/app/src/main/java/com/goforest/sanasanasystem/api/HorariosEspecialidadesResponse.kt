package com.goforest.sanasanasystem.api

data class HorarioDisponible(
    val turno: String
)

data class MedicoDisponible(
    val nombre_completo: String,
    val horarios: List<HorarioDisponible>
)

data class EspecialidadDisponible(
    val especialidad: String,
    val medicos: List<MedicoDisponible>
) 