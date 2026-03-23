/**
 * Color.kt
 *
 * Paleta de colores de la marca HoloCrew.
 * Define todos los colores que se usan a lo largo de la aplicación,
 * organizados por propósito: marca, UI, estados, categorías, etc.
 *
 * La identidad visual de HoloCrew se basa en un estilo streetwear moderno:
 *  - Negro y blanco como colores dominantes
 *  - Acentos puntuales para estados y acciones
 *  - Tonos neutros para fondos y textos secundarios
 *
 * Todos los composables de la app deben usar estos colores en lugar de
 * valores hardcodeados para mantener la coherencia visual.
 */
package com.example.holocrew.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════════════════════
// COLORES PRINCIPALES DE MARCA
// ══════════════════════════════════════════════════════════════════════════════

/** Negro puro — color principal de la marca, usado en fondos destacados y texto principal */
val HoloBlack = Color(0xFF000000)

/** Blanco puro — usado en fondos principales y texto sobre negro */
val HoloWhite = Color(0xFFFFFFFF)

/** Negro suave — alternativa al negro puro para fondos de secciones */
val HoloDarkGray = Color(0xFF1A1A1A)

/** Gris oscuro — usado para textos secundarios importantes */
val HoloCharcoal = Color(0xFF2D2D2D)

// ══════════════════════════════════════════════════════════════════════════════
// COLORES DE ACENTO
// ══════════════════════════════════════════════════════════════════════════════

/** Dorado — acento premium para badges exclusivos y highlights */
val HoloGold = Color(0xFFD4AF37)

/** Rojo coral — acento para ofertas, favoritos y acciones destructivas */
val HoloRed = Color(0xFFE53935)

/** Verde — acento para éxito, stock disponible y botón de compra */
val HoloGreen = Color(0xFF4CAF50)

/** Azul — acento para precios y enlaces */
val HoloBlue = Color(0xFF2196F3)

// ══════════════════════════════════════════════════════════════════════════════
// COLORES NEUTROS (fondos y separadores)
// ══════════════════════════════════════════════════════════════════════════════

/** Fondo general de la app (gris muy claro, casi blanco) */
val HoloBackground = Color(0xFFF5F5F5)

/** Fondo de cards y contenedores */
val HoloSurface = Color(0xFFFFFFFF)

/** Fondo de campos de búsqueda y filtros inactivos */
val HoloLightGray = Color(0xFFEEEEEE)

/** Separadores y bordes sutiles */
val HoloDivider = Color(0xFFE0E0E0)

// ══════════════════════════════════════════════════════════════════════════════
// COLORES DE TEXTO
// ══════════════════════════════════════════════════════════════════════════════

/** Texto principal (títulos, precios) */
val HoloTextPrimary = Color(0xFF000000)

/** Texto secundario (subtítulos, descripciones) */
val HoloTextSecondary = Color(0xFF666666)

/** Texto terciario (placeholders, labels menores) */
val HoloTextTertiary = Color(0xFF9E9E9E)

/** Texto deshabilitado */
val HoloTextDisabled = Color(0xFFBDBDBD)

// ══════════════════════════════════════════════════════════════════════════════
// COLORES DE ESTADO (badges de productos)
// ══════════════════════════════════════════════════════════════════════════════

/** Fondo del badge "Nuevo" */
val HoloBadgeNewBg = Color(0xFFE8F5E9)
val HoloBadgeNewText = Color(0xFF4CAF50)

/** Fondo del badge "Exclusivo" */
val HoloBadgeExclusiveBg = Color(0xFFF3E5F5)
val HoloBadgeExclusiveText = Color(0xFF9C27B0)

/** Fondo del badge "Más vendido" */
val HoloBadgeBestsellerBg = Color(0xFFFFEBEE)
val HoloBadgeBestsellerText = Color(0xFFF44336)

/** Fondo del badge "En oferta" */
val HoloBadgeOfferBg = Color(0xFFFFF3E0)
val HoloBadgeOfferText = Color(0xFFFF9800)

/** Fondo del badge "Premium" */
val HoloBadgePremiumBg = Color(0xFFF5F5F5)
val HoloBadgePremiumText = Color(0xFFD4AF37)

/** Fondo del badge "Limitado" */
val HoloBadgeLimitedBg = Color(0xFFFCE4EC)
val HoloBadgeLimitedText = Color(0xFFE91E63)

/** Fondo del badge "Básico" */
val HoloBadgeBasicBg = Color(0xFFE0F2F1)
val HoloBadgeBasicText = Color(0xFF009688)

// ══════════════════════════════════════════════════════════════════════════════
// COLORES DE CATEGORÍA (badges en cards de producto)
// ══════════════════════════════════════════════════════════════════════════════

/** Color del badge de categoría "Ropa" */
val HoloCategoryRopa = Color(0xFF2196F3)

/** Color del badge de categoría "Denim" */
val HoloCategoryDenim = Color(0xFF9C27B0)

/** Color del badge de categoría "Ropa Interior" */
val HoloCategoryInterior = Color(0xFF4CAF50)

/** Color del badge de categoría "Accesorios" */
val HoloCategoryAccesorios = Color(0xFFFF9800)

/** Color default para categorías no definidas */
val HoloCategoryDefault = Color(0xFF607D8B)

// ══════════════════════════════════════════════════════════════════════════════
// COLORES LEGACY (Material 3 — se mantienen para compatibilidad del tema)
// ══════════════════════════════════════════════════════════════════════════════

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)