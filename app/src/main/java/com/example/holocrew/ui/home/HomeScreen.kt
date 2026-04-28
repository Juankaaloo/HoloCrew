package com.example.holocrew.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.R
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.components.CustomTopAppBar
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.mockProducts
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

// ✅ FIX: id cambiado de Int a String
data class FeedProduct(
    val id: String,
    val subtitle: String,
    val title: String,
    val imageRes: Int,
    val price: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    // ✅ FIX: IDs son ahora UUIDs reales de la BDD
    val feedProducts = remember {
        listOf(
            FeedProduct("2f275839-4230-11f1-a0bc-18c04d629171", "Holo Crew · Hoodie", "Pannel Hoodie\nBlack Edition", R.drawable.pannels_hoodie, "129.99€"),
            FeedProduct("2f306720-4230-11f1-a0bc-18c04d629171", "Holo Crew · Polo", "Glory Holo Polo\nWhite", R.drawable.glory_holo_polo, "199.99€"),
            FeedProduct("2f308ca4-4230-11f1-a0bc-18c04d629171", "Holo Crew · Edición Limitada", "Off-Road Racing Cap\nSolo 500 unidades", R.drawable.offroad_racing_cap, "299.99€"),
            FeedProduct("2f3089b3-4230-11f1-a0bc-18c04d629171", "Holo Crew · Accesorios", "Shoulder Bag\nBlack Leather", R.drawable.shoulder_bag_holo_blackleather, "249.99€")
        )
    }

    val exclusiveProducts = remember { mockProducts.filter { it.status == "Exclusivo" || it.status == "Premium" || it.status == "Limitado" } }
    val collectionProducts = remember { mockProducts.take(6) }
    val trendingProducts = remember { mockProducts.sortedByDescending { it.rating }.take(4) }
    val newProducts = remember { mockProducts.filter { it.status == "Nuevo" || it.status == "En oferta" } }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = { navController.navigate("search") { launchSingleTop = true } },
                onFavoritesClick = { navController.navigate("favorites") { launchSingleTop = true } }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        containerColor = Color.White
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            item {
                HeroBanner(
                    imageRes = R.drawable.footwear,
                    label = "NUEVO DROP",
                    title = "HOLOCREW\nFOOTWEAR",
                    description = "Exclusivo para miembros. Disponibilidad limitada.",
                    onShopClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    SectionHeader(title = "DROPS DESTACADOS", actionText = "Ver todo", onAction = {
                        navController.navigate("available") { launchSingleTop = true }
                    })
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(feedProducts) { product ->
                            PagerCard(product = product, onClick = {
                                navController.navigate("product_detail/${product.id}")
                            })
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
            item {
                DarkSection(
                    title = "EXCLUSIVOS HOLOCREW",
                    subtitle = "Piezas de edición limitada",
                    products = exclusiveProducts,
                    onProductClick = { navController.navigate("product_detail/${it.id}") },
                    onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            item {
                MainProductCard(product = feedProducts[0], onClick = {
                    navController.navigate("product_detail/${feedProducts[0].id}")
                })
            }

            item {
                GraySection(
                    title = "Última oportunidad,\n¡aprovéchala!",
                    products = collectionProducts.take(4),
                    onProductClick = { navController.navigate("product_detail/${it.id}") },
                    onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            item {
                MainProductCard(product = feedProducts[1], onClick = {
                    navController.navigate("product_detail/${feedProducts[1].id}")
                })
            }

            item {
                PromoBanner(onExploreClick = {
                    navController.navigate("upcoming") { launchSingleTop = true }
                })
            }

            item {
                Column(modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)) {
                    SectionHeader(title = "NUEVOS LANZAMIENTOS", actionText = "Ver todo", onAction = {
                        navController.navigate("available") { launchSingleTop = true }
                    })
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(newProducts) { product ->
                            PagerCard(
                                product = FeedProduct(product.id, product.brand, "${product.title}\n${product.subtitle}", product.imageRes, product.price),
                                onClick = { navController.navigate("product_detail/${product.id}") }
                            )
                        }
                    }
                }
            }

            item {
                TrendingSection(
                    products = trendingProducts,
                    onProductClick = { navController.navigate("product_detail/${it.id}") },
                    onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            item {
                FinalBanner(onClick = {
                    navController.navigate("available") { launchSingleTop = true }
                })
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun HeroBanner(imageRes: Int, label: String, title: String, description: String, onShopClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(460.dp)) {
        Image(painterResource(id = imageRes), "Banner", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(horizontal = 20.dp, vertical = 28.dp)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White, lineHeight = 38.sp)
            Spacer(Modifier.height(8.dp))
            Text(description, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(20.dp))
            Button(onClick = onShopClick, colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(50)) {
                Text("COMPRAR AHORA", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String = "", onAction: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 1.sp)
        if (actionText.isNotEmpty()) {
            Row(modifier = Modifier.clickable { onAction() }, verticalAlignment = Alignment.CenterVertically) {
                Text(actionText, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF666666))
                Icon(Icons.Filled.ArrowForward, null, tint = Color(0xFF666666), modifier = Modifier.size(16.dp).padding(start = 4.dp))
            }
        }
    }
}

@Composable
fun PagerCard(product: FeedProduct, onClick: () -> Unit = {}) {
    Box(modifier = Modifier.width(300.dp).height(380.dp).clip(RoundedCornerShape(16.dp)).clickable { onClick() }) {
        Image(painterResource(id = product.imageRes), product.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(product.subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
            Text(product.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 26.sp)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.price, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37))
                Spacer(Modifier.weight(1f))
                Icon(Icons.Outlined.Share, "Compartir", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun MainProductCard(product: FeedProduct, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onClick() }) {
        Text(product.subtitle, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9E9E9E))
        Text(product.title.replace("\n", " "), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(16.dp))) {
            Image(painterResource(id = product.imageRes), product.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(product.price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Comprar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF666666), modifier = Modifier.clickable { onClick() })
        }
    }
}

@Composable
fun DarkSection(title: String, subtitle: String, products: List<ProductDetail>, onProductClick: (ProductDetail) -> Unit, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.Black).padding(vertical = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Column {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                Text(subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(top = 4.dp))
            }
            Text("Ver todo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFD4AF37), modifier = Modifier.clickable { onSeeAllClick() })
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products) { product -> DarkProductCard(product = product, onClick = { onProductClick(product) }) }
        }
    }
}

@Composable
fun DarkProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.width(170.dp).clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A1A1A))) {
            Image(painterResource(id = product.imageRes), product.title, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            IconButton(
                onClick = { scope.launch { FavoritesManager.toggleFavorite(context, product.id) } },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(30.dp)
            ) {
                Icon(if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favorito", tint = if (isFav) Color.Red else Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun GraySection(title: String, products: List<ProductDetail>, onProductClick: (ProductDetail) -> Unit, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5)).padding(vertical = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 26.sp, modifier = Modifier.weight(1f))
            Text("Ver todo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF666666), modifier = Modifier.clickable { onSeeAllClick() }.padding(start = 16.dp, bottom = 4.dp))
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products) { product -> SmallProductCard(product = product, onClick = { onProductClick(product) }) }
        }
    }
}

@Composable
fun SmallProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.width(170.dp).clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(12.dp)).background(Color.White)) {
            Image(painterResource(id = product.imageRes), product.title, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            IconButton(
                onClick = { scope.launch { FavoritesManager.toggleFavorite(context, product.id) } },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(30.dp)
            ) {
                Icon(if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favorito", tint = if (isFav) Color.Red else Color(0xFFBDBDBD), modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.subtitle, fontSize = 12.sp, color = Color(0xFF666666), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun PromoBanner(onExploreClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(16.dp)).background(Color.Black).padding(24.dp)) {
        Column {
            Text("PRÓXIMOS DROPS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text("No te pierdas\nlos lanzamientos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 28.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onExploreClick, shape = RoundedCornerShape(50), border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFD4AF37))))) {
                Text("Explorar", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TrendingSection(products: List<ProductDetail>, onProductClick: (ProductDetail) -> Unit, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("EN TENDENCIA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 1.sp)
            Text("Ver todo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF666666), modifier = Modifier.clickable { onSeeAllClick() })
        }
        Spacer(Modifier.height(14.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(products) { product -> TrendingCard(product = product, onClick = { onProductClick(product) }) }
        }
    }
}

@Composable
fun TrendingCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(modifier = Modifier.width(220.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                Image(painterResource(id = product.imageRes), product.title, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.padding(10.dp).align(Alignment.TopStart).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.7f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("${product.rating}", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(
                    onClick = { scope.launch { FavoritesManager.toggleFavorite(context, product.id) } },
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(32.dp)
                ) {
                    Icon(if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favorito", tint = if (isFav) Color.Red else Color.White.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(product.brand, fontSize = 11.sp, color = Color(0xFF9E9E9E), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Text(product.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.subtitle, fontSize = 12.sp, color = Color(0xFF666666), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(product.price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("${product.reviewCount} reseñas", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                }
            }
        }
    }
}

@Composable
fun FinalBanner(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(16.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF1A1A1A), Color(0xFF333333)))).clickable { onClick() }.padding(24.dp)) {
        Column {
            Text("HOLOCREW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text("Descubre toda\nla colección", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 30.sp)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ver catálogo", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}
