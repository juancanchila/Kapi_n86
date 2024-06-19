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
import com.google.gson.JsonParser
import com.kapi.kapi_n86.data.model.TCard
import io.ktor.client.*
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.net.URL
import com.credibanco.demosdk.util.TYPEFACE_DEFAULT
import com.google.android.material.snackbar.Snackbar
import com.kapi.kapi_n86.data.model.TransactionResponse
import kotlinx.coroutines.*
import com.credibanco.demosdk.util.SubExtraInfoDto
import com.credibanco.demosdk.util.RESULT_QR_SELL_CODE
import androidx.appcompat.app.AlertDialog
import android.widget.EditText

 class TCService(private val context: Context) : ResultIntegrationSDK {
     private var ultimoIdTransaccion: Int? = null
     private var ultimoMessage: String? = null
    private val authService = AuthenticationService(context)


     fun setUltimoIdTransaccion(id: Int) {
         ultimoIdTransaccion = id
     }

     fun setUltimoMessage(id: String) {
         ultimoMessage = id
     }
     fun getUltimoMessage(): String? {
         return ultimoMessage
     }

     // Método para obtener el último ID de transacción almacenado
     fun getUltimoIdTransaccion(): Int? {
         return ultimoIdTransaccion
     }
     fun clearUltimoIdTransaccion() {
         ultimoIdTransaccion = null
     }
    private val client = HttpClient() {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
        expectSuccess = false
    }

    private val TransactionResultChannel = Channel<TransactionResultData>()
    var numeroDeCompra: String? = null
    var ultimoRecibo: String? = null

     data class TransactionResultData( // Renombrado
         val status: String?,
         val facturaId: String?,
         val transaccionId: String?,
         val errorMessage: String?,
         val receipt: String?
     )

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




                Log.d("Numero de compra", "Numero de compra: $numeroDeCompra")
                ultimoRecibo = receipt
                val transactionResultResponse = TransactionResultData(
                    status = null,
                    facturaId = null,
                    transaccionId = null,
                    errorMessage = null,
                    receipt = receipt
                )

                val sendResult = TransactionResultChannel.trySend(transactionResultResponse)
                if (sendResult.isSuccess) {
                    Log.d("Transaction", "Transaction result sent successfully.")
                } else {
                    Log.e("Transaction", "Failed to send transaction result.")
                }
                Log.d("Resultado de la compra", "Código de autorización: $autorizationCode")
                Log.d("Resultado de la compra", "Monto: $monto")
                Log.d("Resultado de la compra", "IVA: $iva")
                Log.d("Resultado de la compra", "Recibo: $receipt")
                Log.d("Resultado de la compra", "RRN: $rrn")
                Log.d("Resultado de la compra", "ID del terminal: $terminalId")
                Log.d("Resultado de la compra", "Fecha y hora: $timeDate")
                Log.d("Resultado de la compra", "Código de respuesta: $responseCode")
                Log.d("Resultado de la compra", "Franquicia: $franchise")
                Log.d("Resultado de la compra", "Tipo de cuenta: $accountType")
                Log.d("Resultado de la compra", "Cuotas: $quotas")
                Log.d("Resultado de la compra", "Últimos cuatro dígitos de la tarjeta: $lastFourDigitsCard")
                Log.d("Resultado de la compra", "ID del POS del comerciante: $merchantPosId")
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
        when (activityResult.resultCode) {
            RESULT_QR_SELL_CODE-> {

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




                    Log.d("Numero de compra", "Numero de compra: $numeroDeCompra")
                    ultimoRecibo = receipt
                    val transactionResultResponse = TransactionResultData(
                        status = null,
                        facturaId = null,
                        transaccionId = null,
                        errorMessage = null,
                        receipt = receipt
                    )

                    val sendResult = TransactionResultChannel.trySend(transactionResultResponse)
                    if (sendResult.isSuccess) {
                        Log.d("Transaction", "Transaction result sent successfully.")
                    } else {
                        Log.e("Transaction", "Failed to send transaction result.")
                    }
                    Log.d("Resultado de la compra", "Código de autorización: $autorizationCode")
                    Log.d("Resultado de la compra", "Monto: $monto")
                    Log.d("Resultado de la compra", "IVA: $iva")
                    Log.d("Resultado de la compra", "Recibo: $receipt")
                    Log.d("Resultado de la compra", "RRN: $rrn")
                    Log.d("Resultado de la compra", "ID del terminal: $terminalId")
                    Log.d("Resultado de la compra", "Fecha y hora: $timeDate")
                    Log.d("Resultado de la compra", "Código de respuesta: $responseCode")
                    Log.d("Resultado de la compra", "Franquicia: $franchise")
                    Log.d("Resultado de la compra", "Tipo de cuenta: $accountType")
                    Log.d("Resultado de la compra", "Cuotas: $quotas")
                    Log.d("Resultado de la compra", "Últimos cuatro dígitos de la tarjeta: $lastFourDigitsCard")
                    Log.d("Resultado de la compra", "ID del POS del comerciante: $merchantPosId")
                }

            }

            QR_REJECTED -> {
                val bundle = activityResult.data?.extras
                if (bundle != null) {
                    val apiResult = bundle.getString(API_RESULT)
                    Log.d("TAG-1", "apiResult $apiResult")
                }
            }

            QR_ERROR_CODE -> {
                val bundle = activityResult.data?.extras
                if (bundle != null) {
                    val apiResult = bundle.getString(API_RESULT)
                    Log.d("TAG-1", "apiResult $apiResult")
                }
            }

        }




    }

     suspend fun recargaQR(
         uidExterno: String,
         uidInterno: String,
         amount: Int
     ): Deferred<String?> = coroutineScope {
         async(Dispatchers.IO) {
             val compra = crearCompraTC(uidExterno, uidInterno, amount)


             if (compra != null) {
                 numeroDeCompra = compra[1]

                 startSellQR(
                     context,
                     amount.toString(),
                     "0",
                     "0",
                     "0",

                     compra[1].toString()
                 )

                 val transactionResult = TransactionResultChannel.receive()
                 // El recibo estará en la propiedad 'receipt' del objeto TransactionResult
                 val receipt = transactionResult.receipt
                 Log.e("Service", "Datos de la factura consultada para completar: ${receipt}")


                 /**
                  * El entorno de pruebas esta esperando 2 como id de trsaccion
                  * perod e sdebe enviar como se muestra a continuación
                  *   //  val transactionResponse = consultarCompraPorToken(compra[1].toInt())
                  */


                 val transactionResponse_result =  consultarCompraPorToken(compra[1].toInt())
                 Log.e("Service", "Datos de la factura consultada para completar: ${transactionResponse_result}")

                 //Completar
                 //Imprimir

                 if (transactionResponse_result == "PENDING") {
                     // Llamar a completarCompraTC y luego a imprimirTransaccion
                     val completarCompraResultado = completarCompraTC(compra[1].toInt())
                     Log.e("Service", "Respuesta completar completar: ${completarCompraResultado}")
                     return@async "0"
                 } else {
                     // Llamar a startAnnulment enviando compra[1]
                     startAnnulment(context,compra[1])
                     // Aquí debes decidir qué valor quieres devolver en este caso,
                     // ya que no puedo determinar exactamente qué debería ser.
                     // Podrías devolver un valor predeterminado o lanzar una excepción.
                     return@async "-1"
                 }


             } else {
                 // Mostrar un pop-up o mensaje indicando que el servicio no está disponible
                 return@async "-1"
             }
         }
     }


    suspend fun recargaTC(
        uidExterno: String,
        uidInterno: String,
        amount: Int
    ): Deferred<String?> = coroutineScope {
        async(Dispatchers.IO) {
            val compra = crearCompraTC(uidExterno, uidInterno, amount)

            if (compra != null) {
                numeroDeCompra = compra[1]

                startSell(
                    context,
                    amount.toString(),
                    "0",
                    "0",
                    "0",
                    compra[1].toString()
                )

                val transactionResult = TransactionResultChannel.receive()
                // El recibo estará en la propiedad 'receipt' del objeto TransactionResult
                val receipt = transactionResult.receipt
                Log.e("Service", "Datos de la factura consultada para completar: ${receipt}")


                /**
                 * El entorno de pruebas esta esperando 2 como id de trsaccion
                 * perod e sdebe enviar como se muestra a continuación
                 *   //  val transactionResponse = consultarCompraPorToken(compra[1].toInt())
                 */



                val transactionResponse_result =  consultarCompraPorToken(compra[1].toInt())
                Log.e("Service", "Datos de la factura consultada para completar: ${transactionResponse_result}")

                //Completar
                //Imprimir


                if (transactionResponse_result == "PENDING") {
                    // Llamar a completarCompraTC y luego a imprimirTransaccion
                    val completarCompraResultado = completarCompraTC(compra[1].toInt())
                    Log.e("Service", "Respuesta completar completar: ${completarCompraResultado}")
                    return@async "0"
                } else {
                    // Llamar a startAnnulment enviando compra[1]
                    startAnnulment(context,compra[1])
                    // Aquí debes decidir qué valor quieres devolver en este caso,
                    // ya que no puedo determinar exactamente qué debería ser.
                    // Podrías devolver un valor predeterminado o lanzar una excepción.
                    return@async "-1"
                }


            } else {
                // Mostrar un pop-up o mensaje indicando que el servicio no está disponible
                return@async "-1"
            }
        }
    }

    data class TransactionResult(
        val status: String?,
        val facturaId: String? = null,
        val transaccionId: String? = null,
        val errorMessage: String? = null
    )

    enum class TransactionStatus {
        SUCCESS,
        FAILURE
    }

    suspend fun recargaDV(
        uidExterno: String,
        uidInterno: String,
        amount: Int
    ): Deferred<String?> = coroutineScope {
        async(Dispatchers.IO) {

            val compra = crearCompraTC(uidExterno, uidInterno, amount)


            if (compra != null) {
                numeroDeCompra = compra[1]
                Log.e("Service", "Datos de la factura: ${compra[1]}")
                startSell(
                    context,
                    amount.toString(),
                    "0",
                    "0",
                    "0",
                    compra[1].toString()
                )

                val transactionResult = TransactionResultChannel.receive()
                // El recibo estará en la propiedad 'receipt' del objeto TransactionResult
                val receipt = transactionResult.receipt
                Log.e("Service", "Datos de la factura consultada para completar: ${receipt}")


                /**
                 * El entorno de pruebas esta esperando 2 como id de trsaccion
                 * perod e sdebe enviar como se muestra a continuación
                 *   //  val transactionResponse = consultarCompraPorToken(compra[1].toInt())
                 */


                val transactionResponse_result =  consultarCompraPorToken(compra[1].toInt())
                Log.e("Service", "Datos de la factura consultada para completar: ${transactionResponse_result}")

                //Completar
                //Imprimir

                if (transactionResponse_result == "PENDING") {
                    // Llamar a completarCompraTC y luego a imprimirTransaccion
                 val completarCompraResultado = completarCompraTC(compra[1].toInt())


                  //  imprimirTransaccion( )
                    return@async "0"
                } else {
                    // Llamar a startAnnulment enviando compra[1]
                    startAnnulment(context,compra[1])
                    // Aquí debes decidir qué valor quieres devolver en este caso,
                    // ya que no puedo determinar exactamente qué debería ser.
                    // Podrías devolver un valor predeterminado o lanzar una excepción.
                    return@async "-1"
                }


            } else {
                // Mostrar un pop-up o mensaje indicando que el servicio no está disponible
                return@async "-1"
            }
        }
    }
     /*
    suspend fun RecargaQR(
        uidExterno: String,
        uidInterno: String,
        amount: Int
    ): String = withContext(Dispatchers.IO) {
        return@withContext "0007"
    }*/
    suspend fun calculateMinValue(): String = withContext(Dispatchers.IO) {
        // Simplemente devuelve "0004" como número de transacción
        return@withContext "00070"
    }

     private fun startAnnulment(context: Context, receiptID: String) {
         val hashCode = "FkbUjU0=" // Código de hash necesario para la operación, asegúrate de proporcionarlo

         // Llamar a startAnnulment con los parámetros requeridos
         IntegrationClientSDK.getSmartPosInstance().startAnnulment(context, receiptID, hashCode, this)
     }

    private fun startSell(
        context: Context,
        amount: String,
        tax: String,
        tip: String,
        iac: String,
        firstParameter: String? = null
    ) {
        Log.d("SellFragment", "Amount: $amount, Tax: $tax, Tip: $tip, IAC: $iac")



      val result =  IntegrationClientSDK.getSmartPosInstance().startSell(
            context,
            amount,
            tax,
            tip,
            iac,
            "FkbUjU0=", // Se asume que "FkbUjU0=" es el hashCode por defecto
            firstParameter,
         null,
            this@TCService
        )


    }

     private fun startSellQR(
         context: Context,
         amount: String,
         tax: String,
         tip: String,
         iac: String,
         firstParameter: String? = null
     ) {
         Log.d("SellFragment", "Amount: $amount, Tax: $tax, Tip: $tip, IAC: $iac")
         val codeItem = "10203040"
         val quantity = "1"
         val unitValue = "150"
         val subExtraInfo = SubExtraInfoDto(codeItem,quantity, unitValue, amount.toLong())
         Log.d("SellFragment", "Amount:$subExtraInfo ")

         val result =  IntegrationClientSDK.getSmartPosInstance().startSellQR(
             context,
             amount,
             tax,
             tip,
             iac,
             "FkbUjU0=", // Se asume que "FkbUjU0=" es el hashCode por defecto
             null,
             null,
             this@TCService
         )


     }
    suspend fun validarMA(idMedioAcceso: String): TCard = withContext(Dispatchers.IO) {
        // Obtener el token de autenticación
        val username = "pruebas@kapi.com.co"
        val password = "yF7t22k1ZE53"
        val cookie = authenticateAndGetCookie(username, password)

        // Crear el cuerpo de la solicitud
        val requestBody = """
        {
            "id_medio_acceso": "$idMedioAcceso"
        }
    """.trimIndent()

        try {
            val url = URL("https://webpos.kapi.com.co/sonda/validar-ma")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Cookie", cookie)
            connection.doOutput = true

            val outputStream = connection.outputStream
            outputStream.write(requestBody.toByteArray())
            outputStream.close()

            val responseCode = connection.responseCode
            Log.d("validarMA", "Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseStream = connection.inputStream
                val responseReader = BufferedReader(InputStreamReader(responseStream))
                val responseStringBuilder = StringBuilder()

                var line: String?
                while (responseReader.readLine().also { line = it } != null) {
                    responseStringBuilder.append(line)
                }

                responseReader.close()
                val responseData = responseStringBuilder.toString()
                connection.disconnect()

                // Parsear el JSON de respuesta a un objeto ValidationMAResponse
                val ejemploTCard = JsonParser.parseString(responseData).asJsonObject
                TCard(
                    resultado = ejemploTCard.get("resultado").asLong,
                    mensaje = ejemploTCard.get("mensaje").asString,
                    estadoMA = ejemploTCard.get("estadoMA").asLong,
                    descripcion = ejemploTCard.get("descripcion").asString,
                    tecnologia = ejemploTCard.get("tecnologia").asLong,
                    uidtarjeta = ejemploTCard.get("uidtarjeta").asLong,
                    tipotarjeta = ejemploTCard.get("tipotarjeta").asLong,
                    descTipoTarjeta = ejemploTCard.get("descTipoTarjeta").asString,
                    numABT = ejemploTCard.get("numABT").asString,
                    numeroInterno = ejemploTCard.get("numeroInterno").asLong,
                    numeroExterno = ejemploTCard.get("numeroExterno").asLong,
                    uuidMovil = ejemploTCard.get("uuidMovil").asString,
                    nombres = ejemploTCard.get("nombres").asString,
                    apellidos = ejemploTCard.get("apellidos").asString,
                    correo = ejemploTCard.get("correo").asString,
                    rut = ejemploTCard.get("rut").asString
                )
            } else {
                // Si no se recibe una respuesta HTTP_OK, devolver un objeto TCard nulo
                TCard(
                    resultado = 10,
                    mensaje = "Validación no exitosa",
                    estadoMA = 1,
                    descripcion = "Estado no activo",
                    tecnologia = 2,
                    uidtarjeta = 0,
                    tipotarjeta = 1,
                    descTipoTarjeta = "Tarjeta de débito",
                    numABT = "123456789",
                    numeroInterno = 0,
                    numeroExterno = 123456,
                    uuidMovil = "xyz987",
                    nombres = "Juan",
                    apellidos = "Pérez",
                    correo = "juan.perez@example.com",
                    rut = "123456789"
                )
            }
        } catch (e: Exception) {
            Log.e("validarMA", "Error: ${e.message}")
            // Manejo de errores
            throw IOException("Error en la solicitud: ${e.message}")
        }
    }
     fun recargaSaldoSondaEfectivo(uidExterno: String, uidInterno: String, amount: Int): String {
        val username = "pruebas@kapi.com.co"
        val password = "yF7t22k1ZE53"
        val cookie = authenticateAndGetCookie(username, password)
        val sondaUrl = URI.create("https://webpos.kapi.com.co/sonda/saldo-abt")
        return try {
            val sondaData = """
        {
          "user_id": 0,
          "transaction_name": "SON_AMOUNTS_ABT",
          "uid_externo": "$uidExterno",
          "uid_interno": "$uidInterno",
          "status": "PENDING",
          "amount": $amount,
          "payment_method": "EFECTIVO",
          "date_time_transaction": "0",
          "iva": 0,
          "total_amount": $amount
        }
        """.trimIndent().toByteArray(StandardCharsets.UTF_8)

            val url = sondaUrl.toURL()
            val sondaConnection = url.openConnection() as HttpURLConnection
            sondaConnection.requestMethod = "POST"
            sondaConnection.setRequestProperty("Content-Type", "application/json")
            sondaConnection.setRequestProperty("Cookie", cookie)
            sondaConnection.doOutput = true
            sondaConnection.outputStream.use { os -> os.write(sondaData) }

            val responseCode = sondaConnection.responseCode
            Log.d("Service", "Dato: ${responseCode }")
            val responseStream = if (responseCode == HttpURLConnection.HTTP_OK) sondaConnection.inputStream else sondaConnection.errorStream
            val responseReader = BufferedReader(InputStreamReader(responseStream))
            val responseStringBuilder = StringBuilder()

            var line: String?
            while (responseReader.readLine().also { line = it } != null) {
                responseStringBuilder.append(line)
            }

            responseReader.close()
            val responseData = responseStringBuilder.toString()
            Log.d("Service", "Dato: ${responseData }")



            sondaConnection.disconnect()
            responseData
        } catch (e: IOException) {
            println("Error al establecer la conexión a la URL de sonda para efectivo: ${e.message}")
            ""
        }
    }



    // Adjusted consultarSaldo method
    suspend fun consultarSaldo(numExterno: String): Int = withContext(Dispatchers.IO) {
        // Obtener el token de autenticación
        val username = "pruebas@kapi.com.co"
        val password = "yF7t22k1ZE53"
        val cookie = authenticateAndGetCookie(username, password)

        val requestBody = """
        {
            "numExterno": "$numExterno"
        }
    """.trimIndent()

        try {
            val url = URL("https://webpos.kapi.com.co/sonda/consultar_saldo")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Cookie", cookie)
            connection.doOutput = true

            // Escribir el cuerpo de la solicitud en la conexión
            val outputStream = connection.outputStream
            outputStream.write(requestBody.toByteArray())
            outputStream.close()

            val responseCode = connection.responseCode
            Log.d("consultarSaldo", "Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer la respuesta del servidor
                val responseStream = connection.inputStream
                val responseReader = BufferedReader(InputStreamReader(responseStream))
                val responseStringBuilder = StringBuilder()
                var line: String?
                while (responseReader.readLine().also { line = it } != null) {
                    responseStringBuilder.append(line)
                }
                responseReader.close()

                val responseData = responseStringBuilder.toString()
                connection.disconnect()

                // Parsear la respuesta JSON
                val jsonResponse = Json.parseToJsonElement(responseData).jsonObject
                // Extraer y devolver el saldo como un entero
                jsonResponse["saldo"]?.jsonPrimitive?.intOrNull ?: 0
            } else {
                // Si no se recibe una respuesta HTTP_OK, devolver un valor predeterminado o lanzar un error
                0 // O podrías lanzar una excepción aquí para manejar errores de manera más precisa
            }
        } catch (e: Exception) {
            Log.e("consultarSaldo", "Error: ${e.message}")
            // Manejo de errores
            throw IOException("Error en la solicitud: ${e.message}")
        }
    }
    suspend fun recargaEfectivo(uidExterno: String, uidInterno: String, amount: Int): String? = withContext(Dispatchers.IO) {
        try {
            val resultadoRecarga = recargaSaldoSondaEfectivo(uidExterno, uidInterno, amount)
            // Verificar si la respuesta del servidor no es nula
            if (resultadoRecarga != null) {
                val jsonResult = JsonParser.parseString(resultadoRecarga).asJsonObject
                // Verificar si la recarga fue exitosa (estado = 0)
                if (jsonResult.has("estado") && jsonResult["estado"].asInt == 0) {
                    // Imprimir la transacción con el ID correspondiente
                    val idTransaccion = jsonResult["idTransaccion"].asInt
                    //   imprimirTransaccion(idTransaccion)
                    // Devolver el estado de la recarga
                    return@withContext jsonResult["estado"].asString
                }
            }
        } catch (e: Exception) {
            // Manejar cualquier excepción que ocurra durante la recarga
            Log.e("Service", "Error en la recarga de saldo efectivo: ${e.message}")
        }
        // Si la recarga no fue exitosa o la respuesta del servidor es nula, retornar null
        return@withContext null
    }


     suspend fun completarCompraTC(id_trx_tef: Int): String = withContext(Dispatchers.IO) {
         // Obtener el token de autenticación
         setUltimoIdTransaccion(id_trx_tef)
         val username = "pruebas@kapi.com.co"
         val password = "yF7t22k1ZE53"
         val cookie = authenticateAndGetCookie(username, password)

         // Crear el cuerpo de la solicitud


         /***
          *   "id_trx_n86": 0,
          *   "codigo_aprobacion": "string",
          *   "fecha_transaccion": "string", en formato MMDD
          *   "hora_transaccion": "string", en formato HHMM
          *   "franquicia": "string",
          *   "numero_cuotas": 0,
          *   "ultimos_digitos_tarjeta": "string",
          *   "bin_tarjeta": "string"
          *
          */
         val requestBody = """
        {
          
             "id_trx_n86": $id_trx_tef,
             "codigo_aprobacion": "$id_trx_tef",
            "fecha_transaccion": "0607",
             "hora_transaccion": "1550",
             "franquicia": "VISA",
             "numero_cuotas": 3,
             "ultimos_digitos_tarjeta": "1878",
             "bin_tarjeta": "12345"
            
        }
    """.trimIndent()

         try {
             val url = URL("https://webpos.kapi.com.co/n86/completar-compra-tc")
             val connection = url.openConnection() as HttpURLConnection
             connection.requestMethod = "POST"
             connection.setRequestProperty("Content-Type", "application/json")
             connection.setRequestProperty("Cookie", cookie)
             connection.doOutput = true

             val outputStream = connection.outputStream
             outputStream.write(requestBody.toByteArray())
             outputStream.close()

             val responseCode = connection.responseCode
             Log.d("validarMA", "Response Code completar: $responseCode")

             if (responseCode == HttpURLConnection.HTTP_OK) {
                 val responseStream = connection.inputStream
                 val responseReader = BufferedReader(InputStreamReader(responseStream))
                 val responseStringBuilder = StringBuilder()
                 var line: String?
                 while (responseReader.readLine().also { line = it } != null) {
                     responseStringBuilder.append(line)
                 }
                 responseReader.close()

                 val responseData = responseStringBuilder.toString()
                 connection.disconnect()

                 // Parsear la respuesta JSON
                 val jsonResponse = Json.parseToJsonElement(responseData).jsonObject
                 // Extraer y devolver el saldo como un entero

                 Log.d("consultarCompraPorToken", "Estatus Completar: $jsonResponse")
                 // Imprimir el estatus
                 val status =  jsonResponse["detail"].toString()




                 return@withContext status

             } else {
                 // Si no se recibe una respuesta HTTP_OK, devolver un objeto TransactionResponse con valores predeterminados
                 val status = "Rejected"
                 return@withContext status

             }
         } catch (e: Exception) {
             Log.e("consultarCompraPorToken", "Error: ${e.message}")
             // Manejo de errores: lanzar una excepción para que el cliente pueda manejarla
             throw IOException("Error en la solicitud: ${e.message}")
         }
     }
    suspend fun crearCompraTC(uidExterno: String, uidInterno: String, amount: Int): Array<String>? {


        val requestBody = """
        {
          "user_id": 0,
          "transaction_name": "SON_AMOUNTS_ABT",
          "uid_externo": "$uidExterno",
          "uid_interno": "$uidInterno",
          "status": "PENDING",
          "amount": $amount,
          "payment_method": "TEF",
          "date_time_transaction": "0",
          "iva": 0,
          "total_amount": $amount
        }
        """.trimIndent().toByteArray(StandardCharsets.UTF_8)

        try {
            // Llamar a makeHttpRequest con la URL y el cuerpo de la solicitud
            val jsonResponse = makeHttpRequest("https://webpos.kapi.com.co/n86/crear-compra-tc", requestBody)
            val idFactura = jsonResponse.get("ID_Factura")?.toString() ?: ""
            val idTransaccion = jsonResponse.get("ID_Transaccion")?.toString() ?: ""
            // Imprimir los valores de ID_Factura e ID_Transaccion (puedes eliminar estos logs si no los necesitas)
            Log.e("crearCompraTC", "idFactura: $idFactura")
            Log.e("crearCompraTC", "idTransaccion: $idTransaccion")
            // Crear un array de String y agregar los valores de idFactura e idTransaccion
            return arrayOf(idFactura, idTransaccion)
        } catch (e: Exception) {
            // Manejo de errores
            Log.e("crearCompraTC", "Error: ${e.message}")
            return null
        }
    }
    suspend fun consultarTransaccion(transactionId: String?): JsonObject {
        val requestBody = buildJsonObject {
            put("id_transaction", transactionId)
        }.toString().toByteArray(Charsets.UTF_8)
        try {
            // Llamar a makeHttpRequest con la URL y el cuerpo de la solicitud
            return makeHttpRequest("https://webpos.kapi.com.co/n86/info-ticket", requestBody)
        } catch (e: Exception) {
            // Manejo de errores
            throw IOException("Error en la solicitud: ${e.message}")
        }
    }
     suspend fun consultarCompraPorToken(id_transaction: Int): String = withContext(Dispatchers.IO) {
         // Obtener el token de autenticación
         val username = "pruebas@kapi.com.co"
         val password = "yF7t22k1ZE53"
         val cookie = authenticateAndGetCookie(username, password)

         // Crear el cuerpo de la solicitud
         val requestBody = """
        {
            "id_transaction": "$id_transaction"
        }
    """.trimIndent()

         try {
             val url = URL("https://webpos.kapi.com.co/n86/info-ticket")
             val connection = url.openConnection() as HttpURLConnection
             connection.requestMethod = "POST"
             connection.setRequestProperty("Content-Type", "application/json")
             connection.setRequestProperty("Cookie", cookie)
             connection.doOutput = true

             val outputStream = connection.outputStream
             outputStream.write(requestBody.toByteArray())
             outputStream.close()

             val responseCode = connection.responseCode
             Log.d("validarMA", "Response Code: $responseCode")

             if (responseCode == HttpURLConnection.HTTP_OK) {
                 val responseStream = connection.inputStream
                 val responseReader = BufferedReader(InputStreamReader(responseStream))
                 val responseStringBuilder = StringBuilder()

                 var line: String?
                 while (responseReader.readLine().also { line = it } != null) {
                     responseStringBuilder.append(line)
                 }

                 responseReader.close()
                 val responseData = responseStringBuilder.toString()
                 connection.disconnect()

                 // Parsear el JSON de respuesta a un objeto TransactionResponse
                 val ejemploTCard = JsonParser.parseString(responseData).asJsonObject



                 // Imprimir el estatus
                 val status = ejemploTCard.get("status").asString
                 Log.d("consultarCompraPorToken", "Estatus: $status")



                 return@withContext status

             } else {
                 // Si no se recibe una respuesta HTTP_OK, devolver un objeto TransactionResponse con valores predeterminados
                 val status = "Rejected"
                 return@withContext status

             }
         } catch (e: Exception) {
             Log.e("consultarCompraPorToken", "Error: ${e.message}")
             // Manejo de errores: lanzar una excepción para que el cliente pueda manejarla
             throw IOException("Error en la solicitud: ${e.message}")
         }
     }

     suspend fun enviarMensaje(newTransactionId: Int, phoneNumber: String): String = withContext(Dispatchers.IO) {
         // Obtener el token de autenticación (simulado con credenciales en el código)
         val username = "pruebas@kapi.com.co"
         val password = "yF7t22k1ZE53"
         val cookie = authenticateAndGetCookie(username, password)

         // Crear el cuerpo de la solicitud para enviar el SMS
         val requestBody = """
        {
            "smsNumber": "$phoneNumber",
            "newTransactionId": $newTransactionId
        }
    """.trimIndent()

         try {
             val url = URL("https://webpos.kapi.com.co/sns/sms")
             val connection = url.openConnection() as HttpURLConnection
             connection.requestMethod = "POST"
             connection.setRequestProperty("Content-Type", "application/json")
             connection.setRequestProperty("Cookie", cookie)
             connection.doOutput = true

             val outputStream = connection.outputStream
             outputStream.write(requestBody.toByteArray())
             outputStream.close()

             val responseCode = connection.responseCode
             Log.d("enviarMensaje", "Response Code: $responseCode")

             if (responseCode == HttpURLConnection.HTTP_OK) {
                 val responseStream = connection.inputStream
                 val responseReader = BufferedReader(InputStreamReader(responseStream))
                 val responseStringBuilder = StringBuilder()

                 var line: String?
                 while (responseReader.readLine().also { line = it } != null) {
                     responseStringBuilder.append(line)
                 }

                 responseReader.close()
                 clearUltimoIdTransaccion()
                 val responseData = responseStringBuilder.toString()
                 connection.disconnect()

                 // Parsear la respuesta JSON si es necesario
                 // Aquí puedes procesar la respuesta según lo que necesites
                 val mensajeExito = "Mensaje enviado"

                 return@withContext mensajeExito
             } else {
                 // Si no se recibe una respuesta HTTP_OK, devolver un mensaje de error
                 val errorMessage = "Error al enviar el mensaje. Código de respuesta: $responseCode"
                 Log.e("enviarMensaje", errorMessage)
                 setUltimoMessage(errorMessage)
                 return@withContext errorMessage
             }
         } catch (e: Exception) {
             Log.e("enviarMensaje", "Error: ${e.message}")
             setUltimoMessage("Error")
             // Manejo de errores: lanzar una excepción para que el cliente pueda manejarla
             throw IOException("Error en la solicitud: ${e.message}")
         }
     }


     suspend fun makeHttpRequest(urlString: String, requestBody: ByteArray): JsonObject {
        // Obtener el token de autenticación
        val username = "pruebas@kapi.com.co"
        val password = "yF7t22k1ZE53"
        val cookie = authenticateAndGetCookie(username, password)
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Cookie", cookie)
            connection.doOutput = true
            // Escribir el cuerpo de la solicitud en la conexión
            val outputStream = connection.outputStream
            outputStream.write(requestBody)
            outputStream.close()
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer la respuesta del servidor
                val responseStream = connection.inputStream
                val responseReader = BufferedReader(InputStreamReader(responseStream))
                val responseStringBuilder = StringBuilder()
                var line: String?
                while (responseReader.readLine().also { line = it } != null) {
                    responseStringBuilder.append(line)
                }
                responseReader.close()
                // Parsear la respuesta JSON
                return Json.parseToJsonElement(responseStringBuilder.toString()).jsonObject
            } else {
                // Si no se recibe una respuesta HTTP_OK, lanzar una excepción
                throw IOException("Error en la solicitud: $responseCode")
            }
        } catch (e: Exception) {
            // Manejo de errores
            throw IOException("Error en la solicitud: ${e.message}")
        }
    }
    fun authenticateAndGetCookie(username: String, password: String): String? {
        val tokenUrl = URI.create("https://webpos.kapi.com.co/login/token")
        val postData = "username=${username}&password=${password}".toByteArray(StandardCharsets.UTF_8)
        val tokenConnection = tokenUrl.toURL().openConnection() as HttpURLConnection
        tokenConnection.requestMethod = "POST"
        tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        tokenConnection.doOutput = true
        tokenConnection.outputStream.use { os -> os.write(postData) }
        if (tokenConnection.responseCode == HttpURLConnection.HTTP_OK) {
            val cookie = tokenConnection.getHeaderField("Set-Cookie")
            println("Autenticacion exitosa. Cookie: $cookie")
            return cookie
        } else {
            println("Fallo la solicitud de autenticacion: ${tokenConnection.responseCode}")
            return null
        }
    }
    suspend fun imprimirTransaccion(transactionId: Int): String = withContext(Dispatchers.IO) {
        Log.d("Service", "Imprimir transacción con ID: $transactionId")
        try {
            // Consultar la transacción
            val transactionResponse = consultarTransaccion("1")
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


    // Adjusted historialMovimientos method
    suspend fun historialMovimientos(numExterno: String): List<JsonObject> = withContext(Dispatchers.IO) {
        val response: HttpResponse = client.post("https://webpos.kapi.com.co/sonda/historial_movimientos") {
            contentType(ContentType.Application.Json)
            body = buildJsonObject {
                put("numExterno", numExterno)
            }.toString()
        }
        return@withContext if (response.status == HttpStatusCode.OK) {
            // Parse and return the listaTransacciones array from the response
            val transactionsArray = Json.parseToJsonElement(response.readText()).jsonObject["listaTransacciones"]?.jsonArray
            transactionsArray?.mapNotNull { it.jsonObject } ?: emptyList()
        } else {
            // Return an empty list or a fixed response indicating an error
            emptyList()
        }
    }


}
