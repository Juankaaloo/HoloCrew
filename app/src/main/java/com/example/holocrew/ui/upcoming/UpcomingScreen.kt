/**
 * UpcomingScreen.kt
 *
 * Pantalla de próximos lanzamientos / drops en HoloCrew.
 *
 * Diseño SNKRS con:
 *  - Header con título + contador de lanzamientos
 *  - Filtros por estado (chips negro/gris)
 *  - Hero card: primer drop a pantalla completa con gradiente + CTA
 *  - Cards alternas: claras (fondo Fog) y oscuras (imagen + gradiente)
 *  - Botón "Notifícame" en cada card
 *  - Labels rojos (Pulse) para acentos + badges de estado colorizados
 *
 * Los productos son datos mock locales (no vienen de la API).
 */
package com.example.holocrew.ui.upcoming

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.R
import com.example.holocrew.components.BottomNavigationBar
import com.example.holocrew.components.CustomTopAppBar
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType

// ══════════════════════════════════════════════════════════════════════════════
// MODELO — Producto próximo (datos mock, no viene de la API)
// ══════════════════════════════════════════════════════════════════════════════
data class UpcomingProduct(
    val id: Int,
    val title: String,
    val subtitle: String,
    val launchDate: String,
    val imageRes: Int,       // Drawable local
    val status: String,      // "Próximamente", "Reserva Abierta", "Pre-orden"
    val price: String? = null
)

/**
 * UpcomingScreen — Pantalla de próximos drops.
 *
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(navController: NavController) {

    // ── Filtro seleccionado ───────────────────────────────────────────────────
    var selectedFilter by remember { mutableStateOf("Todos") }

    // ── Datos mock de próximos lanzamientos ───────────────────────────────────
    val allProducts = remember {
        listOf(
            UpcomingProduct(1, "Winter Collection 2025", "Colección completa de invierno", "25 Dic 2025", R.drawable.outerwear, "Próximamente", "299.99€"),
            UpcomingProduct(2, "Limited Edition Denim", "Jeans numerados · Solo 100 unidades", "15 Ene 2026", R.drawable.newdenims, "Reserva Abierta", "199.99€"),
            UpcomingProduct(3, "HOLOCREW x Artist Collab", "Colaboración exclusiva con artista urbano", "30 Ene 2026", R.drawable.tops, "Próximamente", "249.99€"),
            UpcomingProduct(4, "Techwear Collection", "Ropa técnica para clima extremo", "10 Feb 2026", R.drawable.outerwear, "Pre-orden", "349.99€"),
            UpcomingProduct(5, "Summer Essentials Pack", "Pack verano con 3 piezas básicas", "1 Mar 2026", R.drawable.glory_holo_polo, "Próximamente", "159.99€"),
            UpcomingProduct(6, "HoloCrew x Racing", "Colección motorsport edición especial", "15 Mar 2026", R.drawable.offroad_racing_cap, "Próximamente", "279.99€")
        )
    }

    val filters = listOf("Todos", "Próximamente", "Reserva Abierta", "Pre-orden")

    // Filtrar productos según el chip seleccionado
    val filteredProducts = remember(selectedFilter) {
        if (selectedFilter == "Todos") allProducts
        else allProducts.filter { it.status == selectedFilter }
    }

    // El primer producto se muestra como hero, el resto como cards alternas
    val heroProduct = filteredProducts.firstOrNull()
    val restProducts = if (filteredProducts.size > 1) filteredProducts.drop(1) else emptyList()

    // ── Scaffold con topBar + bottomNav ───────────────────────────────────────
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.BottomNavHeight)
        ) {

            // ══════════════════════════════════════════════════════════════════
            // HEADER — título + contador + filtros
            // ══════════════════════════════════════════════════════════════════
            item {
                Column(
                    modifier = Modifier.padding(
                        horizontal = HoloSpacing.md,
                        vertical = HoloSpacing.md
                    )
                ) {
                    Text(
                        text = "Próximos Drops",
                        style = HoloType.HeadlineLarge,
                        color = HoloColors.TextPrimary
                    )
                    Text(
                        text = "${filteredProducts.size} lanzamientos programados",
                        style = HoloType.BodyMedium,
                        color = HoloColors.TextTertiary,
                        modifier = Modifier.padding(top = HoloSpacing.xxs)
                    )

                    Spacer(Modifier.height(HoloSpacing.md))

                    // Chips de filtro por estado
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(HoloSpacing.xs)) {
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

            // ══════════════════════════════════════════════════════════════════
            // ESTADO VACÍO — cuando no hay drops para el filtro
            // ══════════════════════════════════════════════════════════════════
            if (filteredProducts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HoloSpacing.xxxl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay drops para este filtro",
                            style = HoloType.TitleLarge,
                            color = HoloColors.TextSecondary
                        )
                        Text(
                            text = "Intenta con otro estado",
                            style = HoloType.BodyMedium,
                            color = HoloColors.TextTertiary,
                            modifier = Modifier.padding(top = HoloSpacing.xxs)
                        )
                    }
                }
            }

            // ══════════════════════════════════════════════════════════════════
            // HERO CARD — primer drop, card grande con gradiente
            // ══════════════════════════════════════════════════════════════════
            if (heroProduct != null) {
                item {
                    HeroDropCard(product = heroProduct)
                    Spacer(Modifier.height(HoloSpacing.md))
                }
            }

            // ══════════════════════════════════════════════════════════════════
            // RESTO — cards alternas (clara / oscura)
            // ══════════════════════════════════════════════════════════════════
            itemsIndexed(restProducts) { index, product ->
                if (index % 2 == 0) {
                    LightDropCard(product = product)    // Fondo gris Fog
                } else {
                    DarkDropCard(product = product)     // Imagen + gradiente negro
                }
                Spacer(Modifier.height(HoloSpacing.md))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

/**
 * StatusFilterChip — Chip de filtro por estado de drop.
 * Negro si seleccionado, gris Fog si no.
 */
@Composable
fun StatusFilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(HoloSpacing.RadiusPill))
            .background(if (isSelected) HoloColors.Ink else HoloColors.Fog)
            .clickable { onClick() }
            .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xs)
    ) {
        Text(
            text = text,
            style = if (isSelected) HoloType.TitleMedium else HoloType.BodyMedium,
            color = if (isSelected) HoloColors.Paper else HoloColors.TextSecondary
        )
    }
}

/**
 * HeroDropCard — Card hero para el primer drop.
 *
 * Imagen fullwidth 420dp con gradiente negro, badge de estado,
 * info superpuesta (subtítulo, título, fecha, precio) y botones.
 */
@Composable
fun HeroDropCard(product: UpcomingProduct) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(horizontal = HoloSpacing.md)
            .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradiente negro desde abajo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(HoloColors.Ink.copy(alpha = 0f), HoloColors.InkOverlay80)
                    )
                )
        )

        // Badge de estado (arriba-izquierda)
        Box(
            modifier = Modifier
                .padding(HoloSpacing.sm)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                .background(statusColor(product.status))
                .padding(horizontal = HoloSpacing.sm, vertical = HoloSpacing.xxs)
        ) {
            Text(
                text = product.status.uppercase(),
                style = HoloType.LabelSmall,
                color = HoloColors.Paper
            )
        }

        // Info superpuesta abajo
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(HoloSpacing.lg)
        ) {
            Text(
                text = product.subtitle,
                style = HoloType.BodySmall,
                color = HoloColors.TextOnDarkMuted
            )
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text(
                text = product.title,
                style = HoloType.HeadlineLarge,
                color = HoloColors.TextOnDark
            )
            Spacer(Modifier.height(HoloSpacing.xs))

            // Fecha + precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = HoloColors.TextOnDarkMuted,
                        modifier = Modifier.size(HoloSpacing.IconSizeSmall)
                    )
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Text(
                        text = product.launchDate,
                        style = HoloType.TitleSmall,
                        color = HoloColors.TextOnDarkMuted
                    )
                }
                Text(
                    text = product.price ?: "",
                    style = HoloType.HeadlineMedium,
                    color = HoloColors.TextOnDark
                )
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // Botones: compartir + Notifícame
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Compartir",
                        tint = HoloColors.TextOnDark,
                        modifier = Modifier.size(HoloSpacing.IconSizeDefault)
                    )
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                    colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Paper),
                    contentPadding = PaddingValues(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = HoloColors.Ink,
                        modifier = Modifier.size(HoloSpacing.IconSizeSmall)
                    )
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Text(
                        text = "Notifícame",
                        style = HoloType.TitleMedium,
                        color = HoloColors.Ink
                    )
                }
            }
        }
    }
}

/**
 * LightDropCard — Card clara sobre fondo Fog.
 *
 * Subtítulo, título + precio, fecha + badge de estado,
 * imagen centrada, y fila compartir + Notifícame.
 */
@Composable
fun LightDropCard(product: UpcomingProduct) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HoloSpacing.md),
        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Fog)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HoloSpacing.lg)
        ) {
            // Subtítulo
            Text(
                text = product.subtitle,
                style = HoloType.BodyMedium,
                color = HoloColors.TextSecondary
            )
            Spacer(Modifier.height(HoloSpacing.xxs))

            // Título + precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.title,
                    style = HoloType.HeadlineMedium,
                    color = HoloColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = product.price ?: "",
                    style = HoloType.HeadlineSmall,
                    color = HoloColors.TextPrimary
                )
            }

            Spacer(Modifier.height(HoloSpacing.xxs))

            // Fecha + badge de estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = HoloColors.TextTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(HoloSpacing.xxs))
                Text(
                    text = product.launchDate,
                    style = HoloType.BodySmall,
                    color = HoloColors.TextTertiary
                )
                Spacer(Modifier.width(HoloSpacing.xs))

                // Badge de estado con color de fondo semitransparente
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(HoloSpacing.RadiusXs))
                        .background(statusColor(product.status).copy(alpha = 0.15f))
                        .padding(horizontal = HoloSpacing.xs, vertical = 3.dp)
                ) {
                    Text(
                        text = product.status,
                        style = HoloType.LabelSmall,
                        color = statusColor(product.status)
                    )
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // Imagen del producto centrada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(HoloSpacing.RadiusMd)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // Compartir + Notifícame
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Compartir",
                        tint = HoloColors.Ink,
                        modifier = Modifier.size(HoloSpacing.IconSizeDefault)
                    )
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                    colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                    contentPadding = PaddingValues(horizontal = HoloSpacing.xl, vertical = HoloSpacing.xs)
                ) {
                    Text(
                        text = "NOTIFÍCAME",
                        style = HoloType.LabelMedium,
                        color = HoloColors.Paper
                    )
                }
            }
        }
    }
}

/**
 * DarkDropCard — Card oscura con imagen de fondo + gradiente.
 *
 * Similar a HeroDropCard pero más compacta (340dp).
 */
@Composable
fun DarkDropCard(product: UpcomingProduct) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .padding(horizontal = HoloSpacing.md)
            .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradiente negro
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(HoloColors.Ink.copy(alpha = 0f), HoloColors.InkOverlay80)
                    )
                )
        )

        // Badge de estado
        Box(
            modifier = Modifier
                .padding(HoloSpacing.sm)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                .background(statusColor(product.status))
                .padding(horizontal = HoloSpacing.xs, vertical = HoloSpacing.xxs)
        ) {
            Text(
                text = product.status.uppercase(),
                style = HoloType.LabelSmall,
                color = HoloColors.Paper
            )
        }

        // Info superpuesta
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(HoloSpacing.md)
        ) {
            Text(
                text = product.subtitle,
                style = HoloType.BodySmall,
                color = HoloColors.TextOnDarkMuted
            )
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text(
                text = product.title,
                style = HoloType.HeadlineMedium,
                color = HoloColors.TextOnDark
            )
            Spacer(Modifier.height(HoloSpacing.xxs))

            // Fecha + precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = HoloColors.TextOnDarkMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Text(
                        text = product.launchDate,
                        style = HoloType.BodySmall,
                        color = HoloColors.TextOnDarkMuted
                    )
                }
                Text(
                    text = product.price ?: "",
                    style = HoloType.HeadlineSmall,
                    color = HoloColors.TextOnDark
                )
            }

            Spacer(Modifier.height(HoloSpacing.sm))

            // Compartir + Notifícame
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Compartir",
                        tint = HoloColors.TextOnDark,
                        modifier = Modifier.size(HoloSpacing.IconSizeDefault)
                    )
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                    colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Paper),
                    contentPadding = PaddingValues(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = HoloColors.Ink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(HoloSpacing.xxs))
                    Text(
                        text = "Notifícame",
                        style = HoloType.TitleSmall,
                        color = HoloColors.Ink
                    )
                }
            }
        }
    }
}

/**
 * Color semántico según el estado del drop.
 * Se usa para badges y pills de estado.
 */
fun statusColor(status: String) = when (status) {
    "Próximamente" -> HoloColors.Warning      // Naranja/amarillo
    "Reserva Abierta" -> HoloColors.Success   // Verde
    "Pre-orden" -> HoloColors.Info            // Azul
    else -> HoloColors.Neutral400             // Gris neutro
}