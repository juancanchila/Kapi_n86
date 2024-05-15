package com.kapi.kapi_n86.data.api

import android.view.SurfaceControl
import com.kapi.kapi_n86.data.model.Transacciones

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString


class WebPostService {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun obtenerMontoTransacciones(): String = withContext(Dispatchers.IO) {
        try {
            val response: String = client.get("https://webpos.kapi.com.co/n86/monto-transacciones") {
                headers {
                    append("Content-Type", "application/json")
                }
            }
            response
        } catch (e: Exception) {
            e.toString() // Return error message as string or handle it as needed
        }
    }

    suspend fun obtenerTransacciones(): List<Transacciones> = withContext(Dispatchers.IO) {
        try {
            val response: String = client.get("https://webpos.kapi.com.co/n86/transacciones") {
                headers {
                    append("Content-Type", "application/json")
                }
            }
            Json.decodeFromString<List<Transacciones>>(response)
        } catch (e: Exception) {
            emptyList() // Return an empty list or handle error as needed
        }
    }


    suspend fun obtenerTransaccionesSimuladas(): String = withContext(Dispatchers.IO) {
        val list_transacciones = listOf(
            Transacciones("1", "2024-04-01", 100.0),
            Transacciones("2", "2024-04-02", 150.0),
            Transacciones("3", "2024-04-03", 75.0),
            Transacciones("4", "2024-04-04", 200.0),
            Transacciones("5", "2024-04-05", 50.0),
            Transacciones("6", "2024-04-06", 120.0),
            Transacciones("7", "2024-04-07", 80.0),
            Transacciones("8", "2024-04-08", 90.0),
            Transacciones("9", "2024-04-09", 110.0),
            Transacciones("10", "2024-04-10", 70.0),
            Transacciones("11", "2024-04-11", 100.0),
            Transacciones("12", "2024-04-12", 150.0),
            Transacciones("13", "2024-04-13", 75.0),
            Transacciones("14", "2024-04-14", 200.0),
            Transacciones("15", "2024-04-15", 50.0),
            Transacciones("16", "2024-04-16", 120.0),
            Transacciones("17", "2024-04-17", 80.0),
            Transacciones("18", "2024-04-18", 90.0),
            Transacciones("19", "2024-04-19", 110.0),
            Transacciones("20", "2024-04-20", 70.0)
            // Agrega más transacciones si es necesario
        )
        Json.encodeToString(list_transacciones)
    }
}
