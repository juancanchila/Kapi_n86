package com.kapi.kapi_n86.data.model

data class TransactionResponse(
    val id: Int?,
    val transactionName: String?,
    val uidExterno: String?,
    val uidInterno: String?,
    val status: String?,
    val amount: String?,
    val paymentMethod: String?,
    val dateTimeTransaction: String?,
    val iva: String?,
    val retefuente: String?,
    val igmf: String?,
    val bccomision: String?,
    val kapicomision: String?,
    val totalAmount: String?,
    val idTransaccionSonda: Int?,
    val saldoActualizado: String?,
    val idTransaccionTef: Int?,
    val tefTransaction: String?

)
