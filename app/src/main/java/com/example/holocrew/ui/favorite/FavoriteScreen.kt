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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.mockProducts
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()

    // Los IDs ahora son String, comparamos con product.id.toString()
    val favoriteProducts = mockProducts.filter { favoriteIds.contains(it.id.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Mis Favoritos", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = Color.Black)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        if (favoriteProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFFCCCCCC),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No tienes favoritos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Explora y marca los productos que te gusten", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 8.dp))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                item {
                    Text(
                        text = "${favoriteProducts.size} producto${if (favoriteProducts.size != 1) "s" else ""}",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                val rows = favoriteProducts.chunked(2)
                items(rows) { rowProducts ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                                    // ✅ Correcto: context + productId como String dentro de corrutina
                                    scope.launch {
                                        FavoritesManager.toggleFavorite(context, product.id)
                                    }
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

@Composable
fun FavoriteProductCard(
    product: ProductDetail,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRemoveFavorite: () -> Unit = {}
) {
    Column(modifier = modifier.clickable { onClick() }) {
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
            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(32.dp)
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
        Text(product.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.subtitle, fontSize = 13.sp, color = Color(0xFF666666), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
    }
}