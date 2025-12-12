// ui/available/AvailableScreen.kt
package com.example.holocrew.ui.available

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.holocrew.R
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableScreen(
    navController: NavController? = null,
    viewModel: AvailableViewModel = viewModel()
) {
    // Observar el estado del ViewModel
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val selectedFilter = viewModel.selectedFilter
    val categories = viewModel.categories // Asumiendo que es una lista simple

    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = {
                    println("🔍 Búsqueda desde Disponibles")
                }
            )
        },
        bottomBar = {
            if (navController != null) {
                BottomNavigationBar(navController = navController)
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "PRODUCTOS DISPONIBLES",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${filteredProducts.size} productos encontrados",
                        fontSize = 16.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Filtros por categoría/tipo
                    Text(
                        text = "Filtrar por tipo:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    // Filtros horizontales
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            CategoryFilterChip(
                                text = category,
                                isSelected = selectedFilter == category,
                                onClick = { viewModel.selectFilter(category) }
                            )
                        }
                    }

                    // Mostrar filtro activo
                    if (selectedFilter != "Todos") {
                        Text(
                            text = "Mostrando: $selectedFilter",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Si no hay productos con el filtro
            if (filteredProducts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay productos en esta categoría",
                            fontSize = 18.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Intenta con otro filtro",
                            fontSize = 14.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }

            // Lista de productos filtrados
            items(filteredProducts) { product ->
                AvailableProductCard(
                    product = product,
                    onProductClick = { clickedProduct ->
                        // Navegar a la pantalla de detalle del producto
                        navController?.navigate("product_detail/${clickedProduct.id}")
                    },
                    onFavoriteClick = {
                        println("⭐ Favorito: ${product.title}")
                    },
                    onCartClick = {
                        println("🛒 Carrito: ${product.title}")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun CategoryFilterChip(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) Color.Black else Color(0xFFEEEEEE)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color(0xFF666666),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AvailableProductCard(
    product: AvailableProduct,
    onProductClick: (AvailableProduct) -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val isFavorite = remember { product.isFavorite }
    val inCart = remember { product.inCart }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp)
            .clickable { onProductClick(product) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Imagen del producto
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                // Badge de categoría
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(
                            when (product.category) {
                                "Ropa" -> Color(0xFF2196F3)
                                "Denim" -> Color(0xFF9C27B0)
                                "Ropa Interior" -> Color(0xFF4CAF50)
                                "Accesorios" -> Color(0xFFFF9800)
                                else -> Color(0xFF607D8B)
                            },
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = product.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Contenido
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = product.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )
                    Text(
                        text = product.subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 2
                    )

                    // Mostrar estado del producto
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (product.status) {
                                        "Nuevo" -> Color(0xFFE8F5E9)
                                        "Exclusivo" -> Color(0xFFF3E5F5)
                                        "Más vendido" -> Color(0xFFFFEBEE)
                                        "En oferta" -> Color(0xFFFFF3E0)
                                        "Premium" -> Color(0xFFE8EAF6)
                                        "Básico" -> Color(0xFFE0F2F1)
                                        else -> Color(0xFFEEEEEE)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = product.status,
                                fontSize = 10.sp,
                                color = when (product.status) {
                                    "Nuevo" -> Color(0xFF4CAF50)
                                    "Exclusivo" -> Color(0xFF9C27B0)
                                    "Más vendido" -> Color(0xFFF44336)
                                    "En oferta" -> Color(0xFFFF9800)
                                    "Premium" -> Color(0xFF2196F3)
                                    "Básico" -> Color(0xFF009688)
                                    else -> Color(0xFF666666)
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${product.category}",
                            fontSize = 10.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }

                // Precio y acciones
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.price,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )

                    Row {
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) Color.Red else Color(0xFF666666),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onCartClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Añadir al carrito",
                                tint = if (inCart) Color(0xFF4CAF50) else Color(0xFF666666),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}