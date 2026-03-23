/**
 * Screens.kt
 *
 * Define todas las rutas de navegación de la aplicación HoloCrew.
 * Cada objeto dentro de la sealed class representa una pantalla/destino
 * con su ruta única que se usa en el NavHost y en la navegación.
 *
 * Pantallas disponibles:
 *  - Home: feed principal con drops y colecciones
 *  - Available: catálogo de productos con filtros
 *  - Upcoming: próximos lanzamientos y pre-órdenes
 *  - Cart: carrito de compras
 *  - Profile: perfil del usuario
 *  - Search: buscador de productos en tiempo real
 *
 * NOTA: La ruta de ProductDetail ("product_detail/{productId}") se define
 * directamente en AppNavigation.kt porque recibe argumentos dinámicos.
 */
package com.example.holocrew.navigation

/**
 * Sealed class que agrupa todas las rutas de la app.
 * Al ser sealed, el compilador puede verificar que se manejan
 * todos los casos posibles en expresiones when.
 *
 * @param route String único que identifica la pantalla en el NavHost
 */
sealed class Screen(val route: String) {

    /** Pantalla principal — feed de noticias con drops destacados */
    object Home : Screen("home")

    /** Catálogo de productos disponibles con filtros por categoría */
    object Available : Screen("available")

    /** Próximos lanzamientos y pre-órdenes */
    object Upcoming : Screen("upcoming")

    /** Carrito de compras */
    object Cart : Screen("cart")

    /** Perfil del usuario */
    object Profile : Screen("profile")

    /** Buscador de productos en tiempo real */
    object Search : Screen("search")

    /** Pantalla de productos favoritos del usuario */
    object Favorites : Screen("favorites")

    /** Pantalla de inicio de sesión */
    object Login : Screen("login")

    /** Pantalla de registro de nuevo usuario */
    object Register : Screen("register")
}