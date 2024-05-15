package com.kapi.kapi_n86.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip

@Composable
fun Sidebar(
    drawerState: DrawerState,
    userEmailAddress: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Avatar del usuario y correo electrónico
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier.size(100.dp),
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(userEmailAddress, fontSize = 24.sp, modifier = Modifier.padding(8.dp))
        Divider()

        // Asegúrate de que el string pasado a onNavigate coincida con el ID de ruta en NavHost
        MenuItem(icon = Icons.Default.ShoppingCart, label = "Reporte de ventas") {
            onNavigate("reporteDeVentas") // Se ajusta el ID para coincidir con el de NavHost
        }
        MenuItem(icon = Icons.Default.AccountCircle, label = "Perfil") {
            onNavigate("perfil") // Se asegura de usar el ID correcto para coincidir con el de NavHost
        }
        MenuItem(icon = Icons.Default.ExitToApp, label = "Cerrar sesión") {
            onLogout()
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, fontSize = 18.sp)
    }
}
