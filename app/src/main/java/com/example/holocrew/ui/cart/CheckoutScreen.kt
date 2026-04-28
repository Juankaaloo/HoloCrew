package com.example.holocrew.ui.cart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cartItems by CartManager.items.collectAsState()
    val summary by CartManager.summary.collectAsState()

    // Datos cargados desde la API
    var addresses by remember { mutableStateOf<List<AddressDto>>(emptyList()) }
    var paymentMethods by remember { mutableStateOf<List<PaymentMethodDto>>(emptyList()) }
    var userEmail by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var orderSuccess by remember { mutableStateOf(false) }
    var orderNumber by remember { mutableStateOf("") }

    // Selecciones del usuario
    var selectedAddressId by remember { mutableStateOf<String?>(null) }
    var selectedPaymentId by remember { mutableStateOf<String?>(null) }
    var selectedShipping by remember { mutableStateOf("standard") }

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        val token = TokenManager.getTokenOnce(context) ?: return@LaunchedEffect
        try {
            // Cargar direcciones
            val addrResp = RetrofitClient.api.getAddresses("Bearer $token")
            if (addrResp.isSuccessful && addrResp.body()?.success == true) {
                addresses = addrResp.body()!!.data ?: emptyList()
                selectedAddressId = addresses.firstOrNull { it.is_default }?.id ?: addresses.firstOrNull()?.id
            }

            // Cargar métodos de pago
            val payResp = RetrofitClient.api.getPaymentMethods("Bearer $token")
            if (payResp.isSuccessful && payResp.body()?.success == true) {
                paymentMethods = payResp.body()!!.data ?: emptyList()
                selectedPaymentId = paymentMethods.firstOrNull { it.is_default }?.id ?: paymentMethods.firstOrNull()?.id
            }

            // Cargar email
            val meResp = RetrofitClient.api.getMe("Bearer $token")
            if (meResp.isSuccessful && meResp.body()?.success == true) {
                userEmail = meResp.body()!!.data?.email ?: ""
            }
        } catch (e: Exception) { e.printStackTrace() }
        isLoading = false
    }

    // Pantalla de éxito
    if (orderSuccess) {
        OrderSuccessScreen(
            orderNumber = orderNumber,
            onGoHome = {
                navController.navigate("home") { popUpTo(0) { inclusive = true } }
            },
            onGoOrders = {
                navController.navigate("my_orders") { popUpTo("home") }
            }
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
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Cargando...", fontSize = 16.sp, color = Color(0xFF9E9E9E))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // ── HEADER SNKRS ──
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color.Black, Color(0xFF1A1A1A))))
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text("CHECKOUT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 2.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Finalizar compra", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text("${cartItems.size} producto${if (cartItems.size != 1) "s" else ""} · ${"%.2f".format(total)}€", fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // ── EMAIL DE CONFIRMACIÓN ──
            item {
                CheckoutSection(
                    icon = Icons.Outlined.Email,
                    title = "Confirmación",
                    subtitle = userEmail.ifEmpty { "Sin email" }
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            // ── DIRECCIÓN DE ENVÍO ──
            item {
                SectionTitle2("DIRECCIÓN DE ENVÍO")
            }

            if (addresses.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Outlined.LocationOn,
                        message = "No tienes direcciones guardadas",
                        actionText = "Añadir dirección",
                        onClick = { navController.navigate("addresses") { launchSingleTop = true } }
                    )
                }
            } else {
                addresses.forEach { address ->
                    item {
                        SelectableAddressCard(
                            address = address,
                            isSelected = selectedAddressId == address.id,
                            onClick = { selectedAddressId = address.id }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                item {
                    TextButton(
                        onClick = { navController.navigate("addresses") { launchSingleTop = true } },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Añadir dirección", fontWeight = FontWeight.Medium)
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            // ── MÉTODO DE ENVÍO ──
            item { SectionTitle2("MÉTODO DE ENVÍO") }

            item {
                ShippingOption(
                    title = "Estándar",
                    subtitle = "3-5 días laborables",
                    price = if (summary.subtotal > 150) "GRATIS" else "9.99€",
                    isSelected = selectedShipping == "standard",
                    onClick = { selectedShipping = "standard" }
                )
                Spacer(Modifier.height(8.dp))
                ShippingOption(
                    title = "Express",
                    subtitle = "1-2 días laborables",
                    price = "14.99€",
                    isSelected = selectedShipping == "express",
                    onClick = { selectedShipping = "express" }
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            // ── MÉTODO DE PAGO ──
            item { SectionTitle2("MÉTODO DE PAGO") }

            if (paymentMethods.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Outlined.CreditCard,
                        message = "No tienes métodos de pago",
                        actionText = "Añadir tarjeta",
                        onClick = { navController.navigate("payment_methods") { launchSingleTop = true } }
                    )
                }
            } else {
                paymentMethods.forEach { method ->
                    item {
                        SelectablePaymentCard(
                            method = method,
                            isSelected = selectedPaymentId == method.id,
                            onClick = { selectedPaymentId = method.id }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                item {
                    TextButton(
                        onClick = { navController.navigate("payment_methods") { launchSingleTop = true } },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Añadir tarjeta", fontWeight = FontWeight.Medium)
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            // ── RESUMEN DEL PEDIDO ──
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("RESUMEN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(16.dp))

                        cartItems.forEach { item ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.name} ×${item.quantity}", fontSize = 14.sp, color = Color(0xFF444444), modifier = Modifier.weight(1f))
                                Text("${"%.2f".format(item.price_at_addition * item.quantity)}€", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)))
                        Spacer(Modifier.height(12.dp))

                        SummaryLine("Subtotal", "${"%.2f".format(summary.subtotal)}€")
                        SummaryLine(
                            "Envío ${if (selectedShipping == "express") "Express" else "Estándar"}",
                            if (shippingCost == 0.0) "GRATIS" else "${"%.2f".format(shippingCost)}€",
                            valueColor = if (shippingCost == 0.0) Color(0xFF4CAF50) else Color.Black
                        )

                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)))
                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("TOTAL", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            Text("${"%.2f".format(total)}€", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            // ── BOTÓN CONFIRMAR ──
            item {
                val canCheckout = selectedAddressId != null && (paymentMethods.isEmpty() || selectedPaymentId != null)

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = {
                            val addr = selectedAddress ?: return@Button
                            scope.launch {
                                isPlacingOrder = true
                                val token = TokenManager.getTokenOnce(context) ?: return@launch
                                try {
                                    val response = RetrofitClient.api.createOrder(
                                        "Bearer $token",
                                        CreateOrderRequest(
                                            shipping_address = ShippingAddressDto(
                                                recipient_name = addr.recipient_name,
                                                street_address = addr.street_address,
                                                city = addr.city,
                                                postal_code = addr.postal_code,
                                                country_code = addr.country_code
                                            ),
                                            shipping_method = selectedShipping,
                                            payment_method = when (selectedPayment?.payment_type) {
                                                "debit_card" -> "debit_card"
                                                "paypal" -> "paypal"
                                                else -> "credit_card"
                                            }
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
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canCheckout) Color.Black else Color(0xFFE0E0E0)
                        ),
                        enabled = canCheckout && !isPlacingOrder
                    ) {
                        if (isPlacingOrder) {
                            Text("Procesando...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("CONFIRMAR PEDIDO · ${"%.2f".format(total)}€", fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        }
                    }

                    if (!canCheckout) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (selectedAddressId == null) "⚠ Selecciona una dirección de envío" else "",
                            fontSize = 13.sp, color = Color(0xFFE53935),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Pago 100% seguro y encriptado", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA DE ÉXITO
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun OrderSuccessScreen(orderNumber: String, onGoHome: () -> Unit, onGoOrders: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Icono check animado
            Box(
                modifier = Modifier.size(100.dp).clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(56.dp))
            }

            Spacer(Modifier.height(28.dp))

            Text("¡PEDIDO CONFIRMADO!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text("Gracias por tu compra", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(Modifier.height(8.dp))

            if (orderNumber.isNotEmpty()) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1A1A))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Pedido $orderNumber", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 1.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Recibirás un email con los detalles\ny el seguimiento de tu pedido.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f), lineHeight = 20.sp)

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onGoOrders,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("VER MIS PEDIDOS", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 1.sp)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(50),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color(0xFF444444), Color(0xFF444444))))
            ) {
                Text("SEGUIR COMPRANDO", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES AUXILIARES
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun SectionTitle2(title: String) {
    Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
    Spacer(Modifier.height(6.dp))
}

@Composable
fun CheckoutSection(icon: ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.Black, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
                Text(subtitle, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }
        }
    }
}

@Composable
fun SelectableAddressCard(address: AddressDto, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Color.Black else Color.Transparent, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFF8F8F8) else Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(if (isSelected) Color.Black else Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.LocationOn, null, tint = if (isSelected) Color.White else Color.Black, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(address.recipient_name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("${address.street_address}, ${address.city}", fontSize = 13.sp, color = Color(0xFF666666), modifier = Modifier.padding(top = 2.dp))
                Text("${address.postal_code} · ${address.country_code}", fontSize = 12.sp, color = Color(0xFF9E9E9E))
            }
            if (isSelected) {
                Icon(Icons.Filled.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun SelectablePaymentCard(method: PaymentMethodDto, isSelected: Boolean, onClick: () -> Unit) {
    val brandIcon = when (method.card_brand?.lowercase()) {
        "visa" -> "VISA"
        "mastercard" -> "MC"
        "amex" -> "AMEX"
        else -> "CARD"
    }
    val typeLabel = when (method.payment_type) {
        "credit_card" -> "Crédito"
        "debit_card" -> "Débito"
        "paypal" -> "PayPal"
        else -> method.payment_type
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Color.Black else Color.Transparent, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFF8F8F8) else Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(48.dp).height(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1A1A1A)), contentAlignment = Alignment.Center) {
                Text(brandIcon, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFD4AF37))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(typeLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                if (method.card_last_four != null) {
                    Text("•••• ${method.card_last_four}", fontSize = 13.sp, color = Color(0xFF666666))
                }
            }
            if (isSelected) {
                Icon(Icons.Filled.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun ShippingOption(title: String, subtitle: String, price: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Color.Black else Color.Transparent, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFF8F8F8) else Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(if (isSelected) Color.Black else Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.LocalShipping, null, tint = if (isSelected) Color.White else Color.Black, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF9E9E9E))
            }
            Text(price, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (price == "GRATIS") Color(0xFF4CAF50) else Color.Black)
            if (isSelected) {
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun EmptyStateCard(icon: ImageVector, message: String, actionText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(message, fontSize = 14.sp, color = Color(0xFF666666))
            }
            TextButton(onClick = onClick) {
                Text(actionText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun SummaryLine(label: String, value: String, valueColor: Color = Color.Black) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = Color(0xFF666666))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}