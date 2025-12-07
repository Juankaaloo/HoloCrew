package com.example.holocrew.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.holocrew.ui.available.AvailableScreen
import com.example.holocrew.ui.home.HomeScreen
import com.example.holocrew.ui.map.MapScreen
import com.example.holocrew.ui.news.NewsScreen
import com.example.holocrew.ui.profile.ProfileScreen
import com.example.holocrew.ui.upcoming.UpcomingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route  // ⭐ Home como inicio
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)  // ⭐ HomeScreen (antes NewsScreen)
        }
        composable(Screen.Available.route) {
            AvailableScreen()
        }
        composable(Screen.Upcoming.route) {
            UpcomingScreen(navController)  // ⭐ Nueva pantalla Próximos
        }
        composable(Screen.Map.route) {
            MapScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}