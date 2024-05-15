package com.kapi.kapi_n86.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.view.LayoutInflater
import androidx.compose.foundation.clickable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import com.kapi.kapi_n86.R
import com.kapi.kapi_n86.ui.theme.Kapi_n86Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.kapi.kapi_n86.data.api.AuthenticationService
import android.util.Log
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import com.kapi.kapi_n86.ui.home.HomePage
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginForm(
    navController: NavController? = null,
    authenticationService: AuthenticationService? = null
) {
    // Simula el NavController y AuthenticationService para el preview.
    val localNavController = navController ?: rememberNavController()
    val context = LocalContext.current
    val localAuthenticationService = authenticationService ?: remember { AuthenticationService(context) }



    var email by rememberSaveable { mutableStateOf("pruebas@kapi.com.co") }
    var password by rememberSaveable { mutableStateOf("yF7t22k1ZE53") }
    val localFocusManager = LocalFocusManager.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            factory = { ctx ->
                LayoutInflater.from(ctx).inflate(R.layout.logo_kapi, null, false)
            },
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                localFocusManager.clearFocus() // Aquí solo cerramos el teclado
            })
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    val isSuccess = authenticationService?.login(email, password)
                    if (isSuccess == true) {
                        Log.d("LoginForm", "Inicio de sesión exitoso")

                        navController?.navigate("home") {
                            popUpTo("login") { inclusive = true } // Opcional: Elimina la pantalla de login del backstack
                        }
                    } else {
                        Log.e("LoginForm", "Error en el inicio de sesión")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Olvidé la contraseña",
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.clickable { /* Acción para olvidé la contraseña */ }
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("¿No tienes cuenta?")
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Créala aquí",
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.clickable { /* Acción para crear cuenta */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginForm() {

    Kapi_n86Theme {

        LoginForm()
    }
}
