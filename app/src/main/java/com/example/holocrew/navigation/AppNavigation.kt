package com.example.holocrew.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.holocrew.ui.available.AvailableScreen
import com.example.holocrew.ui.home.HomeScreen
import com.example.holocrew.ui.map.MapScreen
import com.example.holocrew.ui.product.ProductDetailScreen
import com.example.holocrew.ui.profile.ProfileScreen
import com.example.holocrew.ui.upcoming.UpcomingScreen

@Composable
fun AppNavigation() {
    // Controlador de navegación que manejará los cambios entre pantallas
    val navController = rememberNavController()

    // NavHost define la estructura de navegación de toda la app
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route  // Pantalla inicial de la aplicación
    ) {

        // Pantalla principal del home
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // Pantalla que muestra productos disponibles
        composable(Screen.Available.route) {
            AvailableScreen(navController = navController)
        }

        // Pantalla para próximos lanzamientos
        composable(Screen.Upcoming.route) {
            UpcomingScreen(navController = navController)
        }

        // Pantalla del mapa
        composable(Screen.Map.route) {
            MapScreen()
        }

        // Pantalla del perfil del usuario
        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        // Pantalla de detalle de producto con argumento dinámico (productId)
        composable("product_detail/{productId}") { backStackEntry ->
            // Extraemos el parámetro productId desde los argumentos de navegación
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull() ?: 0

            ProductDetailScreen(
                navController = navController,
                productId = productId   // Pasamos el id del producto a la pantalla de detalle
            )
        }
    }
}
