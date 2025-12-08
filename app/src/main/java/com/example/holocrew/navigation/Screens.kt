package com.example.holocrew.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Available : Screen("available")
    object Upcoming : Screen("upcoming")
    object Map : Screen("map")
    object Profile : Screen("profile")

    // Si no usas News, elimínalo. Si lo usas, agrégalo a AppNavigation
    // object News : Screen("news")
}