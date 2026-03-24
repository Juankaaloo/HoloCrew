/**
 * HomeScreen.kt
 *
 * Pantalla principal de la aplicación HoloCrew.
 * Feed vertical con diseño premium inspirado en SNKRS:
 *
 *  1. Hero Banner: imagen a pantalla completa con gradiente y CTA
 *  2. Pager horizontal: cards de drops a ancho completo con scroll lateral
 *  3. Sección negra "Exclusivos": fondo negro con carrusel de productos
 *  4. Cards SNKRS estándar: subtítulo + título + imagen + Notifícame
 *  5. Sección gris "Colección": fondo gris con carrusel
 *  6. Más cards y secciones intercaladas
 *  7. Sección "En Tendencia": cards grandes con rating y marca
 *  8. Banner final promocional
 *
 * Fondo blanco con secciones negras y grises intercaladas para contraste.
 * Sin emojis. Todo con datos mock estáticos.
 */
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

// ══════════════════════════════════════════════════════════════════════════════
// MODELO DE DATOS LOCAL
// ══════════════════════════════════════════════════════════════════════════════

/** Modelo para los drops destacados del feed */
data class FeedProduct(
    val id: Int,
    val subtitle: String,
    val title: String,
    val imageRes: Int,
    val price: String
)

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA PRINCIPAL
// ══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    // ── Datos mock ──
    val feedProducts = remember {
        listOf(
            FeedProduct(1, "Holo Crew · Hoodie", "Pannel Hoodie\nBlack Edition", R.drawable.pannels_hoodie, "$129.99"),
            FeedProduct(4, "Holo Crew · Polo", "Glory Holo Polo\nWhite", R.drawable.glory_holo_polo, "$199.99"),
            FeedProduct(6, "Holo Crew · Edición Limitada", "Off-Road Racing Cap\nSolo 500 unidades", R.drawable.offroad_racing_cap, "$299.99"),
            FeedProduct(5, "Holo Crew · Accesorios", "Shoulder Bag\nBlack Leather", R.drawable.shoulder_bag_holo_blackleather, "$249.99")
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

            // ═════════════════════════════════════════════════════════
            // 1. HERO BANNER — Imagen a pantalla completa con gradiente
            // ═════════════════════════════════════════════════════════
            item {
                HeroBanner(
                    imageRes = R.drawable.footwear,
                    label = "NUEVO DROP",
                    title = "HOLOCREW\nFOOTWEAR",
                    description = "Exclusivo para miembros. Disponibilidad limitada.",
                    onShopClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            // ═════════════════════════════════════════════════════════
            // 2. PAGER HORIZONTAL — Drops a ancho completo
            // ═════════════════════════════════════════════════════════
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

            // ═════════════════════════════════════════════════════════
            // 3. SECCIÓN NEGRA — "Exclusivos HoloCrew"
            // ═════════════════════════════════════════════════════════
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

            // ═════════════════════════════════════════════════════════
            // 4. CARD SNKRS ESTÁNDAR
            // ═════════════════════════════════════════════════════════
            item {
                MainProductCard(product = feedProducts[0], onClick = {
                    navController.navigate("product_detail/${feedProducts[0].id}")
                })
            }

            // ═════════════════════════════════════════════════════════
            // 5. SECCIÓN GRIS — "Última oportunidad"
            // ═════════════════════════════════════════════════════════
            item {
                GraySection(
                    title = "Última oportunidad,\n¡aprovéchala!",
                    products = collectionProducts.take(4),
                    onProductClick = { navController.navigate("product_detail/${it.id}") },
                    onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            // ═════════════════════════════════════════════════════════
            // 6. CARD SNKRS ESTÁNDAR 2
            // ═════════════════════════════════════════════════════════
            item {
                MainProductCard(product = feedProducts[1], onClick = {
                    navController.navigate("product_detail/${feedProducts[1].id}")
                })
            }

            // ═════════════════════════════════════════════════════════
            // 7. BANNER PROMOCIONAL NEGRO
            // ═════════════════════════════════════════════════════════
            item {
                PromoBanner(onExploreClick = {
                    navController.navigate("upcoming") { launchSingleTop = true }
                })
            }

            // ═════════════════════════════════════════════════════════
            // 8. SECCIÓN "NUEVOS LANZAMIENTOS"
            // ═════════════════════════════════════════════════════════
            item {
                SectionHeader(title = "NUEVOS LANZAMIENTOS", actionText = "Ver todo", onAction = {
                    navController.navigate("available") { launchSingleTop = true }
                })
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(collectionProducts.takeLast(4)) { product ->
                        SmallProductCard(product = product, onClick = {
                            navController.navigate("product_detail/${product.id}")
                        })
                    }
                }
            }

            // ═════════════════════════════════════════════════════════
            // 9. CARD SNKRS ESTÁNDAR 3
            // ═════════════════════════════════════════════════════════
            item {
                MainProductCard(product = feedProducts[2], onClick = {
                    navController.navigate("product_detail/${feedProducts[2].id}")
                })
            }

            // ═════════════════════════════════════════════════════════
            // 10. EN TENDENCIA — Cards grandes con rating
            // ═════════════════════════════════════════════════════════
            item {
                TrendingSection(
                    products = trendingProducts,
                    onProductClick = { navController.navigate("product_detail/${it.id}") }
                )
            }

            // ═════════════════════════════════════════════════════════
            // 11. CARD SNKRS ESTÁNDAR 4
            // ═════════════════════════════════════════════════════════
            item {
                MainProductCard(product = feedProducts[3], onClick = {
                    navController.navigate("product_detail/${feedProducts[3].id}")
                })
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

/** Header de sección reutilizable: título + "Ver todo" */
@Composable
fun SectionHeader(title: String, actionText: String? = null, onAction: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black, letterSpacing = 0.5.sp)
        if (actionText != null) {
            Text(
                text = actionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}

/**
 * Hero Banner — Imagen a pantalla completa con gradiente oscuro y CTA.
 */
@Composable
fun HeroBanner(imageRes: Int, label: String, title: String, description: String, onShopClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(520.dp)) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Gradiente
        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp).align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))))
        )
        // Contenido
        Column(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 28.dp)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 3.sp)
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White, lineHeight = 40.sp, letterSpacing = (-1).sp)
            Spacer(Modifier.height(8.dp))
            Text(description, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onShopClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.height(48.dp)
            ) {
                Text("COMPRAR AHORA", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
        }
    }
}

/**
 * Pager Card — Card grande horizontal para el slider de drops.
 * Ocupa casi todo el ancho, con imagen a pantalla completa, gradiente y datos.
 */
@Composable
fun PagerCard(product: FeedProduct, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.width(320.dp).height(400.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
            // Info
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Text(product.subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                Spacer(Modifier.height(4.dp))
                Text(product.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 26.sp)
                Spacer(Modifier.height(8.dp))
                Text(product.price, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

/**
 * Sección con fondo negro — Muestra productos exclusivos con estilo premium.
 */
@Composable
fun DarkSection(title: String, subtitle: String, products: List<ProductDetail>, onProductClick: (ProductDetail) -> Unit, onSeeAllClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color.Black).padding(vertical = 28.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 0.5.sp)
                Text(subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))
            }
            Text("Ver todo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFD4AF37), modifier = Modifier.clickable { onSeeAllClick() })
        }

        Spacer(Modifier.height(16.dp))

        // Carrusel
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                DarkProductCard(product = product, onClick = { onProductClick(product) })
            }
        }
    }
}

/** Card de producto sobre fondo negro */
@Composable
fun DarkProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)

    Column(modifier = Modifier.width(170.dp).clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A1A1A))) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { FavoritesManager.toggleFavorite(product.id) },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(30.dp)
            ) {
                Icon(
                    if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    "Favorito",
                    tint = if (isFav) Color.Red else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), modifier = Modifier.padding(top = 4.dp))
    }
}

/**
 * Sección con fondo gris claro — Para colecciones y agrupaciones.
 */
@Composable
fun GraySection(title: String, products: List<ProductDetail>, onProductClick: (ProductDetail) -> Unit, onSeeAllClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5)).padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 26.sp, modifier = Modifier.weight(1f))
            Text("Ver todo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF666666), modifier = Modifier.clickable { onSeeAllClick() }.padding(start = 16.dp, bottom = 4.dp))
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                SmallProductCard(product = product, onClick = { onProductClick(product) })
            }
        }
    }
}

/** Card de producto estándar para carruseles */
@Composable
fun SmallProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)

    Column(modifier = Modifier.width(170.dp).clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(12.dp)).background(Color.White)) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { FavoritesManager.toggleFavorite(product.id) },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(30.dp)
            ) {
                Icon(
                    if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    "Favorito",
                    tint = if (isFav) Color.Red else Color(0xFFBDBDBD),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.subtitle, fontSize = 12.sp, color = Color(0xFF666666), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
    }
}

/** Card SNKRS estándar: subtítulo + título + imagen + compartir/notifícame */
@Composable
fun MainProductCard(product: FeedProduct, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(product.subtitle, fontSize = 14.sp, color = Color(0xFF666666))
            Spacer(Modifier.height(4.dp))
            Text(product.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 30.sp)
            Spacer(Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Share, "Compartir", tint = Color.Black, modifier = Modifier.size(22.dp))
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text("Notifícame", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}

/** Banner promocional negro con acento dorado */
@Composable
fun PromoBanner(onExploreClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp)).background(Color.Black).padding(28.dp)
    ) {
        Column {
            Text("PROXIMAMENTE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 3.sp)
            Spacer(Modifier.height(12.dp))
            Text("Winter\nCollection 2025", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White, lineHeight = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text("La colección más esperada del año.\nReserva tu lugar.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f), lineHeight = 20.sp)
            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                onClick = onExploreClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color.White, Color.White)))
            ) {
                Text("EXPLORAR DROPS", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, null, modifier = Modifier.size(14.dp))
            }
        }
    }
}

/** Sección En Tendencia con cards grandes y rating */
@Composable
fun TrendingSection(products: List<ProductDetail>, onProductClick: (ProductDetail) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {
        SectionHeader(title = "EN TENDENCIA")
        Spacer(Modifier.height(14.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(products) { product -> TrendingCard(product = product, onClick = { onProductClick(product) }) }
        }
    }
}

/** Card de tendencia: grande, con rating, marca y reseñas */
@Composable
fun TrendingCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)

    Card(
        modifier = Modifier.width(220.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                // Badge rating
                Box(
                    modifier = Modifier.padding(10.dp).align(Alignment.TopStart).clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${product.rating}", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(
                    onClick = { FavoritesManager.toggleFavorite(product.id) },
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(32.dp)
                ) {
                    Icon(
                        if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        "Favorito", tint = if (isFav) Color.Red else Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
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