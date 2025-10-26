package com.example.data.model

data class Socio(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val email: String,
    val telefono: String,
    val fechaAlta: String // formato ISO: "2025-10-25"
)
