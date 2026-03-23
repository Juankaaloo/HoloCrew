/**
 * UpcomingScreen.kt
 *
 * Pantalla de próximos lanzamientos en HoloCrew.
 * Diseño mejorado con:
 *  - Primer drop como hero card a pantalla completa con gradiente
 *  - Filtros por estado (Todos, Próximamente, Reserva, Pre-orden)
 *  - Cards SNKRS claras (fondo gris) con subtítulo, título, imagen y Notifícame
 *  - Cards oscuras con gradiente intercaladas
 *  - Más productos mock y datos
 *  - Fecha de lanzamiento y precio visibles
 */
package com.example.holocrew.ui.upcoming

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
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

// ══════════════════════════════════════════════════════════════════════════════
// MODELO DE DATOS
// ══════════════════════════════════════════════════════════════════════════════

data class UpcomingProduct(
    val id: Int,
    val title: String,
    val subtitle: String,
    val launchDate: String,
    val imageRes: Int,
    val status: String,
    val price: String? = null
)

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA
// ══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(navController: NavController) {

    var selectedFilter by remember { mutableStateOf("Todos") }

    val allProducts = remember {
        listOf(
            UpcomingProduct(1, "Winter Collection 2025", "Colección completa de invierno", "25 Dic 2025", R.drawable.outerwear, "Próximamente", "$299.99"),
            UpcomingProduct(2, "Limited Edition Denim", "Jeans numerados · Solo 100 unidades", "15 Ene 2026", R.drawable.newdenims, "Reserva Abierta", "$199.99"),
            UpcomingProduct(3, "HOLOCREW x Artist Collab", "Colaboración exclusiva con artista urbano", "30 Ene 2026", R.drawable.tops, "Próximamente", "$249.99"),
            UpcomingProduct(4, "Techwear Collection", "Ropa técnica para clima extremo", "10 Feb 2026", R.drawable.outerwear, "Pre-orden", "$349.99"),
            UpcomingProduct(5, "Summer Essentials Pack", "Pack verano con 3 piezas básicas", "1 Mar 2026", R.drawable.glory_holo_polo, "Próximamente", "$159.99"),
            UpcomingProduct(6, "HoloCrew x Racing", "Colección motorsport edición especial", "15 Mar 2026", R.drawable.offroad_racing_cap, "Próximamente", "$279.99")
        )
    }

    val filters = listOf("Todos", "Próximamente", "Reserva Abierta", "Pre-orden")

    val filteredProducts = remember(selectedFilter) {
        if (selectedFilter == "Todos") allProducts
        else allProducts.filter { it.status == selectedFilter }
    }

    val heroProduct = filteredProducts.firstOrNull()
    val restProducts = if (filteredProducts.size > 1) filteredProducts.drop(1) else emptyList()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                onSearchClick = { navController.navigate("search") { launchSingleTop = true } },
                onFavoritesClick = { navController.navigate("favorites") { launchSingleTop = true } }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        containerColor = Color.White
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // ═══ HEADER ═══
            item {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Text("Próximos Drops", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        "${filteredProducts.size} lanzamientos programados",
                        fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    // ═══ FILTROS POR ESTADO ═══
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { filter ->
                            StatusFilterChip(
                                text = filter,
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                }
            }

            // ═══ ESTADO VACÍO ═══
            if (filteredProducts.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay drops para este filtro", fontSize = 16.sp, color = Color(0xFF666666))
                        Text("Intenta con otro estado", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            // ═══ HERO CARD (primer drop, grande con gradiente) ═══
            if (heroProduct != null) {
                item {
                    HeroDropCard(product = heroProduct)
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ═══ RESTO DE CARDS (alternan entre estilo claro y oscuro) ═══
            itemsIndexed(restProducts) { index, product ->
                if (index % 2 == 0) {
                    // Card clara estilo SNKRS (fondo gris, subtítulo + título + imagen + Notifícame)
                    LightDropCard(product = product)
                } else {
                    // Card oscura con gradiente
                    DarkDropCard(product = product)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

/** Chip de filtro por estado */
@Composable
fun StatusFilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Color.Black else Color(0xFFF0F0F0))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text, fontSize = 14.sp, color = if (isSelected) Color.White else Color(0xFF666666), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
    }
}

/**
 * Hero Card — Primer drop, card grande con imagen, gradiente y toda la info.
 */
@Composable
fun HeroDropCard(product: UpcomingProduct) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Imagen
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradiente
        Box(
            modifier = Modifier.fillMaxWidth().height(250.dp).align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
        )

        // Badge estado
        Box(
            modifier = Modifier.padding(14.dp).align(Alignment.TopStart)
                .clip(RoundedCornerShape(8.dp))
                .background(statusColor(product.status))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(product.status.uppercase(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }

        // Info
        Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text(product.subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
            Spacer(Modifier.height(4.dp))
            Text(product.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 28.sp)
            Spacer(Modifier.height(10.dp))

            // Fecha + Precio
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DateRange, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(product.launchDate, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
                }
                Text(product.price ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            // Botones
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Share, "Compartir", tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Filled.Notifications, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Notifícame", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
            }
        }
    }
}

/**
 * Card clara estilo SNKRS — fondo gris, subtítulo arriba, título,
 * imagen centrada, y fila compartir + Notifícame.
 */
@Composable
fun LightDropCard(product: UpcomingProduct) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            // Subtítulo
            Text(product.subtitle, fontSize = 14.sp, color = Color(0xFF666666))
            Spacer(Modifier.height(4.dp))

            // Título + precio
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(product.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 26.sp, modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(product.price ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(Modifier.height(4.dp))

            // Fecha + badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DateRange, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(product.launchDate, fontSize = 13.sp, color = Color(0xFF9E9E9E))
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(statusColor(product.status).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(product.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor(product.status))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Imagen centrada
            Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(16.dp))

            // Compartir + Notifícame
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Share, "Compartir", tint = Color.Black, modifier = Modifier.size(22.dp))
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text("Notifícame", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}

/**
 * Card oscura — imagen de fondo con gradiente, info superpuesta.
 */
@Composable
fun DarkDropCard(product: UpcomingProduct) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
        )

        // Badge
        Box(
            modifier = Modifier.padding(14.dp).align(Alignment.TopStart)
                .clip(RoundedCornerShape(8.dp)).background(statusColor(product.status))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(product.status.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }

        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(product.subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            Spacer(Modifier.height(4.dp))
            Text(product.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 24.sp)
            Spacer(Modifier.height(6.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DateRange, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(product.launchDate, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
                Text(product.price ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Share, "Compartir", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.Notifications, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Notifícame", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
            }
        }
    }
}

/** Color según el estado del drop */
fun statusColor(status: String): Color {
    return when (status) {
        "Próximamente" -> Color(0xFFFF9800)
        "Reserva Abierta" -> Color(0xFF4CAF50)
        "Pre-orden" -> Color(0xFF2196F3)
        else -> Color(0xFF607D8B)
    }
}