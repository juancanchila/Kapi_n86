package com.kapi.kapi_n86.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kapi.kapi_n86.R
import com.kapi.kapi_n86.ui.theme.Kapi_n86Theme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.kapi.kapi_n86.data.api.AuthenticationService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


data class UserProfile(
    val email: String,
    val sessionExpiryDate: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val employeeNumber: String
)

@Composable
fun ProfileView(navController: NavController, authenticationService: AuthenticationService) {
    val coroutineScope = rememberCoroutineScope()
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            val user = authenticationService.getUser()
            user?.let {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                userProfile = UserProfile(
                    email = it.Sub, // Asume que Sub contiene el email del usuario
                    sessionExpiryDate = dateFormat.format(Date(it.Exp * 1000)), // Exp contiene la fecha de expiración en formato Unix
                    firstName = "Juan", // Ejemplo de usuario
                    lastName = "Pérez",
                    phoneNumber = "123456789",
                    employeeNumber = "00001"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                backgroundColor = MaterialTheme.colors.primarySurface,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Icono de Usuario",
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp)
            )

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.8f)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .shadow(1.dp, RoundedCornerShape(10.dp))
                    .padding(24.dp)
            ) {
                userProfile?.let { user ->
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = user.email, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.subtitle1.fontSize)
                        Text(text = "Expira: ${user.sessionExpiryDate}", fontSize = MaterialTheme.typography.body1.fontSize)
                        Text(text = "${user.firstName} ${user.lastName}", fontSize = MaterialTheme.typography.body1.fontSize)
                        Text(text = "Teléfono: ${user.phoneNumber}", fontSize = MaterialTheme.typography.body1.fontSize)
                        Text(text = "Nº de empleado: ${user.employeeNumber}", fontSize = MaterialTheme.typography.body1.fontSize)
                    }
                }
            }
        }
    }
}
