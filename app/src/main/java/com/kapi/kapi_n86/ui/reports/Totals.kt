package com.kapi.kapi_n86.ui.reports

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kapi.kapi_n86.data.api.AuthenticationService
import com.kapi.kapi_n86.data.api.DeviceService
import com.kapi.kapi_n86.data.api.TCService
import com.kapi.kapi_n86.ui.theme.Kapi_n86Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch // Importar launch


data class Transaction(
    val id: String,
    val date: String,
    val value: Double
)

@Composable
fun TotalsScreen(navController: NavController, authService: AuthenticationService, tcService: TCService) {
    val context = LocalContext.current

    val transactions = remember {
        listOf(
            Transaction("1", "2024-04-01", 100.0),
            Transaction("2", "2024-04-02", 150.0),
            Transaction("3", "2024-04-03", 75.0),
            Transaction("4", "2024-04-04", 200.0),
            Transaction("5", "2024-04-05", 50.0),
            Transaction("6", "2024-04-06", 120.0),
            Transaction("7", "2024-04-07", 80.0),
            Transaction("8", "2024-04-08", 90.0),
            Transaction("9", "2024-04-09", 110.0),
            Transaction("10", "2024-04-10", 70.0),
            Transaction("11", "2024-04-11", 100.0),
            Transaction("12", "2024-04-12", 150.0),
            Transaction("13", "2024-04-13", 75.0),
            Transaction("14", "2024-04-14", 200.0),
            Transaction("15", "2024-04-15", 50.0),
            Transaction("16", "2024-04-16", 120.0),
            Transaction("17", "2024-04-17", 80.0),
            Transaction("18", "2024-04-18", 90.0),
            Transaction("19", "2024-04-19", 110.0),
            Transaction("20", "2024-04-20", 70.0)
        )
    }

    var isPrinting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Kapi_n86Theme {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Total de Transacciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch { // Llamar launch en el coroutineScope
                            // Llamar al método imprimirTotales
                            val deviceService = DeviceService(context) // Suponiendo que `context` esté disponible en este ámbito
                            val Pinrting= deviceService.imprimirTotales()
                            Log.d("RecargasTC", "cookies: $Pinrting")
                            isPrinting = false

                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransactionsTable(transactions = transactions)
        }
    }

    // Diálogo de impresión
    if (isPrinting) {
        AlertDialog(
            onDismissRequest = { isPrinting = false },
            title = { Text(text = "Imprimiendo") },
            text = { Text("La impresión está en curso...") },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun TransactionsTable(transactions: List<Transaction>) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(transactions) { transaction ->
                TableRow(transaction.id, transaction.date, transaction.value.toString())
            }
        }
    }
}

@Composable
fun TableRow(id: String, date: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = id, modifier = Modifier.weight(1f))
        Text(text = date, modifier = Modifier.weight(2f))
        Text(text = value, modifier = Modifier.weight(1f))
    }
}
