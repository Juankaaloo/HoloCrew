/**
 * ProfileScreen.kt
 *
 * Pantalla de perfil del usuario en HoloCrew.
 * Diseño premium con:
 *  - Header negro con avatar, nombre y email
 *  - Estadísticas dinámicas (favoritos y carrito reales)
 *  - Accesos rápidos (Pedidos, Favoritos, Carrito)
 *  - Banner de miembro HoloCrew
 *  - Secciones de menú limpias y organizadas
 *  - Botón de cerrar sesión
 *
 * Las estadísticas de favoritos y carrito se leen en tiempo real
 * desde FavoritesManager y CartManager.
 */
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.FavoritesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController? = null) {

    // Estadísticas dinámicas reales
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val cartItems by CartManager.items.collectAsState()
    val favCount = favoriteIds.size
    val cartCount = cartItems.sumOf { it.quantity }

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

            // ════════════════════════════════════════════════════════════
            // HEADER NEGRO con avatar, nombre y email
            // ════════════════════════════════════════════════════════════
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.Black)
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2D2D2D))
                                .border(2.dp, Color(0xFFD4AF37), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "HC",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD4AF37)
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        // Nombre y email
                        Column {
                            Text("Holo User", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("holocrew@example.com", fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.padding(top = 2.dp))
                            // Badge miembro
                            Box(
                                modifier = Modifier.padding(top = 6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFD4AF37).copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("MIEMBRO GOLD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 1.sp)
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        // Icono editar
                        IconButton(onClick = {}) {
                            Icon(Icons.Outlined.Edit, "Editar perfil", tint = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════
            // ESTADÍSTICAS DINÁMICAS
            // ════════════════════════════════════════════════════════════
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.Black).padding(horizontal = 20.dp).padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(value = "12", label = "Pedidos", icon = Icons.Outlined.ShoppingBag)
                    StatCard(value = "$favCount", label = "Favoritos", icon = Icons.Outlined.FavoriteBorder)
                    StatCard(value = "$cartCount", label = "En carrito", icon = Icons.Outlined.ShoppingCart)
                }
            }

            // ════════════════════════════════════════════════════════════
            // ACCESOS RÁPIDOS
            // ════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAction(
                        icon = Icons.Outlined.ShoppingBag,
                        label = "Mis Pedidos",
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                    QuickAction(
                        icon = Icons.Outlined.FavoriteBorder,
                        label = "Favoritos",
                        modifier = Modifier.weight(1f),
                        onClick = { navController?.navigate("favorites") { launchSingleTop = true } }
                    )
                    QuickAction(
                        icon = Icons.Outlined.ShoppingCart,
                        label = "Carrito",
                        modifier = Modifier.weight(1f),
                        onClick = { navController?.navigate("cart") { launchSingleTop = true } }
                    )
                }
            }

            // ════════════════════════════════════════════════════════════
            // BANNER MIEMBRO
            // ════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("HoloCrew Member", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 1.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Envío gratis en todos\ntus pedidos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Acceso anticipado a nuevos drops", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFD4AF37), modifier = Modifier.size(40.dp))
                    }
                }
            }

            // ════════════════════════════════════════════════════════════
            // PEDIDOS RECIENTES
            // ════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("PEDIDOS RECIENTES")
                Spacer(Modifier.height(8.dp))

                MenuItem(icon = Icons.Outlined.ShoppingBag, title = "Holo Pannel Hoodie", subtitle = "Entregado · 15 Nov 2024", badgeText = "Entregado", badgeColor = Color(0xFF4CAF50))
                MenuItem(icon = Icons.Outlined.ShoppingBag, title = "Denim Bison Holo", subtitle = "En camino · 20 Nov 2024", badgeText = "En camino", badgeColor = Color(0xFF2196F3))
                MenuItem(icon = Icons.Outlined.ShoppingBag, title = "Glory Holo Polo", subtitle = "Procesando · 25 Nov 2024", badgeText = "Procesando", badgeColor = Color(0xFFFF9800))
            }

            // ════════════════════════════════════════════════════════════
            // CUENTA
            // ════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(20.dp))
                SectionTitle("MI CUENTA")
                Spacer(Modifier.height(8.dp))

                MenuItem(icon = Icons.Outlined.CreditCard, title = "Métodos de pago", subtitle = "Visa terminada en 4242")
                MenuItem(icon = Icons.Outlined.LocationOn, title = "Direcciones de envío", subtitle = "Casa · Calle Ejemplo 123, Madrid")
                MenuItem(icon = Icons.Outlined.Person, title = "Datos personales", subtitle = "Nombre, email, teléfono")
            }

            // ════════════════════════════════════════════════════════════
            // AJUSTES
            // ════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(20.dp))
                SectionTitle("AJUSTES")
                Spacer(Modifier.height(8.dp))

                MenuItem(icon = Icons.Outlined.Notifications, title = "Notificaciones", subtitle = "Drops, ofertas, pedidos")
                MenuItem(icon = Icons.Outlined.Lock, title = "Privacidad y seguridad", subtitle = "Contraseña, sesiones activas")
                MenuItem(icon = Icons.Outlined.Info, title = "Sobre HoloCrew", subtitle = "Versión 1.0.0")
                MenuItem(icon = Icons.Outlined.Help, title = "Centro de ayuda", subtitle = "FAQ, contacto, soporte")
            }

            // ════════════════════════════════════════════════════════════
            // CERRAR SESIÓN
            // ════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .clickable {
                            // Cerrar sesión: navegar al Login y limpiar el backstack
                            navController?.navigate("login") {
                                popUpTo(0) { inclusive = true }
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

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

/** Card de estadística en el header */
@Composable
fun StatCard(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
    }
}

/** Botón de acceso rápido */
@Composable
fun QuickAction(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, label, tint = Color.Black, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}

/** Título de sección */
@Composable
fun SectionTitle(title: String) {
    Text(
        title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E),
        letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp)
    )
}

/** Item de menú con icono, título, subtítulo, badge opcional y flecha */
@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    badgeText: String? = null,
    badgeColor: Color = Color.Transparent,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono con fondo
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, title, tint = Color.Black, modifier = Modifier.size(20.dp))
        }

        Spacer(Modifier.width(14.dp))

        // Textos
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 2.dp))
            }
        }

        // Badge opcional
        if (badgeText != null) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(badgeText, fontSize = 11.sp, color = badgeColor, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }

        // Flecha
        Icon(Icons.Filled.ChevronRight, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
    }
}