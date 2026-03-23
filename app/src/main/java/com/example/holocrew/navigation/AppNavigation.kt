/**
 * AppNavigation.kt
 *
 * Archivo central de navegación de la aplicación HoloCrew.
 * Define todas las rutas (destinos) disponibles en la app y configura
 * el NavHost de Jetpack Compose Navigation para gestionar las transiciones
 * entre pantallas.
 *
 * Pantallas registradas:
 *  - Home: pantalla principal con banner y carrusel de productos destacados
 *  - Available: catálogo de productos disponibles con filtros por categoría
 *  - Upcoming: listado de próximos lanzamientos y pre-órdenes
 *  - Cart: carrito de compras con resumen del pedido
 *  - Profile: perfil del usuario con pedidos, pagos, direcciones y ajustes
 *  - ProductDetail: pantalla de detalle de un producto individual (recibe productId)
 */
package com.example.holocrew.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.holocrew.ui.available.AvailableScreen
import com.example.holocrew.ui.auth.LoginScreen
import com.example.holocrew.ui.auth.RegisterScreen
import com.example.holocrew.ui.cart.CartScreen
import com.example.holocrew.ui.favorites.FavoritesScreen
import com.example.holocrew.ui.home.HomeScreen
import com.example.holocrew.ui.product.ProductDetailScreen
import com.example.holocrew.ui.profile.ProfileScreen
import com.example.holocrew.ui.search.SearchScreen
import com.example.holocrew.ui.upcoming.UpcomingScreen

/**
 * Composable raíz de navegación.
 *
 * Crea un NavController y define el grafo de navegación completo.
 * El destino inicial es la pantalla de Login.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route  // La app arranca en Login
    ) {

        // ── Pantalla Login ───────────────────────────────────────────────
        // Inicio de sesión del usuario
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        // ── Pantalla Register ────────────────────────────────────────────
        // Registro de nuevo usuario
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // ── Pantalla Home ──────────────────────────────────────────────
        // Muestra el banner principal y el carrusel de últimos lanzamientos
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // ── Pantalla Available (Explorar) ──────────────────────────────
        // Catálogo completo con filtros por categoría (Ropa, Denim, Accesorios, etc.)
        composable(Screen.Available.route) {
            AvailableScreen(navController = navController)
        }

        // ── Pantalla Upcoming (Próximos Drops) ─────────────────────────
        // Lista de productos que se lanzarán próximamente con fechas y pre-órdenes
        composable(Screen.Upcoming.route) {
            UpcomingScreen(navController = navController)
        }

        // ── Pantalla Cart (Carrito) ────────────────────────────────────
        // Carrito de compras con control de cantidades y resumen del pedido
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }

        // ── Pantalla Profile (Perfil) ──────────────────────────────────
        // Perfil del usuario: datos, pedidos, métodos de pago, direcciones y ajustes
        // Se pasa el navController para que tenga la barra de navegación inferior
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        // ── Pantalla Favorites (Favoritos) ─────────────────────────────
        // Lista de productos marcados como favoritos por el usuario
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }

        // ── Pantalla Search (Buscador) ─────────────────────────────────
        // Búsqueda de productos en tiempo real con sugerencias y resultados
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }

        // ── Pantalla ProductDetail (Detalle de Producto) ───────────────
        // Muestra la información completa de un producto: imagen, precio, tallas,
        // colores, descripción, características y botones de compra.
        //
        // Recibe un argumento "productId" de tipo Int a través de la ruta.
        // Ejemplo de navegación: navController.navigate("product_detail/3")
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType  // El ID del producto es un entero
                }
            )
        ) { backStackEntry ->
            // Extraer el productId de los argumentos de la ruta
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0

            ProductDetailScreen(
                navController = navController,
                productId = productId
            )
        }
    }
}