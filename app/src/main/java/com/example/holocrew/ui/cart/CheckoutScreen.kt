package com.example.holocrew.ui.cart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.*
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

/**
 * CheckoutScreen — Pantalla de finalización de compra.
 *
 * Flujo completo: selección de dirección, método de envío, método de pago,
 * resumen del pedido y confirmación. Al confirmar llama a POST /api/orders.
 *
 * Diseño SNKRS: header negro con gradiente, cards seleccionables con borde 2dp,
 * resumen con precio total monospace, botón negro pill.
 *
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cartItems by CartManager.items.collectAsState()
    val summary by CartManager.summary.collectAsState()

    // ── Estado de datos cargados desde la API ─────────────────────────────────
    var addresses by remember { mutableStateOf<List<AddressDto>>(emptyList()) }
    var paymentMethods by remember { mutableStateOf<List<PaymentMethodDto>>(emptyList()) }
    var userEmail by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var orderSuccess by remember { mutableStateOf(false) }
    var orderNumber by remember { mutableStateOf("") }

    // ── Selecciones del usuario ────────────────────────────────────────────────
    var selectedAddressId by remember { mutableStateOf<String?>(null) }
    var selectedPaymentId by remember { mutableStateOf<String?>(null) }
    var selectedShipping by remember { mutableStateOf("standard") }

    // Cargar direcciones, métodos de pago y email al entrar
    LaunchedEffect(Unit) {
        val token = TokenManager.getTokenOnce(context) ?: return@LaunchedEffect
        try {
            val addrResp = RetrofitClient.api.getAddresses("Bearer $token")
            if (addrResp.isSuccessful && addrResp.body()?.success == true) {
                addresses = addrResp.body()!!.data ?: emptyList()
                selectedAddressId = addresses.firstOrNull { it.is_default }?.id ?: addresses.firstOrNull()?.id
            }
            val payResp = RetrofitClient.api.getPaymentMethods("Bearer $token")
            if (payResp.isSuccessful && payResp.body()?.success == true) {
                paymentMethods = payResp.body()!!.data ?: emptyList()
                selectedPaymentId = paymentMethods.firstOrNull { it.is_default }?.id ?: paymentMethods.firstOrNull()?.id
            }
            val meResp = RetrofitClient.api.getMe("Bearer $token")
            if (meResp.isSuccessful && meResp.body()?.success == true) {
                userEmail = meResp.body()!!.data?.email ?: ""
            }
        } catch (e: Exception) { e.printStackTrace() }
        isLoading = false
    }

    // ── Pantalla de éxito post-compra ──────────────────────────────────────────
    if (orderSuccess) {
        OrderSuccessScreen(
            orderNumber = orderNumber,
            onGoHome = { navController.navigate("home") { popUpTo(0) { inclusive = true } } },
            onGoOrders = { navController.navigate("my_orders") { popUpTo("home") } }
        )
        return
    }

    val selectedAddress = addresses.firstOrNull { it.id == selectedAddressId }
    val selectedPayment = paymentMethods.firstOrNull { it.id == selectedPaymentId }
    val shippingCost = if (selectedShipping == "express") 14.99 else summary.shipping
    val total = summary.subtotal + shippingCost

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
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Cargando...", style = HoloType.TitleLarge, color = HoloColors.TextTertiary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.xl)
        ) {
            // ── Header negro SNKRS ────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800)))
                        .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)
                ) {
                    Column {
                        Text("CHECKOUT", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("Finalizar compra", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text(
                            "${cartItems.size} producto${if (cartItems.size != 1) "s" else ""} · ${"%.2f".format(total)}€",
                            style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.md)) }

            // ── Email de confirmación ─────────────────────────────────────────
            item {
                CheckoutSection(icon = Icons.Outlined.Email, title = "Confirmación", subtitle = userEmail.ifEmpty { "Sin email" })
            }

            item { Spacer(Modifier.height(HoloSpacing.sm)) }

            // ── Dirección de envío ────────────────────────────────────────────
            item { SectionTitle2("DIRECCIÓN DE ENVÍO") }

            if (addresses.isEmpty()) {
                item {
                    EmptyStateCard(icon = Icons.Outlined.LocationOn, message = "No tienes direcciones guardadas", actionText = "Añadir dirección",
                        onClick = { navController.navigate("addresses") { launchSingleTop = true } })
                }
            } else {
                addresses.forEach { address ->
                    item {
                        SelectableAddressCard(address = address, isSelected = selectedAddressId == address.id, onClick = { selectedAddressId = address.id })
                        Spacer(Modifier.height(HoloSpacing.xs))
                    }
                }
                item {
                    TextButton(onClick = { navController.navigate("addresses") { launchSingleTop = true } }, modifier = Modifier.padding(horizontal = HoloSpacing.md)) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(HoloSpacing.IconSizeSmall))
                        Spacer(Modifier.width(HoloSpacing.xxs))
                        Text("Añadir dirección", style = HoloType.TitleSmall, color = HoloColors.TextPrimary)
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.sm)) }

            // ── Método de envío ───────────────────────────────────────────────
            item { SectionTitle2("MÉTODO DE ENVÍO") }
            item {
                ShippingOption("Estándar", "3-5 días laborables", if (summary.subtotal > 150) "GRATIS" else "9.99€", selectedShipping == "standard") { selectedShipping = "standard" }
                Spacer(Modifier.height(HoloSpacing.xs))
                ShippingOption("Express", "1-2 días laborables", "14.99€", selectedShipping == "express") { selectedShipping = "express" }
            }

            item { Spacer(Modifier.height(HoloSpacing.sm)) }

            // ── Método de pago ────────────────────────────────────────────────
            item { SectionTitle2("MÉTODO DE PAGO") }
            if (paymentMethods.isEmpty()) {
                item {
                    EmptyStateCard(icon = Icons.Outlined.CreditCard, message = "No tienes métodos de pago", actionText = "Añadir tarjeta",
                        onClick = { navController.navigate("payment_methods") { launchSingleTop = true } })
                }
            } else {
                paymentMethods.forEach { method ->
                    item {
                        SelectablePaymentCard(method = method, isSelected = selectedPaymentId == method.id, onClick = { selectedPaymentId = method.id })
                        Spacer(Modifier.height(HoloSpacing.xs))
                    }
                }
                item {
                    TextButton(onClick = { navController.navigate("payment_methods") { launchSingleTop = true } }, modifier = Modifier.padding(horizontal = HoloSpacing.md)) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(HoloSpacing.IconSizeSmall))
                        Spacer(Modifier.width(HoloSpacing.xxs))
                        Text("Añadir tarjeta", style = HoloType.TitleSmall, color = HoloColors.TextPrimary)
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.sm)) }

            // ── Resumen del pedido ────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
                    shape = RoundedCornerShape(HoloSpacing.RadiusLg),
                    colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(HoloSpacing.lg)) {
                        Text("RESUMEN", style = HoloType.LabelMedium, color = HoloColors.TextTertiary)
                        Spacer(Modifier.height(HoloSpacing.md))

                        // Lista de items
                        cartItems.forEach { item ->
                            Row(Modifier.fillMaxWidth().padding(vertical = HoloSpacing.xxs), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.name} ×${item.quantity}", style = HoloType.BodyMedium, color = HoloColors.Neutral600, modifier = Modifier.weight(1f))
                                Text("${"%.2f".format(item.price_at_addition * item.quantity)}€", style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                            }
                        }

                        Spacer(Modifier.height(HoloSpacing.sm))
                        Divider(color = HoloColors.Neutral100)
                        Spacer(Modifier.height(HoloSpacing.sm))

                        SummaryLine("Subtotal", "${"%.2f".format(summary.subtotal)}€")
                        SummaryLine(
                            "Envío ${if (selectedShipping == "express") "Express" else "Estándar"}",
                            if (shippingCost == 0.0) "GRATIS" else "${"%.2f".format(shippingCost)}€",
                            valueColor = if (shippingCost == 0.0) HoloColors.Success else HoloColors.TextPrimary
                        )

                        Spacer(Modifier.height(HoloSpacing.sm))
                        Divider(color = HoloColors.Neutral100)
                        Spacer(Modifier.height(HoloSpacing.sm))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("TOTAL", style = HoloType.LabelLarge, color = HoloColors.TextPrimary)
                            Text("${"%.2f".format(total)}€", style = HoloType.MonoLarge, color = HoloColors.TextPrimary)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.lg)) }

            // ── Botón confirmar pedido ─────────────────────────────────────────
            item {
                val canCheckout = selectedAddressId != null && (paymentMethods.isEmpty() || selectedPaymentId != null)
                Column(modifier = Modifier.padding(horizontal = HoloSpacing.md)) {
                    Button(
                        onClick = {
                            val addr = selectedAddress ?: return@Button
                            scope.launch {
                                isPlacingOrder = true
                                val token = TokenManager.getTokenOnce(context) ?: return@launch
                                try {
                                    val response = RetrofitClient.api.createOrder("Bearer $token",
                                        CreateOrderRequest(
                                            shipping_address = ShippingAddressDto(addr.recipient_name, addr.street_address, addr.city, addr.postal_code, addr.country_code),
                                            shipping_method = selectedShipping,
                                            payment_method = selectedPayment?.payment_type ?: "credit_card"
                                        )
                                    )
                                    if (response.isSuccessful && response.body()?.success == true) {
                                        orderNumber = response.body()!!.data?.order_number ?: ""
                                        CartManager.loadCart(context)
                                        orderSuccess = true
                                    } else {
                                        Toast.makeText(context, "Error al procesar el pedido", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                                }
                                isPlacingOrder = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                        shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canCheckout) HoloColors.Ink else HoloColors.Neutral200,
                            disabledContainerColor = HoloColors.Neutral200
                        ),
                        enabled = canCheckout && !isPlacingOrder
                    ) {
                        Text(
                            if (isPlacingOrder) "PROCESANDO..." else "CONFIRMAR PEDIDO · ${"%.2f".format(total)}€",
                            style = HoloType.LabelLarge, color = HoloColors.Paper
                        )
                    }

                    if (!canCheckout && selectedAddressId == null) {
                        Spacer(Modifier.height(HoloSpacing.xs))
                        Text("⚠ Selecciona una dirección de envío", style = HoloType.BodySmall, color = HoloColors.Pulse, modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(Modifier.height(HoloSpacing.sm))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, null, tint = HoloColors.TextTertiary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(HoloSpacing.xxs))
                        Text("Pago 100% seguro y encriptado", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.xxl)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA DE ÉXITO
// ══════════════════════════════════════════════════════════════════════════════

/**
 * OrderSuccessScreen — Se muestra tras confirmar el pedido con éxito.
 * Fondo negro, icono check verde, número de pedido en rojo Pulse, dos CTAs.
 */
@Composable
fun OrderSuccessScreen(orderNumber: String, onGoHome: () -> Unit, onGoOrders: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(HoloColors.Ink), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(HoloSpacing.xxl)) {
            // Icono check verde
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(HoloColors.Success.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Check, null, tint = HoloColors.Success, modifier = Modifier.size(HoloSpacing.IconSizeHero))
            }

            Spacer(Modifier.height(HoloSpacing.xxl))
            Text("¡PEDIDO CONFIRMADO!", style = HoloType.LabelSmall, color = HoloColors.Pulse)
            Spacer(Modifier.height(HoloSpacing.xs))
            Text("Gracias por tu compra", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
            Spacer(Modifier.height(HoloSpacing.xs))

            if (orderNumber.isNotEmpty()) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                        .background(HoloColors.Neutral800).padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xs)
                ) {
                    Text("Pedido $orderNumber", style = HoloType.MonoMedium, color = HoloColors.Pulse)
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))
            Text("Recibirás un email con los detalles\ny el seguimiento de tu pedido.", style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted)

            Spacer(Modifier.height(HoloSpacing.xxxl))

            Button(onClick = onGoOrders, modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight), shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Paper)) {
                Text("VER MIS PEDIDOS", style = HoloType.LabelLarge, color = HoloColors.Ink)
            }

            Spacer(Modifier.height(HoloSpacing.sm))

            OutlinedButton(onClick = onGoHome, modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight), shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(HoloColors.Neutral600, HoloColors.Neutral600)))) {
                Text("SEGUIR COMPRANDO", style = HoloType.LabelLarge, color = HoloColors.TextOnDark)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES AUXILIARES DEL CHECKOUT
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun SectionTitle2(title: String) {
    Text(title, style = HoloType.LabelSmall, color = HoloColors.TextTertiary, modifier = Modifier.padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.xxs))
    Spacer(Modifier.height(HoloSpacing.xxs))
}

@Composable
fun CheckoutSection(icon: ImageVector, title: String, subtitle: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(modifier = Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(HoloColors.Fog), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
            }
            Spacer(Modifier.width(HoloSpacing.sm))
            Column {
                Text(title, style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
                Text(subtitle, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
            }
        }
    }
}

@Composable
fun SelectableAddressCard(address: AddressDto, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md)
            .border(if (isSelected) HoloSpacing.BorderDefault else 0.dp, if (isSelected) HoloColors.Ink else HoloColors.Paper, RoundedCornerShape(HoloSpacing.RadiusMd))
            .clickable { onClick() },
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) HoloColors.Neutral50 else HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(if (isSelected) HoloColors.Ink else HoloColors.Fog), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.LocationOn, null, tint = if (isSelected) HoloColors.Paper else HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
            }
            Spacer(Modifier.width(HoloSpacing.sm))
            Column(Modifier.weight(1f)) {
                Text(address.recipient_name, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                Text("${address.street_address}, ${address.city}", style = HoloType.BodySmall, color = HoloColors.TextSecondary, modifier = Modifier.padding(top = 2.dp))
                Text("${address.postal_code} · ${address.country_code}", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
            }
            if (isSelected) { Icon(Icons.Filled.CheckCircle, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }
        }
    }
}

@Composable
fun SelectablePaymentCard(method: PaymentMethodDto, isSelected: Boolean, onClick: () -> Unit) {
    val brandIcon = when (method.card_brand?.lowercase()) { "visa" -> "VISA"; "mastercard" -> "MC"; "amex" -> "AMEX"; else -> "CARD" }
    val typeLabel = when (method.payment_type) { "credit_card" -> "Crédito"; "debit_card" -> "Débito"; "paypal" -> "PayPal"; else -> method.payment_type }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md)
            .border(if (isSelected) HoloSpacing.BorderDefault else 0.dp, if (isSelected) HoloColors.Ink else HoloColors.Paper, RoundedCornerShape(HoloSpacing.RadiusMd))
            .clickable { onClick() },
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) HoloColors.Neutral50 else HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(48.dp).height(32.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(HoloColors.Neutral800), contentAlignment = Alignment.Center) {
                Text(brandIcon, style = HoloType.LabelSmall, color = HoloColors.Pulse)
            }
            Spacer(Modifier.width(HoloSpacing.sm))
            Column(Modifier.weight(1f)) {
                Text(typeLabel, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                if (method.card_last_four != null) { Text("•••• ${method.card_last_four}", style = HoloType.MonoSmall, color = HoloColors.TextSecondary) }
            }
            if (isSelected) { Icon(Icons.Filled.CheckCircle, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }
        }
    }
}

@Composable
fun ShippingOption(title: String, subtitle: String, price: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md)
            .border(if (isSelected) HoloSpacing.BorderDefault else 0.dp, if (isSelected) HoloColors.Ink else HoloColors.Paper, RoundedCornerShape(HoloSpacing.RadiusMd))
            .clickable { onClick() },
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) HoloColors.Neutral50 else HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(if (isSelected) HoloColors.Ink else HoloColors.Fog), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.LocalShipping, null, tint = if (isSelected) HoloColors.Paper else HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
            }
            Spacer(Modifier.width(HoloSpacing.sm))
            Column(Modifier.weight(1f)) {
                Text(title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                Text(subtitle, style = HoloType.BodySmall, color = HoloColors.TextTertiary)
            }
            Text(price, style = HoloType.TitleMedium, color = if (price == "GRATIS") HoloColors.Success else HoloColors.TextPrimary)
            if (isSelected) {
                Spacer(Modifier.width(HoloSpacing.xs))
                Icon(Icons.Filled.CheckCircle, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
            }
        }
    }
}

@Composable
fun EmptyStateCard(icon: ImageVector, message: String, actionText: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(modifier = Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.IconSizeLarge))
            Spacer(Modifier.width(HoloSpacing.sm))
            Column(Modifier.weight(1f)) { Text(message, style = HoloType.BodyMedium, color = HoloColors.TextSecondary) }
            TextButton(onClick = onClick) { Text(actionText, style = HoloType.TitleSmall, color = HoloColors.TextPrimary) }
        }
    }
}

@Composable
fun SummaryLine(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = HoloColors.TextPrimary) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = HoloType.BodyMedium, color = HoloColors.TextSecondary)
        Text(value, style = HoloType.TitleMedium, color = valueColor)
    }
}