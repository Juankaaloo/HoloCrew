package com.example.holocrew.ui.available

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.mockProducts
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf("Todos") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Intentar cargar productos de la API, fallback a mockProducts
    var apiProducts by remember { mutableStateOf<List<ProductDetail>?>(null) }
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getProducts()
            if (response.isSuccessful && response.body()?.success == true) {
                // Si la API devuelve datos, se usarían aquí
                // Por ahora usamos mockProducts ya que las imágenes son locales
            }
        } catch (e: Exception) { /* Usar mock */ }
    }

    val allProducts = mockProducts
    val categories = remember { listOf("Todos") + allProducts.map { it.category }.distinct() }
    val filteredProducts = remember(selectedFilter) {
        if (selectedFilter == "Todos") allProducts else allProducts.filter { it.category == selectedFilter }
    }
    val featuredProduct = filteredProducts.firstOrNull()
    val gridProducts = if (filteredProducts.size > 1) filteredProducts.drop(1) else emptyList()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = { navController?.navigate("search") { launchSingleTop = true } },
                onFavoritesClick = { navController?.navigate("favorites") { launchSingleTop = true } }
            )
        },
        bottomBar = { if (navController != null) BottomNavigationBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(bottom = 80.dp)) {

            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Text("Disponibles", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("${filteredProducts.size} productos", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))
                    Spacer(Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            CategoryFilterChip(text = category, isSelected = selectedFilter == category, onClick = { selectedFilter = category })
                        }
                    }
                }
            }

            if (featuredProduct != null) {
                item {
                    FeaturedProductCard(
                        product = featuredProduct,
                        onClick = { navController?.navigate("product_detail/${featuredProduct.id}") },
                        onAddToCart = {
                            scope.launch {
                                CartManager.addItem(context, featuredProduct.id)
                                snackbarHostState.showSnackbar("${featuredProduct.title} añadido al carrito")
                            }
                        },
                        onToggleFavorite = { scope.launch { FavoritesManager.toggleFavorite(context, featuredProduct.id) } }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            val rows = gridProducts.chunked(2)
            items(rows) { rowProducts ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowProducts.forEach { product ->
                        ProductGridCard(
                            product = product,
                            modifier = Modifier.weight(1f),
                            onClick = { navController?.navigate("product_detail/${product.id}") },
                            onAddToCart = {
                                scope.launch {
                                    CartManager.addItem(context, product.id)
                                    snackbarHostState.showSnackbar("${product.title} añadido al carrito")
                                }
                            },
                            onToggleFavorite = { scope.launch { FavoritesManager.toggleFavorite(context, product.id) } }
                        )
                    }
                    if (rowProducts.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryFilterChip(text: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(if (isSelected) Color.Black else Color(0xFFF0F0F0)).clickable { onClick() }.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(text, fontSize = 14.sp, color = if (isSelected) Color.White else Color(0xFF666666), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
fun FeaturedProductCard(product: ProductDetail, onClick: () -> Unit = {}, onAddToCart: () -> Unit = {}, onToggleFavorite: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(product.id)

    Box(modifier = Modifier.fillMaxWidth().height(380.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp)).clickable { onClick() }) {
        Image(painter = painterResource(id = product.imageRes), contentDescription = product.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
        Box(modifier = Modifier.padding(14.dp).align(Alignment.TopStart).clip(RoundedCornerShape(8.dp)).background(statusBadgeColor(product.status)).padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text(product.status.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onToggleFavorite, modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(36.dp)) {
            Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favorito", tint = if (isFavorite) Color.Red else Color.White, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(product.brand, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
            Text(product.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Text(product.price, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                product.originalPrice?.let { Spacer(Modifier.width(8.dp)); Text(it, fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f), textDecoration = TextDecoration.LineThrough) }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp).size(42.dp).clip(CircleShape).background(Color.White).clickable { onAddToCart() }, contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, "Añadir", tint = Color.Black, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun ProductGridCard(product: ProductDetail, modifier: Modifier = Modifier, onClick: () -> Unit = {}, onAddToCart: () -> Unit = {}, onToggleFavorite: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(product.id)

    Column(modifier = modifier.clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF5F5F5))) {
            Image(painter = painterResource(id = product.imageRes), contentDescription = product.title, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop)
            if (product.status.isNotEmpty()) {
                Box(modifier = Modifier.padding(8.dp).align(Alignment.TopStart).clip(RoundedCornerShape(6.dp)).background(statusBadgeColor(product.status)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(product.status.uppercase(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            IconButton(onClick = onToggleFavorite, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(32.dp)) {
                Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favorito", tint = if (isFavorite) Color.Red else Color(0xFFBDBDBD), modifier = Modifier.size(18.dp))
            }
            Box(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).size(32.dp).clip(CircleShape).background(Color.Black).clickable { onAddToCart() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Add, "Añadir", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(product.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.subtitle, fontSize = 13.sp, color = Color(0xFF666666), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
            Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            product.originalPrice?.let { Spacer(Modifier.width(6.dp)); Text(it, fontSize = 12.sp, color = Color(0xFF9E9E9E), textDecoration = TextDecoration.LineThrough) }
        }
    }
}

fun statusBadgeColor(status: String): Color = when (status) {
    "Nuevo" -> Color(0xFF4CAF50)
    "Exclusivo" -> Color(0xFF9C27B0)
    "Más vendido" -> Color(0xFFF44336)
    "En oferta" -> Color(0xFFFF9800)
    "Premium" -> Color(0xFFD4AF37)
    "Limitado" -> Color(0xFFE91E63)
    else -> Color(0xFF607D8B)
}