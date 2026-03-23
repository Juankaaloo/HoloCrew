/**
 * ProductDetailScreen.kt
 *
 * Pantalla de detalle de producto en HoloCrew.
 * Diseño blanco limpio con bottom sheet para selección de tallas.
 *
 * Flujo de compra:
 *  1. Usuario ve el producto (imagen, precio, descripción, etc.)
 *  2. Pulsa "Comprar ahora" o "Añadir a la cesta"
 *  3. Se abre un bottom sheet con las tallas en grid
 *  4. Al seleccionar una talla se ejecuta la acción correspondiente
 *
 * Secciones:
 *  - Imagen hero con gradiente y badges
 *  - Info: marca, título, precio, rating
 *  - Colores (selección directa en pantalla)
 *  - Botones de compra (abren bottom sheet de tallas)
 *  - Envío y devoluciones
 *  - Descripción y características
 *  - Info técnica
 *  - Productos relacionados
 */
package com.example.holocrew.ui.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.FavoritesManager
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA
// ══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, productId: Int) {
    val product = remember(productId) { mockProducts.find { it.id == productId } }
    if (product == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Producto no encontrado", fontSize = 18.sp, color = Color(0xFF666666))
                Spacer(Modifier.height(16.dp))
                Button(onClick = { navController.navigateUp() }) { Text("Volver") }
            }
        }
        return
    }
    ProductDetailContent(navController, product)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(navController: NavController, product: ProductDetail) {

    // ── Estados ──
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(product.id)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Bottom sheet para selección de tallas
    var showSizeSheet by remember { mutableStateOf(false) }
    var sizeSheetAction by remember { mutableStateOf("cart") } // "cart" o "buy"
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Productos relacionados
    val relatedProducts = remember {
        mockProducts.filter { it.category == product.category && it.id != product.id }.take(4)
    }

    // Rango de tallas
    val sizeRange = if (product.sizes.size > 1) "${product.sizes.first()} – ${product.sizes.last()}" else product.sizes.firstOrNull() ?: ""

    // ── Bottom Sheet de tallas ──
    if (showSizeSheet && product.sizes.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showSizeSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            SizeSelectionSheet(
                sizes = product.sizes,
                onSizeSelected = { selectedSize ->
                    showSizeSheet = false
                    val color = ""

                    if (sizeSheetAction == "buy") {
                        // Comprar ahora: añadir al carrito y navegar
                        CartManager.addItem(product, selectedSize, color)
                        navController.navigate("cart") { launchSingleTop = true }
                    } else {
                        // Añadir a la cesta: añadir y mostrar snackbar
                        CartManager.addItem(product, selectedSize, color)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("${product.title} (${selectedSize}) añadido al carrito")
                        }
                    }
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { FavoritesManager.toggleFavorite(product.id) }) {
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            "Favorito", tint = if (isFavorite) Color.Red else Color.Black
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Share, "Compartir", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)
        ) {

            // ═══ IMAGEN HERO ═══
            item {
                Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradiente inferior para transición suave
                    Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp).align(Alignment.BottomCenter)
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.White)))
                    )
                    // Badge de descuento
                    if (product.originalPrice != null) {
                        Box(
                            modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                                .clip(RoundedCornerShape(8.dp)).background(Color(0xFFE53935))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("OFERTA", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            // ═══ INFORMACIÓN PRINCIPAL ═══
            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    // Marca
                    Text(product.brand, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))

                    // Título y precio
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        // Título
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(product.subtitle, fontSize = 15.sp, color = Color(0xFF666666), modifier = Modifier.padding(top = 2.dp))
                        }
                        // Precio
                        Column(horizontalAlignment = Alignment.End) {
                            Text(product.price, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            product.originalPrice?.let {
                                Text(it, fontSize = 14.sp, color = Color(0xFF9E9E9E), textDecoration = TextDecoration.LineThrough)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Tallas disponibles
                    if (sizeRange.isNotEmpty()) {
                        Text(sizeRange, fontSize = 15.sp, color = Color(0xFF666666))
                    }

                    Spacer(Modifier.height(12.dp))

                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                Icons.Filled.Star, "Estrella",
                                tint = if (index < product.rating.toInt()) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("${product.rating}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(Modifier.width(4.dp))
                        Text("(${product.reviewCount} reseñas)", fontSize = 14.sp, color = Color(0xFF9E9E9E))
                    }

                    Spacer(Modifier.height(20.dp))

                    // ═══ BOTONES DE COMPRA ═══
                    // "Comprar ahora" abre bottom sheet con acción "buy"
                    Button(
                        onClick = {
                            if (product.sizes.isNotEmpty()) {
                                sizeSheetAction = "buy"
                                showSizeSheet = true
                            } else {
                                val color = ""
                                CartManager.addItem(product, "", color)
                                navController.navigate("cart") { launchSingleTop = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Comprar ahora", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }

                    Spacer(Modifier.height(10.dp))

                    // "Añadir a la cesta" abre bottom sheet con acción "cart"
                    OutlinedButton(
                        onClick = {
                            if (product.sizes.isNotEmpty()) {
                                sizeSheetAction = "cart"
                                showSizeSheet = true
                            } else {
                                val color = ""
                                CartManager.addItem(product, "", color)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("${product.title} añadido al carrito")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(50),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(Color(0xFFCCCCCC), Color(0xFFCCCCCC)))
                        )
                    ) {
                        Text("Añadir a la cesta", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    }

                    Spacer(Modifier.height(24.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(24.dp))

                    // ═══ ENVÍO Y DEVOLUCIONES ═══
                    ShippingInfoCard()

                    Spacer(Modifier.height(24.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(24.dp))

                    // ═══ DESCRIPCIÓN ═══
                    Text(product.description, fontSize = 15.sp, color = Color(0xFF666666), lineHeight = 24.sp)

                    Spacer(Modifier.height(20.dp))

                    // ═══ CARACTERÍSTICAS ═══
                    if (product.features.isNotEmpty()) {
                        product.features.forEach { feature ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 10.dp)) {
                                Box(Modifier.size(6.dp).clip(CircleShape).background(Color.Black))
                                Spacer(Modifier.width(12.dp))
                                Text(feature, fontSize = 15.sp, color = Color(0xFF444444))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(16.dp))

                    // ═══ FECHA DE PUBLICACIÓN ═══
                    Text("Publicado el ${product.publishedDate}", fontSize = 14.sp, color = Color(0xFF9E9E9E))

                    Spacer(Modifier.height(24.dp))
                }
            }

            // ═══ PRODUCTOS RELACIONADOS ═══
            if (relatedProducts.isNotEmpty()) {
                item {
                    Text(
                        "TAMBIEN TE PUEDE GUSTAR", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = Color.Black, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(relatedProducts) { related ->
                            RelatedProductCard(product = related, onClick = {
                                navController.navigate("product_detail/${related.id}") { launchSingleTop = true }
                            })
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// BOTTOM SHEET DE TALLAS
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Contenido del bottom sheet para seleccionar talla.
 * Muestra un grid de 3 columnas con las tallas disponibles.
 * Al pulsar una talla se ejecuta el callback.
 */
@Composable
fun SizeSelectionSheet(sizes: List<String>, onSizeSelected: (String) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Título
        Text(
            text = "Selecciona una talla",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(20.dp))

        // Grid de tallas (3 columnas)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height((((sizes.size + 2) / 3) * 56).dp) // Altura dinámica según filas
        ) {
            items(sizes) { size ->
                val isSelected = sizes.indexOf(size) == selectedIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isSelected) Modifier.background(Color.Black)
                            else Modifier.border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp)).background(Color.White)
                        )
                        .clickable { selectedIndex = sizes.indexOf(size) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = size,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón de confirmar (solo activo si hay talla seleccionada)
        Button(
            onClick = {
                if (selectedIndex >= 0) {
                    onSizeSelected(sizes[selectedIndex])
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedIndex >= 0) Color.Black else Color(0xFFE0E0E0)
            ),
            enabled = selectedIndex >= 0
        ) {
            Text(
                "Añadir a la cesta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (selectedIndex >= 0) Color.White else Color(0xFF9E9E9E)
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

/** Chip selector de color con check */
@Composable
fun ColorChip(colorOption: ColorOption, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier.size(44.dp)
                .then(if (isSelected) Modifier.border(2.5.dp, Color.Black, CircleShape) else Modifier)
                .padding(3.dp).clip(CircleShape).background(colorOption.color),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    Icons.Filled.Check, "Seleccionado",
                    tint = if (colorOption.colorHex == "#FFFFFF" || colorOption.colorHex == "#C0C0C0") Color.Black else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/** Card de información de envío */
@Composable
fun ShippingInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            ShippingRow(Icons.Filled.LocalShipping, "Envío gratis", "En pedidos superiores a 150€")
            Spacer(Modifier.height(12.dp))
            ShippingRow(Icons.Filled.Loop, "Devolución gratuita", "30 días para devoluciones")
            Spacer(Modifier.height(12.dp))
            ShippingRow(Icons.Filled.Verified, "Producto original", "Garantía de autenticidad")
        }
    }
}

@Composable
fun ShippingRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.Black, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF9E9E9E))
        }
    }
}

/** Fila de info técnica */
@Composable
fun InfoRow(title: String, value: String) {
    if (value.isNotEmpty()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 13.sp, color = Color(0xFF9E9E9E))
            Text(value, fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 16.dp), maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Card de producto relacionado */
@Composable
fun RelatedProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.width(150.dp).clickable { onClick() }) {
        Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5))) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.price, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
    }
}