package com.example.holocrew.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.holocrew.ui.available.AvailableScreen
import com.example.holocrew.ui.auth.LoginScreen
import com.example.holocrew.ui.auth.RegisterScreen
import com.example.holocrew.ui.cart.CartScreen
import com.example.holocrew.ui.favorites.FavoritesScreen
import com.example.holocrew.ui.home.HomeScreen
import com.example.holocrew.ui.product.ProductDetailScreen
import com.example.holocrew.ui.profile.AddressesScreen
import com.example.holocrew.ui.profile.EditProfileScreen
import com.example.holocrew.ui.profile.MyOrdersScreen
import com.example.holocrew.ui.profile.PaymentMethodsScreen
import com.example.holocrew.ui.profile.ProfileScreen
import com.example.holocrew.ui.search.SearchScreen
import com.example.holocrew.ui.upcoming.UpcomingScreen
import com.example.holocrew.ui.cart.CheckoutScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) { LoginScreen(navController = navController) }
        composable(Screen.Register.route) { RegisterScreen(navController = navController) }
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Available.route) { AvailableScreen(navController = navController) }
        composable(Screen.Upcoming.route) { UpcomingScreen(navController = navController) }
        composable(Screen.Cart.route) { CartScreen(navController = navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
        composable(Screen.Favorites.route) { FavoritesScreen(navController = navController) }
        composable(Screen.Search.route) { SearchScreen(navController = navController) }

        // Nuevas pantallas de perfil
        composable(Screen.EditProfile.route) { EditProfileScreen(navController = navController) }
        composable(Screen.MyOrders.route) { MyOrdersScreen(navController = navController) }
        composable(Screen.Addresses.route) { AddressesScreen(navController = navController) }
        composable(Screen.PaymentMethods.route) { PaymentMethodsScreen(navController = navController) }
        composable(Screen.Checkout.route) { CheckoutScreen(navController = navController) }

        // Product Detail con UUID
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(navController = navController, productId = productId)
        }
    }
}
