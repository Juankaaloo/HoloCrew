package com.example.holocrew.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.data.TokenManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController? = null) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userName by TokenManager.getUserName(context).collectAsState(initial = "Holo User")
    val userEmail by TokenManager.getUserEmail(context).collectAsState(initial = "")
    val userTier by TokenManager.getUserTier(context).collectAsState(initial = "bronze")

    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val cartItems by CartManager.items.collectAsState()
    val favCount = favoriteIds.size
    val cartCount = cartItems.sumOf { it.quantity }

    val initials = remember(userName) {
        userName?.split(" ")?.take(2)?.mapNotNull { it.firstOrNull()?.uppercase() }?.joinToString("") ?: "HC"
    }

    val tierDisplay = remember(userTier) {
        when (userTier?.lowercase()) {
            "platinum" -> "MIEMBRO PLATINUM"
            "gold" -> "MIEMBRO GOLD"
            "silver" -> "MIEMBRO SILVER"
            else -> "MIEMBRO BRONZE"
        }
    }

    val tierColor = remember(userTier) {
        when (userTier?.lowercase()) {
            "platinum" -> Color(0xFFE5E4E2)
            "gold" -> Color(0xFFD4AF37)
            "silver" -> Color(0xFFC0C0C0)
            else -> Color(0xFFCD7F32)
        }
    }

    Scaffold(
        bottomBar = {
            if (navController != null) BottomNavigationBar(navController = navController)
        },
        containerColor = Color.White
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // HEADER
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color.Black)
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(64.dp).clip(CircleShape)
                                .background(Color(0xFF2D2D2D)).border(2.dp, tierColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = tierColor)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(userName ?: "Holo User", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(userEmail ?: "", fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.padding(top = 2.dp))
                            Box(
                                modifier = Modifier.padding(top = 6.dp).clip(RoundedCornerShape(4.dp))
                                    .background(tierColor.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(tierDisplay, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tierColor, letterSpacing = 1.sp)
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        // ✅ Botón editar perfil funcional
                        IconButton(onClick = { navController?.navigate("edit_profile") { launchSingleTop = true } }) {
                            Icon(Icons.Outlined.Edit, "Editar perfil", tint = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            // ESTADÍSTICAS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.Black).padding(horizontal = 20.dp).padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(value = "0", label = "Pedidos", icon = Icons.Outlined.ShoppingBag)
                    StatCard(value = "$favCount", label = "Favoritos", icon = Icons.Outlined.FavoriteBorder)
                    StatCard(value = "$cartCount", label = "En carrito", icon = Icons.Outlined.ShoppingCart)
                }
            }

            // ACCESOS RÁPIDOS
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ✅ Mis Pedidos funcional
                    QuickAction(icon = Icons.Outlined.ShoppingBag, label = "Mis Pedidos", modifier = Modifier.weight(1f),
                        onClick = { navController?.navigate("my_orders") { launchSingleTop = true } })
                    QuickAction(icon = Icons.Outlined.FavoriteBorder, label = "Favoritos", modifier = Modifier.weight(1f),
                        onClick = { navController?.navigate("favorites") { launchSingleTop = true } })
                    QuickAction(icon = Icons.Outlined.ShoppingCart, label = "Carrito", modifier = Modifier.weight(1f),
                        onClick = { navController?.navigate("cart") { launchSingleTop = true } })
                }
            }

            // BANNER MIEMBRO
            item {
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))))
                        .padding(20.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("HoloCrew Member", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = tierColor, letterSpacing = 1.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Envío gratis en todos\ntus pedidos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Acceso anticipado a nuevos drops", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        Icon(Icons.Filled.Star, null, tint = tierColor, modifier = Modifier.size(40.dp))
                    }
                }
            }

            // MI CUENTA — ✅ Todos funcionales
            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("MI CUENTA")
                Spacer(Modifier.height(8.dp))
                MenuItem(icon = Icons.Outlined.CreditCard, title = "Métodos de pago", subtitle = "Gestiona tus métodos de pago",
                    onClick = { navController?.navigate("payment_methods") { launchSingleTop = true } })
                MenuItem(icon = Icons.Outlined.LocationOn, title = "Direcciones de envío", subtitle = "Gestiona tus direcciones",
                    onClick = { navController?.navigate("addresses") { launchSingleTop = true } })
                MenuItem(icon = Icons.Outlined.Person, title = "Datos personales", subtitle = "Nombre, email, teléfono",
                    onClick = { navController?.navigate("edit_profile") { launchSingleTop = true } })
            }

            // AJUSTES
            item {
                Spacer(Modifier.height(20.dp))
                SectionTitle("AJUSTES")
                Spacer(Modifier.height(8.dp))
                MenuItem(icon = Icons.Outlined.Notifications, title = "Notificaciones", subtitle = "Drops, ofertas, pedidos")
                MenuItem(icon = Icons.Outlined.Lock, title = "Privacidad y seguridad", subtitle = "Contraseña, sesiones activas")
                MenuItem(icon = Icons.Outlined.Info, title = "Sobre HoloCrew", subtitle = "Versión 1.0.0")
                MenuItem(icon = Icons.Outlined.Help, title = "Centro de ayuda", subtitle = "FAQ, contacto, soporte")
            }

            // CERRAR SESIÓN
            item {
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .clickable {
                            scope.launch {
                                TokenManager.clearToken(context)
                                CartManager.clearCart(context)
                                FavoritesManager.clearFavorites()
                                navController?.navigate("login") { popUpTo(0) { inclusive = true } }
                            }
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cerrar sesión", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFFE53935))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
    }
}

@Composable
fun QuickAction(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, label, tint = Color.Black, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun MenuItem(icon: ImageVector, title: String, subtitle: String = "", badgeText: String? = null, badgeColor: Color = Color.Transparent, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
            Icon(icon, title, tint = Color.Black, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            if (subtitle.isNotEmpty()) Text(subtitle, fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 2.dp))
        }
        if (badgeText != null) {
            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(badgeColor.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(badgeText, fontSize = 11.sp, color = badgeColor, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }
        Icon(Icons.Filled.ChevronRight, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
    }
}
