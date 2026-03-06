package com.example.holocrew.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.holocrew.ui.available.AvailableScreen
import com.example.holocrew.ui.cart.CartScreen
import com.example.holocrew.ui.home.HomeScreen
import com.example.holocrew.ui.profile.ProfileScreen
import com.example.holocrew.ui.upcoming.UpcomingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Available.route) {
            AvailableScreen(navController = navController)
        }
        composable(Screen.Upcoming.route) {
            UpcomingScreen(navController = navController)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}