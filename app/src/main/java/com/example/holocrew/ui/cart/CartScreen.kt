package com.example.holocrew.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.network.CartItemDto
import kotlinx.coroutines.launch
import androidx.compose.material.icons.outlined.LocalShipping

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cartItems by CartManager.items.collectAsState()
    val summary by CartManager.summary.collectAsState()

    // Cargar el carrito al abrir la pantalla
    LaunchedEffect(Unit) {
        CartManager.loadCart(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.Black)
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = {
                            scope.launch { CartManager.clearCart(context) }
                        }) {
                            Text("Vaciar", color = Color(0xFFE53935), fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = Color.Black)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ShoppingCart, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(80.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Tu carrito está vacío", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Añade productos para continuar", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 8.dp))
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { navController.navigateUp() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black), shape = RoundedCornerShape(10.dp)) {
                        Text("Explorar productos", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(bottom = 16.dp)) {

                item {
                    Text("${cartItems.size} productos", fontSize = 13.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                }

                items(cartItems, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { scope.launch { CartManager.updateItem(context, item.id, item.quantity + 1) } },
                        onDecrease = {
                            scope.launch {
                                if (item.quantity > 1) CartManager.updateItem(context, item.id, item.quantity - 1)
                                else CartManager.removeItem(context, item.id)
                            }
                        },
                        onRemove = { scope.launch { CartManager.removeItem(context, item.id) } }
                    )
                    Spacer(Modifier.height(10.dp))
                }

                item { Spacer(Modifier.height(8.dp)) }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("RESUMEN DEL PEDIDO", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 16.dp))
                            SummaryRow("Subtotal", "${"%.2f".format(summary.subtotal)}€")
                            SummaryRow("Envío", if (summary.shipping == 0.0) "GRATIS" else "${"%.2f".format(summary.shipping)}€", valueColor = if (summary.shipping == 0.0) Color(0xFF4CAF50) else Color.Black)
                            if (summary.subtotal <= 150) {
                                Text("Añade ${"%.2f".format(150 - summary.subtotal)}€ más para envío gratis", fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(vertical = 4.dp))
                            }
                            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("TOTAL", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("${"%.2f".format(summary.total)}€", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Spacer(Modifier.height(20.dp))
                            Button(
                                onClick = { navController.navigate("checkout") { launchSingleTop = true } },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                            ) {
                                Text("FINALIZAR COMPRA", fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp)
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItemDto, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            // Imagen placeholder o color
            Box(
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ShoppingBag, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(32.dp))
            }

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        val details = buildString {
                            item.size_name?.let { append("Talla $it") }
                            if (!item.size_name.isNullOrEmpty() && !item.color_name.isNullOrEmpty()) append(" · ")
                            item.color_name?.let { append(it) }
                        }
                        if (details.isNotEmpty()) {
                            Text(details, fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, "Eliminar", tint = Color(0xFF9E9E9E), modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${"%.2f".format(item.price_at_addition)}€", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F0F0))
                    ) {
                        Box(modifier = Modifier.size(32.dp).clickable { onDecrease() }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Remove, "Menos", tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                        Text("${item.quantity}", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                        Box(modifier = Modifier.size(32.dp).clickable { onIncrease() }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Add, "Más", tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = Color(0xFF666666))
        Text(value, fontSize = 14.sp, color = valueColor, fontWeight = FontWeight.Medium)
    }
}