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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.data.network.CartRepository
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.data.TokenManager
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

/**
 * ProfileScreen — Pantalla de perfil del usuario.
 *
 * Diseño SNKRS con:
 *  - Header negro con avatar (iniciales), nombre, email y badge de tier
 *  - Estadísticas (pedidos, favoritos, carrito) sobre fondo negro
 *  - Accesos rápidos en cards grises
 *  - Banner de membresía con gradiente oscuro
 *  - Menú "Mi cuenta" y "Ajustes" con items clickables
 *  - Botón cerrar sesión en rojo Pulse
 *
 * El tier del usuario se refleja en el color del badge y el borde del avatar.
 * Colores de tier: Platinum=gris claro, Gold=dorado, Silver=plata, Bronze=bronce.
 * (Nota: los colores de tier son los ÚNICOS que no usan Pulse — son propios de cada nivel)
 *
 * @param navController Controlador de navegación (nullable para previews).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController? = null) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── Datos del usuario desde DataStore ──────────────────────────────────────
    val userName by TokenManager.getUserName(context).collectAsState(initial = "Holo User")
    val userEmail by TokenManager.getUserEmail(context).collectAsState(initial = "")
    val userTier by TokenManager.getUserTier(context).collectAsState(initial = "bronze")

    // ── Contadores de favoritos y carrito (observados en tiempo real) ──────────
    val favoriteIds by WishlistRepository.favoriteIds.collectAsState()
    val cartItems by CartRepository.cartItems.collectAsState()
    val favCount = favoriteIds.size
    val cartCount = cartItems.sumOf { it.quantity }

    var orderCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        orderCount = com.example.holocrew.data.network.OrderRepository.getOrders().size
    }

    // ── Iniciales del usuario para el avatar ──────────────────────────────────
    val initials = remember(userName) {
        userName?.split(" ")
            ?.take(2)
            ?.mapNotNull { it.firstOrNull()?.uppercase() }
            ?.joinToString("") ?: "HC"
    }

    // ── Display y color del tier ──────────────────────────────────────────────
    // Estos colores son PROPIOS de cada tier (dorado, plata, etc.) — no usan Pulse.
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
            "platinum" -> HoloColors.Neutral300
            "gold" -> HoloColors.Warning
            "silver" -> HoloColors.Neutral400
            else -> HoloColors.Pulse   // Bronze usa Pulse (rojo) como acento
        }
    }

    // ── Scaffold con bottomNav ────────────────────────────────────────────────
    Scaffold(
        bottomBar = {
            if (navController != null) BottomNavigationBar(navController = navController)
        },
        containerColor = HoloColors.Paper
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.BottomNavHeight)
        ) {

            // ══════════════════════════════════════════════════════════════════
            // HEADER NEGRO — avatar, nombre, email, badge de tier, botón editar
            // ══════════════════════════════════════════════════════════════════
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HoloColors.Ink)
                        .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xxl)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar con iniciales y borde de color del tier
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(HoloColors.Neutral700)
                                .border(HoloSpacing.BorderDefault, tierColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = HoloType.HeadlineMedium,
                                color = tierColor
                            )
                        }

                        Spacer(Modifier.width(HoloSpacing.md))

                        Column {
                            // Nombre del usuario
                            Text(
                                text = userName ?: "Holo User",
                                style = HoloType.HeadlineMedium,
                                color = HoloColors.TextOnDark
                            )
                            // Email
                            Text(
                                text = userEmail ?: "",
                                style = HoloType.BodySmall,
                                color = HoloColors.TextOnDarkMuted,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            // Badge de tier
                            Box(
                                modifier = Modifier
                                    .padding(top = HoloSpacing.xxs)
                                    .clip(RoundedCornerShape(HoloSpacing.RadiusXs))
                                    .background(tierColor.copy(alpha = 0.15f))
                                    .padding(horizontal = HoloSpacing.xs, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tierDisplay,
                                    style = HoloType.LabelSmall,
                                    color = tierColor
                                )
                            }
                        }
                    }
                }
            }

            // ══════════════════════════════════════════════════════════════════
            // ESTADÍSTICAS — pedidos, favoritos, carrito (sobre fondo negro)
            // ══════════════════════════════════════════════════════════════════
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HoloColors.Ink)
                        .padding(horizontal = HoloSpacing.lg)
                        .padding(bottom = HoloSpacing.xl),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(value = "$orderCount", label = "Pedidos", icon = Icons.Outlined.ShoppingBag)
                    StatCard(value = "$favCount", label = "Favoritos", icon = Icons.Outlined.FavoriteBorder)
                    StatCard(value = "$cartCount", label = "En carrito", icon = Icons.Outlined.ShoppingCart)
                }
            }

            // ══════════════════════════════════════════════════════════════════
            // ACCESOS RÁPIDOS — 3 cards grises en fila
            // ══════════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(HoloSpacing.md))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HoloSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
                ) {
                    QuickAction(
                        icon = Icons.Outlined.ShoppingBag,
                        label = "Pedidos",
                        modifier = Modifier.weight(1f),
                        onClick = { navController?.navigate("my_orders") { launchSingleTop = true } }
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

            // ══════════════════════════════════════════════════════════════════
            // BANNER MIEMBRO — gradiente oscuro con info del tier
            // ══════════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(HoloSpacing.lg))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HoloSpacing.md)
                        .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
                        .clickable { navController?.navigate("members") { launchSingleTop = true } }
                        .background(
                            Brush.horizontalGradient(
                                listOf(HoloColors.Neutral800, HoloColors.Neutral700)
                            )
                        )
                        .padding(HoloSpacing.lg)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "HOLOCREW MEMBER",
                                style = HoloType.LabelMedium,
                                color = tierColor
                            )
                            Spacer(Modifier.height(HoloSpacing.xxs))
                            Text(
                                text = "Envío gratis en todos\ntus pedidos",
                                style = HoloType.TitleLarge,
                                color = HoloColors.TextOnDark
                            )
                            Spacer(Modifier.height(HoloSpacing.xxs))
                            Text(
                                text = "Acceso anticipado a nuevos drops",
                                style = HoloType.BodySmall,
                                color = HoloColors.TextOnDarkMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = tierColor,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            // ══════════════════════════════════════════════════════════════════
            // MI CUENTA — menú con items navegables
            // ══════════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(HoloSpacing.xl))
                SectionTitle("MI CUENTA")
                Spacer(Modifier.height(HoloSpacing.xs))

                MenuItem(icon = Icons.Outlined.CreditCard, title = "Métodos de pago", subtitle = "Tarjetas guardadas",
                    onClick = { navController?.navigate("payment_methods") { launchSingleTop = true } })
                MenuItem(icon = Icons.Outlined.LocationOn, title = "Direcciones de envío", subtitle = "Gestiona tus direcciones",
                    onClick = { navController?.navigate("addresses") { launchSingleTop = true } })
                MenuItem(icon = Icons.Outlined.Person, title = "Datos personales", subtitle = "Nombre, email, teléfono",
                    onClick = { navController?.navigate("edit_profile") { launchSingleTop = true } })
            }

            // ══════════════════════════════════════════════════════════════════
            // AJUSTES — menú informativo (no navegable en esta entrega)
            // ══════════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(HoloSpacing.lg))
                SectionTitle("AJUSTES")
                Spacer(Modifier.height(HoloSpacing.xs))

                MenuItem(icon = Icons.Outlined.Notifications, title = "Notificaciones", subtitle = "Drops, ofertas, pedidos",
                    onClick = { navController?.navigate("notifications") { launchSingleTop = true } })
                MenuItem(icon = Icons.Outlined.Lock, title = "Privacidad y seguridad", subtitle = "Contraseña, eliminar cuenta",
                    onClick = { navController?.navigate("security") { launchSingleTop = true } })
                MenuItem(icon = Icons.Outlined.Info, title = "Sobre HoloCrew", subtitle = "Version 1.0.0",
                    onClick = { navController?.navigate("about") { launchSingleTop = true } })
                MenuItem(icon = Icons.Outlined.Help, title = "Centro de ayuda", subtitle = "FAQ, contacto, soporte",
                    onClick = { navController?.navigate("help") { launchSingleTop = true } })
            }

            // ══════════════════════════════════════════════════════════════════
            // CERRAR SESIÓN — borde rojo, texto rojo Pulse
            // ══════════════════════════════════════════════════════════════════
            item {
                Spacer(Modifier.height(HoloSpacing.xl))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HoloSpacing.md)
                        .clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                        .border(
                            HoloSpacing.BorderHairline,
                            HoloColors.BorderSubtle,
                            RoundedCornerShape(HoloSpacing.RadiusMd)
                        )
                        .clickable {
                            scope.launch {
                                // Limpiar sesión: token + carrito + favoritos
                                TokenManager.clearToken(context)
                                scope.launch { CartRepository.clearCart() }
                                WishlistRepository.clearLocal()
                                // Volver a login limpiando todo el backstack
                                navController?.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                        .padding(HoloSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cerrar sesión",
                        style = HoloType.TitleMedium,
                        color = HoloColors.Pulse   // Rojo para acción destructiva
                    )
                }
                Spacer(Modifier.height(HoloSpacing.md))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES DEL PERFIL
// ══════════════════════════════════════════════════════════════════════════════

/**
 * StatCard — Estadística individual (pedidos, favoritos, carrito).
 * Icono + número + label sobre fondo negro.
 */
@Composable
fun StatCard(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HoloColors.TextOnDarkMuted,
            modifier = Modifier.size(HoloSpacing.IconSizeDefault)
        )
        Spacer(Modifier.height(HoloSpacing.xxs))
        Text(text = value, style = HoloType.HeadlineMedium, color = HoloColors.TextOnDark)
        Text(text = label, style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted)
    }
}

/**
 * QuickAction — Card de acceso rápido (Mis Pedidos, Favoritos, Carrito).
 * Fondo gris Fog, icono negro + label.
 */
@Composable
fun QuickAction(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Fog),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HoloSpacing.sm),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = HoloColors.Ink,
                modifier = Modifier.size(HoloSpacing.IconSizeDefault)
            )
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text(text = label, style = HoloType.BodySmall, color = HoloColors.TextPrimary)
        }
    }
}

/**
 * SectionTitle — Título de sección del menú.
 * Estilo label uppercase gris.
 */
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = HoloType.LabelMedium,
        color = HoloColors.TextTertiary,
        modifier = Modifier.padding(horizontal = HoloSpacing.md)
    )
}

/**
 * MenuItem — Item individual del menú de perfil.
 *
 * Icono en cuadrado gris + título + subtítulo + chevron derecho.
 * Opcionalmente puede llevar un badge de color.
 *
 * @param icon Icono Material del item.
 * @param title Texto principal.
 * @param subtitle Texto secundario bajo el título.
 * @param badgeText Texto del badge (null = sin badge).
 * @param badgeColor Color del badge.
 * @param onClick Callback al pulsar el item.
 */
@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    badgeText: String? = null,
    badgeColor: androidx.compose.ui.graphics.Color = HoloColors.Ink,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono en cuadrado gris
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                .background(HoloColors.Fog),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = HoloColors.Ink,
                modifier = Modifier.size(HoloSpacing.IconSizeDefault)
            )
        }

        Spacer(Modifier.width(HoloSpacing.sm))

        // Título + subtítulo
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = HoloType.BodySmall,
                    color = HoloColors.TextTertiary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Badge opcional
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(HoloSpacing.RadiusXs))
                    .background(badgeColor.copy(alpha = 0.1f))
                    .padding(horizontal = HoloSpacing.xs, vertical = HoloSpacing.xxs)
            ) {
                Text(text = badgeText, style = HoloType.LabelSmall, color = badgeColor)
            }
            Spacer(Modifier.width(HoloSpacing.xs))
        }

        // Chevron derecho
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = HoloColors.Neutral300,
            modifier = Modifier.size(HoloSpacing.IconSizeDefault)
        )
    }
}