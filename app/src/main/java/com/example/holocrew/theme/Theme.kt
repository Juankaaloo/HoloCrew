package com.example.holocrew.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * HoloCrew Design System — Theme
 *
 * Solo light mode por ahora (vibe SNKRS es light por defecto).
 * Cuando quieras añadir dark mode, duplica HoloLightColorScheme y ajusta.
 */

private val HoloLightColorScheme = lightColorScheme(
    primary = HoloColors.Ink,
    onPrimary = HoloColors.Paper,
    secondary = HoloColors.Pulse,
    onSecondary = HoloColors.Paper,
    tertiary = HoloColors.Neutral500,
    onTertiary = HoloColors.Paper,
    background = HoloColors.Paper,
    onBackground = HoloColors.Ink,
    surface = HoloColors.Paper,
    onSurface = HoloColors.Ink,
    surfaceVariant = HoloColors.Fog,
    onSurfaceVariant = HoloColors.Neutral500,
    error = HoloColors.Pulse,
    onError = HoloColors.Paper,
    outline = HoloColors.Neutral300,
    outlineVariant = HoloColors.Neutral200
)

/**
 * CompositionLocal para acceder al spacing desde cualquier Composable
 * sin tener que importar HoloSpacing en cada archivo.
 *
 * Uso: val spacing = HoloTheme.spacing
 */
val LocalHoloSpacing = staticCompositionLocalOf { HoloSpacing }

object HoloTheme {
    val spacing: HoloSpacing
        @Composable get() = LocalHoloSpacing.current

    val type: HoloType = HoloType
    val colors: HoloColors = HoloColors
}

@Composable
fun HoloCrewTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalHoloSpacing provides HoloSpacing
    ) {
        MaterialTheme(
            colorScheme = HoloLightColorScheme,
            typography = HoloTypography,
            content = content
        )
    }
}