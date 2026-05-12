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
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.AddressRepository
import com.example.holocrew.data.network.CartRepository
import com.example.holocrew.data.network.PaymentRepository
import com.example.holocrew.data.network.SbAddressDto
import com.example.holocrew.data.network.SbPaymentMethodDto
import com.example.holocrew.data.network.SupabaseClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cartItems by CartRepository.cartItems.collectAsState()
    val subtotal = CartRepository.getSubtotal()
    val userEmail by TokenManager.getUserEmail(context).collectAsState()

    var addresses by remember { mutableStateOf<List<SbAddressDto>>(emptyList()) }
    var paymentMethods by remember { mutableStateOf<List<SbPaymentMethodDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var orderSuccess by remember { mutableStateOf(false) }
    var orderNumber by remember { mutableStateOf("") }

    var selectedAddressId by remember { mutableStateOf<Int?>(null) }
    var selectedPaymentId by remember { mutableStateOf<String?>(null) }
    var selectedShipping by remember { mutableStateOf("standard") }

    LaunchedEffect(Unit) {
        addresses = AddressRepository.getAddresses()
        paymentMethods = PaymentRepository.getPaymentMethods()
        selectedAddressId = addresses.firstOrNull { it.isDefault }?.id ?: addresses.firstOrNull()?.id
        selectedPaymentId = paymentMethods.firstOrNull { it.isDefault }?.id ?: paymentMethods.firstOrNull()?.id
        isLoading = false
    }

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
    val baseShipping = if (subtotal >= 150.0) 0.0 else 9.99
    val shippingCost = if (selectedShipping == "express") 14.99 else baseShipping
    val total = subtotal + shippingCost

    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper) }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink))
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = HoloColors.Ink) }
            return@Scaffold
        }

        LazyColumn(Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(bottom = HoloSpacing.xl)) {

            // Header
            item {
                Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800))).padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)) {
                    Column {
                        Text("CHECKOUT", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("Finalizar compra", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("${cartItems.size} producto${if (cartItems.size != 1) "s" else ""} \u00B7 ${"%.2f".format(total)}\u20AC", style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted)
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.md)) }
            item { CheckoutSection(icon = Icons.Outlined.Email, title = "Confirmacion", subtitle = userEmail ?: "Sin email") }
            item { Spacer(Modifier.height(HoloSpacing.sm)) }

            // Direccion
            item { SectionTitle2("DIRECCION DE ENVIO") }
            if (addresses.isEmpty()) {
                item { EmptyStateCard(Icons.Outlined.LocationOn, "No tienes direcciones guardadas", "Agregar") { navController.navigate("addresses") { launchSingleTop = true } } }
            } else {
                addresses.forEach { address ->
                    item { SelectableAddressCard(address, selectedAddressId == address.id) { selectedAddressId = address.id }; Spacer(Modifier.height(HoloSpacing.xs)) }
                }
                item { TextButton(onClick = { navController.navigate("addresses") { launchSingleTop = true } }, modifier = Modifier.padding(horizontal = HoloSpacing.md)) { Icon(Icons.Filled.Add, null, modifier = Modifier.size(HoloSpacing.IconSizeSmall)); Spacer(Modifier.width(HoloSpacing.xxs)); Text("Agregar direccion", style = HoloType.TitleSmall, color = HoloColors.TextPrimary) } }
            }

            item { Spacer(Modifier.height(HoloSpacing.sm)) }

            // Envio
            item { SectionTitle2("METODO DE ENVIO") }
            item {
                ShippingOption("Estandar", "3-5 dias laborables", if (subtotal > 150) "GRATIS" else "9.99\u20AC", selectedShipping == "standard") { selectedShipping = "standard" }
                Spacer(Modifier.height(HoloSpacing.xs))
                ShippingOption("Express", "1-2 dias laborables", "14.99\u20AC", selectedShipping == "express") { selectedShipping = "express" }
            }

            item { Spacer(Modifier.height(HoloSpacing.sm)) }

            // Pago
            item { SectionTitle2("METODO DE PAGO") }
            if (paymentMethods.isEmpty()) {
                item { EmptyStateCard(Icons.Outlined.CreditCard, "No tienes metodos de pago", "Agregar") { navController.navigate("payment_methods") { launchSingleTop = true } } }
            } else {
                paymentMethods.forEach { method ->
                    item { SelectablePaymentCard(method, selectedPaymentId == method.id) { selectedPaymentId = method.id }; Spacer(Modifier.height(HoloSpacing.xs)) }
                }
                item { TextButton(onClick = { navController.navigate("payment_methods") { launchSingleTop = true } }, modifier = Modifier.padding(horizontal = HoloSpacing.md)) { Icon(Icons.Filled.Add, null, modifier = Modifier.size(HoloSpacing.IconSizeSmall)); Spacer(Modifier.width(HoloSpacing.xxs)); Text("Agregar tarjeta", style = HoloType.TitleSmall, color = HoloColors.TextPrimary) } }
            }

            item { Spacer(Modifier.height(HoloSpacing.sm)) }

            // Resumen
            item {
                Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusLg), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(Modifier.padding(HoloSpacing.lg)) {
                        Text("RESUMEN", style = HoloType.LabelMedium, color = HoloColors.TextTertiary)
                        Spacer(Modifier.height(HoloSpacing.md))
                        cartItems.forEach { item ->
                            Row(Modifier.fillMaxWidth().padding(vertical = HoloSpacing.xxs), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.product.title} \u00D7${item.quantity}", style = HoloType.BodyMedium, color = HoloColors.Neutral600, modifier = Modifier.weight(1f))
                                Text("${"%.2f".format(item.priceAtAdd * item.quantity)}\u20AC", style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                            }
                        }
                        Spacer(Modifier.height(HoloSpacing.sm)); Divider(color = HoloColors.Neutral100); Spacer(Modifier.height(HoloSpacing.sm))
                        SummaryLine("Subtotal", "${"%.2f".format(subtotal)}\u20AC")
                        SummaryLine("Envio ${if (selectedShipping == "express") "Express" else "Estandar"}", if (shippingCost == 0.0) "GRATIS" else "${"%.2f".format(shippingCost)}\u20AC", valueColor = if (shippingCost == 0.0) HoloColors.Success else HoloColors.TextPrimary)
                        Spacer(Modifier.height(HoloSpacing.sm)); Divider(color = HoloColors.Neutral100); Spacer(Modifier.height(HoloSpacing.sm))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("TOTAL", style = HoloType.LabelLarge, color = HoloColors.TextPrimary)
                            Text("${"%.2f".format(total)}\u20AC", style = HoloType.MonoLarge, color = HoloColors.TextPrimary)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.lg)) }

            // Boton confirmar
            item {
                val canCheckout = selectedAddressId != null
                Column(Modifier.padding(horizontal = HoloSpacing.md)) {
                    Button(
                        onClick = {
                            val addr = selectedAddress ?: return@Button
                            scope.launch {
                                try {
                                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return@launch
                                    val orderNum = "HC-${System.currentTimeMillis().toString().takeLast(8)}"

                                    // 1. Crear orden y obtener el ID de vuelta
                                    val orderResult = SupabaseClient.client.from("orders")
                                        .insert(buildJsonObject {
                                            put("user_id", userId)
                                            put("order_number", orderNum)
                                            put("status", "pending")
                                            put("payment_status", "pending")
                                            put("subtotal", subtotal)
                                            put("shipping_cost", shippingCost)
                                            put("total", total)
                                            put("shipping_method", selectedShipping)
                                            put("payment_method", selectedPayment?.paymentType ?: "credit_card")
                                            put("shipping_address", buildJsonObject {
                                                put("full_name", addr.fullName)
                                                put("street", addr.street)
                                                put("city", addr.city)
                                                put("postal_code", addr.postalCode)
                                                put("country_code", addr.countryCode)
                                            })
                                        }) {
                                            select()
                                        }.decodeSingle<com.example.holocrew.data.network.SbOrderDto>()

                                    // 2. Insertar cada item del carrito en order_items
                                    cartItems.forEach { item ->
                                        SupabaseClient.client.from("order_items")
                                            .insert(buildJsonObject {
                                                put("order_id", orderResult.id)
                                                put("product_id", item.product.id)
                                                put("product_name", item.product.title)
                                                put("product_image_url", item.product.imageUrl)
                                                put("size", item.size ?: "")
                                                put("color", item.color ?: "default")
                                                put("quantity", item.quantity)
                                                put("unit_price", item.priceAtAdd)
                                                put("total", item.priceAtAdd * item.quantity)
                                            })
                                    }

                                    // 3. Limpiar carrito y mostrar exito
                                    CartRepository.clearCart()
                                    orderNumber = orderNum
                                    orderSuccess = true
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                        shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                        colors = ButtonDefaults.buttonColors(containerColor = if (canCheckout) HoloColors.Ink else HoloColors.Neutral200, disabledContainerColor = HoloColors.Neutral200),
                        enabled = canCheckout && !isPlacingOrder
                    ) { Text(if (isPlacingOrder) "PROCESANDO..." else "CONFIRMAR PEDIDO \u00B7 ${"%.2f".format(total)}\u20AC", style = HoloType.LabelLarge, color = HoloColors.Paper) }
                    if (!canCheckout) { Spacer(Modifier.height(HoloSpacing.xs)); Text("Selecciona una direccion de envio", style = HoloType.BodySmall, color = HoloColors.Pulse) }
                    Spacer(Modifier.height(HoloSpacing.sm))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, null, tint = HoloColors.TextTertiary, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(HoloSpacing.xxs))
                        Text("Pago 100% seguro y encriptado", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
                    }
                }
            }
            item { Spacer(Modifier.height(HoloSpacing.xxl)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
@Composable fun OrderSuccessScreen(orderNumber: String, onGoHome: () -> Unit, onGoOrders: () -> Unit) {
    Box(Modifier.fillMaxSize().background(HoloColors.Ink), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(HoloSpacing.xxl)) {
            Box(Modifier.size(100.dp).clip(CircleShape).background(HoloColors.Success.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Check, null, tint = HoloColors.Success, modifier = Modifier.size(HoloSpacing.IconSizeHero)) }
            Spacer(Modifier.height(HoloSpacing.xxl)); Text("PEDIDO CONFIRMADO!", style = HoloType.LabelSmall, color = HoloColors.Pulse)
            Spacer(Modifier.height(HoloSpacing.xs)); Text("Gracias por tu compra", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
            Spacer(Modifier.height(HoloSpacing.xs))
            if (orderNumber.isNotEmpty()) { Box(Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusMd)).background(HoloColors.Neutral800).padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xs)) { Text("Pedido $orderNumber", style = HoloType.MonoMedium, color = HoloColors.Pulse) } }
            Spacer(Modifier.height(HoloSpacing.md)); Text("Recibiras un email con los detalles\ny el seguimiento de tu pedido.", style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted)
            Spacer(Modifier.height(HoloSpacing.xxxl))
            Button(onClick = onGoOrders, modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight), shape = RoundedCornerShape(HoloSpacing.RadiusPill), colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Paper)) { Text("VER MIS PEDIDOS", style = HoloType.LabelLarge, color = HoloColors.Ink) }
            Spacer(Modifier.height(HoloSpacing.sm))
            OutlinedButton(onClick = onGoHome, modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight), shape = RoundedCornerShape(HoloSpacing.RadiusPill), border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(HoloColors.Neutral600, HoloColors.Neutral600)))) { Text("SEGUIR COMPRANDO", style = HoloType.LabelLarge, color = HoloColors.TextOnDark) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
@Composable fun SectionTitle2(title: String) { Text(title, style = HoloType.LabelSmall, color = HoloColors.TextTertiary, modifier = Modifier.padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.xxs)); Spacer(Modifier.height(HoloSpacing.xxs)) }
@Composable fun CheckoutSection(icon: ImageVector, title: String, subtitle: String) { Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) { Row(Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(HoloColors.Fog), contentAlignment = Alignment.Center) { Icon(icon, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }; Spacer(Modifier.width(HoloSpacing.sm)); Column { Text(title, style = HoloType.LabelSmall, color = HoloColors.TextTertiary); Text(subtitle, style = HoloType.TitleMedium, color = HoloColors.TextPrimary) } } } }

@Composable fun SelectableAddressCard(address: SbAddressDto, isSelected: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md).border(if (isSelected) HoloSpacing.BorderDefault else 0.dp, if (isSelected) HoloColors.Ink else HoloColors.Paper, RoundedCornerShape(HoloSpacing.RadiusMd)).clickable { onClick() }, shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = CardDefaults.cardColors(containerColor = if (isSelected) HoloColors.Neutral50 else HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(if (isSelected) HoloColors.Ink else HoloColors.Fog), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.LocationOn, null, tint = if (isSelected) HoloColors.Paper else HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }
            Spacer(Modifier.width(HoloSpacing.sm)); Column(Modifier.weight(1f)) { Text(address.fullName, style = HoloType.TitleMedium, color = HoloColors.TextPrimary); Text("${address.street}, ${address.city}", style = HoloType.BodySmall, color = HoloColors.TextSecondary, modifier = Modifier.padding(top = 2.dp)); Text("${address.postalCode} \u00B7 ${address.countryCode}", style = HoloType.BodySmall, color = HoloColors.TextTertiary) }
            if (isSelected) Icon(Icons.Filled.CheckCircle, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
        }
    }
}

@Composable fun SelectablePaymentCard(method: SbPaymentMethodDto, isSelected: Boolean, onClick: () -> Unit) {
    val brandIcon = when (method.cardBrand?.lowercase()) { "visa" -> "VISA"; "mastercard" -> "MC"; "amex" -> "AMEX"; else -> "CARD" }
    val typeLabel = when (method.paymentType) { "credit_card" -> "Credito"; "debit_card" -> "Debito"; "paypal" -> "PayPal"; else -> method.paymentType }
    Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md).border(if (isSelected) HoloSpacing.BorderDefault else 0.dp, if (isSelected) HoloColors.Ink else HoloColors.Paper, RoundedCornerShape(HoloSpacing.RadiusMd)).clickable { onClick() }, shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = CardDefaults.cardColors(containerColor = if (isSelected) HoloColors.Neutral50 else HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(48.dp).height(32.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(HoloColors.Neutral800), contentAlignment = Alignment.Center) { Text(brandIcon, style = HoloType.LabelSmall, color = HoloColors.Pulse) }
            Spacer(Modifier.width(HoloSpacing.sm)); Column(Modifier.weight(1f)) { Text(typeLabel, style = HoloType.TitleMedium, color = HoloColors.TextPrimary); if (method.cardLastFour != null) Text("\u2022\u2022\u2022\u2022 ${method.cardLastFour}", style = HoloType.MonoSmall, color = HoloColors.TextSecondary) }
            if (isSelected) Icon(Icons.Filled.CheckCircle, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
        }
    }
}

@Composable fun ShippingOption(title: String, subtitle: String, price: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md).border(if (isSelected) HoloSpacing.BorderDefault else 0.dp, if (isSelected) HoloColors.Ink else HoloColors.Paper, RoundedCornerShape(HoloSpacing.RadiusMd)).clickable { onClick() }, shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = CardDefaults.cardColors(containerColor = if (isSelected) HoloColors.Neutral50 else HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(if (isSelected) HoloColors.Ink else HoloColors.Fog), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.LocalShipping, null, tint = if (isSelected) HoloColors.Paper else HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }
            Spacer(Modifier.width(HoloSpacing.sm)); Column(Modifier.weight(1f)) { Text(title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary); Text(subtitle, style = HoloType.BodySmall, color = HoloColors.TextTertiary) }
            Text(price, style = HoloType.TitleMedium, color = if (price == "GRATIS") HoloColors.Success else HoloColors.TextPrimary)
            if (isSelected) { Spacer(Modifier.width(HoloSpacing.xs)); Icon(Icons.Filled.CheckCircle, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }
        }
    }
}

@Composable fun EmptyStateCard(icon: ImageVector, message: String, actionText: String, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.IconSizeLarge)); Spacer(Modifier.width(HoloSpacing.sm)); Column(Modifier.weight(1f)) { Text(message, style = HoloType.BodyMedium, color = HoloColors.TextSecondary) }; TextButton(onClick = onClick) { Text(actionText, style = HoloType.TitleSmall, color = HoloColors.TextPrimary) } }
    }
}

@Composable fun SummaryLine(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = HoloColors.TextPrimary) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, style = HoloType.BodyMedium, color = HoloColors.TextSecondary); Text(value, style = HoloType.TitleMedium, color = valueColor) }
}