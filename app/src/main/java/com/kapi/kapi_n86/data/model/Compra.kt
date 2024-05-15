package com.kapi.kapi_n86.data.model

data class Compra(
    val userId: Int,
    val transactionName: String,
    val uidExterno: String,
    val uidInterno: String,
    val status: String,
    val amount: Int,
    val paymentMethod: String,
    val dateTimeTransaction: String,
    val iva: Int,
    val totalAmount: Int
)
