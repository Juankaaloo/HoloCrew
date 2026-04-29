package com.example.holocrew.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.network.CartItemDto
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

/**
 * CartScreen — Pantalla del carrito de compras.
 *
 * Diseño SNKRS:
 *  - TopBar blanca con título "Mi Carrito" y botón "Vaciar" en rojo Pulse
 *  - Fondo gris Fog para separar visualmente las cards
 *  - Cards blancas para cada item con imagen placeholder, nombre, talla/color,
 *    precio y controles de cantidad (+/-)
 *  - Card de resumen del pedido: subtotal, envío (gratis >150€), total
 *  - Botón "FINALIZAR COMPRA" negro al final
 *  - Estado vacío con icono grande y CTA para explorar
 *
 * El carrito se recarga desde la API al abrir la pantalla (LaunchedEffect).
 * Los cambios (añadir, quitar, actualizar cantidad) se hacen via CartManager
 * que llama a la API y actualiza el StateFlow local.
 *
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── Observar estado del carrito en tiempo real ─────────────────────────────
    val cartItems by CartManager.items.collectAsState()
    val summary by CartManager.summary.collectAsState()

    // Recargar el carrito al abrir la pantalla
    LaunchedEffect(Unit) {
        CartManager.loadCart(context)
    }

    // ── Scaffold con TopBar ───────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Carrito",
                        style = HoloType.HeadlineMedium,
                        color = HoloColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = HoloColors.Ink
                        )
                    }
                },
                actions = {
                    // Botón "Vaciar" solo visible si hay items
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = {
                            scope.launch { CartManager.clearCart(context) }
                        }) {
                            Text(
                                text = "Vaciar",
                                style = HoloType.TitleMedium,
                                color = HoloColors.Pulse   // Rojo — acción destructiva
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HoloColors.Paper,
                    titleContentColor = HoloColors.TextPrimary
                )
            )
        },
        containerColor = HoloColors.Fog   // Fondo gris para separar cards
    ) { paddingValues ->

        // ══════════════════════════════════════════════════════════════════════
        // ESTADO VACÍO — carrito sin productos
        // ══════════════════════════════════════════════════════════════════════
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = HoloColors.Neutral300,
                        modifier = Modifier.size(HoloSpacing.huge)
                    )

                    Spacer(Modifier.height(HoloSpacing.md))

                    Text(
                        text = "Tu carrito está vacío",
                        style = HoloType.HeadlineMedium,
                        color = HoloColors.TextPrimary
                    )
                    Text(
                        text = "Añade productos para continuar",
                        style = HoloType.BodyMedium,
                        color = HoloColors.TextTertiary,
                        modifier = Modifier.padding(top = HoloSpacing.xs)
                    )

                    Spacer(Modifier.height(HoloSpacing.xl))

                    // CTA para volver a explorar
                    Button(
                        onClick = { navController.navigateUp() },
                        colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                        shape = RoundedCornerShape(HoloSpacing.RadiusMd)
                    ) {
                        Text(
                            text = "EXPLORAR PRODUCTOS",
                            style = HoloType.LabelLarge,
                            color = HoloColors.Paper
                        )
                    }
                }
            }
        } else {
            // ══════════════════════════════════════════════════════════════════
            // CONTENIDO DEL CARRITO — items + resumen + CTA
            // ══════════════════════════════════════════════════════════════════
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = HoloSpacing.md)
            ) {

                // ── Contador de productos ─────────────────────────────────────
                item {
                    Text(
                        text = "${cartItems.size} producto${if (cartItems.size != 1) "s" else ""}",
                        style = HoloType.BodySmall,
                        color = HoloColors.TextTertiary,
                        modifier = Modifier.padding(
                            horizontal = HoloSpacing.md,
                            vertical = HoloSpacing.sm
                        )
                    )
                }

                // ── Cards de cada item del carrito ────────────────────────────
                items(cartItems, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = {
                            scope.launch { CartManager.updateItem(context, item.id, item.quantity + 1) }
                        },
                        onDecrease = {
                            scope.launch {
                                if (item.quantity > 1)
                                    CartManager.updateItem(context, item.id, item.quantity - 1)
                                else
                                    CartManager.removeItem(context, item.id)
                            }
                        },
                        onRemove = {
                            scope.launch { CartManager.removeItem(context, item.id) }
                        }
                    )
                    Spacer(Modifier.height(HoloSpacing.xs))
                }

                item { Spacer(Modifier.height(HoloSpacing.xs)) }

                // ══════════════════════════════════════════════════════════════
                // RESUMEN DEL PEDIDO — subtotal, envío, total, botón checkout
                // ══════════════════════════════════════════════════════════════
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HoloSpacing.md),
                        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
                        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(HoloSpacing.lg)) {
                            // Título del resumen
                            Text(
                                text = "RESUMEN DEL PEDIDO",
                                style = HoloType.LabelMedium,
                                color = HoloColors.TextTertiary,
                                modifier = Modifier.padding(bottom = HoloSpacing.md)
                            )

                            // Subtotal
                            SummaryRow(
                                label = "Subtotal",
                                value = "${"%.2f".format(summary.subtotal)}€"
                            )

                            // Envío (verde si gratis, negro si no)
                            SummaryRow(
                                label = "Envío",
                                value = if (summary.shipping == 0.0) "GRATIS"
                                else "${"%.2f".format(summary.shipping)}€",
                                valueColor = if (summary.shipping == 0.0)
                                    HoloColors.Success
                                else
                                    HoloColors.TextPrimary
                            )

                            // Mensaje de envío gratis si falta poco
                            if (summary.subtotal <= 150) {
                                Text(
                                    text = "Añade ${"%.2f".format(150 - summary.subtotal)}€ más para envío gratis",
                                    style = HoloType.BodySmall,
                                    color = HoloColors.TextTertiary,
                                    modifier = Modifier.padding(vertical = HoloSpacing.xxs)
                                )
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = HoloSpacing.md),
                                color = HoloColors.Neutral100
                            )

                            // Total grande
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TOTAL",
                                    style = HoloType.LabelLarge,
                                    color = HoloColors.TextPrimary
                                )
                                Text(
                                    text = "${"%.2f".format(summary.total)}€",
                                    style = HoloType.MonoLarge,
                                    color = HoloColors.TextPrimary
                                )
                            }

                            Spacer(Modifier.height(HoloSpacing.lg))

                            // Botón FINALIZAR COMPRA
                            Button(
                                onClick = {
                                    navController.navigate("checkout") { launchSingleTop = true }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(HoloSpacing.ButtonHeight),
                                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HoloColors.Ink
                                )
                            ) {
                                Text(
                                    text = "FINALIZAR COMPRA",
                                    style = HoloType.LabelLarge,
                                    color = HoloColors.Paper
                                )
                            }
                        }
                    }
                }

                // Espacio extra para que no quede pegado al borde inferior
                item { Spacer(Modifier.height(HoloSpacing.BottomNavHeight)) }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES DEL CARRITO
// ══════════════════════════════════════════════════════════════════════════════

/**
 * CartItemCard — Card individual de un producto en el carrito.
 *
 * Muestra imagen placeholder, nombre, talla/color, precio y controles +/-.
 * El botón X (Close) elimina el item directamente.
 *
 * @param item Datos del item del carrito (viene de la API).
 * @param onIncrease Callback al pulsar +.
 * @param onDecrease Callback al pulsar - (si qty=1, elimina).
 * @param onRemove Callback al pulsar X.
 */
@Composable
fun CartItemCard(
    item: CartItemDto,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HoloSpacing.md),
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HoloSpacing.sm)
        ) {
            // ── Imagen placeholder (hasta migrar a Supabase Storage) ──────────
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                    .background(HoloColors.Fog),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = null,
                    tint = HoloColors.Neutral300,
                    modifier = Modifier.size(32.dp)
                )
            }

            // ── Info del producto ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = HoloSpacing.sm)
            ) {
                // Fila superior: nombre + botón eliminar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Nombre del producto
                        Text(
                            text = item.name,
                            style = HoloType.TitleMedium,
                            color = HoloColors.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Detalles: talla · color
                        val details = buildString {
                            item.size_name?.let { append("Talla $it") }
                            if (!item.size_name.isNullOrEmpty() && !item.color_name.isNullOrEmpty()) append(" · ")
                            item.color_name?.let { append(it) }
                        }
                        if (details.isNotEmpty()) {
                            Text(
                                text = details,
                                style = HoloType.BodySmall,
                                color = HoloColors.TextTertiary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Botón X para eliminar
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Eliminar",
                            tint = HoloColors.TextTertiary,
                            modifier = Modifier.size(HoloSpacing.IconSizeSmall)
                        )
                    }
                }

                Spacer(Modifier.height(HoloSpacing.sm))

                // Fila inferior: precio + controles de cantidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Precio monospace para alineación limpia
                    Text(
                        text = "${"%.2f".format(item.price_at_addition)}€",
                        style = HoloType.MonoMedium,
                        color = HoloColors.TextPrimary
                    )

                    // Controles +/- con fondo gris
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                            .background(HoloColors.Fog)
                    ) {
                        // Botón menos
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { onDecrease() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Menos",
                                tint = HoloColors.Ink,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Cantidad
                        Text(
                            text = "${item.quantity}",
                            style = HoloType.TitleMedium,
                            color = HoloColors.TextPrimary,
                            modifier = Modifier.padding(horizontal = HoloSpacing.sm)
                        )

                        // Botón más
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { onIncrease() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Más",
                                tint = HoloColors.Ink,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * SummaryRow — Fila de resumen (label a la izquierda, valor a la derecha).
 *
 * Se usa para mostrar subtotal, envío y cualquier otra línea del resumen.
 *
 * @param label Texto descriptivo ("Subtotal", "Envío").
 * @param value Valor formateado ("129.99€", "GRATIS").
 * @param valueColor Color del valor (verde para GRATIS, negro por defecto).
 */
@Composable
fun SummaryRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = HoloColors.TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HoloSpacing.xxs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = HoloType.BodyMedium,
            color = HoloColors.TextSecondary
        )
        Text(
            text = value,
            style = HoloType.TitleMedium,
            color = valueColor
        )
    }
}