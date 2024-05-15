package com.kapi.kapi_n86.ui.components
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
@Composable
fun ErrorDialog(
    showErrorDialog: Boolean,
    errorMessage: String,
    onCloseDialog: () -> Unit
) {
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = onCloseDialog,
            title = {
                Text(text = "Error")
            },
            text = {
                Text(errorMessage)
            },
            confirmButton = {
                Button(
                    onClick = onCloseDialog
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}
