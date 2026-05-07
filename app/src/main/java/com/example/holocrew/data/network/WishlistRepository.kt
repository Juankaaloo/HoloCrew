package com.example.holocrew.data.network

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object WishlistRepository {

    private val client get() = SupabaseClient.client

    // IDs de productos en la wishlist (reactivo para la UI)
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private fun getCurrentUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }

    // ── Cargar wishlist del usuario ──────────────────────────────────────────
    suspend fun loadFavorites() {
        val userId = getCurrentUserId() ?: return
        try {
            val items = client.from("wishlist_items")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SbWishlistItemDto>()

            _favoriteIds.value = items.map { it.productId }.toSet()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── Toggle favorito (agregar o quitar) ──────────────────────────────────
    suspend fun toggleFavorite(productId: Int, currentPrice: Double = 0.0): Boolean {
        val userId = getCurrentUserId() ?: return false
        return try {
            if (_favoriteIds.value.contains(productId)) {
                // Quitar de favoritos
                client.from("wishlist_items")
                    .delete {
                        filter {
                            eq("user_id", userId)
                            eq("product_id", productId)
                        }
                    }
                _favoriteIds.value = _favoriteIds.value - productId
            } else {
                // Agregar a favoritos
                client.from("wishlist_items")
                    .insert(SbWishlistItemInsert(
                        userId = userId,
                        productId = productId,
                        priceAtAdd = currentPrice
                    ))
                _favoriteIds.value = _favoriteIds.value + productId
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ── Comprobar si un producto es favorito ─────────────────────────────────
    fun isFavorite(productId: Int): Boolean {
        return _favoriteIds.value.contains(productId)
    }

    // ── Obtener productos completos de la wishlist ──────────────────────────
    suspend fun getFavoriteProducts(): List<com.example.holocrew.ui.product.ProductDetail> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            val items = client.from("wishlist_items")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SbWishlistItemDto>()

            items.mapNotNull { wishlistItem ->
                ProductRepository.getById(wishlistItem.productId)?.copy(isFavorite = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ── Limpiar estado (logout) ─────────────────────────────────────────────
    fun clearLocal() {
        _favoriteIds.value = emptySet()
    }
}