package com.kapi.kapi_n86.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CustomBottomNavigationBar(selectedIndex: Int, onItemTapped: (Int) -> Unit, navController: NavController) {

    BottomNavigation(
        backgroundColor = Color(0xFF0047AB), // Azul de fondo
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            selected = selectedIndex == 0,
            onClick = { navController.navigate("home") },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
         BottomNavigationItem(
            icon = { Icon(Icons.Filled.List, contentDescription = null) },
            selected = selectedIndex == 2,
             onClick = { navController.navigate("reporteDeVentas") },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            selected = selectedIndex == 3,
            onClick = { navController.navigate("perfil") },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
    }
}