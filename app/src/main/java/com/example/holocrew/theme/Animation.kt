package com.example.holocrew.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

/**
 * HoloCrew Design System — Motion
 *
 * Set agresivo de animaciones inspiradas en SNKRS:
 *  - Slide-ins desde abajo en cards/items
 *  - Hero zoom (paralax al hacer scroll en imágenes hero)
 *  - Press feedback con scale
 *  - Fades secuenciales para reveal
 *
 * Filosofía: nunca lineal. Curvas con personalidad.
 */
object HoloMotion {

    // ── EASINGS ───────────────────────────────────────────────────────────────
    /** Easing principal de SNKRS — entrada decisiva, salida suave */
    val SnkrsEasing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

    /** Easing más rápido para feedbacks de toque */
    val QuickEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

    /** Easing dramático tipo "drop" — frena al final */
    val DropEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

    // ── DURATIONS (ms) ────────────────────────────────────────────────────────
    const val DurationFast = 150
    const val DurationDefault = 300
    const val DurationSlow = 500
    const val DurationHero = 700

    // ── TRANSITIONS PREDEFINIDAS ──────────────────────────────────────────────

    /**
     * Slide-in desde abajo + fade. Para cards en grids al cargar pantalla.
     */
    fun slideInBottom(
        delayMs: Int = 0,
        durationMs: Int = DurationDefault
    ): EnterTransition = slideInVertically(
        animationSpec = tween(durationMs, delayMs, SnkrsEasing),
        initialOffsetY = { it / 4 }
    ) + fadeIn(
        animationSpec = tween(durationMs, delayMs, SnkrsEasing)
    )

    fun slideOutBottom(
        durationMs: Int = DurationFast
    ): ExitTransition = slideOutVertically(
        animationSpec = tween(durationMs, easing = QuickEasing),
        targetOffsetY = { it / 4 }
    ) + fadeOut(animationSpec = tween(durationMs))

    /**
     * Slide horizontal — para nav forwards
     */
    fun slideInRight(
        durationMs: Int = DurationDefault
    ): EnterTransition = slideInHorizontally(
        animationSpec = tween(durationMs, easing = SnkrsEasing),
        initialOffsetX = { it / 3 }
    ) + fadeIn(animationSpec = tween(durationMs))

    fun slideOutLeft(
        durationMs: Int = DurationDefault
    ): ExitTransition = slideOutHorizontally(
        animationSpec = tween(durationMs, easing = QuickEasing),
        targetOffsetX = { -it / 3 }
    ) + fadeOut(animationSpec = tween(durationMs))

    /**
     * Hero zoom-in para entrada de imagen principal de detalle
     */
    fun heroZoomIn(
        durationMs: Int = DurationHero
    ): EnterTransition = scaleIn(
        animationSpec = tween(durationMs, easing = DropEasing),
        initialScale = 1.15f
    ) + fadeIn(animationSpec = tween(durationMs / 2))

    fun heroZoomOut(): ExitTransition = scaleOut(
        animationSpec = tween(DurationDefault, easing = QuickEasing),
        targetScale = 0.95f
    ) + fadeOut(animationSpec = tween(DurationDefault))

    // ── SPRING SPECS ──────────────────────────────────────────────────────────

    /** Spring rebotón para botones y favoritos */
    fun <T> bouncySpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    /** Spring suave para transiciones de color/escala sin rebote */
    fun <T> smoothSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
}

/**
 * Modifier que aplica efecto parallax sobre una imagen hero según el scroll.
 * Úsalo en LazyColumn al mostrar imágenes grandes:
 *
 *   Image(modifier = Modifier.parallaxScroll(listState, itemIndex = 0))
 *
 * Cuando scrolleas, la imagen se mueve más lento que el resto creando
 * sensación de profundidad — SNKRS lo usa en el hero del detalle.
 */
fun Modifier.parallaxScroll(
    listState: LazyListState,
    itemIndex: Int = 0,
    factor: Float = 0.5f
): Modifier = this.then(
    Modifier.graphicsLayer {
        val item = listState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == itemIndex }
        if (item != null) {
            translationY = -item.offset * factor
        }
    }
)

/**
 * Modifier que escala ligeramente al pulsar — feedback táctil.
 *
 * Combinado con clickable produce vibe SNKRS: presionar = compactar.
 */
@Composable
fun Modifier.pressScale(pressed: Boolean, scaleAmount: Float = 0.96f): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (pressed) scaleAmount else 1f,
        animationSpec = HoloMotion.smoothSpring(),
        label = "pressScale"
    )
    return this.scale(scale)
}