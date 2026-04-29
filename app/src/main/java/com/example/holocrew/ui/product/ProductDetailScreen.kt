package com.example.holocrew.ui.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

/**
 * ProductDetailScreen — Pantalla de detalle de producto.
 *
 * El "momento drop" de la app. Estilo SNKRS:
 *  - Imagen hero grande (420dp) con gradiente blanco inferior
 *  - Badge "OFERTA" en rojo Pulse si hay precio original
 *  - Info del producto: marca, título, precio, rating con estrellas
 *  - Dos CTAs: "COMPRAR AHORA" (negro pill) y "AÑADIR A LA CESTA" (outlined 2dp)
 *  - BottomSheet para selección de talla antes de añadir
 *  - Card de info de envío (gratis >150€, devolución, autenticidad)
 *  - Descripción + features con bullets
 *  - Carrusel "También te puede gustar" con productos relacionados
 *
 * @param navController Controlador de navegación.
 * @param productId UUID del producto (String) — viene de la ruta de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, productId: String) {

    // ── Buscar producto en los datos mock por UUID ────────────────────────────
    val product = remember(productId) { mockProducts.find { it.id == productId } }

    // Estado de error: producto no encontrado
    if (product == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Producto no encontrado",
                    style = HoloType.HeadlineSmall,
                    color = HoloColors.TextSecondary
                )
                Spacer(Modifier.height(HoloSpacing.md))
                Button(onClick = { navController.navigateUp() }) {
                    Text("Volver")
                }
            }
        }
        return
    }

    // ── Estado y dependencias ─────────────────────────────────────────────────
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val favoriteIds by FavoritesManager.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(product.id)
    val snackbarHostState = remember { SnackbarHostState() }

    // Control del BottomSheet de tallas
    var showSizeSheet by remember { mutableStateOf(false) }
    var sizeSheetAction by remember { mutableStateOf("cart") }  // "cart" o "buy"
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Productos relacionados (misma categoría, excluyendo el actual)
    val relatedProducts = remember {
        mockProducts.filter { it.category == product.category && it.id != product.id }.take(4)
    }

    // Rango de tallas para mostrar ("XS – XXL" o "Único")
    val sizeRange = if (product.sizes.size > 1)
        "${product.sizes.first()} – ${product.sizes.last()}"
    else
        product.sizes.firstOrNull() ?: ""

    // ══════════════════════════════════════════════════════════════════════════
    // BOTTOM SHEET — Selección de talla
    // Se muestra antes de añadir al carrito o comprar.
    // ══════════════════════════════════════════════════════════════════════════
    if (showSizeSheet && product.sizes.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showSizeSheet = false },
            sheetState = sheetState,
            containerColor = HoloColors.Paper,
            shape = RoundedCornerShape(topStart = HoloSpacing.RadiusXl, topEnd = HoloSpacing.RadiusXl)
        ) {
            SizeSelectionSheet(
                sizes = product.sizes,
                onSizeSelected = { selectedSize ->
                    showSizeSheet = false
                    scope.launch {
                        CartManager.addItem(context, product.id)
                        if (sizeSheetAction == "buy") {
                            navController.navigate("cart") { launchSingleTop = true }
                        } else {
                            snackbarHostState.showSnackbar("${product.title} ($selectedSize) añadido al carrito")
                        }
                    }
                }
            )
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SCAFFOLD — TopBar transparente + contenido scrolleable
    // ══════════════════════════════════════════════════════════════════════════
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = HoloColors.Ink
                        )
                    }
                },
                actions = {
                    // Botón favorito — corazón rojo Pulse si activo
                    IconButton(onClick = {
                        scope.launch { FavoritesManager.toggleFavorite(context, product.id) }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) HoloColors.Pulse else HoloColors.Ink
                        )
                    }
                    // Botón compartir
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Share, "Compartir", tint = HoloColors.Ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Paper)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(HoloColors.Paper)
        ) {

            // ══════════════════════════════════════════════════════════════════
            // IMAGEN HERO — 420dp con gradiente blanco inferior
            // ══════════════════════════════════════════════════════════════════
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    // Imagen del producto
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradiente blanco inferior (transición suave a la info)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(HoloColors.Paper.copy(alpha = 0f), HoloColors.Paper)
                                )
                            )
                    )

                    // Badge "OFERTA" en rojo si tiene precio original
                    if (product.originalPrice != null) {
                        Box(
                            modifier = Modifier
                                .padding(HoloSpacing.md)
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                                .background(HoloColors.Pulse)
                                .padding(horizontal = HoloSpacing.sm, vertical = HoloSpacing.xxs)
                        ) {
                            Text(
                                text = "OFERTA",
                                style = HoloType.LabelSmall,
                                color = HoloColors.Paper
                            )
                        }
                    }
                }
            }

            // ══════════════════════════════════════════════════════════════════
            // INFO DEL PRODUCTO — marca, título, precio, rating, CTAs
            // ══════════════════════════════════════════════════════════════════
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HoloSpacing.md)
                ) {
                    // Marca en label style
                    Text(
                        text = product.brand,
                        style = HoloType.LabelMedium,
                        color = HoloColors.TextTertiary
                    )

                    Spacer(Modifier.height(HoloSpacing.xxs))

                    // Título + precio lado a lado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Columna izquierda: título y subtítulo
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.title,
                                style = HoloType.HeadlineLarge,
                                color = HoloColors.TextPrimary
                            )
                            Text(
                                text = product.subtitle,
                                style = HoloType.BodyMedium,
                                color = HoloColors.TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Columna derecha: precio actual + precio original tachado
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = product.price,
                                style = HoloType.HeadlineLarge,
                                color = HoloColors.TextPrimary
                            )
                            product.originalPrice?.let {
                                Text(
                                    text = it,
                                    style = HoloType.BodyMedium,
                                    color = HoloColors.TextTertiary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }

                    // Rango de tallas disponibles
                    if (sizeRange.isNotEmpty()) {
                        Spacer(Modifier.height(HoloSpacing.xs))
                        Text(
                            text = sizeRange,
                            style = HoloType.BodyMedium,
                            color = HoloColors.TextSecondary
                        )
                    }

                    Spacer(Modifier.height(HoloSpacing.sm))

                    // ── Rating con estrellas ──────────────────────────────────
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Estrella",
                                tint = if (index < product.rating.toInt())
                                    HoloColors.Warning    // Amarillo para estrellas activas
                                else
                                    HoloColors.Neutral200, // Gris para inactivas
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(HoloSpacing.xs))
                        Text(
                            text = "${product.rating}",
                            style = HoloType.TitleMedium,
                            color = HoloColors.TextPrimary
                        )
                        Spacer(Modifier.width(HoloSpacing.xxs))
                        Text(
                            text = "(${product.reviewCount} reseñas)",
                            style = HoloType.BodyMedium,
                            color = HoloColors.TextTertiary
                        )
                    }

                    Spacer(Modifier.height(HoloSpacing.lg))

                    // ══════════════════════════════════════════════════════════
                    // CTAs — Comprar ahora (negro pill) + Añadir a cesta (outlined)
                    // ══════════════════════════════════════════════════════════

                    // Botón primario: COMPRAR AHORA
                    Button(
                        onClick = {
                            if (product.sizes.isNotEmpty()) {
                                sizeSheetAction = "buy"
                                showSizeSheet = true
                            } else {
                                scope.launch {
                                    CartManager.addItem(context, product.id)
                                    navController.navigate("cart") { launchSingleTop = true }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HoloSpacing.ButtonHeight),
                        shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                        colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink)
                    ) {
                        Text(
                            text = "COMPRAR AHORA",
                            style = HoloType.LabelLarge,
                            color = HoloColors.Paper
                        )
                    }

                    Spacer(Modifier.height(HoloSpacing.xs))

                    // Botón secundario: AÑADIR A LA CESTA (borde 2dp)
                    OutlinedButton(
                        onClick = {
                            if (product.sizes.isNotEmpty()) {
                                sizeSheetAction = "cart"
                                showSizeSheet = true
                            } else {
                                scope.launch {
                                    CartManager.addItem(context, product.id)
                                    snackbarHostState.showSnackbar("${product.title} añadido al carrito")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HoloSpacing.ButtonHeight),
                        shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = HoloSpacing.BorderDefault
                        )
                    ) {
                        Text(
                            text = "AÑADIR A LA CESTA",
                            style = HoloType.LabelLarge,
                            color = HoloColors.TextPrimary
                        )
                    }

                    Spacer(Modifier.height(HoloSpacing.xl))
                    Divider(color = HoloColors.Neutral100)
                    Spacer(Modifier.height(HoloSpacing.xl))

                    // ══════════════════════════════════════════════════════════
                    // INFO DE ENVÍO — card gris con 3 filas
                    // ══════════════════════════════════════════════════════════
                    ShippingInfoCard()

                    Spacer(Modifier.height(HoloSpacing.xl))
                    Divider(color = HoloColors.Neutral100)
                    Spacer(Modifier.height(HoloSpacing.xl))

                    // ══════════════════════════════════════════════════════════
                    // DESCRIPCIÓN + FEATURES
                    // ══════════════════════════════════════════════════════════
                    Text(
                        text = product.description,
                        style = HoloType.BodyLarge,
                        color = HoloColors.TextSecondary
                    )

                    Spacer(Modifier.height(HoloSpacing.lg))

                    // Lista de features con bullets negros
                    product.features.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = HoloSpacing.xs)
                        ) {
                            // Bullet negro
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(HoloColors.Ink)
                            )
                            Spacer(Modifier.width(HoloSpacing.sm))
                            Text(
                                text = feature,
                                style = HoloType.BodyMedium,
                                color = HoloColors.Neutral600
                            )
                        }
                    }

                    Spacer(Modifier.height(HoloSpacing.xl))
                }
            }

            // ══════════════════════════════════════════════════════════════════
            // PRODUCTOS RELACIONADOS — carrusel horizontal
            // ══════════════════════════════════════════════════════════════════
            if (relatedProducts.isNotEmpty()) {
                item {
                    Text(
                        text = "TAMBIÉN TE PUEDE GUSTAR",
                        style = HoloType.LabelLarge,
                        color = HoloColors.TextPrimary,
                        modifier = Modifier.padding(horizontal = HoloSpacing.md)
                    )

                    Spacer(Modifier.height(HoloSpacing.sm))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = HoloSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)
                    ) {
                        items(relatedProducts) { related ->
                            RelatedProductCard(
                                product = related,
                                onClick = {
                                    navController.navigate("product_detail/${related.id}") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.xxxl)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES DEL DETALLE
// ══════════════════════════════════════════════════════════════════════════════

/**
 * SizeSelectionSheet — BottomSheet con grid de tallas.
 *
 * Grid de 3 columnas. Talla seleccionada = fondo negro + texto blanco.
 * Botón "AÑADIR A LA CESTA" se activa solo cuando hay talla seleccionada.
 */
@Composable
fun SizeSelectionSheet(sizes: List<String>, onSizeSelected: (String) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.md)
    ) {
        Text(
            text = "Selecciona una talla",
            style = HoloType.HeadlineMedium,
            color = HoloColors.TextPrimary
        )

        Spacer(Modifier.height(HoloSpacing.lg))

        // Grid 3 columnas de tallas
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(HoloSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(HoloSpacing.xs),
            modifier = Modifier.height((((sizes.size + 2) / 3) * 56).dp)
        ) {
            items(sizes) { size ->
                val isSelected = sizes.indexOf(size) == selectedIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                        .then(
                            if (isSelected)
                                Modifier.background(HoloColors.Ink)
                            else
                                Modifier
                                    .border(HoloSpacing.BorderHairline, HoloColors.BorderSubtle, RoundedCornerShape(HoloSpacing.RadiusSm))
                                    .background(HoloColors.Paper)
                        )
                        .clickable { selectedIndex = sizes.indexOf(size) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = size,
                        style = if (isSelected) HoloType.TitleMedium else HoloType.BodyMedium,
                        color = if (isSelected) HoloColors.Paper else HoloColors.TextPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(HoloSpacing.xl))

        // Botón confirmar — deshabilitado si no hay talla seleccionada
        Button(
            onClick = { if (selectedIndex >= 0) onSizeSelected(sizes[selectedIndex]) },
            modifier = Modifier
                .fillMaxWidth()
                .height(HoloSpacing.ButtonHeight),
            shape = RoundedCornerShape(HoloSpacing.RadiusPill),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedIndex >= 0) HoloColors.Ink else HoloColors.Neutral200,
                disabledContainerColor = HoloColors.Neutral200
            ),
            enabled = selectedIndex >= 0
        ) {
            Text(
                text = "AÑADIR A LA CESTA",
                style = HoloType.LabelLarge,
                color = if (selectedIndex >= 0) HoloColors.Paper else HoloColors.TextTertiary
            )
        }

        Spacer(Modifier.height(HoloSpacing.lg))
    }
}

/**
 * ShippingInfoCard — Card gris con info de envío, devolución y autenticidad.
 */
@Composable
fun ShippingInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Fog),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(HoloSpacing.md)) {
            ShippingRow(Icons.Filled.LocalShipping, "Envío gratis", "En pedidos superiores a 150€")
            Spacer(Modifier.height(HoloSpacing.sm))
            ShippingRow(Icons.Filled.Loop, "Devolución gratuita", "30 días para devoluciones")
            Spacer(Modifier.height(HoloSpacing.sm))
            ShippingRow(Icons.Filled.Verified, "Producto original", "Garantía de autenticidad")
        }
    }
}

/**
 * ShippingRow — Fila individual dentro de ShippingInfoCard.
 */
@Composable
fun ShippingRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HoloColors.Ink,
            modifier = Modifier.size(HoloSpacing.IconSizeDefault)
        )
        Spacer(Modifier.width(HoloSpacing.sm))
        Column {
            Text(text = title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
            Text(text = subtitle, style = HoloType.BodySmall, color = HoloColors.TextTertiary)
        }
    }
}

/**
 * RelatedProductCard — Card compacta para el carrusel "También te puede gustar".
 */
@Composable
fun RelatedProductCard(product: ProductDetail, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                .background(HoloColors.Fog)
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(HoloSpacing.RadiusMd)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(HoloSpacing.xs))

        Text(
            text = product.title,
            style = HoloType.TitleSmall,
            color = HoloColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = product.price,
            style = HoloType.TitleMedium,
            color = HoloColors.TextPrimary,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}