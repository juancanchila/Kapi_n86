package com.kapi.kapi_n86.data.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import android.util.Base64
import com.google.gson.Gson
import com.kapi.kapi_n86.data.model.User
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets

class AuthenticationService(private val context: Context) {


    companion object {
        const val API_URL = "https://webpos.kapi.com.co/login/token"
        const val API_URL_LOGOUT = "https://webpos.kapi.com.co/login/logout"
        const val PREFERENCES_FILE = "com.kapi.authentication"
        const val ACCESS_TOKEN_KEY = "accessToken"
    }

    private val client = OkHttpClient()

    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder()
            .add("username", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(API_URL)
            .post(formBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    val jsonObject = JSONObject(it)
                    val accessToken = jsonObject.getString("access_token")
                    val cookies = response.headers("Set-Cookie") // Esto es una lista de cookies
                    // Decodifica el JWT para obtener sub y exp
                    val parts = accessToken.split(".")
                    if (parts.size == 3) {
                        val payload = parts[1]
                        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
                        val decodedString = String(decodedBytes)
                        val payloadJson = JSONObject(decodedString)
                        val sub = payloadJson.getString("sub")
                        val exp = payloadJson.getLong("exp")
                        // Almacenar el User en las preferencias compartidas
                        saveUser(User(Token = accessToken, Sub = sub, Exp = exp))
                    }
                    saveAccessToken(accessToken)

                    return@withContext true
                } ?: return@withContext false
            } else {
                Log.e("AuthenticationService", "Login failed: ${response.code}")
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("AuthenticationService", "Login error", e)
            return@withContext false
        }
    }
    private fun saveUser(user: User) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("user", Gson().toJson(user)) // Usa Gson para convertir el objeto User a String
            apply()
        }
    }

    fun getUser(): User? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("user", null)
        return if (userJson != null) Gson().fromJson(userJson, User::class.java) else null
    }
    private fun saveAccessToken(accessToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, accessToken).apply()
    }

    fun getAccessToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

        // Verifica si el token es válido
        if (accessToken != null) {
            // Elimina el prefijo "Bearer " si está presente
            val formattedToken = accessToken.removePrefix("Bearer ")

            // Construye la cadena de cookie
            return "access_token=\"$formattedToken\"; Path=/; HttpOnly;"
        } else {
            return null
        }
    }


    fun clearAccessToken() {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(ACCESS_TOKEN_KEY).apply()
    }


    public suspend fun authenticateAndGetCookie(username: String, password: String): String? {
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

}
