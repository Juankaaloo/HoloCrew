package com.example.holocrew.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.holocrew.data.network.ProductRepository
import com.example.holocrew.ui.product.ProductDetail
import kotlinx.coroutines.launch
import com.example.holocrew.components.SoldOutOverlay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    var searchResults by remember { mutableStateOf<List<ProductDetail>>(emptyList()) }
    var popularProducts by remember { mutableStateOf<List<ProductDetail>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var allProducts by remember { mutableStateOf<List<ProductDetail>>(emptyList()) }  // NUEVO

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        allProducts = ProductRepository.getAll()
        popularProducts = ProductRepository.getTrending(4)
        categories = allProducts.map { it.category }.distinct().filter { it.isNotEmpty() }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            searchResults = emptyList()
            return@LaunchedEffect
        }
        isSearching = true
        // Si el query coincide exactamente con una categoría, filtrar por categoría
        val isCategory = categories.any { it.equals(searchQuery, ignoreCase = true) }
        searchResults = if (isCategory) {
            allProducts.filter { it.category.equals(searchQuery, ignoreCase = true) }
        } else {
            allProducts.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.subtitle.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
        isSearching = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchTextField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClear = { searchQuery = "" },
                        focusRequester = focusRequester
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {

            // SUGERENCIAS (campo vacio)
            if (searchQuery.isBlank()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                        Text("SUGERENCIAS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { category ->
                                SuggestionChip(text = category, onClick = { searchQuery = category })
                            }
                        }
                        Spacer(Modifier.height(32.dp))
                        Text("POPULAR", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                    }
                }

                items(popularProducts) { product ->
                    PopularSearchItem(
                        product = product,
                        onClick = { navController.navigate("product_detail/${product.id}") },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // RESULTADOS
            if (searchQuery.isNotBlank()) {
                item {
                    if (isSearching) {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        }
                    } else {
                        Text(
                            text = "${searchResults.size} resultado${if (searchResults.size != 1) "s" else ""}",
                            fontSize = 14.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }

                if (!isSearching && searchResults.isEmpty()) {
                    item {
                        Column(Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(60.dp))
                            Spacer(Modifier.height(16.dp))
                            Text("Sin resultados para \"$searchQuery\"", fontSize = 16.sp, color = Color(0xFF666666))
                            Text("Intenta con otra busqueda", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }

                val rows = searchResults.chunked(2)
                items(rows) { rowProducts ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowProducts.forEach { product ->
                            SearchResultCard(
                                product = product,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("product_detail/${product.id}") }
                            )
                        }
                        if (rowProducts.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun SearchTextField(query: String, onQueryChange: (String) -> Unit, onClear: () -> Unit, focusRequester: FocusRequester) {
    Box(
        modifier = Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F0F0)).padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = Color(0xFF9E9E9E), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) Text("Buscar productos...", fontSize = 15.sp, color = Color(0xFF9E9E9E))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 15.sp, color = Color.Black),
                    cursorBrush = SolidColor(Color.Black),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                )
            }
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Limpiar", tint = Color(0xFF666666), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    Box(Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFF0F0F0)).clickable { onClick() }.padding(horizontal = 16.dp, vertical = 10.dp)) {
        Text(text, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PopularSearchItem(product: ProductDetail, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF5F5F5)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(product.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(product.category, fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 2.dp))
        }
        if (product.stock <= 0) {
            Text("SOLD OUT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        } else {
            Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun SearchResultCard(product: ProductDetail, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Column(modifier = modifier.clickable { onClick() }) {
        Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF5F5F5))) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            if (product.stock <= 0) { SoldOutOverlay() }
        }
        Spacer(Modifier.height(8.dp))
        Text(product.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.subtitle, fontSize = 12.sp, color = Color(0xFF666666), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        Text(product.price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
    }
}