package com.example.holocrew.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Available : Screen("available")
    object Upcoming : Screen("upcoming")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Login : Screen("login")
    object Register : Screen("register")

    // Nuevas pantallas de perfil
    object EditProfile : Screen("edit_profile")
    object MyOrders : Screen("my_orders")
    object Addresses : Screen("addresses")
    object PaymentMethods : Screen("payment_methods")
    object Security : Screen("security")
    object Help : Screen("help")
    object About : Screen("about")

    object Checkout : Screen("checkout")
}
