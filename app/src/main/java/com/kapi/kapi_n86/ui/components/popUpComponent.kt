package com.kapi.kapi_n86.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PopUpComponent(
    isVisible: MutableState<Boolean>,
    message: String,
    onDismiss: () -> Unit
) {
    if (isVisible.value) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Message") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}
