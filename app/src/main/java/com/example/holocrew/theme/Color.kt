package com.example.holocrew.theme

import androidx.compose.ui.graphics.Color

/**
 * HoloCrew Design System — Color Tokens
 *
 * Paleta inspirada en SNKRS / Supreme:
 *  - Negro absoluto + blanco puro como base
 *  - Rojo #FF0033 ("Pulse") como acento agresivo
 *  - Escala neutra de grises para profundidad
 *
 * Reglas:
 *  - NUNCA usar Color(0xFF...) suelto en pantallas → usar siempre estos tokens.
 *  - El rojo es PUNTUAL, no de relleno. Se usa para acentos, badges críticos,
 *    sold out, elementos destacados (countdown, cursor de inputs, tier).
 */
object HoloColors {

    // ── CORE ──────────────────────────────────────────────────────────────────
    val Ink = Color(0xFF000000)         // Negro primario (textos, headers, botones)
    val Paper = Color(0xFFFFFFFF)       // Blanco puro (fondos, textos sobre Ink)
    val Pulse = Color(0xFFFF0033)       // Rojo acento — Supreme bandera
    val Fog = Color(0xFFF5F5F5)         // Gris claro para fondos de pantalla

    // ── NEUTRAL SCALE (de claro a oscuro) ─────────────────────────────────────
    val Neutral50 = Color(0xFFFAFAFA)
    val Neutral100 = Color(0xFFF5F5F5)
    val Neutral200 = Color(0xFFE0E0E0)
    val Neutral300 = Color(0xFFCCCCCC)
    val Neutral400 = Color(0xFF9E9E9E)
    val Neutral500 = Color(0xFF666666)
    val Neutral600 = Color(0xFF444444)
    val Neutral700 = Color(0xFF2D2D2D)
    val Neutral800 = Color(0xFF1A1A1A)
    val Neutral900 = Color(0xFF0A0A0A)

    // ── SEMANTIC ──────────────────────────────────────────────────────────────
    val Success = Color(0xFF0EBE74)     // Verde stock disponible
    val Danger = Pulse                  // Sold out, errores críticos
    val Warning = Color(0xFFFFB020)     // Stock bajo, avisos
    val Info = Color(0xFF2D7FF9)        // Información neutral

    // ── ELEVATION (tints semitransparentes) ────────────────────────────────────
    val InkOverlay20 = Color(0x33000000)    // 20% negro - overlays sobre imagen
    val InkOverlay50 = Color(0x80000000)    // 50% negro - hero gradients
    val InkOverlay80 = Color(0xCC000000)    // 80% negro - modal backdrops
    val PaperOverlay60 = Color(0x99FFFFFF)  // 60% blanco - sobre fondos oscuros

    // ── TEXT ROLES ────────────────────────────────────────────────────────────
    val TextPrimary = Ink
    val TextSecondary = Neutral500
    val TextTertiary = Neutral400
    val TextDisabled = Neutral300
    val TextOnDark = Paper
    val TextOnDarkMuted = Color(0x99FFFFFF)     // 60% blanco para subtítulos en negro

    // ── BORDERS ───────────────────────────────────────────────────────────────
    val BorderSubtle = Neutral200
    val BorderDefault = Neutral300
    val BorderStrong = Ink
    val BorderAccent = Pulse

    // ── SURFACE ROLES ─────────────────────────────────────────────────────────
    val SurfaceLight = Paper
    val SurfaceMuted = Fog
    val SurfaceCard = Paper
    val SurfaceDark = Ink
    val SurfaceDarkElevated = Neutral800
}