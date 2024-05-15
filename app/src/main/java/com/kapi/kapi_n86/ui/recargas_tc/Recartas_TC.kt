package com.kapi.kapi_n86.ui.recargas_tc

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kapi.kapi_n86.ui.theme.Kapi_n86Theme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.kapi.kapi_n86.data.api.TCService

data class RecargaInfo(
    val uid: String,
    val valorRecarga: Int,
    val recargaSeleccionada: String
)

fun String.formatearComoMoneda(): String {
    return try {
        val valorNumerico = this.toDouble()
        val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        formato.maximumFractionDigits = 0 // No se mostrarán decimales
        formato.currency = Currency.getInstance("COP") // Establecer la moneda a pesos colombianos
        formato.format(valorNumerico.toLong()) // Convertir a Long para evitar la parte decimal
    } catch (e: Exception) {
        "$0" // En caso de error, devolver el formato básico
    }
}

@Composable
fun Recargas_TC(navController: NavController, tcService: TCService, onBackClicked: () -> Unit = { navController.popBackStack() }) {
    var mostrandoCarga by remember { mutableStateOf(false) }
    var mostrarBotonRecargar by remember { mutableStateOf(false) }
    val valores = listOf(3, 6, 9, 12, 30, 60, 120, "Otro")
    var selectedValueIndex by remember { mutableStateOf<Int?>(null) }
    var otroValor by remember { mutableStateOf("") }
    var nid by remember { mutableStateOf("") }
    var uidInterno by remember { mutableStateOf("") }
    var mostrarResultado by remember { mutableStateOf(false) }
    var resultadoNid by remember { mutableStateOf("") }
    var saldo by remember { mutableStateOf("") }
    val recargaMinima = 100 // Valor fijo para la recarga mínima
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var fixedNumber by remember { mutableStateOf<String?>(null) }
    var otroValorEnviar by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recargas Transcaribe") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Habilita el desplazamiento para toda la columna
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = nid,
                onValueChange = { nid = it },
                label = { Text("UID") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    Log.d("RecargasTC", "nid: $nid")
                    if (nid.isNotEmpty()) {
                        // Mostrar el CircularProgressIndicator al iniciar la consulta
                        mostrandoCarga = true

                        coroutineScope.launch {
                            try {
                                val card = tcService.validarMA(nid)
                                Log.d("RecargasTC", "Response :$card")
                                uidInterno = card.numeroInterno.toString()

                                if (card.resultado.toInt() == 0) {


                                    resultadoNid = card.numeroExterno.toString()
                                    saldo = tcService.consultarSaldo(resultadoNid).toString() // Consulta del saldo

                                    mostrarResultado = true
                                    mostrarBotonRecargar = true
                                } else {
                                    // Manejo de la respuesta no exitosa
                                    mostrarResultado = false
                                    showErrorDialog = true
                                    mostrarBotonRecargar = false
                                }
                            } finally {
                                // Ocultar el CircularProgressIndicator después de completar la consulta
                                mostrandoCarga = false
                            }
                        }
                    } else {
                        // Manejo del caso en que el UID esté vacío
                        showErrorDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Consultar")
            }


            Spacer(modifier = Modifier.height(8.dp))

            if (mostrarResultado) {
                Text("UID: $resultadoNid")
                Text("Saldo: $saldo")
                val recargaMinimaCalculada = if (saldo.toInt() >= 3000) 100 else ((3000 - saldo.toInt()) + 99) / 100 * 100
                Text(
                    text = "Recarga mínima: ${
                        // Aplicar formato de moneda colombiana (COP)
                        recargaMinimaCalculada.formatearMonedaColombiana()
                    }",
                    // Establecer estilo de texto para la moneda colombiana (COP)
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grilla simulada para botones en dos columnas
            valores.chunked(2).forEach { rowValues ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowValues.forEach { valor ->
                        Button(
                            onClick = { selectedValueIndex = valores.indexOf(valor) },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = if (selectedValueIndex == valores.indexOf(valor)) MaterialTheme.colors.primary else Color.LightGray),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = if (rowValues.indexOf(valor) == 0) 4.dp else 0.dp) // Agrega padding solo entre los botones
                        ) {
                            Text(if (valor is Int) "$valor K" else valor.toString())
                        }
                    }
                    if (rowValues.size < 2) { // Asegura que la fila tenga siempre dos elementos
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (mostrandoCarga) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                    CircularProgressIndicator()
                }
            }
            if (selectedValueIndex == valores.size - 1) {
                OutlinedTextField(
                    value = otroValor,
                    onValueChange = {
                        otroValor = it
                        // Intenta convertir el valor a entero y guárdalo en otroValorEnviar
                        otroValorEnviar = try {
                            it.toInt()
                        } catch (e: NumberFormatException) {
                            0 // Si hay un error al convertir, asigna 0 como valor predeterminado
                        }
                    },
                    label = { Text("Otro") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            // No necesitas formatear aquí, simplemente guarda el valor entero
                            otroValorEnviar = otroValor.toIntOrNull() ?: 0 // Si no se puede convertir, asigna 0 como valor predeterminado
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                    if (resultadoNid.isNotEmpty() && selectedValueIndex != null) {
                        val valorRecarga = if (selectedValueIndex != null) {
                            if (selectedValueIndex == valores.size - 1) {
                                otroValorEnviar
                            } else {
                                (valores[selectedValueIndex!!] as Int) * 1000 // Aquí se multiplica por 1000
                            }
                        } else {
                            0
                        }
                        val recargaSeleccionada = if (selectedValueIndex == valores.size - 1) otroValor else "${valores[selectedValueIndex!!]} K"
                        val recargaInfo = RecargaInfo( resultadoNid, valorRecarga, recargaSeleccionada)
                        println("Recarga Info: $recargaInfo")
                        // Navegar a la siguiente pantalla con el objeto recargaInfo
                        navController.navigate("mediosDePago/$resultadoNid/$valorRecarga/$recargaSeleccionada/$uidInterno")

                    } else {
                        showErrorDialog = true
                    }

                },
                enabled = mostrarBotonRecargar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recargar")
            }
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = {
                    showErrorDialog = false
                },
                title = {
                    Text(text = "Error")
                },
                text = {
                    Text("Por favor, ingrese el UID y seleccione una cantidad de recarga.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorDialog = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

// Función de extensión para formatear a moneda colombiana
fun Int.formatearMonedaColombiana(): String = "$this COP"
