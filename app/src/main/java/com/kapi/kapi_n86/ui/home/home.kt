package com.kapi.kapi_n86.ui.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.kapi.kapi_n86.data.api.AuthenticationService
import com.kapi.kapi_n86.ui.components.CustomBottomNavigationBar
import com.kapi.kapi_n86.ui.components.HomeButtons
import com.kapi.kapi_n86.ui.components.SimpleTopAppBar
import com.kapi.kapi_n86.ui.components.Sidebar
import com.kapi.kapi_n86.data.api.TCService
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun HomePage(navController: NavController, authenticationService: AuthenticationService, tcService: TCService) {
    val selectedIndex = remember { mutableStateOf(0) }
    val scaffoldState = rememberScaffoldState(drawerState = rememberDrawerState(DrawerValue.Closed))
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Obtén el usuario almacenado para pasar su correo al Sidebar
    val user = authenticationService.getUser()
    val userEmail = user?.Sub ?: "Usuario"

    // Estado para el número de teléfono ingresado en el AlertDialog
    val telefonoState = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val phoneNumberRegex = Regex("""^\d{1,10}$""")
    var ultimoMessage by remember { mutableStateOf<String?>(null) }
    // Lanzar un AlertDialog cuando cambia el último ID de transacción
    LaunchedEffect(Unit) {
        val ultimoId = tcService.getUltimoIdTransaccion()
        ultimoMessage = tcService.getUltimoMessage().toString()
        Log.d("HomePage", "Última factura: $ultimoId")
        if (ultimoId != null) {
            showDialog.value = true
        }
    }

    // Composable que maneja el AlertDialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = { Text(text = "Ingrese el número de teléfono") },
            text = {
                Column {
                    ultimoMessage?.let { message ->
                        Text(
                            text = "Último mensaje: $message",
                            style = MaterialTheme.typography.subtitle1,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    OutlinedTextField(
                        value = telefonoState.value,
                        onValueChange = {
                            if (it.length <= 10 && it.matches(phoneNumberRegex)) {
                                telefonoState.value = it
                            }
                        },
                        label = { Text("Número de teléfono") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        // Llamar a enviarMensaje usando un coroutine scope
                        coroutineScope.launch {
                            // Obtener el último ID de transacción almacenado
                            val ultimoId = tcService.getUltimoIdTransaccion()
                            if (ultimoId != null) {
                                // Llamar a enviarMensaje con los datos
                                val result = tcService.enviarMensaje(ultimoId, telefonoState.value)

                                navController.navigate("home") {
                                    // Ajusta las opciones de navegación según tus necesidades
                                    popUpTo("home") { inclusive = true }
                                }
                                Log.d("HomePage", "Resultado de enviarMensaje: $result")
                            }
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        tcService.clearUltimoIdTransaccion()
                        showDialog.value = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            SimpleTopAppBar(onMenuClicked = {
                coroutineScope.launch {
                    scaffoldState.drawerState.open() // Abre el drawer
                }
            })
        },
        drawerContent = {
            Sidebar(drawerState = scaffoldState.drawerState, userEmailAddress = userEmail, onNavigate = { route ->
                coroutineScope.launch {
                    scaffoldState.drawerState.close() // Cierra el drawer
                    when (route) {
                        "reporteDeVentas" -> navController.navigate("reporteDeVentas")
                        "perfil" -> navController.navigate("perfil")
                        // Incluye aquí más casos según tus rutas de navegación
                    }
                }
            }, onLogout = {
                coroutineScope.launch {
                    authenticationService.clearAccessToken()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true } // Remueve todas las otras pantallas de la pila de navegación.
                    }
                }
            })
        },
        bottomBar = {
            CustomBottomNavigationBar(
                selectedIndex = selectedIndex.value,
                onItemTapped = { index ->
                    selectedIndex.value = index
                    // Aquí puedes manejar la navegación basada en el índice si es necesario.
                },
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HomeButtons(
                onButton1Pressed = { /* Acción para el botón 1 */ },
                onButton2Pressed = { /* Acción para el botón 2 */ },
                onButton3Pressed = { /* Acción para el botón 3 */ },
                onButton4Pressed = {
                    navController.navigate("recargasTC")
                }
            )
        }
    }
}