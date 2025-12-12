package com.example.holocrew.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.holocrew.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    onSearchClick: () -> Unit = {}   // Callback que se ejecuta cuando el usuario presiona el botón de búsqueda
) {
    // Barra superior personalizada usando Material 3
    TopAppBar(
        title = {
            // Usamos un Row para centrar verticalmente el logo dentro del título
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logografiti1),  // Carga del logo desde los recursos
                    contentDescription = "Logo",                              // Descripción para accesibilidad
                    modifier = Modifier
                        .height(35.dp)                                        // Altura del contenedor del logo
                        .size(80.dp),                                         // Tamaño del logo
                    contentScale = ContentScale.Fit                            // Ajuste de imagen sin recortarla
                )
            }
        },
        actions = {
            // Botón de búsqueda situado a la derecha de la app bar
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,   // Icono del buscador
                    contentDescription = "Buscar",        // Descripción accesible
                    tint = Color.Black                    // Color negro para el icono
                )
            }
        },
        // Colores de la barra: fondo blanco
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}
