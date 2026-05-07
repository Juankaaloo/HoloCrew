package com.example.holocrew.ui.upcoming

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.components.CustomTopAppBar
import com.example.holocrew.data.network.SbUpcomingDto
import com.example.holocrew.data.network.UpcomingRepository
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("Todos") }
    var allProducts by remember { mutableStateOf<List<SbUpcomingDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val filters = listOf("Todos", "upcoming", "reservation", "preorder")
    val filterLabels = mapOf("Todos" to "Todos", "upcoming" to "Proximamente", "reservation" to "Reserva", "preorder" to "Pre-orden")

    LaunchedEffect(Unit) {
        isLoading = true
        allProducts = UpcomingRepository.getAll()
        isLoading = false
    }

    val filteredProducts = remember(selectedFilter, allProducts) {
        if (selectedFilter == "Todos") allProducts else allProducts.filter { it.status == selectedFilter }
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
        containerColor = HoloColors.Paper
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.BottomNavHeight)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.md)) {
                    Text("Proximos Drops", style = HoloType.HeadlineLarge, color = HoloColors.TextPrimary)
                    Text("${filteredProducts.size} lanzamientos programados", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xxs))
                    Spacer(Modifier.height(HoloSpacing.md))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(HoloSpacing.xs)) {
                        items(filters) { filter ->
                            StatusFilterChip(
                                text = filterLabels[filter] ?: filter,
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = HoloColors.Ink)
                    }
                }
            } else if (filteredProducts.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(HoloSpacing.xxxl), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay drops para este filtro", style = HoloType.TitleLarge, color = HoloColors.TextSecondary)
                        Text("Intenta con otro estado", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xxs))
                    }
                }
            }

            if (!isLoading && heroProduct != null) {
                item {
                    HeroDropCard(product = heroProduct)
                    Spacer(Modifier.height(HoloSpacing.md))
                }
            }

            if (!isLoading) {
                itemsIndexed(restProducts) { index, product ->
                    if (index % 2 == 0) LightDropCard(product = product)
                    else DarkDropCard(product = product)
                    Spacer(Modifier.height(HoloSpacing.md))
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

fun statusLabel(status: String): String = when (status) {
    "upcoming" -> "PROXIMAMENTE"
    "reservation" -> "RESERVA ABIERTA"
    "preorder" -> "PRE-ORDEN"
    else -> status.uppercase()
}

fun statusColor(status: String) = when (status) {
    "upcoming" -> HoloColors.Warning
    "reservation" -> HoloColors.Success
    "preorder" -> HoloColors.Info
    else -> HoloColors.Neutral400
}

fun formatPrice(price: Double?): String = price?.let { "%.2f\u20AC".format(it) } ?: ""

@Composable
fun StatusFilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusPill))
            .background(if (isSelected) HoloColors.Ink else HoloColors.Fog)
            .clickable { onClick() }
            .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xs)
    ) {
        Text(text, style = if (isSelected) HoloType.TitleMedium else HoloType.BodyMedium, color = if (isSelected) HoloColors.Paper else HoloColors.TextSecondary)
    }
}

@Composable
fun HeroDropCard(product: SbUpcomingDto) {
    Box(
        modifier = Modifier.fillMaxWidth().height(420.dp).padding(horizontal = HoloSpacing.md).clip(RoundedCornerShape(HoloSpacing.RadiusLg))
    ) {
        if (product.imageUrl != null) {
            AsyncImage(model = product.imageUrl, contentDescription = product.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Box(Modifier.fillMaxSize().background(HoloColors.Neutral800))
        }
        Box(Modifier.fillMaxWidth().height(250.dp).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(HoloColors.Ink.copy(alpha = 0f), HoloColors.InkOverlay80))))
        Box(Modifier.padding(HoloSpacing.sm).align(Alignment.TopStart).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(statusColor(product.status)).padding(horizontal = HoloSpacing.sm, vertical = HoloSpacing.xxs)) {
            Text(statusLabel(product.status), style = HoloType.LabelSmall, color = HoloColors.Paper)
        }
        Column(Modifier.align(Alignment.BottomStart).padding(HoloSpacing.lg)) {
            Text(product.subtitle ?: "", style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted)
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text(product.title, style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
            Spacer(Modifier.height(HoloSpacing.xs))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DateRange, null, tint = HoloColors.TextOnDarkMuted, modifier = Modifier.size(HoloSpacing.IconSizeSmall))
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Text(product.launchDate?.take(10) ?: "", style = HoloType.TitleSmall, color = HoloColors.TextOnDarkMuted)
                }
                Text(formatPrice(product.price), style = HoloType.HeadlineMedium, color = HoloColors.TextOnDark)
            }
            Spacer(Modifier.height(HoloSpacing.md))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) { Icon(Icons.Outlined.Share, "Compartir", tint = HoloColors.TextOnDark, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }
                Button(onClick = {}, shape = RoundedCornerShape(HoloSpacing.RadiusPill), colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Paper), contentPadding = PaddingValues(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xs)) {
                    Icon(Icons.Filled.Notifications, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeSmall))
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Text("Notificame", style = HoloType.TitleMedium, color = HoloColors.Ink)
                }
            }
        }
    }
}

@Composable
fun LightDropCard(product: SbUpcomingDto) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Fog)
    ) {
        Column(Modifier.fillMaxWidth().padding(HoloSpacing.lg)) {
            Text(product.subtitle ?: "", style = HoloType.BodyMedium, color = HoloColors.TextSecondary)
            Spacer(Modifier.height(HoloSpacing.xxs))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(product.title, style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary, modifier = Modifier.weight(1f))
                Text(formatPrice(product.price), style = HoloType.HeadlineSmall, color = HoloColors.TextPrimary)
            }
            Spacer(Modifier.height(HoloSpacing.xxs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DateRange, null, tint = HoloColors.TextTertiary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(HoloSpacing.xxs))
                Text(product.launchDate?.take(10) ?: "", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
                Spacer(Modifier.width(HoloSpacing.xs))
                Box(Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusXs)).background(statusColor(product.status).copy(alpha = 0.15f)).padding(horizontal = HoloSpacing.xs, vertical = 3.dp)) {
                    Text(statusLabel(product.status), style = HoloType.LabelSmall, color = statusColor(product.status))
                }
            }
            Spacer(Modifier.height(HoloSpacing.md))
            if (product.imageUrl != null) {
                Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                    AsyncImage(model = product.imageUrl, contentDescription = product.title, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(HoloSpacing.RadiusMd)), contentScale = ContentScale.Fit)
                }
                Spacer(Modifier.height(HoloSpacing.md))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) { Icon(Icons.Outlined.Share, "Compartir", tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }
                Button(onClick = {}, shape = RoundedCornerShape(HoloSpacing.RadiusPill), colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink), contentPadding = PaddingValues(horizontal = HoloSpacing.xl, vertical = HoloSpacing.xs)) {
                    Text("NOTIFICAME", style = HoloType.LabelMedium, color = HoloColors.Paper)
                }
            }
        }
    }
}

@Composable
fun DarkDropCard(product: SbUpcomingDto) {
    Box(
        modifier = Modifier.fillMaxWidth().height(340.dp).padding(horizontal = HoloSpacing.md).clip(RoundedCornerShape(HoloSpacing.RadiusLg))
    ) {
        if (product.imageUrl != null) {
            AsyncImage(model = product.imageUrl, contentDescription = product.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Box(Modifier.fillMaxSize().background(HoloColors.Neutral800))
        }
        Box(Modifier.fillMaxWidth().height(200.dp).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(HoloColors.Ink.copy(alpha = 0f), HoloColors.InkOverlay80))))
        Box(Modifier.padding(HoloSpacing.sm).align(Alignment.TopStart).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(statusColor(product.status)).padding(horizontal = HoloSpacing.xs, vertical = HoloSpacing.xxs)) {
            Text(statusLabel(product.status), style = HoloType.LabelSmall, color = HoloColors.Paper)
        }
        Column(Modifier.align(Alignment.BottomStart).padding(HoloSpacing.md)) {
            Text(product.subtitle ?: "", style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted)
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text(product.title, style = HoloType.HeadlineMedium, color = HoloColors.TextOnDark)
            Spacer(Modifier.height(HoloSpacing.xxs))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DateRange, null, tint = HoloColors.TextOnDarkMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Text(product.launchDate?.take(10) ?: "", style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted)
                }
                Text(formatPrice(product.price), style = HoloType.HeadlineSmall, color = HoloColors.TextOnDark)
            }
            Spacer(Modifier.height(HoloSpacing.sm))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(36.dp)) { Icon(Icons.Outlined.Share, "Compartir", tint = HoloColors.TextOnDark, modifier = Modifier.size(HoloSpacing.IconSizeDefault)) }
                Button(onClick = {}, shape = RoundedCornerShape(HoloSpacing.RadiusPill), colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Paper), contentPadding = PaddingValues(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xs)) {
                    Icon(Icons.Filled.Notifications, null, tint = HoloColors.Ink, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Text("Notificame", style = HoloType.TitleSmall, color = HoloColors.Ink)
                }
            }
        }
    }
}