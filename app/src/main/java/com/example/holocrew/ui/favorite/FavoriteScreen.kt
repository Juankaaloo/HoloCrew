package com.example.holocrew.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.product.ProductDetail
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val favoriteIds by WishlistRepository.favoriteIds.collectAsState()

    var favoriteProducts by remember { mutableStateOf<List<ProductDetail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar productos favoritos desde Supabase
    LaunchedEffect(favoriteIds) {
        isLoading = true
        favoriteProducts = WishlistRepository.getFavoriteProducts()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Mis Favoritos", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = HoloColors.Ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Paper)
            )
        },
        containerColor = HoloColors.Paper
    ) { paddingValues ->

        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HoloColors.Ink)
            }
            return@Scaffold
        }

        if (favoriteProducts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = HoloColors.Neutral300,
                        modifier = Modifier.size(HoloSpacing.huge)
                    )
                    Spacer(Modifier.height(HoloSpacing.md))
                    Text(text = "No tienes favoritos", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
                    Text(
                        text = "Explora y marca los productos que te gusten",
                        style = HoloType.BodyMedium,
                        color = HoloColors.TextTertiary,
                        modifier = Modifier.padding(top = HoloSpacing.xs)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(bottom = HoloSpacing.xxxl)
            ) {
                item {
                    Text(
                        text = "${favoriteProducts.size} producto${if (favoriteProducts.size != 1) "s" else ""}",
                        style = HoloType.BodyMedium,
                        color = HoloColors.TextTertiary,
                        modifier = Modifier.padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.sm)
                    )
                }

                val rows = favoriteProducts.chunked(2)
                items(rows) { rowProducts ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
                    ) {
                        rowProducts.forEach { product ->
                            FavoriteProductCard(
                                product = product,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("product_detail/${product.id}") },
                                onRemoveFavorite = {
                                    scope.launch { WishlistRepository.toggleFavorite(product.id, product.priceRaw) }
                                }
                            )
                        }
                        if (rowProducts.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(HoloSpacing.md))
                }
            }
        }
    }
}

@Composable
fun FavoriteProductCard(
    product: ProductDetail,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRemoveFavorite: () -> Unit = {}
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                .background(HoloColors.Fog)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(HoloSpacing.RadiusMd)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.align(Alignment.TopEnd).padding(HoloSpacing.xxs).size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Quitar de favoritos",
                    tint = HoloColors.Pulse,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(Modifier.height(HoloSpacing.xs))
        Text(text = product.title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(text = product.subtitle, style = HoloType.BodySmall, color = HoloColors.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        Text(text = product.price, style = HoloType.TitleMedium, color = HoloColors.TextPrimary, modifier = Modifier.padding(top = HoloSpacing.xxs))
    }
}