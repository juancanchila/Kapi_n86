package com.kapi.kapi_n86.data.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class Transacciones (
    val id: String,
    val date : String,
    val amount: Double
)