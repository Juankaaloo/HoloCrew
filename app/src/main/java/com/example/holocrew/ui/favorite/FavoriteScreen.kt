package com.example.holocrew.ui.favorites

import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.mockProducts
import kotlinx.coroutines.launch

/**
 * FavoritesScreen — Pantalla de productos marcados como favoritos.
 *
 * Diseño SNKRS:
 *  - TopBar blanca con título bold y flecha atrás
 *  - Grid de 2 columnas con cards de producto
 *  - Corazón rojo Pulse en cada card para quitar de favoritos
 *  - Estado vacío con icono grande y mensaje centrado
 *  - Contador de productos en la parte superior
 *
 * Los favoritos se filtran desde mockProducts comparando con los IDs
 * almacenados en FavoritesManager (que vienen de la API /favorites/ids).
 *
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── Observar IDs de favoritos en tiempo real ──────────────────────────────
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()

    // Filtrar mockProducts que están en favoritos (id ya es String)
    val favoriteProducts = remember(favoriteIds) {
        mockProducts.filter { favoriteIds.contains(it.id) }
    }

    // ── Scaffold con TopBar ───────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Favoritos",
                        style = HoloType.HeadlineMedium,
                        color = HoloColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = HoloColors.Ink
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HoloColors.Paper,
                    titleContentColor = HoloColors.TextPrimary
                )
            )
        },
        containerColor = HoloColors.Paper
    ) { paddingValues ->

        // ══════════════════════════════════════════════════════════════════════
        // ESTADO VACÍO — cuando no hay favoritos
        // ══════════════════════════════════════════════════════════════════════
        if (favoriteProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icono corazón grande gris
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = HoloColors.Neutral300,
                        modifier = Modifier.size(HoloSpacing.huge)
                    )

                    Spacer(Modifier.height(HoloSpacing.md))

                    Text(
                        text = "No tienes favoritos",
                        style = HoloType.HeadlineMedium,
                        color = HoloColors.TextPrimary
                    )
                    Text(
                        text = "Explora y marca los productos que te gusten",
                        style = HoloType.BodyMedium,
                        color = HoloColors.TextTertiary,
                        modifier = Modifier.padding(top = HoloSpacing.xs)
                    )
                }
            }
        } else {
            // ══════════════════════════════════════════════════════════════════
            // GRID DE FAVORITOS — 2 columnas con cards de producto
            // ══════════════════════════════════════════════════════════════════
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = HoloSpacing.xxxl)
            ) {
                // Contador de productos
                item {
                    Text(
                        text = "${favoriteProducts.size} producto${if (favoriteProducts.size != 1) "s" else ""}",
                        style = HoloType.BodyMedium,
                        color = HoloColors.TextTertiary,
                        modifier = Modifier.padding(
                            horizontal = HoloSpacing.md,
                            vertical = HoloSpacing.sm
                        )
                    )
                }

                // Grid manual de 2 columnas (chunked)
                val rows = favoriteProducts.chunked(2)
                items(rows) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HoloSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
                    ) {
                        rowProducts.forEach { product ->
                            FavoriteProductCard(
                                product = product,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate("product_detail/${product.id}")
                                },
                                onRemoveFavorite = {
                                    // Toggle favorito dentro de corrutina
                                    scope.launch {
                                        FavoritesManager.toggleFavorite(context, product.id)
                                    }
                                }
                            )
                        }
                        // Si la fila tiene solo 1 producto, añadir spacer para mantener el grid
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(HoloSpacing.md))
                }
            }
        }
    }
}

/**
 * FavoriteProductCard — Card de producto en la pantalla de favoritos.
 *
 * Imagen con botón de corazón rojo (Pulse) para quitar de favoritos,
 * título, subtítulo y precio debajo.
 *
 * @param product Datos del producto a mostrar.
 * @param modifier Modifier externo (para weight en el grid).
 * @param onClick Callback al pulsar la card (navega al detalle).
 * @param onRemoveFavorite Callback al pulsar el corazón (quita de favoritos).
 */
@Composable
fun FavoriteProductCard(
    product: ProductDetail,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRemoveFavorite: () -> Unit = {}
) {
    Column(modifier = modifier.clickable { onClick() }) {
        // ── Imagen con botón favorito superpuesto ─────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                .background(HoloColors.Fog)
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(HoloSpacing.RadiusMd)),
                contentScale = ContentScale.Crop
            )

            // Corazón rojo Pulse (siempre lleno porque está en favoritos)
            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(HoloSpacing.xxs)
                    .size(32.dp)
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

        // ── Info del producto ─────────────────────────────────────────────────
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
        Text(
            text = product.price,
            style = HoloType.TitleMedium,
            color = HoloColors.TextPrimary,
            modifier = Modifier.padding(top = HoloSpacing.xxs)
        )
    }
}