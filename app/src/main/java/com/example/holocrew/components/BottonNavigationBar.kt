/**
 * BottomNavigationBar.kt
 *
 * Barra de navegación inferior personalizada de la aplicación HoloCrew.
 * Diseño con forma de píldora flotante con un botón central del carrito
 * elevado que muestra un badge con el número de items.
 *
 * Estructura:
 *  - Píldora blanca con sombra que contiene los iconos de navegación
 *  - Lado izquierdo: Home + Explore
 *  - Centro: botón flotante del carrito (elevado sobre la píldora)
 *  - Lado derecho: Drops + Profile
 *
 * El badge del carrito se actualiza en tiempo real gracias al CartManager.
 * Los iconos cambian de color y peso según la pantalla activa.
 */
package com.example.holocrew.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.holocrew.data.network.CartRepository
import com.example.holocrew.navigation.Screen
import androidx.compose.material.icons.filled.GridView


/**
 * Barra de navegación inferior con diseño de píldora flotante.
 *
 * @param navController Controlador de navegación para cambiar entre pantallas
 * @param modifier Modifier opcional para personalizar desde el padre
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Observar la ruta actual para resaltar el icono activo
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Observar el número de items en el carrito para el badge
    val cartItems by CartRepository.cartItems.collectAsState()
    val cartItemCount = cartItems.sumOf { it.quantity }

    // Definir los items de navegación (izquierda y derecha del carrito)
    val leftItems = listOf(
        NavItem(screen = Screen.Home, icon = Icons.Filled.Home, label = "Home"),
        NavItem(screen = Screen.Available, icon = Icons.Filled.GridView, label = "Shop")
    )
    val rightItems = listOf(
        NavItem(screen = Screen.Upcoming, icon = Icons.Filled.Bolt, label = "Drops"),
        NavItem(screen = Screen.Profile, icon = Icons.Filled.Person, label = "Perfil")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // ── Píldora principal (barra blanca con sombra) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = Color.Black.copy(alpha = 0.12f),
                    spotColor = Color.Black.copy(alpha = 0.12f)
                )
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Items izquierdos
                leftItems.forEach { item ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        NavBarItem(
                            item = item,
                            isSelected = currentRoute == item.screen.route,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }

                // Espacio central para el carrito (proporcional)
                Spacer(modifier = Modifier.weight(1.2f))

                // Items derechos
                rightItems.forEach { item ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        NavBarItem(
                            item = item,
                            isSelected = currentRoute == item.screen.route,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // ── Botón flotante del carrito (centrado, elevado) ──
        // Cambia a verde cuando hay productos en el carrito
        val hasItems = cartItemCount > 0
        val cartBgColor by animateColorAsState(
            targetValue = if (hasItems) Color(0xFF4CAF50) else Color.White,
            label = "cartBg"
        )
        val cartIconColor by animateColorAsState(
            targetValue = if (hasItems) Color.White else Color.Black,
            label = "cartIcon"
        )

        Box(
            modifier = Modifier
                .size(58.dp)
                .align(Alignment.TopCenter)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = Color.Black.copy(alpha = 0.18f),
                    spotColor = Color.Black.copy(alpha = 0.18f)
                )
                .clip(RoundedCornerShape(18.dp))
                .background(cartBgColor)
                .clickable {
                    navController.navigate(Screen.Cart.route) { launchSingleTop = true }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Carrito",
                tint = cartIconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Item individual de la barra de navegación.
 * Muestra un icono y un label debajo. Cambia de estilo cuando está seleccionado:
 *  - Seleccionado: icono negro, texto negro en negrita, ligeramente escalado
 *  - No seleccionado: icono gris, texto gris normal
 *
 * @param item Datos del item (pantalla, icono, label)
 * @param isSelected Si este item corresponde a la pantalla actual
 * @param onClick Callback al pulsar el item
 */
@Composable
private fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animación de color para transición suave
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color(0xFF9E9E9E),
        label = "navIconColor"
    )

    // Animación de escala sutil al seleccionar
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "navScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                // Quitar el ripple para un look más limpio
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .scale(scale)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = item.label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = iconColor,
            maxLines = 1
        )
    }
}

/**
 * Modelo de datos para un item de la barra de navegación.
 *
 * @param screen Pantalla destino (de la sealed class Screen)
 * @param icon Icono Material del item
 * @param label Texto que se muestra debajo del icono
 */
data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)