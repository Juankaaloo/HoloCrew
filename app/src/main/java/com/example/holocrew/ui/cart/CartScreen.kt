package com.example.holocrew.ui.cart

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.R

data class CartItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val price: Double,
    val imageRes: Int,
    val size: String,
    val color: String,
    var quantity: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {

    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem(1, "Holo Pannel Hoodie", "Talla M · Negro", 129.99, R.drawable.pannels_hoodie, "M", "Negro"),
                CartItem(2, "Denim Bison Holo", "Talla L · Azul", 89.99, R.drawable.bison_denim_holo, "L", "Azul"),
                CartItem(3, "Glory Holo Polo", "Talla S · Blanco", 199.99, R.drawable.glory_holo_polo, "S", "Blanco")
            )
        )
    }

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val shipping = if (subtotal > 150) 0.0 else 9.99
    val total = subtotal + shipping

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Carrito",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { cartItems = emptyList() }) {
                        Text("Vaciar", color = Color(0xFFE53935), fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        if (cartItems.isEmpty()) {
            // Carrito vacío
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
                        tint = Color(0xFFCCCCCC),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Añade productos para continuar",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigateUp() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Explorar productos", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Contador de items
                item {
                    Text(
                        text = "${cartItems.size} productos",
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // Lista de productos
                items(cartItems, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = {
                            cartItems = cartItems.map {
                                if (it.id == item.id) it.copy(quantity = it.quantity + 1) else it
                            }
                        },
                        onDecrease = {
                            if (item.quantity > 1) {
                                cartItems = cartItems.map {
                                    if (it.id == item.id) it.copy(quantity = it.quantity - 1) else it
                                }
                            } else {
                                cartItems = cartItems.filter { it.id != item.id }
                            }
                        },
                        onRemove = {
                            cartItems = cartItems.filter { it.id != item.id }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Resumen del pedido
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "RESUMEN DEL PEDIDO",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            SummaryRow("Subtotal", "$${"%.2f".format(subtotal)}")
                            SummaryRow(
                                label = "Envío",
                                value = if (shipping == 0.0) "GRATIS" else "$${"%.2f".format(shipping)}",
                                valueColor = if (shipping == 0.0) Color(0xFF4CAF50) else Color.Black
                            )

                            if (subtotal <= 150) {
                                Text(
                                    text = "Añade ${"%.2f".format(150 - subtotal)}€ más para envío gratis",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9E9E9E),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFFEEEEEE)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TOTAL",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "$${"%.2f".format(total)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { /* placeholder */ },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                            ) {
                                Text(
                                    text = "FINALIZAR COMPRA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Imagen
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1
                        )
                        Text(
                            text = item.subtitle,
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Eliminar",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${"%.2f".format(item.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Contador cantidad
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F0F0))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { onDecrease() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Menos",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "${item.quantity}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { onIncrease() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Más",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF666666))
        Text(text = value, fontSize = 14.sp, color = valueColor, fontWeight = FontWeight.Medium)
    }
}