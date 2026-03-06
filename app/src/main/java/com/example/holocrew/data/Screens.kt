package com.example.holocrew.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Available : Screen("available")
    object Upcoming : Screen("upcoming")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
}