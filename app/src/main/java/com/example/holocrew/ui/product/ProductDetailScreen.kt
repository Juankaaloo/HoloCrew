package com.example.holocrew.ui.product

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.holocrew.data.network.CartRepository
import com.example.holocrew.data.network.ProductRepository
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.holocrew.components.SoldOutOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, productId: Int) {

    // Estado del producto cargado desde Supabase
    var product by remember { mutableStateOf<ProductDetail?>(null) }
    var relatedProducts by remember { mutableStateOf<List<ProductDetail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val favoriteIds by WishlistRepository.favoriteIds.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showSizeSheet by remember { mutableStateOf(false) }
    var sizeSheetAction by remember { mutableStateOf("cart") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Cargar producto desde Supabase
    LaunchedEffect(productId) {
        isLoading = true
        product = ProductRepository.getById(productId)
        product?.let { p ->
            if (p.categoryId != null) {
                relatedProducts = ProductRepository.getRelated(p.id, p.categoryId, 4)
            }
        }
        isLoading = false
    }

    // Estado de carga
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HoloColors.Ink)
        }
        return
    }

    // Producto no encontrado
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

    val p = product!!
    val isFavorite = favoriteIds.contains(p.id)
    val sizeRange = if (p.sizes.size > 1) "${p.sizes.first()} - ${p.sizes.last()}" else p.sizes.firstOrNull() ?: ""

    // BottomSheet de tallas
    if (showSizeSheet && p.sizes.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showSizeSheet = false },
            sheetState = sheetState,
            containerColor = HoloColors.Paper,
            shape = RoundedCornerShape(topStart = HoloSpacing.RadiusXl, topEnd = HoloSpacing.RadiusXl)
        ) {
            SizeSelectionSheet(
                sizes = p.sizes,
                onSizeSelected = { selectedSize ->
                    showSizeSheet = false
                    scope.launch {
                        CartRepository.addItem(
                            productId = p.id,
                            price = p.priceRaw,
                            size = selectedSize
                        )
                        if (sizeSheetAction == "buy") {
                            navController.navigate("cart") { launchSingleTop = true }
                        } else {
                            snackbarHostState.showSnackbar("${p.title} ($selectedSize) agregado al carrito")
                        }
                    }
                }
            )
        }
    }

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
                    IconButton(onClick = {
                        scope.launch { WishlistRepository.toggleFavorite(p.id, p.priceRaw) }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) HoloColors.Pulse else HoloColors.Ink
                        )
                    }
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

            // IMAGEN HERO
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    AsyncImage(
                        model = p.imageUrl,
                        contentDescription = p.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (p.stock <= 0) { SoldOutOverlay() }
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
                    if (p.originalPrice != null) {
                        Box(
                            modifier = Modifier
                                .padding(HoloSpacing.md)
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(HoloSpacing.RadiusSm))
                                .background(HoloColors.Pulse)
                                .padding(horizontal = HoloSpacing.sm, vertical = HoloSpacing.xxs)
                        ) {
                            Text(text = "OFERTA", style = HoloType.LabelSmall, color = HoloColors.Paper)
                        }
                    }
                }
            }

            // INFO DEL PRODUCTO
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HoloSpacing.md)
                ) {
                    Text(text = p.brand, style = HoloType.LabelMedium, color = HoloColors.TextTertiary)
                    Spacer(Modifier.height(HoloSpacing.xxs))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = p.title, style = HoloType.HeadlineLarge, color = HoloColors.TextPrimary)
                            Text(
                                text = p.subtitle,
                                style = HoloType.BodyMedium,
                                color = HoloColors.TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = p.price, style = HoloType.HeadlineLarge, color = HoloColors.TextPrimary)
                            p.originalPrice?.let {
                                Text(
                                    text = it,
                                    style = HoloType.BodyMedium,
                                    color = HoloColors.TextTertiary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }

                    if (sizeRange.isNotEmpty()) {
                        Spacer(Modifier.height(HoloSpacing.xs))
                        Text(text = sizeRange, style = HoloType.BodyMedium, color = HoloColors.TextSecondary)
                    }

                    Spacer(Modifier.height(HoloSpacing.sm))

                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Estrella",
                                tint = if (index < p.rating.toInt()) HoloColors.Warning else HoloColors.Neutral200,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(HoloSpacing.xs))
                        Text(text = "${p.rating}", style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                        Spacer(Modifier.width(HoloSpacing.xxs))
                        Text(
                            text = "(${p.reviewCount} resenas)",
                            style = HoloType.BodyMedium,
                            color = HoloColors.TextTertiary
                        )
                    }

                    Spacer(Modifier.height(HoloSpacing.lg))

                    // CTAs
                    Button(
                        onClick = {
                            if (p.sizes.isNotEmpty()) {
                                sizeSheetAction = "buy"
                                showSizeSheet = true
                            } else {
                                scope.launch {
                                    CartRepository.addItem(productId = p.id, price = p.priceRaw)
                                    navController.navigate("cart") { launchSingleTop = true }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HoloSpacing.ButtonHeight),
                        shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                        colors = ButtonDefaults.buttonColors(containerColor = if (p.stock > 0) HoloColors.Ink else HoloColors.Neutral300),
                        enabled = p.stock > 0
                    ) {
                        Text(text = if (p.stock > 0) "COMPRAR AHORA" else "SIN STOCK", style = HoloType.LabelLarge, color = HoloColors.Paper)
                    }

                    Spacer(Modifier.height(HoloSpacing.xs))

                    if (p.stock > 0) {
                        OutlinedButton(
                            onClick = {
                                if (p.sizes.isNotEmpty()) {
                                    sizeSheetAction = "cart"
                                    showSizeSheet = true
                                } else {
                                    scope.launch {
                                        CartRepository.addItem(productId = p.id, price = p.priceRaw)
                                        snackbarHostState.showSnackbar("${p.title} agregado al carrito")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(HoloSpacing.ButtonHeight),
                            shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = HoloSpacing.BorderDefault)
                        ) {
                            Text(text = "AGREGAR A LA CESTA", style = HoloType.LabelLarge, color = HoloColors.TextPrimary)
                        }
                    }

                    Spacer(Modifier.height(HoloSpacing.xl))
                    Divider(color = HoloColors.Neutral100)
                    Spacer(Modifier.height(HoloSpacing.xl))

                    ShippingInfoCard()

                    Spacer(Modifier.height(HoloSpacing.xl))
                    Divider(color = HoloColors.Neutral100)
                    Spacer(Modifier.height(HoloSpacing.xl))

                    // Descripcion
                    if (p.description.isNotEmpty()) {
                        Text(text = p.description, style = HoloType.BodyLarge, color = HoloColors.TextSecondary)
                        Spacer(Modifier.height(HoloSpacing.lg))
                    }

                    // Features
                    p.features.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = HoloSpacing.xs)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(HoloColors.Ink)
                            )
                            Spacer(Modifier.width(HoloSpacing.sm))
                            Text(text = feature, style = HoloType.BodyMedium, color = HoloColors.Neutral600)
                        }
                    }

                    Spacer(Modifier.height(HoloSpacing.xl))
                }
            }

            // PRODUCTOS RELACIONADOS
            if (relatedProducts.isNotEmpty()) {
                item {
                    Text(
                        text = "TAMBIEN TE PUEDE GUSTAR",
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
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun SizeSelectionSheet(sizes: List<String>, onSizeSelected: (String) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.md)
    ) {
        Text(text = "Selecciona una talla", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
        Spacer(Modifier.height(HoloSpacing.lg))

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
                text = "AGREGAR A LA CESTA",
                style = HoloType.LabelLarge,
                color = if (selectedIndex >= 0) HoloColors.Paper else HoloColors.TextTertiary
            )
        }

        Spacer(Modifier.height(HoloSpacing.lg))
    }
}

@Composable
fun ShippingInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Fog),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(HoloSpacing.md)) {
            ShippingRow(Icons.Filled.LocalShipping, "Envio gratis", "En pedidos superiores a 150 EUR")
            Spacer(Modifier.height(HoloSpacing.sm))
            ShippingRow(Icons.Filled.Loop, "Devolucion gratuita", "30 dias para devoluciones")
            Spacer(Modifier.height(HoloSpacing.sm))
            ShippingRow(Icons.Filled.Verified, "Producto original", "Garantia de autenticidad")
        }
    }
}

@Composable
fun ShippingRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
        Spacer(Modifier.width(HoloSpacing.sm))
        Column {
            Text(text = title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
            Text(text = subtitle, style = HoloType.BodySmall, color = HoloColors.TextTertiary)
        }
    }
}

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
            AsyncImage(
                model = product.imageUrl,
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