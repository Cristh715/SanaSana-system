package com.goforest.sanasanasystem.api

import com.google.gson.annotations.SerializedName

data class BienestarRequest(
    @SerializedName("estado_emocional")
    val estadoEmocional: String,
    @SerializedName("sintomas_fisicos")
    val sintomasFisicos: List<String>?,
    @SerializedName("nota_adicional")
    val notaAdicional: String?
)

data class BienestarResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("id_registro_bienestar")
    val idRegistroBienestar: Long?,
    @SerializedName("fecha_registro")
    val fechaRegistro: String? 
)