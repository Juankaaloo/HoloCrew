package com.example.holocrew.ui.news

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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.holocrew.R
import com.example.holocrew.navigation.Screen

// Modelo de datos para productos
data class ProductNews(
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
fun NewsScreen(navController: NavController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Datos de ejemplo usando TUS IMÁGENES
    val productItems = remember {
        listOf(
            ProductNews(
                id = 1,
                title = "HOLOCHEM Tops",
                subtitle = "Nueva colección primavera/verano 2024",
                date = "Hoy",
                imageRes = R.drawable.tops,
                category = "Tops",
                price = "$89.99"
            ),
            ProductNews(
                id = 2,
                title = "New Denims Collection",
                subtitle = "Jeans premium edición limitada - Corte slim fit",
                date = "Ayer",
                imageRes = R.drawable.newdenims,
                category = "Denim",
                price = "$129.99"
            ),
            ProductNews(
                id = 3,
                title = "Outerwears Premium",
                subtitle = "Chaquetas y abrigos para invierno - Materiales premium",
                date = "15 Dic",
                imageRes = R.drawable.outerwear,
                category = "Exterior",
                price = "$199.99"
            ),
            ProductNews(
                id = 4,
                title = "HOLOCHEM Agency",
                subtitle = "Colección exclusiva para agencia - Edición limitada",
                date = "10 Dic",
                imageRes = R.drawable.tops,
                category = "Exclusivo",
                price = "$249.99"
            ),
            ProductNews(
                id = 5,
                title = "Streetwear Essentials",
                subtitle = "Lo básico para tu armario urbano - Calidad premium",
                date = "5 Dic",
                imageRes = R.drawable.newdenims,
                category = "Básicos",
                price = "$79.99"
            ),
            ProductNews(
                id = 6,
                title = "Limited Edition",
                subtitle = "Productos exclusivos numerados - Solo 100 unidades",
                date = "1 Dic",
                imageRes = R.drawable.outerwear,
                category = "Limitado",
                price = "$299.99"
            )
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = {
                    // Acción de búsqueda
                    println("🔍 Botón de búsqueda presionado")
                    // Aquí podrías navegar a una pantalla de búsqueda
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    Text(
                        text = "HOLOCHEM Collection",
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

            // Lista de productos
            items(productItems) { product ->
                ProductCard(product = product)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Espacio final
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    onSearchClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Logo HOLOCHEM - usando tu imagen tops.png
                Image(
                    painter = painterResource(id = R.drawable.logografiti1),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp), // Tamaño cuadrado
                    contentScale = ContentScale.Crop
                )
            }
        },
        actions = {
            // Botón de búsqueda (lupa)
            IconButton(
                onClick = onSearchClick
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar productos",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        )
    )
}

@Composable
fun ProductCard(product: ProductNews) {
    var isFavorite by remember { mutableStateOf(product.isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen del producto
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
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Badge de categoría con color según categoría
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            when (product.category) {
                                "Tops" -> Color(0xFF4CAF50)       // Verde
                                "Denim" -> Color(0xFF2196F3)      // Azul
                                "Exterior" -> Color(0xFF9C27B0)   // Púrpura
                                "Exclusivo" -> Color(0xFFFF9800)  // Naranja
                                "Básicos" -> Color(0xFF795548)    // Marrón
                                "Limitado" -> Color(0xFFF44336)   // Rojo
                                else -> Color(0xFF607D8B)         // Gris
                            },
                            RoundedCornerShape(6.dp)
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

            // Contenido
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
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = product.subtitle,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    // Precio
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

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    // Crear una lista de ítems de navegación con tipo explícito
    val navigationItems = listOf(
        NavigationItem(
            screen = Screen.News,
            icon = Icons.Filled.Notifications,
            label = "Noticias"
        ),
        NavigationItem(
            screen = Screen.Available,
            icon = Icons.Filled.ShoppingCart,
            label = "Disponibles"
        ),
        NavigationItem(
            screen = Screen.Upcoming,
            icon = Icons.Default.DateRange,
            label = "Próximos"
        ),
        NavigationItem(
            screen = Screen.Map,
            icon = Icons.Filled.LocationOn,
            label = "Mapa"
        ),
        NavigationItem(
            screen = Screen.Profile,
            icon = Icons.Filled.Person,
            label = "Perfil"
        )
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp
                    )
                },
                selected = currentRoute == item.screen.route,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

// Clase auxiliar para los ítems de navegación
data class NavigationItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)