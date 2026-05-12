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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.holocrew.data.network.CartRepository
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val cartItems by CartRepository.cartItems.collectAsState()
    val cartCount by CartRepository.cartCount.collectAsState()

    LaunchedEffect(Unit) { CartRepository.loadCart() }

    val subtotal = CartRepository.getSubtotal()
    val shipping = CartRepository.getShippingCost()
    val total = CartRepository.getTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Ink)
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = { scope.launch { CartRepository.clearCart() } }) {
                            Text("Vaciar", style = HoloType.TitleMedium, color = HoloColors.Pulse)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Paper)
            )
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->

        if (cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ShoppingCart, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.huge))
                    Spacer(Modifier.height(HoloSpacing.md))
                    Text("Tu carrito esta vacio", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
                    Text("Agrega productos para continuar", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xs))
                    Spacer(Modifier.height(HoloSpacing.xl))
                    Button(
                        onClick = { navController.navigate("available") { launchSingleTop = true } },
                        colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                        shape = RoundedCornerShape(HoloSpacing.RadiusMd)
                    ) {
                        Text("EXPLORAR PRODUCTOS", style = HoloType.LabelLarge, color = HoloColors.Paper)
                    }
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(bottom = HoloSpacing.md)) {

                item {
                    Text(
                        "${cartItems.size} producto${if (cartItems.size != 1) "s" else ""}",
                        style = HoloType.BodySmall, color = HoloColors.TextTertiary,
                        modifier = Modifier.padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.sm)
                    )
                }

                items(cartItems, key = { it.cartItemId }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { scope.launch { CartRepository.updateQuantity(item.cartItemId, item.quantity + 1) } },
                        onDecrease = {
                            scope.launch {
                                if (item.quantity > 1) CartRepository.updateQuantity(item.cartItemId, item.quantity - 1)
                                else CartRepository.removeItem(item.cartItemId)
                            }
                        },
                        onRemove = { scope.launch { CartRepository.removeItem(item.cartItemId) } }
                    )
                    Spacer(Modifier.height(HoloSpacing.xs))
                }

                item { Spacer(Modifier.height(HoloSpacing.xs)) }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
                        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
                        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(Modifier.padding(HoloSpacing.lg)) {
                            Text("RESUMEN DEL PEDIDO", style = HoloType.LabelMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(bottom = HoloSpacing.md))

                            SummaryRow("Subtotal", "${"%.2f".format(subtotal)}\u20AC")
                            SummaryRow(
                                "Envio",
                                if (shipping == 0.0) "GRATIS" else "${"%.2f".format(shipping)}\u20AC",
                                valueColor = if (shipping == 0.0) HoloColors.Success else HoloColors.TextPrimary
                            )

                            if (subtotal < 150) {
                                Text(
                                    "Agrega ${"%.2f".format(150 - subtotal)}\u20AC mas para envio gratis",
                                    style = HoloType.BodySmall, color = HoloColors.TextTertiary,
                                    modifier = Modifier.padding(vertical = HoloSpacing.xxs)
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = HoloSpacing.md), color = HoloColors.Neutral100)

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("TOTAL", style = HoloType.LabelLarge, color = HoloColors.TextPrimary)
                                Text("${"%.2f".format(total)}\u20AC", style = HoloType.MonoLarge, color = HoloColors.TextPrimary)
                            }

                            Spacer(Modifier.height(HoloSpacing.lg))

                            Button(
                                onClick = { navController.navigate("checkout") { launchSingleTop = true } },
                                modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                                colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink)
                            ) {
                                Text("FINALIZAR COMPRA", style = HoloType.LabelLarge, color = HoloColors.Paper)
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(HoloSpacing.BottomNavHeight)) }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartRepository.CartProductItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(HoloSpacing.sm)) {
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.title,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.weight(1f).padding(start = HoloSpacing.sm)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        Text(item.product.title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        val details = buildString {
                            item.size?.let { append("Talla $it") }
                            if (!item.size.isNullOrEmpty() && !item.color.isNullOrEmpty()) append(" \u00B7 ")
                            item.color?.let { append(it) }
                        }
                        if (details.isNotEmpty()) {
                            Text(details, style = HoloType.BodySmall, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, "Eliminar", tint = HoloColors.TextTertiary, modifier = Modifier.size(HoloSpacing.IconSizeSmall))
                    }
                }

                Spacer(Modifier.height(HoloSpacing.sm))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${"%.2f".format(item.priceAtAdd)}\u20AC", style = HoloType.MonoMedium, color = HoloColors.TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(HoloColors.Fog)) {
                        Box(Modifier.size(32.dp).clickable { onDecrease() }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Remove, "Menos", tint = HoloColors.Ink, modifier = Modifier.size(14.dp))
                        }
                        Text("${item.quantity}", style = HoloType.TitleMedium, color = HoloColors.TextPrimary, modifier = Modifier.padding(horizontal = HoloSpacing.sm))
                        Box(Modifier.size(32.dp).clickable { onIncrease() }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Add, "Mas", tint = HoloColors.Ink, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = HoloColors.TextPrimary) {
    Row(Modifier.fillMaxWidth().padding(vertical = HoloSpacing.xxs), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = HoloType.BodyMedium, color = HoloColors.TextSecondary)
        Text(value, style = HoloType.TitleMedium, color = valueColor)
    }
}