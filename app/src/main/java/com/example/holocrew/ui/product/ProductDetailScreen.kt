package com.example.holocrew.ui.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, productId: String) {  // ✅ FIX: Int → String
    val product = remember(productId) { mockProducts.find { it.id == productId } }  // ✅ FIX: comparación directa
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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(product.id)  // ✅ FIX: ya es String, no necesita .toString()
    val snackbarHostState = remember { SnackbarHostState() }

    var showSizeSheet by remember { mutableStateOf(false) }
    var sizeSheetAction by remember { mutableStateOf("cart") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val relatedProducts = remember { mockProducts.filter { it.category == product.category && it.id != product.id }.take(4) }
    val sizeRange = if (product.sizes.size > 1) "${product.sizes.first()} – ${product.sizes.last()}" else product.sizes.firstOrNull() ?: ""

    if (showSizeSheet && product.sizes.isNotEmpty()) {
        ModalBottomSheet(onDismissRequest = { showSizeSheet = false }, sheetState = sheetState, containerColor = Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
            SizeSelectionSheet(sizes = product.sizes, onSizeSelected = { selectedSize ->
                showSizeSheet = false
                scope.launch {
                    CartManager.addItem(context, product.id)  // ✅ FIX: ya es String
                    if (sizeSheetAction == "buy") {
                        navController.navigate("cart") { launchSingleTop = true }
                    } else {
                        snackbarHostState.showSnackbar("${product.title} ($selectedSize) añadido al carrito")
                    }
                }
            })
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.Black) } },
                actions = {
                    IconButton(onClick = { scope.launch { FavoritesManager.toggleFavorite(context, product.id) } }) {  // ✅ FIX
                        Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favorito", tint = if (isFavorite) Color.Red else Color.Black)
                    }
                    IconButton(onClick = {}) { Icon(Icons.Filled.Share, "Compartir", tint = Color.Black) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {

            item {
                Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                    Image(painterResource(id = product.imageRes), product.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(Color.Transparent, Color.White))))
                    if (product.originalPrice != null) {
                        Box(modifier = Modifier.padding(16.dp).align(Alignment.TopStart).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE53935)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text("OFERTA", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(product.brand, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column(Modifier.weight(1f)) {
                            Text(product.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(product.subtitle, fontSize = 15.sp, color = Color(0xFF666666), modifier = Modifier.padding(top = 2.dp))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(product.price, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            product.originalPrice?.let { Text(it, fontSize = 14.sp, color = Color(0xFF9E9E9E), textDecoration = TextDecoration.LineThrough) }
                        }
                    }

                    if (sizeRange.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Text(sizeRange, fontSize = 15.sp, color = Color(0xFF666666)) }

                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index -> Icon(Icons.Filled.Star, "Estrella", tint = if (index < product.rating.toInt()) Color(0xFFFFC107) else Color(0xFFE0E0E0), modifier = Modifier.size(18.dp)) }
                        Spacer(Modifier.width(8.dp))
                        Text("${product.rating}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Text("(${product.reviewCount} reseñas)", fontSize = 14.sp, color = Color(0xFF9E9E9E))
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (product.sizes.isNotEmpty()) { sizeSheetAction = "buy"; showSizeSheet = true }
                            else { scope.launch { CartManager.addItem(context, product.id); navController.navigate("cart") { launchSingleTop = true } } }  // ✅ FIX
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) { Text("Comprar ahora", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White) }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            if (product.sizes.isNotEmpty()) { sizeSheetAction = "cart"; showSizeSheet = true }
                            else { scope.launch { CartManager.addItem(context, product.id); snackbarHostState.showSnackbar("${product.title} añadido al carrito") } }  // ✅ FIX
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(50),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color(0xFFCCCCCC), Color(0xFFCCCCCC))))
                    ) { Text("Añadir a la cesta", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black) }

                    Spacer(Modifier.height(24.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(24.dp))

                    ShippingInfoCard()

                    Spacer(Modifier.height(24.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(24.dp))

                    Text(product.description, fontSize = 15.sp, color = Color(0xFF666666), lineHeight = 24.sp)
                    Spacer(Modifier.height(20.dp))

                    product.features.forEach { feature ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 10.dp)) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(Color.Black))
                            Spacer(Modifier.width(12.dp))
                            Text(feature, fontSize = 15.sp, color = Color(0xFF444444))
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }

            if (relatedProducts.isNotEmpty()) {
                item {
                    Text("TAMBIÉN TE PUEDE GUSTAR", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(14.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(relatedProducts) { related ->
                            RelatedProductCard(product = related, onClick = { navController.navigate("product_detail/${related.id}") { launchSingleTop = true } })
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun SizeSelectionSheet(sizes: List<String>, onSizeSelected: (String) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("Selecciona una talla", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(Modifier.height(20.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height((((sizes.size + 2) / 3) * 56).dp)) {
            items(sizes) { size ->
                val isSelected = sizes.indexOf(size) == selectedIndex
                Box(
                    modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(10.dp))
                        .then(if (isSelected) Modifier.background(Color.Black) else Modifier.border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp)).background(Color.White))
                        .clickable { selectedIndex = sizes.indexOf(size) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(size, fontSize = 15.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color.White else Color.Black)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { if (selectedIndex >= 0) onSizeSelected(sizes[selectedIndex]) },
            modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = if (selectedIndex >= 0) Color.Black else Color(0xFFE0E0E0)),
            enabled = selectedIndex >= 0
        ) { Text("Añadir a la cesta", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = if (selectedIndex >= 0) Color.White else Color(0xFF9E9E9E)) }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun ShippingInfoCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), elevation = CardDefaults.cardElevation(0.dp)) {
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

@Composable
fun RelatedProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.width(150.dp).clickable { onClick() }) {
        Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5))) {
            Image(painterResource(id = product.imageRes), product.title, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
        }
        Spacer(Modifier.height(8.dp))
        Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.price, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
    }
}
