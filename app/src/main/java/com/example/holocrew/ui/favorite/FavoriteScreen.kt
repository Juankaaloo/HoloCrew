/**
 * FavoritesScreen.kt
 *
 * Pantalla de productos favoritos del usuario en HoloCrew.
 * Muestra todos los productos que el usuario ha marcado con el corazón
 * desde Available o ProductDetail, en un grid de 2 columnas.
 *
 * Lee los IDs de favoritos desde FavoritesManager (singleton global)
 * y busca los productos correspondientes en mockProducts.
 *
 * Funcionalidades:
 *  - Ver todos los productos marcados como favoritos
 *  - Quitar de favoritos desde esta pantalla
 *  - Navegar al detalle de cada producto
 *  - Estado vacío cuando no hay favoritos
 */
package com.example.holocrew.ui.favorites

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.mockProducts

/**
 * Pantalla de productos favoritos.
 *
 * Observa el FavoritesManager y se actualiza automáticamente
 * cuando se añaden o quitan favoritos desde cualquier pantalla.
 *
 * @param navController Controlador de navegación para volver y navegar al detalle
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {

    // Observar los IDs de favoritos desde el gestor global
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()

    // Obtener los productos completos que coinciden con los IDs favoritos
    val favoriteProducts = mockProducts.filter { favoriteIds.contains(it.id) }

    Scaffold(
        // ── Barra superior con título y botón volver ──
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Favoritos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        // ════════════════════════════════════════════════════════════
        // ESTADO VACÍO: Cuando no hay favoritos
        // ════════════════════════════════════════════════════════════
        if (favoriteProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icono grande de corazón vacío
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFFCCCCCC),
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No tienes favoritos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "Explora y marca los productos que te gusten",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {

            // ════════════════════════════════════════════════════════════
            // GRID DE FAVORITOS: 2 columnas
            // ════════════════════════════════════════════════════════════
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                // Contador
                item {
                    Text(
                        text = "${favoriteProducts.size} producto${if (favoriteProducts.size != 1) "s" else ""}",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // Grid de 2 columnas
                val rows = favoriteProducts.chunked(2)
                items(rows) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { product ->
                            FavoriteProductCard(
                                product = product,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate("product_detail/${product.id}")
                                },
                                onRemoveFavorite = {
                                    FavoritesManager.toggleFavorite(product.id)
                                }
                            )
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Card de producto favorito.
 * Similar a ProductGridCard pero con corazón rojo lleno (es favorito seguro).
 * Al pulsar el corazón se quita de favoritos.
 *
 * @param product Datos del producto
 * @param modifier Modifier externo (para weight del grid)
 * @param onClick Callback al pulsar la card (navega al detalle)
 * @param onRemoveFavorite Callback al pulsar el corazón (quita de favoritos)
 */
@Composable
fun FavoriteProductCard(
    product: ProductDetail,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRemoveFavorite: () -> Unit = {}
) {
    Column(
        modifier = modifier.clickable { onClick() }
    ) {
        // ── Imagen con corazón ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            // Corazón rojo (siempre lleno porque es favorito)
            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Quitar de favoritos",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Nombre
        Text(
            text = product.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Subtítulo
        Text(
            text = product.subtitle,
            fontSize = 13.sp,
            color = Color(0xFF666666),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
        )

        // Precio
        Text(
            text = product.price,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}