package com.example.holocrew.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.CartRepository
import com.example.holocrew.data.network.SupabaseClient
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.available.AvailableScreen
import com.example.holocrew.ui.auth.LoginScreen
import com.example.holocrew.ui.auth.RegisterScreen
import com.example.holocrew.ui.cart.CartScreen
import com.example.holocrew.ui.cart.CheckoutScreen
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
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import com.example.holocrew.ui.profile.SecurityScreen
import com.example.holocrew.ui.profile.HelpScreen
import com.example.holocrew.ui.profile.AboutScreen
import com.example.holocrew.ui.profile.NotificationsScreen
import com.example.holocrew.ui.profile.MembersScreen
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash — comprueba si hay sesion guardada
        composable("splash") {
            val sessionStatus by SupabaseClient.client.auth.sessionStatus.collectAsState()

            LaunchedEffect(sessionStatus) {
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        TokenManager.loadProfile()
                        WishlistRepository.loadFavorites()
                        CartRepository.loadCart()
                        navController.navigate("home") { popUpTo(0) { inclusive = true } }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                    else -> { /* LoadingFromStorage — esperar */ }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().background(HoloColors.Ink),
                contentAlignment = Alignment.Center
            ) {
                Text("HOLOCREW", style = HoloType.DisplayMedium, color = HoloColors.Pulse)
            }
        }

        composable(Screen.Login.route) { LoginScreen(navController = navController) }
        composable(Screen.Register.route) { RegisterScreen(navController = navController) }
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Available.route) { AvailableScreen(navController = navController) }
        composable(Screen.Upcoming.route) { UpcomingScreen(navController = navController) }
        composable(Screen.Cart.route) { CartScreen(navController = navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
        composable(Screen.Favorites.route) { FavoritesScreen(navController = navController) }
        composable(Screen.Search.route) { SearchScreen(navController = navController) }

        composable(Screen.EditProfile.route) { EditProfileScreen(navController = navController) }
        composable(Screen.MyOrders.route) { MyOrdersScreen(navController = navController) }
        composable(Screen.Addresses.route) { AddressesScreen(navController = navController) }
        composable(Screen.PaymentMethods.route) { PaymentMethodsScreen(navController = navController) }
        composable(Screen.Checkout.route) { CheckoutScreen(navController = navController) }
        composable("security") { SecurityScreen(navController = navController) }
        composable("help") { HelpScreen(navController = navController) }
        composable("about") { AboutScreen(navController = navController) }
        composable("notifications") { NotificationsScreen(navController = navController) }
        composable("members") { MembersScreen(navController = navController) }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(navController = navController, productId = productId)
        }
    }
}