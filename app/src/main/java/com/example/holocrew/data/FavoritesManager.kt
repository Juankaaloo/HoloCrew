package com.example.holocrew.data

import android.content.Context
import com.example.holocrew.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

object FavoritesManager {

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    suspend fun loadFavorites(context: Context) {
        val token = TokenManager.getToken(context).first() ?: return
        try {
            val response = RetrofitClient.api.getFavoriteIds("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                _favoriteIds.value = response.body()!!.data!!.toSet()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun toggleFavorite(context: Context, productId: String) {
        val token = TokenManager.getToken(context).first() ?: return
        try {
            RetrofitClient.api.toggleFavorite("Bearer $token", productId)
            val current = _favoriteIds.value.toMutableSet()
            if (current.contains(productId)) current.remove(productId) else current.add(productId)
            _favoriteIds.value = current
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun isFavorite(productId: String): Boolean = _favoriteIds.value.contains(productId)
    fun getCount(): Int = _favoriteIds.value.size

    fun clearFavorites() {
        _favoriteIds.value = emptySet()
    }
}