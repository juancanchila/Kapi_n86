package com.kapi.kapi_n86.data.api

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import com.credibanco.demosdk.IntegrationClientSDK
import com.credibanco.demosdk.IntegrationPeripherialSDK
import com.credibanco.demosdk.ResultIntegrationSDK
import com.credibanco.demosdk.util.*
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext

class DeviceService(private val context: Context) : ResultIntegrationSDK {
    private val authService = AuthenticationService(context)
    private val client = HttpClient() {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
        expectSuccess = false
    }
    private val nfcResultChannel = Channel<String>(Channel.CONFLATED)

    override fun resultActivity(activityResult: ActivityResult) {
        Log.d("NFCService", "Evaluando la respuesta: ${activityResult.resultCode}")

        if (activityResult.resultCode == RESULT_SELL_CODE) {
            val bundle = activityResult.data?.extras
            bundle?.let { bundleIt ->
                val autorizationCode: String? = bundleIt.getString(AUTORIZATION_SELL_APPROVED)
                val monto: String? = bundleIt.getString(TOTAL_AMOUNT_APROVED)
                val iva: String? = bundleIt.getString(IVA_TO_PRINT)
                val receipt: String? = bundleIt.getString(RECEIPT_TO_PRINT)
                val rrn: String? = bundleIt.getString(RRN_TO_PRINT)
                val terminalId: String? = bundleIt.getString(TERMINAL_ID)
                val timeDate: String? = bundleIt.getString(TIMEDATE_TO_PRINT)
                val responseCode: String? = bundleIt.getString(RESPONSE_CODE)
                val franchise: String? = bundleIt.getString(FRANCHISE_TO_PRINT)
                val accountType: String? = bundleIt.getString(ACCTYPE_TO_PRINT)
                val quotas: String? = bundleIt.getString(QUOTAS_TO_PRINT)
                val lastFourDigitsCard: String? = bundleIt.getString(LAST4_TO_PRINT)
                val merchantPosId: String? = bundleIt.getString(MERCHANT_POS_ID)
            }
        }
        when (activityResult.resultCode) {
            RESULT_PRINT_CODE -> {
                val bundle = activityResult.data?.extras
                // Obtener el mensaje de la impresión desde el Intent
                bundle?.let { bundleIt ->
                    val returnString: String? = bundleIt.getString("PRINT_READ_TAG")
                    // Mostrar el mensaje usando Snackbar
                    returnString?.let { message ->
                        val rootView = (context as? Activity)?.findViewById<View>(android.R.id.content)
                        rootView?.let {
                            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                    }
                }
            }
        }
    }

    internal fun startSell(
        context: Context,
        amount: String,
        tax: String,
        tip: String,
        iac: String,
        firstParameter: String? = null
    ) {
        Log.d("SellFragment", "Amount: $amount, Tax: $tax, Tip: $tip, IAC: $iac")

        IntegrationClientSDK.getSmartPosInstance().startSell(
            context,
            amount,
            tax,
            tip,
            iac,
            "FkbUjU0=", // Se asume que "FkbUjU0=" es el hashCode por defecto
            firstParameter,
            null,
            this@DeviceService
        )
    }

    private fun startAnnulment(context: Context, receiptID: String) {
        val hashCode = "FkbUjU0=" // Código de hash necesario para la operación, asegúrate de proporcionarlo

        // Llamar a startAnnulment con los parámetros requeridos
        IntegrationClientSDK.getSmartPosInstance().startAnnulment(context, receiptID, hashCode, this)
    }

    private fun startSellQR(context: Context, ammount: String, tax: String, tip: String, iac: String) {
        Log.d("SellQrFragment", "Amount: $ammount, Tax: $tax, Tip: $tip, IAC: $iac")
        IntegrationClientSDK.getSmartPosInstance().startSellQR(
            context,
            ammount,
            tax,
            tip,
            iac,
            "FkbUjU0=",
            resultIntegrationSDK = this
        )
    }
    suspend fun calculateMinValue(): String = withContext(Dispatchers.IO) {
        // Simplemente devuelve "0004" como número de transacción
        return@withContext "00070"
    }
    suspend fun imprimirTotales(): String {
        // Texto a imprimir
        val valuesToSend = ArrayList<String>()
        // Definir los valores para los parámetros necesarios
        val typeface = TYPEFACE_DEFAULT // Tipo de fuente predeterminado
        val letterSpacing = 0 // Espaciado entre letras, ajusta según sea necesario
        val grayLevel = GRAY_LEVEL_2 // Nivel de gris predeterminado
        val hashCode = "FkbUjU0=" // Código de hash necesario para la operación, asegúrate de proporcionarlo
        // Lista de valores aleatorios para enviar
        valuesToSend.add("Hola MUndo")
        // Agregar valores aleatorios a la lista
        repeat(10) {
            valuesToSend.add("Valor_$it") // Ajusta el formato o contenido según sea necesario
        }
        // Imprimir las líneas de texto
        IntegrationPeripherialSDK.getSmartPosInstancePeripherals().starPrint(
            context,
            TYPEFACE_DEFAULT,
            6,
            GRAY_LEVEL_2,
            hashCode,
            valuesToSend,
            this@DeviceService
        )
        // Devolver un mensaje indicando que se inició la impresión
        return "Se ha enviado la solicitud para imprimir la lista de valores aleatorios"
    }

/*
    suspend fun imprimirTransaccion(transactionId: Int): String = withContext(Dispatchers.IO) {
        Log.d("Service", "Imprimir transacción con ID: $transactionId")
        try {
            // Consultar la transacción
            val transactionResponse = consultarTransaccion(1)
            Log.d("Service", "Imprimir transacción : $transactionResponse")
            // Preparar el texto con los detalles de la transacción
            val texto = """
            ID: ${transactionResponse["id"]}
            Nombre de la transacción: ${transactionResponse["transaction_name"]}
            UID Externo: ${transactionResponse["uid_externo"]}
            UID Interno: ${transactionResponse["uid_interno"]}
            Estado: ${transactionResponse["status"]}
            Monto: ${transactionResponse["amount"]}
            Método de pago: ${transactionResponse["payment_method"]}
            Fecha y hora de la transacción: ${transactionResponse["date_time_transaction"]}
            IVA: ${transactionResponse["iva"]}
            Retefuente: ${transactionResponse["retefuente"]}
            IGMF: ${transactionResponse["igmf"]}
            BCCOMISION: ${transactionResponse["bccomision"]}
            KAPICOMISION: ${transactionResponse["kapicomision"]}
            Total Amount: ${transactionResponse["total_amount"]}
            ID Transacción Sonda: ${transactionResponse["id_transaccion_sonda"]}
            Saldo Actualizado: ${transactionResponse["saldo_actualizado"]}
            ID Transacción TEF: ${transactionResponse["id_transaccion_tef"]}
            TEF Transaction: ${transactionResponse["tef_transaction"]}
        """.trimIndent()
            // Definir los valores para los parámetros necesarios para imprimir
            val typeface = TYPEFACE_DEFAULT // Tipo de fuente predeterminado
            val letterSpacing = 0 // Espaciado entre letras, ajusta según sea necesario
            val grayLevel = GRAY_LEVEL_2 // Nivel de gris predeterminado
            val hashCode = "FkbUjU0=" // Código de hash necesario para la operación, asegúrate de proporcionarlo
            // Lista de valores a enviar para imprimir
            val valuesToSend = arrayListOf(texto)
            // Imprimir el texto
            IntegrationPeripherialSDK.getSmartPosInstancePeripherals().starPrint(
                context,
                typeface,
                letterSpacing.toInt(),
                grayLevel,
                hashCode,
                valuesToSend,
                this@TCService
            )
            // Devolver un mensaje indicando que se inició la impresión
            return@withContext "Se ha enviado la solicitud para imprimir la transacción"
        } catch (e: Exception) {
            // Manejar cualquier error que ocurra durante el proceso
            return@withContext "Error al imprimir la transacción: ${e.message}"
        }
    }
*/
    // Otros métodos que necesites para interactuar con el dispositivo

}
