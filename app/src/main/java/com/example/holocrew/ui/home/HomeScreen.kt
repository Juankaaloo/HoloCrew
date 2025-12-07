package com.example.holocrew.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.R
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.components.CustomTopAppBar

// Modelo de datos para Home
data class HomeProduct(
    val id: Int,
    val title: String,
    val subtitle: String,
    val date: String,
    val imageRes: Int,
    val category: String,
    val price: String? = null,
    val isFavorite: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Datos para Home (Noticias como pantalla principal)
    val homeProducts = remember {
        listOf(
            HomeProduct(
                id = 1,
                title = "HOLOCHEM Tops",
                subtitle = "Nueva colección primavera/verano 2024",
                date = "Hoy",
                imageRes = R.drawable.tops,
                category = "Nuevo",
                price = "$89.99"
            ),
            HomeProduct(
                id = 2,
                title = "New Denims Collection",
                subtitle = "Jeans premium edición limitada - Corte slim fit",
                date = "Ayer",
                imageRes = R.drawable.newdenims,
                category = "Denim",
                price = "$129.99"
            ),
            HomeProduct(
                id = 3,
                title = "Outerwears Premium",
                subtitle = "Chaquetas y abrigos para invierno - Materiales premium",
                date = "15 Dic",
                imageRes = R.drawable.outerwear,
                category = "Exterior",
                price = "$199.99"
            ),
            HomeProduct(
                id = 4,
                title = "HOLOCHEM Agency",
                subtitle = "Colección exclusiva para agencia - Edición limitada",
                date = "10 Dic",
                imageRes = R.drawable.tops,
                category = "Exclusivo",
                price = "$249.99"
            )
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = {
                    println("🔍 Búsqueda desde Home")
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    Text(
                        text = "HOLOCHEM HOME",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Moda urbana y streetwear premium",
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            items(homeProducts) { product ->
                HomeProductCard(product = product)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HomeProductCard(product: HomeProduct) {
    var isFavorite by remember { mutableStateOf(product.isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                bottomStart = 16.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = when (product.category) {
                                "Nuevo" -> Color(0xFF4CAF50)
                                "Denim" -> Color(0xFF2196F3)
                                "Exterior" -> Color(0xFF9C27B0)
                                "Exclusivo" -> Color(0xFFFF9800)
                                else -> Color(0xFF607D8B)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = product.category.uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = product.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )
                    Text(
                        text = product.subtitle,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.price ?: "Consultar precio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Fecha",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = product.date,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }

                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color.Red else Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
}