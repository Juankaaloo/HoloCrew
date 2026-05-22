/**
 * CustomTopAppBar.kt
 *
 * Barra superior personalizada de la aplicación HoloCrew.
 * Muestra el logo de la marca a la izquierda y dos iconos de acción
 * a la derecha: corazón (favoritos con badge) y lupa (búsqueda).
 *
 * El logo usa el drawable logografiti1 con un tamaño ajustado
 * para que se vea nítido y proporcionado en el header.
 */
package com.example.holocrew.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holocrew.R
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.theme.HoloColors

/**
 * Barra superior personalizada con logo, favoritos y búsqueda.
 *
 * @param onSearchClick Callback al pulsar la lupa
 * @param onFavoritesClick Callback al pulsar el corazón
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    onSearchClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {}
) {
    // Observar el número de favoritos para el badge
    val favoriteIds by WishlistRepository.favoriteIds.collectAsState()
    val favCount = favoriteIds.size

    TopAppBar(
        title = {
            // Logo de la marca
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logografiti1),
                    contentDescription = "Logo HoloCrew",
                    modifier = Modifier
                        .height(80.dp)
                        .width(180.dp),
                    contentScale = ContentScale.Fit
                )
            }
        },
        actions = {
            // ── Botón de favoritos (corazón + badge) ──
            Box {
                IconButton(onClick = onFavoritesClick) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Mis Favoritos",
                        tint = HoloColors.Ink,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Badge rojo con contador (solo si hay favoritos)
                if (favCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 6.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(HoloColors.Pulse),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (favCount > 9) "9+" else "$favCount",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Botón de búsqueda (lupa) ──
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar productos",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Paper)
    )
}