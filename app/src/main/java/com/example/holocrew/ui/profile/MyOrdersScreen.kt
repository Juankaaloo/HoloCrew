package com.example.holocrew.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.OrderDto
import com.example.holocrew.data.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController) {
    val context = LocalContext.current

    var orders by remember { mutableStateOf<List<OrderDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token = TokenManager.getTokenOnce(context) ?: return@LaunchedEffect
        try {
            val response = RetrofitClient.api.getOrders("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                orders = response.body()!!.data ?: emptyList()
            }
        } catch (e: Exception) { e.printStackTrace() }
        isLoading = false
    }

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

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color.Black, Color(0xFF1A1A1A))))
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text("MIS PEDIDOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 2.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Historial de compras", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (orders.isEmpty() && !isLoading) "No tienes pedidos todavía" else "${orders.size} pedido${if (orders.size != 1) "s" else ""}",
                            fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Cargando...", fontSize = 16.sp, color = Color(0xFF9E9E9E))
                    }
                }
            } else if (orders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(24.dp))
                        Icon(Icons.Outlined.Inventory2, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(80.dp))
                        Spacer(Modifier.height(20.dp))
                        Text("Aún no has hecho ningún pedido", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF666666))
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { navController.navigate("available") { launchSingleTop = true } },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("EXPLORAR PRODUCTOS", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(16.dp)) }
                items(orders) { order ->
                    OrderCard(order = order)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderDto) {
    val statusConfig = when (order.status) {
        "delivered" -> Pair(Color(0xFF4CAF50), "Entregado")
        "shipped" -> Pair(Color(0xFF2196F3), "En camino")
        "processing" -> Pair(Color(0xFFFF9800), "Procesando")
        "paid" -> Pair(Color(0xFF9C27B0), "Pagado")
        "pending" -> Pair(Color(0xFF9E9E9E), "Pendiente")
        "cancelled" -> Pair(Color(0xFFE53935), "Cancelado")
        "refunded" -> Pair(Color(0xFFE53935), "Reembolsado")
        else -> Pair(Color(0xFF9E9E9E), order.status)
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(order.order_number, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 0.5.sp)
                    Text(order.created_at.take(10), fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 2.dp))
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(statusConfig.first.copy(alpha = 0.1f)).padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text(statusConfig.second, fontSize = 12.sp, color = statusConfig.first, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)))
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${order.item_count} producto${if (order.item_count != 1) "s" else ""}", fontSize = 14.sp, color = Color(0xFF666666))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${order.total_eur}€", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Filled.ChevronRight, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}