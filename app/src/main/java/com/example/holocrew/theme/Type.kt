package com.example.holocrew.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * HoloCrew Design System — Typography
 *
 * Versión SIN fuentes custom — usa la fuente del sistema (Roboto en Android).
 * Cuando quieras añadir fuentes custom (Space Grotesk, Inter, etc.), solo
 * tienes que cambiar las constantes DisplayFamily / TextFamily / MonoFamily
 * por FontFamily(Font(R.font.xxx)).
 *
 * La jerarquía y los tamaños se mantienen idénticos al sistema completo.
 */

// ── FAMILIAS (todas usan la del sistema por ahora) ────────────────────────────
private val DisplayFamily = FontFamily.Default   // Para display y headlines
private val TextFamily = FontFamily.Default      // Para body, labels, todo
private val MonoFamily = FontFamily.Monospace    // Para precios, números, códigos

/**
 * Estilos custom de HoloCrew. Úsalos directamente en lugar de Typography.x
 * para tener nombres semánticos claros.
 */
object HoloType {

    // ── DISPLAY (hero, títulos enormes en banners y splash) ───────────────────
    val DisplayHuge = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Black,
        fontSize = 56.sp,
        lineHeight = 60.sp,
        letterSpacing = (-1.5).sp
    )

    val DisplayLarge = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Black,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-1).sp
    )

    val DisplayMedium = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    )

    // ── HEADLINE (títulos de sección, pantalla) ───────────────────────────────
    val HeadlineLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp
    )

    val HeadlineMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    )

    val HeadlineSmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )

    // ── TITLE (títulos de cards, productos) ───────────────────────────────────
    val TitleLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )

    val TitleMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    val TitleSmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )

    // ── BODY (descripciones, texto largo) ─────────────────────────────────────
    val BodyLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    val BodyMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    val BodySmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )

    // ── LABEL (botones, chips, badges, headers) ───────────────────────────────
    // Siempre uppercase en uso, letter-spacing alto. Estilo SNKRS.
    val LabelLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 1.5.sp
    )

    val LabelMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.5.sp
    )

    val LabelSmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 2.sp
    )

    // ── MONO (precios, números de pedido, contadores, tracking) ───────────────
    val MonoLarge = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp
    )

    val MonoMedium = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )

    val MonoSmall = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}

// Material 3 Typography mapping (para el MaterialTheme)
val HoloTypography = Typography(
    displayLarge = HoloType.DisplayLarge,
    displayMedium = HoloType.DisplayMedium,
    displaySmall = HoloType.HeadlineLarge,
    headlineLarge = HoloType.HeadlineLarge,
    headlineMedium = HoloType.HeadlineMedium,
    headlineSmall = HoloType.HeadlineSmall,
    titleLarge = HoloType.TitleLarge,
    titleMedium = HoloType.TitleMedium,
    titleSmall = HoloType.TitleSmall,
    bodyLarge = HoloType.BodyLarge,
    bodyMedium = HoloType.BodyMedium,
    bodySmall = HoloType.BodySmall,
    labelLarge = HoloType.LabelLarge,
    labelMedium = HoloType.LabelMedium,
    labelSmall = HoloType.LabelSmall
)