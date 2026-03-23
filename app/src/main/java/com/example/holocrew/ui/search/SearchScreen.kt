/**
 * SearchScreen.kt
 *
 * Pantalla de búsqueda de productos en la aplicación HoloCrew.
 * Permite al usuario buscar productos en tiempo real escribiendo
 * en un campo de texto. Los resultados se filtran instantáneamente
 * por nombre, subtítulo y categoría del producto.
 *
 * Estructura de la pantalla:
 *  1. Barra superior con campo de búsqueda y botón de cerrar
 *  2. Sugerencias rápidas (cuando el campo está vacío)
 *  3. Resultados de búsqueda en grid de 2 columnas (estilo SNKRS)
 *  4. Estado vacío cuando no hay coincidencias
 *
 * Los datos se filtran directamente desde [mockProducts] en Product.kt.
 */
package com.example.holocrew.ui.search

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.mockProducts

/**
 * Pantalla de búsqueda de productos.
 *
 * Al abrirse, el campo de texto recibe el foco automáticamente
 * para que el usuario pueda escribir directamente.
 *
 * @param navController Controlador de navegación para volver y navegar al detalle
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {

    // ── Estado de la búsqueda ──
    var searchQuery by remember { mutableStateOf("") }

    // FocusRequester para abrir el teclado automáticamente
    val focusRequester = remember { FocusRequester() }

    // Filtrar productos en tiempo real según el texto escrito
    // Busca coincidencias en título, subtítulo y categoría (sin distinguir mayúsculas)
    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            mockProducts.filter { product ->
                product.title.contains(searchQuery, ignoreCase = true) ||
                        product.subtitle.contains(searchQuery, ignoreCase = true) ||
                        product.category.contains(searchQuery, ignoreCase = true) ||
                        product.brand.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Sugerencias rápidas (categorías disponibles)
    val quickSuggestions = remember {
        listOf("Ropa", "Denim", "Accesorios", "Ropa Interior", "Hoodie", "Polo", "Premium")
    }

    // Abrir el teclado automáticamente al entrar a la pantalla
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        // ── Barra superior con campo de búsqueda ──
        topBar = {
            TopAppBar(
                title = {
                    // Campo de búsqueda personalizado
                    SearchTextField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClear = { searchQuery = "" },
                        focusRequester = focusRequester
                    )
                },
                // Botón para volver a la pantalla anterior
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {

            // ════════════════════════════════════════════════════════════
            // SUGERENCIAS RÁPIDAS (cuando el campo está vacío)
            // ════════════════════════════════════════════════════════════
            if (searchQuery.isBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        // Título de la sección
                        Text(
                            text = "SUGERENCIAS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E9E9E),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Chips de sugerencias rápidas
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(quickSuggestions) { suggestion ->
                                SuggestionChip(
                                    text = suggestion,
                                    onClick = { searchQuery = suggestion }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Texto informativo
                        Text(
                            text = "POPULAR",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E9E9E),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Mostrar algunos productos populares
                        mockProducts.take(4).forEach { product ->
                            PopularSearchItem(
                                product = product,
                                onClick = {
                                    navController.navigate("product_detail/${product.id}")
                                }
                            )
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════
            // RESULTADOS DE BÚSQUEDA
            // ════════════════════════════════════════════════════════════
            if (searchQuery.isNotBlank()) {

                // Contador de resultados
                item {
                    Text(
                        text = "${searchResults.size} resultado${if (searchResults.size != 1) "s" else ""}",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // Estado vacío: sin resultados
                if (searchResults.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = Color(0xFFCCCCCC),
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sin resultados para \"$searchQuery\"",
                                fontSize = 16.sp,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "Intenta con otra búsqueda",
                                fontSize = 14.sp,
                                color = Color(0xFF9E9E9E),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Grid de resultados en 2 columnas
                val rows = searchResults.chunked(2)
                items(rows) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { product ->
                            SearchResultCard(
                                product = product,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate("product_detail/${product.id}")
                                }
                            )
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Campo de búsqueda personalizado.
 *
 * Barra con fondo gris claro, icono de lupa, texto de input y
 * botón X para limpiar el texto.
 *
 * @param query Texto actual de búsqueda
 * @param onQueryChange Callback cuando el texto cambia
 * @param onClear Callback al pulsar el botón X
 * @param focusRequester Para controlar el foco del teclado
 */
@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    focusRequester: FocusRequester
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icono de lupa
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF9E9E9E),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Campo de texto
            Box(modifier = Modifier.weight(1f)) {
                // Placeholder cuando el campo está vacío
                if (query.isEmpty()) {
                    Text(
                        text = "Buscar productos...",
                        fontSize = 15.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }

                // Input de texto real
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = Color.Black
                    ),
                    cursorBrush = SolidColor(Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }

            // Botón X para limpiar (solo visible si hay texto)
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Limpiar",
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Chip de sugerencia rápida.
 * Píldora con texto que al pulsarla rellena el campo de búsqueda.
 *
 * @param text Texto de la sugerencia
 * @param onClick Callback al pulsar
 */
@Composable
fun SuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFF0F0F0))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Item de producto popular (lista vertical, estilo sugerencia).
 * Muestra imagen pequeña, nombre y categoría en una fila horizontal.
 *
 * @param product Datos del producto
 * @param onClick Callback al pulsar (navega al detalle)
 */
@Composable
fun PopularSearchItem(
    product: ProductDetail,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen pequeña del producto
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.title,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Nombre y categoría
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = product.category,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Precio
        Text(
            text = product.price,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

/**
 * Card de resultado de búsqueda en grid de 2 columnas.
 * Mismo estilo que las cards de Available (imagen + nombre + subtítulo + precio).
 *
 * @param product Datos del producto
 * @param modifier Modifier externo (para weight del grid)
 * @param onClick Callback al pulsar
 */
@Composable
fun SearchResultCard(
    product: ProductDetail,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.clickable { onClick() }
    ) {
        // Imagen del producto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre
        Text(
            text = product.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Subtítulo
        Text(
            text = product.subtitle,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
        )

        // Precio
        Text(
            text = product.price,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}