package com.example.holocrew.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.holocrew.data.network.CartItemDto
import com.example.holocrew.data.network.CartSummaryDto
import com.example.holocrew.ui.product.mockProducts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

/**
 * CartManager — Gestión del carrito con persistencia local (DataStore).
 *
 * Auth usa Supabase (cloud), carrito usa DataStore (local).
 * Persiste entre sesiones sin tocar la BDD compartida.
 */

private val Context.cartStore by preferencesDataStore(name = "holocrew_cart")

object CartManager {

    private val CART_KEY = stringPreferencesKey("cart_items")

    private val _items = MutableStateFlow<List<CartItemDto>>(emptyList())
    val items: StateFlow<List<CartItemDto>> = _items.asStateFlow()

    private val _summary = MutableStateFlow(CartSummaryDto(0.0, 0.0, 0.0, 0))
    val summary: StateFlow<CartSummaryDto> = _summary.asStateFlow()

    private var nextId = 1

    suspend fun loadCart(context: Context) {
        try {
            val json = context.cartStore.data.first()[CART_KEY] ?: return
            val array = JSONArray(json)
            val loaded = mutableListOf<CartItemDto>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                loaded.add(CartItemDto(
                    id = obj.getString("id"),
                    quantity = obj.getInt("quantity"),
                    price_at_addition = obj.getDouble("price"),
                    product_id = obj.getString("product_id"),
                    name = obj.getString("name"),
                    price_eur = obj.getDouble("price")
                ))
                val idNum = obj.getString("id").removePrefix("cart_").toIntOrNull() ?: 0
                if (idNum >= nextId) nextId = idNum + 1
            }
            _items.value = loaded
            recalculateSummary()
        } catch (e: Exception) { e.printStackTrace() }
    }

    private suspend fun saveCart(context: Context) {
        val array = JSONArray()
        _items.value.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("product_id", item.product_id)
                put("name", item.name)
                put("price", item.price_at_addition)
                put("quantity", item.quantity)
            })
        }
        context.cartStore.edit { it[CART_KEY] = array.toString() }
    }

    suspend fun addItem(context: Context, productId: String, variantId: String? = null, quantity: Int = 1) {
        val current = _items.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product_id == productId }
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            val product = mockProducts.find { it.id == productId }
            val price = product?.let {
                it.price.replace("€", "").replace(",", ".").toDoubleOrNull()
            } ?: 0.0
            current.add(CartItemDto(
                id = "cart_${nextId++}",
                quantity = quantity,
                price_at_addition = price,
                product_id = productId,
                name = product?.title ?: "Producto",
                price_eur = price
            ))
        }
        _items.value = current
        recalculateSummary()
        saveCart(context)
    }

    suspend fun updateItem(context: Context, itemId: String, quantity: Int) {
        val current = _items.value.toMutableList()
        val index = current.indexOfFirst { it.id == itemId }
        if (index >= 0) {
            if (quantity <= 0) current.removeAt(index)
            else current[index] = current[index].copy(quantity = quantity)
        }
        _items.value = current
        recalculateSummary()
        saveCart(context)
    }

    suspend fun removeItem(context: Context, itemId: String) {
        _items.value = _items.value.filter { it.id != itemId }
        recalculateSummary()
        saveCart(context)
    }

    suspend fun clearCart(context: Context) {
        _items.value = emptyList()
        _summary.value = CartSummaryDto(0.0, 0.0, 0.0, 0)
    }

    private fun recalculateSummary() {
        val items = _items.value
        val subtotal = items.sumOf { it.price_at_addition * it.quantity }
        val shipping = if (subtotal > 150.0) 0.0 else 9.99
        _summary.value = CartSummaryDto(subtotal, shipping, subtotal + shipping, items.sumOf { it.quantity })
    }
}