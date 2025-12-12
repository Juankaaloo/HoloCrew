package com.example.holocrew.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.holocrew.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Obtenemos la entrada actual del backstack (pantalla actual)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    // Extraemos la ruta actual para marcar el elemento seleccionado
    val currentRoute = currentBackStackEntry?.destination?.route

    // Barra de navegación inferior de Material 3
    NavigationBar(
        containerColor = Color.White,   // Fondo blanco
        tonalElevation = 8.dp,           // Sombra para dar elevación visual
        modifier = modifier
    ) {
        // Lista de items que aparecerán en la barra inferior
        val navigationItems = listOf(
            NavItem(
                screen = Screen.Home,
                icon = Icons.Filled.Home,
                label = "Inicio"
            ),
            NavItem(
                screen = Screen.Available,
                icon = Icons.Filled.ShoppingCart,
                label = "Disponibles"
            ),
            NavItem(
                screen = Screen.Upcoming,
                icon = Icons.Filled.DateRange,
                label = "Próximos"
            ),
            NavItem(
                screen = Screen.Map,
                icon = Icons.Filled.LocationOn,
                label = "Mapa"
            ),
            NavItem(
                screen = Screen.Profile,
                icon = Icons.Filled.Person,
                label = "Perfil"
            )
        )

        // Dibujamos cada ítem de la barra inferior
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    // Icono visible del item
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    // Texto descriptivo del item
                    Text(
                        text = item.label,
                        fontSize = 12.sp
                    )
                },
                // Marcamos el item como seleccionado según la ruta actual
                selected = currentRoute == item.screen.route,
                onClick = {
                    // Navegamos solo si no estamos ya en la pantalla destino
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            launchSingleTop = true      // Evita duplicar pantallas en el stack
                            restoreState = true         // Restaura estado previo si existía
                        }
                    }
                }
            )
        }
    }
}

// Clase para representar cada item del menú inferior
data class NavItem(
    val screen: Screen,          // Pantalla destino
    val icon: ImageVector,       // Icono que se mostrará
    val label: String            // Texto visible bajo el icono
)
