package com.kapi.kapi_n86.ui.home

import androidx.compose.foundation.layout.*
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

@Composable
fun HomePage(navController: NavController, authenticationService: AuthenticationService) {
    val selectedIndex = remember { mutableStateOf(0) }
    val scaffoldState = rememberScaffoldState(drawerState = rememberDrawerState(DrawerValue.Closed))
    val coroutineScope = rememberCoroutineScope()

    // Obtén el usuario almacenado para pasar su correo al Sidebar
    val user = authenticationService.getUser()
    val userEmail = user?.Sub ?: "Usuario"

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
