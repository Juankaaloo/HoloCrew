package com.example.holocrew.theme

import androidx.compose.ui.unit.dp

/**
 * HoloCrew Design System — Spacing Tokens
 *
 * Sistema de 4dp consistente. Usa estos tokens en lugar de valores raw.
 *
 * Filosofía SNKRS: respiración generosa entre secciones, denso dentro de cards.
 */
object HoloSpacing {

    // ── ESCALA BASE (4dp) ─────────────────────────────────────────────────────
    val xxs = 4.dp      // Gaps mínimos (entre icono y texto adyacente)
    val xs = 8.dp       // Padding interno cards pequeñas
    val sm = 12.dp      // Gap entre items en grid
    val md = 16.dp      // Padding horizontal estándar de pantalla
    val lg = 20.dp      // Padding interno cards grandes
    val xl = 24.dp      // Separación entre secciones
    val xxl = 32.dp     // Separación entre bloques mayores
    val xxxl = 48.dp    // Hero spacing
    val huge = 64.dp    // Top padding de pantallas auth

    // ── COMPONENT SPECIFIC ────────────────────────────────────────────────────
    val ButtonHeight = 56.dp
    val ButtonHeightSmall = 44.dp
    val InputHeight = 56.dp
    val IconSizeSmall = 16.dp
    val IconSizeDefault = 22.dp
    val IconSizeLarge = 28.dp
    val IconSizeHero = 56.dp

    val BottomNavHeight = 90.dp
    val TopBarHeight = 64.dp

    // ── CORNER RADIUS ─────────────────────────────────────────────────────────
    val RadiusNone = 0.dp
    val RadiusXs = 4.dp
    val RadiusSm = 8.dp
    val RadiusMd = 12.dp
    val RadiusLg = 16.dp
    val RadiusXl = 20.dp
    val RadiusPill = 50.dp        // Botones tipo "pill"

    // ── BORDER WIDTHS ─────────────────────────────────────────────────────────
    val BorderHairline = 1.dp     // Divisores sutiles
    val BorderDefault = 2.dp      // Botones outlined, inputs focused (vibe Supreme)
    val BorderThick = 4.dp        // Acentos agresivos (línea roja al lado de countdown)
}