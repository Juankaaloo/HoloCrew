// ui/upcoming/UpcomingScreen.kt
package com.example.holocrew.ui.upcoming

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

// Modelo para próximos lanzamientos
data class UpcomingProduct(
    val id: Int,
    val title: String,
    val subtitle: String,
    val launchDate: String,
    val imageRes: Int,
    val status: String,
    val price: String? = null,
    val isFavorite: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(navController: NavController) {
    val upcomingProducts = remember {
        listOf(
            UpcomingProduct(
                id = 1,
                title = "Winter Collection 2024",
                subtitle = "Colección completa de invierno",
                launchDate = "25 Dic 2024",
                imageRes = R.drawable.outerwear,
                status = "Próximamente",
                price = "$299.99"
            ),
            UpcomingProduct(
                id = 2,
                title = "Limited Edition Denim",
                subtitle = "Jeans numerados - Solo 100 unidades",
                launchDate = "15 Ene 2025",
                imageRes = R.drawable.newdenims,
                status = "Reserva Abierta",
                price = "$199.99"
            ),
            UpcomingProduct(
                id = 3,
                title = "HOLOCHEM x Artist Collab",
                subtitle = "Colaboración exclusiva con artista urbano",
                launchDate = "30 Ene 2025",
                imageRes = R.drawable.tops,
                status = "Próximamente",
                price = "$249.99"
            ),
            UpcomingProduct(
                id = 4,
                title = "Techwear Collection",
                subtitle = "Ropa técnica para clima extremo",
                launchDate = "10 Feb 2025",
                imageRes = R.drawable.outerwear,
                status = "Pre-orden",
                price = "$349.99"
            )
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = {
                    println("🔍 Búsqueda desde Próximos")
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
                        text = "PRÓXIMOS LANZAMIENTOS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Productos que llegarán pronto",
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            items(upcomingProducts) { product ->
                UpcomingProductCard(product = product)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun UpcomingProductCard(product: UpcomingProduct) {
    var isFavorite by remember { mutableStateOf(product.isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.width(150.dp).fillMaxSize()) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            when (product.status) {
                                "Próximamente" -> Color(0xFFFF9800)
                                "Reserva Abierta" -> Color(0xFF4CAF50)
                                "Pre-orden" -> Color(0xFF2196F3)
                                else -> Color(0xFF607D8B)
                            },
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = product.status.uppercase(),
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
                        text = product.price ?: "Precio por anunciar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // ✅ CORRECCIÓN: Usamos DateRange en lugar de CalendarToday
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Fecha de lanzamiento",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lanzamiento: ${product.launchDate}",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Notificarme",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        IconButton(
                            onClick = { isFavorite = !isFavorite },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) Color.Red else Color(0xFF666666)
                            )
                        }
                    }
                }
            }
        }
    }
}