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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.holocrew.R
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.components.CustomTopAppBar
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.product.ProductDetail
import kotlinx.coroutines.launch
import com.example.holocrew.components.SoldOutOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val favoriteIds by WishlistRepository.favoriteIds.collectAsState()

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

        // Estado de carga
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HoloColors.Ink)
            }
            return@Scaffold
        }

        // Estado de error
        if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.error ?: "Error desconocido",
                        style = HoloType.BodyLarge,
                        color = HoloColors.TextSecondary
                    )
                    Spacer(Modifier.height(HoloSpacing.md))
                    Button(
                        onClick = { homeViewModel.refresh() },
                        colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink)
                    ) {
                        Text("Reintentar", color = HoloColors.Paper)
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.BottomNavHeight)
        ) {

            // 1. HERO BANNER (imagen local — no depende de Supabase)
            item {
                HeroBanner(
                    imageRes = R.drawable.footwear,
                    label = "NUEVO DROP",
                    title = "HOLOCREW\nFOOTWEAR",
                    description = "Exclusivo para miembros. Disponibilidad limitada.",
                    onShopClick = { navController.navigate("available") { launchSingleTop = true } }
                )
            }

            // 2. DROPS DESTACADOS
            if (uiState.featuredProducts.isNotEmpty()) {
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
                            items(uiState.featuredProducts) { product ->
                                PagerCard(
                                    product = product,
                                    onClick = { navController.navigate("product_detail/${product.id}") }
                                )
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(HoloSpacing.xl)) }
            }

            // 3. EXCLUSIVOS
            if (uiState.exclusiveProducts.isNotEmpty()) {
                item {
                    DarkSection(
                        title = "EXCLUSIVOS HOLOCREW",
                        subtitle = "Piezas de edicion limitada",
                        products = uiState.exclusiveProducts,
                        onProductClick = { navController.navigate("product_detail/${it.id}") },
                        onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                    )
                }
            }

            // 4. CARD DESTACADA #1
            if (uiState.featuredProducts.isNotEmpty()) {
                item {
                    MainProductCard(
                        product = uiState.featuredProducts.first(),
                        onClick = { navController.navigate("product_detail/${uiState.featuredProducts.first().id}") }
                    )
                }
            }

            // 5. ULTIMA OPORTUNIDAD
            if (uiState.onSaleProducts.isNotEmpty()) {
                item {
                    GraySection(
                        title = "Ultima oportunidad,\naprovechala!",
                        products = uiState.onSaleProducts,
                        onProductClick = { navController.navigate("product_detail/${it.id}") },
                        onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                    )
                }
            }

            // 6. CARD DESTACADA #2
            if (uiState.featuredProducts.size > 1) {
                item {
                    MainProductCard(
                        product = uiState.featuredProducts[1],
                        onClick = { navController.navigate("product_detail/${uiState.featuredProducts[1].id}") }
                    )
                }
            }

            // 7. PROMO BANNER
            item {
                PromoBanner(
                    onExploreClick = { navController.navigate("upcoming") { launchSingleTop = true } }
                )
            }

            // 8. NUEVOS LANZAMIENTOS
            if (uiState.newProducts.isNotEmpty()) {
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
                            items(uiState.newProducts) { product ->
                                PagerCard(
                                    product = product,
                                    onClick = { navController.navigate("product_detail/${product.id}") }
                                )
                            }
                        }
                    }
                }
            }

            // 9. EN TENDENCIA
            if (uiState.trendingProducts.isNotEmpty()) {
                item {
                    TrendingSection(
                        products = uiState.trendingProducts,
                        onProductClick = { navController.navigate("product_detail/${it.id}") },
                        onSeeAllClick = { navController.navigate("available") { launchSingleTop = true } }
                    )
                }
            }

            // 10. BANNER FINAL
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
// ══════════════════════════════════════════════════════════════════════════════

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
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xxl)
        ) {
            Text(text = label, style = HoloType.LabelMedium, color = HoloColors.Pulse)
            Spacer(Modifier.height(HoloSpacing.xs))
            Text(text = title, style = HoloType.DisplayMedium, color = HoloColors.TextOnDark)
            Spacer(Modifier.height(HoloSpacing.xs))
            Text(text = description, style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted)
            Spacer(Modifier.height(HoloSpacing.lg))
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
        Text(text = title, style = HoloType.LabelLarge, color = HoloColors.TextPrimary)
        if (actionText.isNotEmpty()) {
            Row(
                modifier = Modifier.clickable { onAction() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = actionText, style = HoloType.TitleSmall, color = HoloColors.TextSecondary)
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

// PagerCard ahora usa ProductDetail + AsyncImage (Coil)
@Composable
fun PagerCard(product: ProductDetail, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(380.dp)
            .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (product.stock <= 0) { SoldOutOverlay() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(HoloColors.Ink.copy(alpha = 0f), HoloColors.InkOverlay80)
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(HoloSpacing.md)
        ) {
            Text(text = product.subtitle, style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted)
            Text(text = product.title, style = HoloType.HeadlineMedium, color = HoloColors.TextOnDark)
            Spacer(Modifier.height(HoloSpacing.xs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = product.price, style = HoloType.HeadlineSmall, color = HoloColors.Pulse)
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

// MainProductCard ahora usa ProductDetail + AsyncImage
@Composable
fun MainProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HoloSpacing.md)
            .clickable { onClick() }
    ) {
        Text(text = product.subtitle, style = HoloType.TitleSmall, color = HoloColors.TextTertiary)
        Text(
            text = product.title,
            style = HoloType.HeadlineMedium,
            color = HoloColors.TextPrimary,
            modifier = Modifier.padding(top = HoloSpacing.xxs)
        )
        Spacer(Modifier.height(HoloSpacing.sm))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (product.stock <= 0) { SoldOutOverlay() }
        }
        Spacer(Modifier.height(HoloSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = product.price, style = HoloType.TitleLarge, color = HoloColors.TextPrimary)
            Text(
                text = "Comprar",
                style = HoloType.TitleMedium,
                color = HoloColors.TextSecondary,
                modifier = Modifier.clickable { onClick() }
            )
        }
    }
}

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HoloSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(text = title, style = HoloType.HeadlineSmall, color = HoloColors.TextOnDark)
                Text(
                    text = subtitle,
                    style = HoloType.BodySmall,
                    color = HoloColors.TextOnDarkMuted,
                    modifier = Modifier.padding(top = HoloSpacing.xxs)
                )
            }
            Text(
                text = "Ver todo",
                style = HoloType.TitleSmall,
                color = HoloColors.Pulse,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
        Spacer(Modifier.height(HoloSpacing.md))
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

@Composable
fun DarkProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by WishlistRepository.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
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
                .background(HoloColors.Neutral800)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(HoloSpacing.RadiusMd)),
                contentScale = ContentScale.Crop
            )
            if (product.stock <= 0) { SoldOutOverlay() }
            IconButton(
                onClick = { scope.launch { WishlistRepository.toggleFavorite(product.id, product.priceRaw) } },
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
        Text(
            text = product.title,
            style = HoloType.TitleSmall,
            color = HoloColors.TextOnDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = product.price,
            style = HoloType.TitleMedium,
            color = HoloColors.Pulse,
            modifier = Modifier.padding(top = HoloSpacing.xxs)
        )
    }
}

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

@Composable
fun SmallProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by WishlistRepository.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
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
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(HoloSpacing.RadiusMd)),
                contentScale = ContentScale.Crop
            )
            if (product.stock <= 0) { SoldOutOverlay() }
            IconButton(
                onClick = { scope.launch { WishlistRepository.toggleFavorite(product.id, product.priceRaw) } },
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
            Text(text = "PROXIMOS DROPS", style = HoloType.LabelMedium, color = HoloColors.Pulse)
            Spacer(Modifier.height(HoloSpacing.xs))
            Text(
                text = "No te pierdas\nlos lanzamientos",
                style = HoloType.HeadlineLarge,
                color = HoloColors.TextOnDark
            )
            Spacer(Modifier.height(HoloSpacing.md))
            OutlinedButton(
                onClick = onExploreClick,
                shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(HoloColors.Pulse, HoloColors.Pulse))
                )
            ) {
                Text(text = "EXPLORAR", style = HoloType.LabelLarge, color = HoloColors.Pulse)
            }
        }
    }
}

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
        SectionHeader(title = "EN TENDENCIA", actionText = "Ver todo", onAction = onSeeAllClick)
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

@Composable
fun TrendingCard(product: ProductDetail, onClick: () -> Unit = {}) {
    val favoriteIds by WishlistRepository.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(product.id)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = HoloSpacing.RadiusLg, topEnd = HoloSpacing.RadiusLg)),
                    contentScale = ContentScale.Crop
                )
                if (product.stock <= 0) { SoldOutOverlay() }
                Box(
                    modifier = Modifier
                        .padding(HoloSpacing.xs)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                        .background(HoloColors.InkOverlay80)
                        .padding(horizontal = HoloSpacing.xs, vertical = HoloSpacing.xxs)
                ) {
                    Text(text = "${product.rating}", style = HoloType.LabelSmall, color = HoloColors.Warning)
                }
                IconButton(
                    onClick = { scope.launch { WishlistRepository.toggleFavorite(product.id, product.priceRaw) } },
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
            Column(modifier = Modifier.padding(HoloSpacing.sm)) {
                Text(text = product.brand, style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = product.price, style = HoloType.TitleLarge, color = HoloColors.TextPrimary)
                    Text(
                        text = "${product.reviewCount} resenas",
                        style = HoloType.BodySmall,
                        color = HoloColors.TextTertiary
                    )
                }
            }
        }
    }
}

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
            Text(text = "HOLOCREW", style = HoloType.LabelMedium, color = HoloColors.Pulse)
            Spacer(Modifier.height(HoloSpacing.xs))
            Text(
                text = "Descubre toda\nla coleccion",
                style = HoloType.HeadlineLarge,
                color = HoloColors.TextOnDark
            )
            Spacer(Modifier.height(HoloSpacing.md))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Ver catalogo", style = HoloType.TitleMedium, color = HoloColors.TextOnDark)
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