package com.example.holocrew.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.OrderDto
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType

/**
 * MyOrdersScreen — Historial de pedidos del usuario.
 *
 * Header negro SNKRS + lista de OrderCards con badge de estado colorizado.
 * Los pedidos se cargan desde GET /api/orders al entrar.
 *
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController) {
    val context = LocalContext.current
    var orders by remember { mutableStateOf<List<OrderDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar pedidos al entrar
    LaunchedEffect(Unit) {
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink)
            )
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.xl)
        ) {
            // ── Header negro con gradiente ────────────────────────────────────
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800)))
                        .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)
                ) {
                    Column {
                        Text("MIS PEDIDOS", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("Historial de compras", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text(
                            if (orders.isEmpty() && !isLoading) "No tienes pedidos todavía"
                            else "${orders.size} pedido${if (orders.size != 1) "s" else ""}",
                            style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Cargando...", style = HoloType.TitleLarge, color = HoloColors.TextTertiary)
                    }
                }
            } else if (orders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(HoloSpacing.xxxl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.Inventory2, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.huge))
                        Spacer(Modifier.height(HoloSpacing.lg))
                        Text("Aún no has hecho ningún pedido", style = HoloType.TitleLarge, color = HoloColors.TextSecondary)
                        Spacer(Modifier.height(HoloSpacing.xl))
                        Button(
                            onClick = { navController.navigate("available") { launchSingleTop = true } },
                            colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                            shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                            modifier = Modifier.height(HoloSpacing.ButtonHeightSmall)
                        ) {
                            Text("EXPLORAR PRODUCTOS", style = HoloType.LabelLarge, color = HoloColors.Paper)
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(HoloSpacing.md)) }
                items(orders) { order ->
                    OrderCard(order = order)
                    Spacer(Modifier.height(HoloSpacing.sm))
                }
            }
        }
    }
}

/**
 * OrderCard — Card individual de un pedido.
 * Muestra order_number, fecha, badge de estado, nº productos y total.
 */
@Composable
fun OrderCard(order: OrderDto) {
    // Color y label según el estado del pedido
    val statusConfig = when (order.status) {
        "delivered" -> HoloColors.Success to "Entregado"
        "shipped" -> HoloColors.Info to "En camino"
        "processing" -> HoloColors.Warning to "Procesando"
        "paid" -> HoloColors.Info to "Pagado"
        "pending" -> HoloColors.Neutral400 to "Pendiente"
        "cancelled" -> HoloColors.Pulse to "Cancelado"
        "refunded" -> HoloColors.Pulse to "Reembolsado"
        else -> HoloColors.Neutral400 to order.status
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(HoloSpacing.lg)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    // Número de pedido en monospace
                    Text(order.order_number, style = HoloType.MonoMedium, color = HoloColors.TextPrimary)
                    Text(order.created_at.take(10), style = HoloType.BodySmall, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = 2.dp))
                }
                // Badge de estado con color semitransparente
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusPill))
                        .background(statusConfig.first.copy(alpha = 0.1f))
                        .padding(horizontal = HoloSpacing.sm, vertical = HoloSpacing.xxs)
                ) {
                    Text(statusConfig.second, style = HoloType.LabelSmall, color = statusConfig.first)
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))
            Divider(color = HoloColors.Neutral100)
            Spacer(Modifier.height(HoloSpacing.md))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${order.item_count} producto${if (order.item_count != 1) "s" else ""}", style = HoloType.BodyMedium, color = HoloColors.TextSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${order.total_eur}€", style = HoloType.MonoLarge, color = HoloColors.TextPrimary)
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Icon(Icons.Filled.ChevronRight, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
                }
            }
        }
    }
}