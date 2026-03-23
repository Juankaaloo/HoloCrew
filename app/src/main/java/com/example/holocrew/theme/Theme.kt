/**
 * Theme.kt
 *
 * Configuración del tema Material 3 para la aplicación HoloCrew.
 * Define los esquemas de color para modo claro y oscuro, aplicando
 * la identidad visual de la marca (negro, blanco, dorado).
 *
 * El tema se aplica globalmente desde MainActivity y todos los composables
 * heredan automáticamente estos colores a través de MaterialTheme.
 *
 * Configuración actual:
 *  - Modo claro: fondo blanco, elementos negros, acento dorado
 *  - Modo oscuro: fondo negro, elementos blancos, acento dorado
 *  - Color dinámico (Android 12+): DESACTIVADO para mantener la identidad de marca
 */
package com.example.holocrew.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Esquema de colores para modo oscuro.
 * Fondo negro con elementos claros y acento dorado.
 */
private val DarkColorScheme = darkColorScheme(
    primary = HoloWhite,             // Elementos principales en blanco
    onPrimary = HoloBlack,           // Texto sobre elementos principales en negro
    secondary = HoloGold,            // Acento dorado para elementos secundarios
    onSecondary = HoloBlack,         // Texto sobre elementos secundarios
    tertiary = HoloGold,             // Terciario también dorado (coherencia)
    background = HoloBlack,          // Fondo negro
    onBackground = HoloWhite,        // Texto sobre fondo en blanco
    surface = HoloDarkGray,          // Superficies (cards) en gris oscuro
    onSurface = HoloWhite,           // Texto sobre superficies en blanco
    surfaceVariant = HoloCharcoal,   // Variante de superficie
    onSurfaceVariant = HoloTextTertiary  // Texto terciario sobre variante
)

/**
 * Esquema de colores para modo claro (el principal de la app).
 * Fondo blanco con elementos negros y acento dorado.
 */
private val LightColorScheme = lightColorScheme(
    primary = HoloBlack,             // Elementos principales en negro
    onPrimary = HoloWhite,           // Texto sobre elementos principales en blanco
    secondary = HoloGold,            // Acento dorado para elementos secundarios
    onSecondary = HoloBlack,         // Texto sobre elementos secundarios
    tertiary = HoloGold,             // Terciario dorado
    background = HoloBackground,     // Fondo gris muy claro
    onBackground = HoloBlack,        // Texto sobre fondo en negro
    surface = HoloWhite,             // Superficies (cards) en blanco
    onSurface = HoloBlack,           // Texto sobre superficies en negro
    surfaceVariant = HoloLightGray,  // Variante de superficie (inputs, filtros)
    onSurfaceVariant = HoloTextSecondary  // Texto secundario sobre variante
)

/**
 * Tema principal de la aplicación HoloCrew.
 *
 * Aplica el esquema de color según el modo del sistema (claro/oscuro)
 * y la tipografía personalizada definida en Type.kt.
 *
 * NOTA: El color dinámico de Android 12+ está DESACTIVADO intencionalmente
 * para que la app siempre muestre los colores de la marca HoloCrew,
 * independientemente del wallpaper del usuario.
 *
 * @param darkTheme Si se debe usar el tema oscuro (por defecto sigue al sistema)
 * @param content Contenido composable al que se aplica el tema
 */
@Composable
fun HoloCrewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Seleccionar el esquema de color según el modo del sistema
    // No se usa color dinámico para mantener la identidad de marca
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}