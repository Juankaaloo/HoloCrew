// Screen.kt en: com.example.holocrew.navigation
package com.example.holocrew.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Available : Screen("available")
    object Upcoming : Screen("upcoming")
    object Map : Screen("map")
    object Profile : Screen("profile")
}