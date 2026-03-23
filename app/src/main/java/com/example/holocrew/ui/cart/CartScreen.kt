/**
 * CartScreen.kt
 *
 * Pantalla del carrito de compras de la aplicación HoloCrew.
 * Lee los datos directamente del CartManager (singleton global) para
 * mostrar los productos añadidos desde Available y ProductDetail.
 *
 * Funcionalidades:
 *  - Ver todos los productos añadidos al carrito
 *  - Modificar cantidades (+ / -)
 *  - Eliminar productos individuales
 *  - Vaciar todo el carrito
 *  - Ver resumen del pedido (subtotal, envío, total)
 *  - Envío gratis automático si el subtotal supera $150
 *
 * Diseño limpio y minimalista siguiendo el estilo del resto de la app.
 */
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.data.CartItem
import com.example.holocrew.data.CartManager

/**
 * Pantalla del carrito de compras.
 * Observa el estado del CartManager y se recompone automáticamente
 * cuando se añaden, eliminan o modifican productos.
 *
 * @param navController Controlador de navegación para el botón de volver
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {

    // Observar los items del carrito desde el singleton global
    val cartItems by CartManager.items.collectAsState()

    // Calcular totales (se recalculan automáticamente al cambiar los items)
    val subtotal = CartManager.getSubtotal()
    val shipping = CartManager.getShipping()
    val total = CartManager.getTotal()

    Scaffold(
        // ── Barra superior: título + botón volver + vaciar ──
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Carrito",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                // Botón para volver a la pantalla anterior
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                // Botón para vaciar todo el carrito
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = { CartManager.clearCart() }) {
                            Text(
                                text = "Vaciar",
                                color = Color(0xFFE53935),
                                fontSize = 14.sp
                            )
                        }
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

        // ════════════════════════════════════════════════════════════
        // ESTADO VACÍO: Cuando el carrito no tiene productos
        // ════════════════════════════════════════════════════════════
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icono grande del carrito vacío
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

                    // Botón para explorar productos
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

            // ════════════════════════════════════════════════════════════
            // CARRITO CON PRODUCTOS
            // ════════════════════════════════════════════════════════════
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

                // ── Lista de productos en el carrito ──
                items(cartItems, key = { "${it.product.id}_${it.selectedSize}_${it.selectedColor}" }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = {
                            CartManager.increaseQuantity(
                                item.product.id,
                                item.selectedSize,
                                item.selectedColor
                            )
                        },
                        onDecrease = {
                            CartManager.decreaseQuantity(
                                item.product.id,
                                item.selectedSize,
                                item.selectedColor
                            )
                        },
                        onRemove = {
                            CartManager.removeItem(
                                item.product.id,
                                item.selectedSize,
                                item.selectedColor
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // ════════════════════════════════════════════════════════
                // RESUMEN DEL PEDIDO
                // ════════════════════════════════════════════════════════
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "RESUMEN DEL PEDIDO",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9E9E9E),
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Subtotal
                            SummaryRow(
                                label = "Subtotal",
                                value = "${"%.2f".format(subtotal)}€"
                            )

                            // Envío (gratis si > $150)
                            SummaryRow(
                                label = "Envío",
                                value = if (shipping == 0.0) "GRATIS" else "${"%.2f".format(shipping)}€",
                                valueColor = if (shipping == 0.0) Color(0xFF4CAF50) else Color.Black
                            )

                            // Mensaje de envío gratis si aún no se alcanza
                            if (subtotal <= 150) {
                                Text(
                                    text = "Añade ${"%.2f".format(150 - subtotal)}€ más para envío gratis",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9E9E9E),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            // Separador
                            Divider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFF0F0F0)
                            )

                            // ── Total ──
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
                                    text = "${"%.2f".format(total)}€",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // ── Botón finalizar compra ──
                            Button(
                                onClick = { /* TODO: Implementar checkout */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                            ) {
                                Text(
                                    text = "FINALIZAR COMPRA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                // Espacio final
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES DEL CARRITO
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Card de un producto dentro del carrito.
 *
 * Muestra la imagen, nombre, talla/color seleccionados, precio,
 * controles de cantidad y botón de eliminar.
 *
 * @param item Item del carrito con producto, cantidad, talla y color
 * @param onIncrease Callback al pulsar "+"
 * @param onDecrease Callback al pulsar "-"
 * @param onRemove Callback al pulsar eliminar (X)
 */
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // ── Imagen del producto ──
            Image(
                painter = painterResource(id = item.product.imageRes),
                contentDescription = item.product.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            // ── Información del producto ──
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
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
                            text = item.product.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Talla y color seleccionados
                        val details = buildString {
                            if (item.selectedSize.isNotEmpty()) append("Talla ${item.selectedSize}")
                            if (item.selectedSize.isNotEmpty() && item.selectedColor.isNotEmpty()) append(" · ")
                            if (item.selectedColor.isNotEmpty()) append(item.selectedColor)
                        }
                        if (details.isNotEmpty()) {
                            Text(
                                text = details,
                                fontSize = 12.sp,
                                color = Color(0xFF9E9E9E),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Botón eliminar (X)
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

                Spacer(modifier = Modifier.height(12.dp))

                // Fila inferior: precio + controles de cantidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Precio del producto
                    Text(
                        text = item.product.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // ── Controles de cantidad (- cantidad +) ──
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F0F0))
                    ) {
                        // Botón decrementar
                        Box(
                            modifier = Modifier
                                .size(32.dp)
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

                        // Cantidad actual
                        Text(
                            text = "${item.quantity}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        // Botón incrementar
                        Box(
                            modifier = Modifier
                                .size(32.dp)
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

/**
 * Fila del resumen del pedido (label : valor).
 *
 * @param label Texto de la izquierda (ej: "Subtotal", "Envío")
 * @param value Texto de la derecha (ej: "419.97€", "GRATIS")
 * @param valueColor Color del valor (verde para "GRATIS", negro para el resto)
 */
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
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}