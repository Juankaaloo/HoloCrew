// ui/home/HomeScreen.kt
package com.example.holocrew.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// Modelo de datos para el contenido del carrusel/tarjeta principal
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
    // Datos de ejemplo para la pantalla de inicio
    val mainItem = remember {
        HomeContent(
            title = "HoloCree Footwear",
            subtitle = "Primicos",
            imageRes = R.drawable.footwear,
            category = "DROP",
            details = "Un lanzamiento exclusivo con los Roneantes. Disponibilidad limitada. Entra para participar en el sorteo."
        )
    }

    val secondaryItems = remember {
        listOf(
            mainItem.copy(title = "Retro Holo LongSleeve", subtitle = "Detalle 2", imageRes = R.drawable.retro_holo_fc_longsleeve),
            mainItem.copy(title = "Champio Ring", subtitle = "Detalle 3", imageRes = R.drawable.championring_holo_black)
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
            // ✅ CORRECCIÓN: Solo pasa navController, NO currentRoute
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Banner Principal
            item {
                HomeBanner(item = mainItem)
            }

            // 2. Título de la Sección
            item {
                Column(
                    modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp)
                ) {
                    Text(
                        text = "ÚLTIMOS LANZAMIENTOS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // 3. Contenido Secundario
            items(secondaryItems) { item ->
                HomeContentCard(item = item)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Espacio final
            item {
                Spacer(modifier = Modifier.height(80.dp))
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
fun HomeContentCard(item: HomeContent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}