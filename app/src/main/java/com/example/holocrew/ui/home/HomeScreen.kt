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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.R
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.components.CustomTopAppBar
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.mockProducts
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════════════════════
// MODELO — Producto del feed principal (hero carousel y main cards)
// Usa UUIDs reales de la BDD MySQL para navegación directa al detalle.
// ══════════════════════════════════════════════════════════════════════════════
data class FeedProduct(
    val id: String,         // UUID real de la tabla products
    val subtitle: String,   // Línea descriptiva ("Holo Crew · Hoodie")
    val title: String,      // Nombre del producto (puede tener \n)
    val imageRes: Int,      // R.drawable.xxx — local hasta migrar a Supabase Storage
    val price: String       // Precio formateado con € ("129.99€")
)

/**
 * HomeScreen — Pantalla principal / escaparate de la app.
 *
 * Feed vertical estilo SNKRS con secciones intercaladas:
 *  1. Hero banner a pantalla completa con gradiente oscuro
 *  2. Carrusel horizontal "Drops Destacados"
 *  3. Sección negra "Exclusivos" con cards oscuras
 *  4. Card de producto destacado (grande, con imagen)
 *  5. Sección gris "Última oportunidad"
 *  6. Card de producto destacado #2
 *  7. Banner promo "Próximos drops"
 *  8. Carrusel "Nuevos lanzamientos"
 *  9. Sección "En tendencia" con cards de reseñas
 *  10. Banner final "Descubre toda la colección"
 *
 * Paleta: negro/blanco/rojo (Pulse #FF0033 sustituye al dorado anterior).
 *
 * @param navController Controlador de navegación para ir a detalle, available, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    // ── Datos del feed principal (UUIDs reales de MySQL) ──────────────────────
    val feedProducts = remember {
        listOf(
            FeedProduct("2f275839-4230-11f1-a0bc-18c04d629171", "Holo Crew · Hoodie", "Pannel Hoodie\nBlack Edition", R.drawable.pannels_hoodie, "129.99€"),
            FeedProduct("2f306720-4230-11f1-a0bc-18c04d629171", "Holo Crew · Polo", "Glory Holo Polo\nWhite", R.drawable.glory_holo_polo, "199.99€"),
            FeedProduct("2f308ca4-4230-11f1-a0bc-18c04d629171", "Holo Crew · Edición Limitada", "Off-Road Racing Cap\nSolo 500 unidades", R.drawable.offroad_racing_cap, "299.99€"),
            FeedProduct("2f3089b3-4230-11f1-a0bc-18c04d629171", "Holo Crew · Accesorios", "Shoulder Bag\nBlack Leather", R.drawable.shoulder_bag_holo_blackleather, "249.99€")
        )
    }

    // ── Filtros de productos mock para cada sección ───────────────────────────
    val exclusiveProducts = remember {
        mockProducts.filter { it.status == "Exclusivo" || it.status == "Premium" || it.status == "Limitado" }
    }
    val collectionProducts = remember { mockProducts.take(6) }
    val trendingProducts = remember { mockProducts.sortedByDescending { it.rating }.take(4) }
    val newProducts = remember { mockProducts.filter { it.status == "Nuevo" || it.status == "En oferta" } }

    // ── Scaffold con topBar custom + bottomNav flotante ───────────────────────
    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = { navController.navigate("search") { launchSingleTop = true } },
                onFavoritesClick = { navController.navigate("favorites") { launchSingleTop = true } }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        containerColor = HoloColors.Paper
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.BottomNavHeight)
        ) {

            // ══════════════════════════════════════════════════════════════════
            // 1. HERO BANNER — imagen fullwidth con gradiente y CTA
            // ══════════════════════════════════════════════════════════════════
            item {
                HeroBanner(
                    imageRes = R.drawable.footwear,
                    label = "NUEVO DROP",
                    title = "HOLOCREW\nFOOTWEAR",
                    description = "Exclusivo para miembros. Disponibilidad limitada.",
                    onShopClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            // ══════════════════════════════════════════════════════════════════
            // 2. DROPS DESTACADOS — carrusel horizontal de PagerCards
            // ══════════════════════════════════════════════════════════════════
            item {
                Column(modifier = Modifier.padding(top = HoloSpacing.xl)) {
                    SectionHeader(
                        title = "DROPS DESTACADOS",
                        actionText = "Ver todo",
                        onAction = { navController.navigate("available") { launchSingleTop = true } }
                    )
                    Spacer(Modifier.height(HoloSpacing.sm))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = HoloSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
                    ) {
                        items(feedProducts) { product ->
                            PagerCard(
                                product = product,
                                onClick = { navController.navigate("product_detail/${product.id}") }
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.xl)) }

            // ══════════════════════════════════════════════════════════════════
            // 3. EXCLUSIVOS — sección con fondo negro + cards oscuras
            // ══════════════════════════════════════════════════════════════════
            item {
                DarkSection(
                    title = "EXCLUSIVOS HOLOCREW",
                    subtitle = "Piezas de edición limitada",
                    products = exclusiveProducts,
                    onProductClick = { navController.navigate("product_detail/${it.id}") },
                    onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            // ══════════════════════════════════════════════════════════════════
            // 4. CARD DESTACADA #1 — producto grande con imagen
            // ══════════════════════════════════════════════════════════════════
            item {
                MainProductCard(
                    product = feedProducts[0],
                    onClick = { navController.navigate("product_detail/${feedProducts[0].id}") }
                )
            }

            // ══════════════════════════════════════════════════════════════════
            // 5. ÚLTIMA OPORTUNIDAD — sección con fondo gris claro
            // ══════════════════════════════════════════════════════════════════
            item {
                GraySection(
                    title = "Última oportunidad,\n¡aprovéchala!",
                    products = collectionProducts.take(4),
                    onProductClick = { navController.navigate("product_detail/${it.id}") },
                    onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            // ══════════════════════════════════════════════════════════════════
            // 6. CARD DESTACADA #2
            // ══════════════════════════════════════════════════════════════════
            item {
                MainProductCard(
                    product = feedProducts[1],
                    onClick = { navController.navigate("product_detail/${feedProducts[1].id}") }
                )
            }

            // ══════════════════════════════════════════════════════════════════
            // 7. PROMO BANNER — bloque negro con CTA a Upcoming
            // ══════════════════════════════════════════════════════════════════
            item {
                PromoBanner(
                    onExploreClick = { navController.navigate("upcoming") { launchSingleTop = true } }
                )
            }

            // ══════════════════════════════════════════════════════════════════
            // 8. NUEVOS LANZAMIENTOS — carrusel horizontal
            // ══════════════════════════════════════════════════════════════════
            item {
                Column(modifier = Modifier.padding(vertical = HoloSpacing.xl)) {
                    SectionHeader(
                        title = "NUEVOS LANZAMIENTOS",
                        actionText = "Ver todo",
                        onAction = { navController.navigate("available") { launchSingleTop = true } }
                    )
                    Spacer(Modifier.height(HoloSpacing.sm))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = HoloSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
                    ) {
                        items(newProducts) { product ->
                            PagerCard(
                                product = FeedProduct(
                                    product.id,
                                    product.brand,
                                    "${product.title}\n${product.subtitle}",
                                    product.imageRes,
                                    product.price
                                ),
                                onClick = { navController.navigate("product_detail/${product.id}") }
                            )
                        }
                    }
                }
            }

            // ══════════════════════════════════════════════════════════════════
            // 9. EN TENDENCIA — cards con rating y reseñas
            // ══════════════════════════════════════════════════════════════════
            item {
                TrendingSection(
                    products = trendingProducts,
                    onProductClick = { navController.navigate("product_detail/${it.id}") },
                    onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            // ══════════════════════════════════════════════════════════════════
            // 10. BANNER FINAL — CTA al catálogo completo
            // ══════════════════════════════════════════════════════════════════
            item {
                FinalBanner(
                    onClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            item { Spacer(Modifier.height(HoloSpacing.xl)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES DEL HOME
// Cada composable es un bloque visual independiente del feed.
// ══════════════════════════════════════════════════════════════════════════════

/**
 * HeroBanner — Imagen a pantalla completa con gradiente oscuro y CTA.
 *
 * Estilo SNKRS: imagen de fondo, gradiente negro desde abajo,
 * label rojo (Pulse), título blanco bold, botón blanco pill.
 */
@Composable
fun HeroBanner(
    imageRes: Int,
    label: String,
    title: String,
    description: String,
    onShopClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(460.dp)
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradiente oscuro desde abajo para leer el texto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            HoloColors.Ink.copy(alpha = 0f),
                            HoloColors.InkOverlay80
                        )
                    )
                )
        )

        // Contenido superpuesto (label, título, descripción, botón)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xxl)
        ) {
            // Label rojo — "NUEVO DROP" en uppercase con tracking
            Text(
                text = label,
                style = HoloType.LabelMedium,
                color = HoloColors.Pulse
            )

            Spacer(Modifier.height(HoloSpacing.xs))

            // Título grande blanco
            Text(
                text = title,
                style = HoloType.DisplayMedium,
                color = HoloColors.TextOnDark
            )

            Spacer(Modifier.height(HoloSpacing.xs))

            // Descripción
            Text(
                text = description,
                style = HoloType.BodyMedium,
                color = HoloColors.TextOnDarkMuted
            )

            Spacer(Modifier.height(HoloSpacing.lg))

            // Botón CTA blanco con texto negro
            Button(
                onClick = onShopClick,
                colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Paper),
                shape = RoundedCornerShape(HoloSpacing.RadiusPill)
            ) {
                Text(
                    text = "COMPRAR AHORA",
                    style = HoloType.LabelLarge,
                    color = HoloColors.Ink,
                    modifier = Modifier.padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.xxs)
                )
            }
        }
    }
}

/**
 * SectionHeader — Título de sección con acción "Ver todo" a la derecha.
 *
 * Estilo SNKRS: label uppercase negro a la izquierda, texto gris + flecha
 * a la derecha como acción secundaria.
 */
@Composable
fun SectionHeader(
    title: String,
    actionText: String = "",
    onAction: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HoloSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = HoloType.LabelLarge,
            color = HoloColors.TextPrimary
        )

        if (actionText.isNotEmpty()) {
            Row(
                modifier = Modifier.clickable { onAction() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = actionText,
                    style = HoloType.TitleSmall,
                    color = HoloColors.TextSecondary
                )
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = HoloColors.TextSecondary,
                    modifier = Modifier
                        .size(HoloSpacing.IconSizeSmall)
                        .padding(start = HoloSpacing.xxs)
                )
            }
        }
    }
}

/**
 * PagerCard — Card grande horizontal para carruseles de drops.
 *
 * 300x380dp con imagen de fondo, gradiente oscuro inferior,
 * subtítulo + título + precio en rojo Pulse + icono compartir.
 */
@Composable
fun PagerCard(product: FeedProduct, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(380.dp)
            .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
            .clickable { onClick() }
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradiente oscuro desde abajo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(HoloColors.Ink.copy(alpha = 0f), HoloColors.InkOverlay80)
                    )
                )
        )

        // Info superpuesta abajo
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(HoloSpacing.md)
        ) {
            Text(
                text = product.subtitle,
                style = HoloType.BodySmall,
                color = HoloColors.TextOnDarkMuted
            )
            Text(
                text = product.title,
                style = HoloType.HeadlineMedium,
                color = HoloColors.TextOnDark
            )

            Spacer(Modifier.height(HoloSpacing.xs))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Precio en rojo Pulse (antes era dorado)
                Text(
                    text = product.price,
                    style = HoloType.HeadlineSmall,
                    color = HoloColors.Pulse
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Compartir",
                    tint = HoloColors.TextOnDarkMuted,
                    modifier = Modifier.size(HoloSpacing.IconSizeDefault)
                )
            }
        }
    }
}

/**
 * MainProductCard — Card de producto destacado a ancho completo.
 *
 * Muestra subtítulo, título, imagen grande (300dp), precio y enlace "Comprar".
 * Se usa entre secciones para romper el ritmo del scroll.
 */
@Composable
fun MainProductCard(product: FeedProduct, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HoloSpacing.md)
            .clickable { onClick() }
    ) {
        // Subtítulo gris
        Text(
            text = product.subtitle,
            style = HoloType.TitleSmall,
            color = HoloColors.TextTertiary
        )

        // Título en negro
        Text(
            text = product.title.replace("\n", " "),
            style = HoloType.HeadlineMedium,
            color = HoloColors.TextPrimary,
            modifier = Modifier.padding(top = HoloSpacing.xxs)
        )

        Spacer(Modifier.height(HoloSpacing.sm))

        // Imagen del producto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(HoloSpacing.sm))

        // Precio + enlace "Comprar"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.price,
                style = HoloType.TitleLarge,
                color = HoloColors.TextPrimary
            )
            Text(
                text = "Comprar",
                style = HoloType.TitleMedium,
                color = HoloColors.TextSecondary,
                modifier = Modifier.clickable { onClick() }
            )
        }
    }
}

/**
 * DarkSection — Sección con fondo negro para productos exclusivos.
 *
 * Header con título blanco + "Ver todo" en rojo Pulse.
 * Carrusel horizontal de DarkProductCards.
 */
@Composable
fun DarkSection(
    title: String,
    subtitle: String,
    products: List<ProductDetail>,
    onProductClick: (ProductDetail) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HoloColors.Ink)
            .padding(vertical = HoloSpacing.xl)
    ) {
        // Header de la sección
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HoloSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = title,
                    style = HoloType.HeadlineSmall,
                    color = HoloColors.TextOnDark
                )
                Text(
                    text = subtitle,
                    style = HoloType.BodySmall,
                    color = HoloColors.TextOnDarkMuted,
                    modifier = Modifier.padding(top = HoloSpacing.xxs)
                )
            }
            // "Ver todo" en rojo Pulse (antes dorado)
            Text(
                text = "Ver todo",
                style = HoloType.TitleSmall,
                color = HoloColors.Pulse,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }

        Spacer(Modifier.height(HoloSpacing.md))

        // Carrusel horizontal de cards oscuras
        LazyRow(
            contentPadding = PaddingValues(horizontal = HoloSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
        ) {
            items(products) { product ->
                DarkProductCard(product = product, onClick = { onProductClick(product) })
            }
        }
    }
}

/**
 * DarkProductCard — Card de producto sobre fondo negro.
 *
 * Imagen con botón de favorito, título blanco, precio en rojo Pulse.
 */
@Composable
fun DarkProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() }
    ) {
        // Imagen con botón favorito superpuesto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                .background(HoloColors.Neutral800)
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(HoloSpacing.RadiusMd)),
                contentScale = ContentScale.Crop
            )

            // Botón favorito (corazón)
            IconButton(
                onClick = { scope.launch { FavoritesManager.toggleFavorite(context, product.id) } },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(HoloSpacing.xxs)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFav) HoloColors.Pulse else HoloColors.TextOnDarkMuted,
                    modifier = Modifier.size(HoloSpacing.IconSizeSmall)
                )
            }
        }

        Spacer(Modifier.height(HoloSpacing.xs))

        // Título blanco
        Text(
            text = product.title,
            style = HoloType.TitleSmall,
            color = HoloColors.TextOnDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Precio en rojo Pulse
        Text(
            text = product.price,
            style = HoloType.TitleMedium,
            color = HoloColors.Pulse,
            modifier = Modifier.padding(top = HoloSpacing.xxs)
        )
    }
}

/**
 * GraySection — Sección con fondo gris claro (Fog) para productos en oferta.
 *
 * Título grande negro, "Ver todo" a la derecha, carrusel de SmallProductCards.
 */
@Composable
fun GraySection(
    title: String,
    products: List<ProductDetail>,
    onProductClick: (ProductDetail) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HoloColors.Fog)
            .padding(vertical = HoloSpacing.xl)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HoloSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = title,
                style = HoloType.HeadlineMedium,
                color = HoloColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Ver todo",
                style = HoloType.TitleSmall,
                color = HoloColors.TextSecondary,
                modifier = Modifier
                    .clickable { onSeeAllClick() }
                    .padding(start = HoloSpacing.md, bottom = HoloSpacing.xxs)
            )
        }

        Spacer(Modifier.height(HoloSpacing.md))

        LazyRow(
            contentPadding = PaddingValues(horizontal = HoloSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
        ) {
            items(products) { product ->
                SmallProductCard(product = product, onClick = { onProductClick(product) })
            }
        }
    }
}

/**
 * SmallProductCard — Card compacta para carruseles sobre fondo claro.
 *
 * Imagen con favorito, título negro, subtítulo gris, precio negro.
 */
@Composable
fun SmallProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                .background(HoloColors.Paper)
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(HoloSpacing.RadiusMd)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { scope.launch { FavoritesManager.toggleFavorite(context, product.id) } },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(HoloSpacing.xxs)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFav) HoloColors.Pulse else HoloColors.Neutral300,
                    modifier = Modifier.size(HoloSpacing.IconSizeSmall)
                )
            }
        }

        Spacer(Modifier.height(HoloSpacing.xs))

        Text(
            text = product.title,
            style = HoloType.TitleSmall,
            color = HoloColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = product.subtitle,
            style = HoloType.BodySmall,
            color = HoloColors.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = product.price,
            style = HoloType.TitleMedium,
            color = HoloColors.TextPrimary,
            modifier = Modifier.padding(top = HoloSpacing.xxs)
        )
    }
}

/**
 * PromoBanner — Bloque negro con CTA a la pantalla Upcoming.
 *
 * Label rojo "PRÓXIMOS DROPS", título blanco, botón outlined rojo.
 */
@Composable
fun PromoBanner(onExploreClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HoloSpacing.md)
            .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
            .background(HoloColors.Ink)
            .padding(HoloSpacing.xl)
    ) {
        Column {
            // Label rojo — "PRÓXIMOS DROPS"
            Text(
                text = "PRÓXIMOS DROPS",
                style = HoloType.LabelMedium,
                color = HoloColors.Pulse
            )

            Spacer(Modifier.height(HoloSpacing.xs))

            Text(
                text = "No te pierdas\nlos lanzamientos",
                style = HoloType.HeadlineLarge,
                color = HoloColors.TextOnDark
            )

            Spacer(Modifier.height(HoloSpacing.md))

            // Botón outlined con borde rojo Pulse
            OutlinedButton(
                onClick = onExploreClick,
                shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(HoloColors.Pulse, HoloColors.Pulse))
                )
            ) {
                Text(
                    text = "EXPLORAR",
                    style = HoloType.LabelLarge,
                    color = HoloColors.Pulse
                )
            }
        }
    }
}

/**
 * TrendingSection — Sección "En Tendencia" con cards que muestran rating.
 */
@Composable
fun TrendingSection(
    products: List<ProductDetail>,
    onProductClick: (ProductDetail) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HoloSpacing.md)
    ) {
        SectionHeader(
            title = "EN TENDENCIA",
            actionText = "Ver todo",
            onAction = onSeeAllClick
        )

        Spacer(Modifier.height(HoloSpacing.sm))

        LazyRow(
            contentPadding = PaddingValues(horizontal = HoloSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
        ) {
            items(products) { product ->
                TrendingCard(product = product, onClick = { onProductClick(product) })
            }
        }
    }
}

/**
 * TrendingCard — Card de producto con badge de rating y contador de reseñas.
 *
 * Más grande que SmallProductCard (220dp ancho, 240dp imagen).
 * Badge negro con rating en amarillo arriba-izquierda.
 */
@Composable
fun TrendingCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Fog)
    ) {
        Column {
            // Imagen con badge de rating y favorito
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = HoloSpacing.RadiusLg, topEnd = HoloSpacing.RadiusLg)),
                    contentScale = ContentScale.Crop
                )

                // Badge de rating (arriba-izquierda)
                Box(
                    modifier = Modifier
                        .padding(HoloSpacing.xs)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                        .background(HoloColors.InkOverlay80)
                        .padding(horizontal = HoloSpacing.xs, vertical = HoloSpacing.xxs)
                ) {
                    Text(
                        text = "${product.rating}",
                        style = HoloType.LabelSmall,
                        color = HoloColors.Warning     // Amarillo para estrellas
                    )
                }

                // Botón favorito (arriba-derecha)
                IconButton(
                    onClick = { scope.launch { FavoritesManager.toggleFavorite(context, product.id) } },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(HoloSpacing.xxs)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFav) HoloColors.Pulse else HoloColors.TextOnDarkMuted,
                        modifier = Modifier.size(HoloSpacing.IconSizeSmall)
                    )
                }
            }

            // Info del producto debajo de la imagen
            Column(modifier = Modifier.padding(HoloSpacing.sm)) {
                // Marca en label style
                Text(
                    text = product.brand,
                    style = HoloType.LabelSmall,
                    color = HoloColors.TextTertiary
                )

                Spacer(Modifier.height(HoloSpacing.xxs))

                Text(
                    text = product.title,
                    style = HoloType.TitleMedium,
                    color = HoloColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.subtitle,
                    style = HoloType.BodySmall,
                    color = HoloColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(Modifier.height(HoloSpacing.xs))

                // Precio + nº de reseñas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price,
                        style = HoloType.TitleLarge,
                        color = HoloColors.TextPrimary
                    )
                    Text(
                        text = "${product.reviewCount} reseñas",
                        style = HoloType.BodySmall,
                        color = HoloColors.TextTertiary
                    )
                }
            }
        }
    }
}

/**
 * FinalBanner — Banner de cierre con CTA al catálogo completo.
 *
 * Gradiente oscuro horizontal, label rojo, título blanco, flecha.
 */
@Composable
fun FinalBanner(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HoloSpacing.md)
            .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
            .background(
                Brush.horizontalGradient(
                    listOf(HoloColors.Neutral800, HoloColors.Neutral600)
                )
            )
            .clickable { onClick() }
            .padding(HoloSpacing.xl)
    ) {
        Column {
            // Label rojo — "HOLOCREW"
            Text(
                text = "HOLOCREW",
                style = HoloType.LabelMedium,
                color = HoloColors.Pulse
            )

            Spacer(Modifier.height(HoloSpacing.xs))

            Text(
                text = "Descubre toda\nla colección",
                style = HoloType.HeadlineLarge,
                color = HoloColors.TextOnDark
            )

            Spacer(Modifier.height(HoloSpacing.md))

            // Flecha de acción
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ver catálogo",
                    style = HoloType.TitleMedium,
                    color = HoloColors.TextOnDark
                )
                Spacer(Modifier.width(HoloSpacing.xs))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = HoloColors.TextOnDark,
                    modifier = Modifier.size(HoloSpacing.IconSizeSmall)
                )
            }
        }
    }
}