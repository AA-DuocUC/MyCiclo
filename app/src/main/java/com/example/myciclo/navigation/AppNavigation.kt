package com.example.myciclo.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myciclo.ui.views.BitacoraScreen
import com.example.myciclo.ui.views.HomeScreen
import com.example.myciclo.ui.views.LoginScreen
import com.example.myciclo.ui.views.RegistroScreen
import com.example.myciclo.ui.viewmodel.AppViewModelProvider
import com.example.myciclo.ui.viewmodel.CalendarioViewModel
import com.example.myciclo.ui.viewmodel.DiarioViewModel
import com.example.myciclo.ui.viewmodel.LoginViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
            LoginScreen(
                viewModel = loginViewModel,
                onIrARegistro = {
                    navController.navigate("registro")
                },
                onLoginExitoso = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onEntrarInvitado = {
                    navController.navigate("invitado") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registro") {
            RegistroScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            val diarioViewModel: DiarioViewModel = viewModel(factory = AppViewModelProvider.Factory)
            HomeScreen(
                diarioViewModel = diarioViewModel,
                onAbrirBitacora = { navController.navigate("bitacora/false") },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("invitado") {
            val diarioViewModel: DiarioViewModel = viewModel(factory = AppViewModelProvider.FactoryInvitado)
            HomeScreen(
                diarioViewModel = diarioViewModel,
                esInvitado = true,
                onAbrirBitacora = { navController.navigate("bitacora/true") },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("invitado") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "bitacora/{invitado}",
            arguments = listOf(navArgument("invitado") { type = NavType.BoolType })
        ) { entrada ->
            val invitado = entrada.arguments?.getBoolean("invitado") ?: false
            val calendarioViewModel: CalendarioViewModel = viewModel(
                factory = if (invitado) AppViewModelProvider.FactoryInvitado
                else AppViewModelProvider.Factory
            )
            BitacoraScreen(
                viewModel = calendarioViewModel,
                esInvitado = invitado,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}