// ui/home/HomeScreen.kt
package com.example.holocrew.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

data class HomeContent(
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val category: String,
    val details: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val mainItem = remember {
        HomeContent(
            title = "HoloCrew Footwear",
            subtitle = "Primicos",
            imageRes = R.drawable.footwear,
            category = "DROP",
            details = "Un lanzamiento exclusivo con los Roneantes. Disponibilidad limitada."
        )
    }

    val carouselItems = remember {
        listOf(
            HomeContent(
                title = "HoloCrew Footwear",
                subtitle = "DROP Exclusivo",
                imageRes = R.drawable.footwear,
                category = "DROP",
                details = "Disponibilidad limitada."
            ),
            HomeContent(
                title = "Retro Holo LongSleeve",
                subtitle = "Nueva Colección",
                imageRes = R.drawable.retro_holo_fc_longsleeve,
                category = "ROPA",
                details = "Edición retro limitada."
            ),
            HomeContent(
                title = "Champion Ring",
                subtitle = "Accesorios",
                imageRes = R.drawable.championring_holo_black,
                category = "ACCESORIO",
                details = "Pieza única de la colección."
            )
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(onSearchClick = { })
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Banner principal grande
            item {
                HomeBanner(item = mainItem)
            }

            // Título sección carrusel
            item {
                Text(
                    text = "ÚLTIMOS LANZAMIENTOS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
                )
            }

            // Carrusel horizontal
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(carouselItems) { item ->
                        CarouselCard(item = item)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun HomeBanner(item: HomeContent) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = item.details,
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.title,
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.subtitle,
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CarouselCard(item: HomeContent) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(260.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                // Badge de categoría
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            // Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
                Text(
                    text = item.subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1
                )
            }
        }
    }
}