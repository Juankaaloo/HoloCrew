/**
 * Available.kt (AvailableScreen)
 *
 * Catálogo de productos disponibles en HoloCrew.
 * Diseño híbrido SNKRS:
 *  - Producto destacado en card grande a ancho completo (1 columna)
 *  - Resto de productos en grid de 2 columnas
 *  - Filtros por categoría en chips horizontales
 *  - Precio original tachado si hay descuento
 *  - Botón rápido "+" de añadir al carrito en cada card
 *  - Favoritos globales sincronizados
 */
package com.example.holocrew.ui.available

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.components.CustomTopAppBar
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.mockProducts
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableScreen(navController: NavController? = null) {

    // ── Estados ──
    var selectedFilter by remember { mutableStateOf("Todos") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val categories = remember {
        listOf("Todos") + mockProducts.map { it.category }.distinct()
    }

    val filteredProducts = remember(selectedFilter) {
        if (selectedFilter == "Todos") mockProducts
        else mockProducts.filter { it.category == selectedFilter }
    }

    // Separar: primer producto destacado + resto para grid
    val featuredProduct = filteredProducts.firstOrNull()
    val gridProducts = if (filteredProducts.size > 1) filteredProducts.drop(1) else emptyList()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = { navController?.navigate("search") { launchSingleTop = true } },
                onFavoritesClick = { navController?.navigate("favorites") { launchSingleTop = true } }
            )
        },
        bottomBar = {
            if (navController != null) BottomNavigationBar(navController = navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // ════════════════════════════════════════════════════════════
            // HEADER: Título, contador y filtros
            // ════════════════════════════════════════════════════════════
            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Disponibles", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Text(
                        "${filteredProducts.size} productos",
                        fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(Modifier.height(16.dp))

                    // Filtros
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            CategoryFilterChip(
                                text = category,
                                isSelected = selectedFilter == category,
                                onClick = { selectedFilter = category }
                            )
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════
            // ESTADO VACÍO
            // ════════════════════════════════════════════════════════════
            if (filteredProducts.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay productos en esta categoría", fontSize = 16.sp, color = Color(0xFF666666))
                        Text("Intenta con otro filtro", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            // ════════════════════════════════════════════════════════════
            // PRODUCTO DESTACADO (card grande, 1 columna)
            // ════════════════════════════════════════════════════════════
            if (featuredProduct != null) {
                item {
                    FeaturedProductCard(
                        product = featuredProduct,
                        onClick = { navController?.navigate("product_detail/${featuredProduct.id}") },
                        onAddToCart = {
                            CartManager.addItem(featuredProduct)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("${featuredProduct.title} añadido al carrito")
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ════════════════════════════════════════════════════════════
            // GRID DE PRODUCTOS (2 columnas)
            // ════════════════════════════════════════════════════════════
            val rows = gridProducts.chunked(2)
            items(rows) { rowProducts ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowProducts.forEach { product ->
                        ProductGridCard(
                            product = product,
                            modifier = Modifier.weight(1f),
                            onClick = { navController?.navigate("product_detail/${product.id}") },
                            onAddToCart = {
                                CartManager.addItem(product)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("${product.title} añadido al carrito")
                                }
                            }
                        )
                    }
                    if (rowProducts.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

/** Chip de filtro por categoría */
@Composable
fun CategoryFilterChip(text: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Color.Black else Color(0xFFF0F0F0))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text, fontSize = 14.sp,
            color = if (isSelected) Color.White else Color(0xFF666666),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

/**
 * Card de producto DESTACADO — ancho completo, imagen grande con gradiente,
 * info superpuesta abajo, botón de carrito y favorito.
 */
@Composable
fun FeaturedProductCard(
    product: ProductDetail,
    onClick: () -> Unit = {},
    onAddToCart: () -> Unit = {}
) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(product.id)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradiente inferior
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
        )

        // Badge de estado (arriba izquierda)
        Box(
            modifier = Modifier.padding(14.dp).align(Alignment.TopStart)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when (product.status) {
                        "Nuevo" -> Color(0xFF4CAF50)
                        "Exclusivo" -> Color(0xFF9C27B0)
                        "Más vendido" -> Color(0xFFF44336)
                        "En oferta" -> Color(0xFFFF9800)
                        "Premium" -> Color(0xFFD4AF37)
                        "Limitado" -> Color(0xFFE91E63)
                        else -> Color(0xFF607D8B)
                    }
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(product.status.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }

        // Favorito (arriba derecha)
        IconButton(
            onClick = { FavoritesManager.toggleFavorite(product.id) },
            modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(36.dp)
        ) {
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                "Favorito", tint = if (isFavorite) Color.Red else Color.White, modifier = Modifier.size(22.dp)
            )
        }

        // Info superpuesta (abajo)
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Text(product.brand, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(4.dp))
            Text(product.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(product.subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(top = 2.dp))
            Spacer(Modifier.height(8.dp))

            // Precio + precio original tachado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.price, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                product.originalPrice?.let {
                    Spacer(Modifier.width(8.dp))
                    Text(it, fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f), textDecoration = TextDecoration.LineThrough)
                }
            }
        }

        // Botón rápido de carrito (abajo derecha)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onAddToCart() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, "Añadir al carrito", tint = Color.Black, modifier = Modifier.size(22.dp))
        }
    }
}

/**
 * Card de producto para grid de 2 columnas.
 * Imagen con badge, favorito, nombre, subtítulo, precio con descuento tachado,
 * y botón "+" para añadir al carrito rápidamente.
 */
@Composable
fun ProductGridCard(
    product: ProductDetail,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onAddToCart: () -> Unit = {}
) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(product.id)

    Column(modifier = modifier.clickable { onClick() }) {
        // ── Imagen ──
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp)
                .clip(RoundedCornerShape(14.dp)).background(Color(0xFFF5F5F5))
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            // Badge de estado
            if (product.status.isNotEmpty()) {
                Box(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (product.status) {
                                "Nuevo" -> Color(0xFF4CAF50)
                                "Exclusivo" -> Color(0xFF9C27B0)
                                "Más vendido" -> Color(0xFFF44336)
                                "En oferta" -> Color(0xFFFF9800)
                                "Premium" -> Color(0xFFD4AF37)
                                "Limitado" -> Color(0xFFE91E63)
                                else -> Color(0xFF607D8B)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(product.status.uppercase(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
            }

            // Favorito
            IconButton(
                onClick = { FavoritesManager.toggleFavorite(product.id) },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(32.dp)
            ) {
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    "Favorito", tint = if (isFavorite) Color.Red else Color(0xFFBDBDBD),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Botón rápido "+" de carrito (abajo derecha de la imagen)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable { onAddToCart() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, "Añadir", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Nombre ──
        Text(product.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)

        // ── Subtítulo ──
        Text(product.subtitle, fontSize = 13.sp, color = Color(0xFF666666), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))

        // ── Precio + precio original tachado ──
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
            Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            product.originalPrice?.let {
                Spacer(Modifier.width(6.dp))
                Text(it, fontSize = 12.sp, color = Color(0xFF9E9E9E), textDecoration = TextDecoration.LineThrough)
            }
        }
    }
}