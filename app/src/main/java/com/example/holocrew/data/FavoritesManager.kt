package com.example.holocrew.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

/**
 * FavoritesManager — Gestión de favoritos con persistencia local (DataStore).
 *
 * Los IDs de productos favoritos se guardan como string separado por comas.
 * Persisten entre sesiones sin tocar la BDD compartida de Supabase.
 */

private val Context.favStore by preferencesDataStore(name = "holocrew_favorites")

object FavoritesManager {

    private val FAV_KEY = stringPreferencesKey("favorite_ids")

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    /**
     * Cargar favoritos desde DataStore.
     */
    suspend fun loadFavorites(context: Context) {
        try {
            val saved = context.favStore.data.first()[FAV_KEY] ?: return
            if (saved.isNotBlank()) {
                _favoriteIds.value = saved.split(",").toSet()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    /**
     * Guardar favoritos en DataStore.
     */
    private suspend fun saveFavorites(context: Context) {
        context.favStore.edit { prefs ->
            prefs[FAV_KEY] = _favoriteIds.value.joinToString(",")
        }
    }

    /**
     * Toggle: añadir o quitar un producto de favoritos.
     */
    suspend fun toggleFavorite(context: Context, productId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(productId)) current.remove(productId)
        else current.add(productId)
        _favoriteIds.value = current
        saveFavorites(context)
    }

    fun isFavorite(productId: String): Boolean = _favoriteIds.value.contains(productId)
    fun getCount(): Int = _favoriteIds.value.size

    /**
     * Limpiar favoritos (al cerrar sesión).
     */
    fun clearFavorites() {
        _favoriteIds.value = emptySet()
    }
}