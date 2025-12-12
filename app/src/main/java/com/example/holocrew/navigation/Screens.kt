package com.example.holocrew.navigation

// Sealed class que define todas las rutas de navegación de la app.
// Usar una sealed class permite tener rutas tipadas y seguras en tiempo de compilación.
sealed class Screen(val route: String) {

    // Cada objeto representa una pantalla específica de la app.
    // La propiedad 'route' es la cadena que se usará para navegar.

    object Home : Screen("home")              // Ruta para la pantalla de inicio
    object Available : Screen("available")    // Ruta para productos disponibles
    object Upcoming : Screen("upcoming")      // Ruta para próximos lanzamientos
    object Map : Screen("map")                // Ruta para el mapa
    object Profile : Screen("profile")        // Ruta para el perfil del usuario

    // Ejemplo de pantalla extra. Si no se usa, se puede eliminar.
    // object News : Screen("news")
}
