package com.kapi.kapi_n86.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kapi.kapi_n86.R // Asegúrate de cambiar esto por tu paquete real

@Composable
fun HomeButtons(
    onButton1Pressed: () -> Unit,
    onButton2Pressed: () -> Unit,
    onButton3Pressed: () -> Unit,
    onButton4Pressed: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ButtonWithBackground("Servicios", Color.Gray, onButton1Pressed) }
        item { ButtonWithBackground("Recarga Kapi", Color.Gray, onButton2Pressed) }
        item { ButtonWithBackground("Consulta tu saldo\n-\nTranscaribe", Color.Gray, onButton3Pressed) }
        item { ButtonWithBackground("Recargas\n-\nTranscaribe", MaterialTheme.colors.primaryVariant, onButton4Pressed, R.drawable.bus) } // Asume que tienes bus.png en tu carpeta drawable
    }
}

@Composable
fun ButtonWithBackground(title: String, backgroundColor: Color, onClick: () -> Unit, backgroundImage: Int? = null) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(color = backgroundColor)
            .clickable(onClick = onClick)
    ) {
        if (backgroundImage != null) {

        }
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
