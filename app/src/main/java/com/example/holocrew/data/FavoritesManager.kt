/**
 * FavoritesManager.kt
 *
 * Gestor global de productos favoritos de la aplicación HoloCrew.
 * Implementado como un objeto singleton para que el estado de favoritos
 * se comparta entre todas las pantallas (Available, Detail, Home, etc.).
 *
 * Al ser un proyecto solo front-end, los favoritos viven en memoria.
 * En una versión con backend se persistirían en base de datos.
 *
 * Funcionalidades:
 *  - Añadir/quitar productos de favoritos (toggle)
 *  - Comprobar si un producto es favorito
 *  - Obtener la lista completa de productos favoritos
 *  - Contar el número total de favoritos
 *
 * Se usa MutableStateFlow para que los composables se recompongan
 * automáticamente cuando los favoritos cambian.
 */
package com.example.holocrew.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Objeto singleton que gestiona el estado global de favoritos.
 *
 * Almacena un Set de IDs de productos marcados como favoritos.
 * Se accede desde cualquier pantalla con: FavoritesManager.toggleFavorite(id),
 * FavoritesManager.isFavorite(id), etc.
 */
object FavoritesManager {

    // ── Estado observable de los favoritos ──
    // Set de IDs de productos que el usuario ha marcado como favoritos
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())

    /** Set observable de IDs de productos favoritos */
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    // ── Operaciones ──

    /**
     * Alterna el estado de favorito de un producto.
     * Si ya es favorito, lo quita. Si no lo es, lo añade.
     *
     * @param productId ID del producto a togglear
     */
    fun toggleFavorite(productId: Int) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _favoriteIds.value = current
    }

    /**
     * Comprueba si un producto está marcado como favorito.
     *
     * @param productId ID del producto a comprobar
     * @return true si el producto es favorito, false si no
     */
    fun isFavorite(productId: Int): Boolean {
        return _favoriteIds.value.contains(productId)
    }

    /**
     * Cuenta el número total de productos favoritos.
     *
     * @return Número de favoritos
     */
    fun getCount(): Int {
        return _favoriteIds.value.size
    }
}