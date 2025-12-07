package com.example.holocrew.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
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
        // Lista de ítems de navegación
        val items = listOf(
            Screen.News to Icons.Filled.Notifications,  // Noticias
            Screen.Available to Icons.Filled.ShoppingCart,
            Screen.Upcoming to Icons.Filled.DateRange,
            Screen.Map to Icons.Filled.LocationOn,
            Screen.Profile to Icons.Filled.Person
        )

        items.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.route,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = when (screen) {
                            Screen.News -> "Noticias"
                            Screen.Available -> "Disponibles"
                            Screen.Upcoming -> "Próximos"
                            Screen.Map -> "Mapa"
                            Screen.Profile -> "Perfil"
                            else -> screen.route
                        },
                        fontSize = 12.sp
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}