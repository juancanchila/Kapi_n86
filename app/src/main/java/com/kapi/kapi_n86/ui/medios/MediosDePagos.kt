package com.kapi.kapi_n86.ui.medios

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.kapi.kapi_n86.data.api.TCService
import com.kapi.kapi_n86.data.model.TransactionResponse
import com.kapi.kapi_n86.ui.components.PopUpComponent
import kotlinx.serialization.json.JsonObject


class MyViewModel : ViewModel() {

}


@Composable
fun MediosDePagos(
    navController: NavController,
    uid: String,
    valorRecarga: String,
    recargaSeleccionada: String,
    uidInterno: String,
    tcService: TCService
) {
    val viewModel = remember { MyViewModel() }
    val mostrarDialogoCarga = remember { mutableStateOf(false) }
    val mostrarDialogoError = remember { mutableStateOf(false) }
    val mensajeError = remember { mutableStateOf("") }
    val mostrarPopUp = remember { mutableStateOf(false) }
    val mostrarPopUpNum = remember { mutableStateOf(false) }


    fun onClickEfectivo() {
        viewModel.viewModelScope.launch {
            try {
                mostrarDialogoCarga.value = true
                // Llamada modificada al servicio TCService para realizar recarga
                val resultadoRecarga: String? = tcService.recargaEfectivo(uid, uidInterno, valorRecarga.toInt())
                Log.d("RecargasTC", "Resultado recarga efectivo: $resultadoRecarga")
                val saldo = tcService.consultarSaldo(uid.toString())
                mostrarDialogoCarga.value = false

                if (resultadoRecarga != null) {
                                          // Recarga exitosa
                        val mensaje = "Recarga exitosa. Nuevo saldo: $saldo , Tarjeta: $uid"
                        mensajeError.value = mensaje
                        mostrarPopUp.value = true

                } else {
                    // Recarga fallida
                    mensajeError.value = "Error al realizar la recarga: $resultadoRecarga"
                    mostrarDialogoError.value = true
                }
            } catch (e: Exception) {
                // Error durante la recarga
                mensajeError.value = "Error al realizar la recarga: ${e.message}"
                mostrarDialogoError.value = true
            } finally {
                mostrarDialogoCarga.value = false
            }
        }
    }

    fun onClickQR() {
        viewModel.viewModelScope.launch {
            try {
                mostrarDialogoCarga.value = true
                val resultadoRecarga: String? = tcService.recargaQR(uid, uidInterno, valorRecarga.toInt()).await()
                 val saldo = tcService.consultarSaldo(uid.toString())

                Log.d("RecargasTC", "Resultado recarga débito: $resultadoRecarga")
                mostrarDialogoCarga.value = false

                if (resultadoRecarga == "0") {
                    // Recarga exitosa
                    val mensaje = "Recarga exitosa. Nuevo saldo: $saldo, Tarjeta: $uid"
                    mensajeError.value = mensaje
                    mostrarPopUp.value = true

                } else {
                    // Recarga fallida
                    mensajeError.value = "Error al realizar la recarga: $resultadoRecarga"
                    mostrarDialogoError.value = true
                }
            } catch (e: Exception) {
                // Error durante la recarga
                mensajeError.value = "Error al realizar la recarga: ${e.message}"
                mostrarDialogoError.value = true
            } finally {
                mostrarDialogoCarga.value = false
            }
        }
    }
    fun onClickDebito() {
        viewModel.viewModelScope.launch {
            try {
                mostrarDialogoCarga.value = true
                val resultadoRecarga: String? = tcService.recargaDV(uid, uidInterno, valorRecarga.toInt()).await()
                val saldo = tcService.consultarSaldo(uid.toString())

                    Log.d("RecargasTC", "Resultado recarga débito: $resultadoRecarga")
                mostrarDialogoCarga.value = false

                if (resultadoRecarga == "0") {
                        // Recarga exitosa
                        val mensaje = "Recarga exitosa. Nuevo saldo: $saldo, Tarjeta: $uid"
                        mensajeError.value = mensaje
                        mostrarPopUp.value = true

                } else {
                    // Recarga fallida
                    mensajeError.value = "Error al realizar la recarga: $resultadoRecarga"
                    mostrarDialogoError.value = true
                }
            } catch (e: Exception) {
                // Error durante la recarga
                mensajeError.value = "Error al realizar la recarga: ${e.message}"
                mostrarDialogoError.value = true
            } finally {
                mostrarDialogoCarga.value = false
            }
        }
    }

    fun onClickCredito() {
        viewModel.viewModelScope.launch {
            try {
                mostrarPopUpNum.value = true
                mostrarDialogoCarga.value = true
                val resultadoRecarga: String? = tcService.recargaTC(uid, uidInterno, valorRecarga.toInt()).await()
                Log.d("RecargasTC", "Resultado recarga crédito: $resultadoRecarga")
                mostrarDialogoCarga.value = false

                 val saldo = tcService.consultarSaldo(uid.toString())

                if (resultadoRecarga == "0") {

                        val mensaje = "Recarga exitosa. Nuevo saldo: $saldo, Tarjeta: $uid"


                        mensajeError.value = mensaje
                        mostrarPopUp.value = true

                    } else {
                        // Recarga fallida
                        mensajeError.value = "Error al realizar la recarga: $resultadoRecarga"
                        mostrarDialogoError.value = true

                }
            } catch (e: Exception) {
                // Error durante la recarga
                mensajeError.value = "Error al realizar la recarga: ${e.message}"
                mostrarDialogoError.value = true
            } finally {
                mostrarDialogoCarga.value = false
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medio de Pago") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("UID: $uid", style = MaterialTheme.typography.h6)
                    Text("Valor a recargar: $valorRecarga", style = MaterialTheme.typography.h6)
                }
            }

            Button(
                onClick = { onClickEfectivo() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 4.dp)
                    .height(50.dp)
            ) {
                Text("Efectivo")
            }

            Button(
                onClick = { onClickDebito() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 4.dp)
                    .height(50.dp)
            ) {
                Text("Tarjeta Débito")
            }

            Button(
                onClick = { onClickCredito() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 4.dp)
                    .height(50.dp)
            ) {
                Text("Tarjeta Crédito")
            }

            Button(
                onClick = { onClickQR() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 4.dp)
                    .height(50.dp)
            ) {
                Text("QR")
            }

            if (mostrarDialogoCarga.value) {
                Dialog(onDismissRequest = { mostrarDialogoCarga.value = false }) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (mostrarPopUp.value) {
                PopUpComponent(
                    isVisible = mostrarPopUp,
                    message = mensajeError.value,
                    onDismiss = {
                        mostrarPopUp.value = false
                        navController.navigate("home")
                    }
                )
            }
        }
    }



    if (mostrarDialogoError.value) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError.value = false },
            title = { Text("Error") },
            text = { Text(mensajeError.value) },
            confirmButton = {
                Button(onClick = { mostrarDialogoError.value = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
