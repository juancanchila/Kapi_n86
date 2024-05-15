package com.kapi.kapi_n86

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kapi.kapi_n86.ui.login.LoginForm
import com.kapi.kapi_n86.ui.theme.Kapi_n86Theme
import com.kapi.kapi_n86.ui.home.HomePage
import com.kapi.kapi_n86.data.api.AuthenticationService
import com.kapi.kapi_n86.data.api.TCService
import com.kapi.kapi_n86.ui.medios.MediosDePagos
import com.kapi.kapi_n86.ui.recargas_tc.Recargas_TC
import com.kapi.kapi_n86.ui.reports.TotalsScreen
import com.kapi.kapi_n86.ui.user.ProfileView
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authenticationService = AuthenticationService(this)
        val tcService = TCService(this)
        setContent {
            Kapi_n86Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginForm(navController, authenticationService) }
                    composable("home") { HomePage(navController, authenticationService) }
                    composable("recargasTC") { Recargas_TC(navController, tcService) }
                    composable("perfil") { ProfileView(navController, authenticationService) }
                    composable("reporteDeVentas") { TotalsScreen(navController, authenticationService ,tcService) }
                    composable(
                        route = "mediosDePago/{uid}/{valorRecarga}/{recargaSeleccionada}/{uidInterno}",
                        arguments = listOf(
                            navArgument("uid") { type = NavType.StringType },
                            navArgument("valorRecarga") { type = NavType.StringType },
                            navArgument("recargaSeleccionada") { type = NavType.StringType },
                            navArgument("uidInterno") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        MediosDePagos(
                            navController = navController,
                            uid = backStackEntry.arguments?.getString("uid") ?: "",
                            valorRecarga = backStackEntry.arguments?.getString("valorRecarga") ?: "",
                            recargaSeleccionada = backStackEntry.arguments?.getString("recargaSeleccionada") ?: "",
                            uidInterno = backStackEntry.arguments?.getString("uidInterno") ?: "",
                            tcService
                        )
                    }

                    // Añade más composable según necesites
                }
            }
        }
    }
}
