package com.example.holocrew.data

import android.content.Context
import com.example.holocrew.data.network.AddToCartRequest
import com.example.holocrew.data.network.CartItemDto
import com.example.holocrew.data.network.CartSummaryDto
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.data.network.UpdateCartItemRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

object CartManager {

    private val _items = MutableStateFlow<List<CartItemDto>>(emptyList())
    val items: StateFlow<List<CartItemDto>> = _items.asStateFlow()

    private val _summary = MutableStateFlow(CartSummaryDto(0.0, 0.0, 0.0, 0))
    val summary: StateFlow<CartSummaryDto> = _summary.asStateFlow()

    suspend fun loadCart(context: Context) {
        val token = TokenManager.getToken(context).first() ?: return
        try {
            val response = RetrofitClient.api.getCart("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                val cart = response.body()!!.data!!
                _items.value = cart.items
                _summary.value = cart.summary
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun addItem(context: Context, productId: String, variantId: String? = null, quantity: Int = 1) {
        val token = TokenManager.getToken(context).first() ?: return
        try {
            RetrofitClient.api.addToCart("Bearer $token", AddToCartRequest(productId, variantId, quantity))
            loadCart(context)
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun updateItem(context: Context, itemId: String, quantity: Int) {
        val token = TokenManager.getToken(context).first() ?: return
        try {
            RetrofitClient.api.updateCartItem("Bearer $token", itemId, UpdateCartItemRequest(quantity))
            loadCart(context)
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun removeItem(context: Context, itemId: String) {
        val token = TokenManager.getToken(context).first() ?: return
        try {
            RetrofitClient.api.removeCartItem("Bearer $token", itemId)
            loadCart(context)
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun clearCart(context: Context) {
        val token = TokenManager.getToken(context).first() ?: return
        try {
            RetrofitClient.api.clearCart("Bearer $token")
            _items.value = emptyList()
            _summary.value = CartSummaryDto(0.0, 0.0, 0.0, 0)
        } catch (e: Exception) { e.printStackTrace() }
    }
}