package com.kapi.kapi_n86.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Factura(
    @SerialName("ID_Factura") val idFactura: Int,
    @SerialName("ID_Transaccion") val idTransaccion: Int
)