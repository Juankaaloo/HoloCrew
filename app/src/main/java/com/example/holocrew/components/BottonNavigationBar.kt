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
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
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

        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp
                    )
                },
                selected = currentRoute == item.screen.route,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

// Clase auxiliar para los items de navegación
data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)