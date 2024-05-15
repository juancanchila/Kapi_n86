package com.kapi.kapi_n86.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
    data class TCard(
    val resultado: Long,
    val mensaje: String,
    val estadoMA: Long,
    val descripcion: String,
    val tecnologia: Long,
    val uidtarjeta: Long,
    val tipotarjeta: Long,
    val descTipoTarjeta: String,
    val numABT: String,
    val numeroInterno: Long,
    val numeroExterno: Long,
    val uuidMovil: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val rut: String
) {
    companion object {
        fun serializer(): KSerializer<TCard> = TCard.serializer()

        fun fromJson(json: String?): TCard {
            return Json.decodeFromString(serializer(), json ?: "{}")
        }
    }
}
