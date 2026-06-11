package com.example.holocrew.data.network

import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.toProductDetail
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CartRepository {

    private val client get() = SupabaseClient.client

    // Estado reactivo del carrito para que la UI se actualice automaticamente
    private val _cartItems = MutableStateFlow<List<CartProductItem>>(emptyList())
    val cartItems: StateFlow<List<CartProductItem>> = _cartItems.asStateFlow()

    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    // Modelo combinado: item del carrito + datos del producto
    data class CartProductItem(
        val cartItemId: Int,
        val product: ProductDetail,
        val size: String?,
        val color: String?,
        val quantity: Int,
        val priceAtAdd: Double
    )

    private fun getCurrentUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }

    // ── Cargar carrito completo ──────────────────────────────────────────────
    suspend fun loadCart() {
        val userId = getCurrentUserId() ?: return
        try {
            val items = client.from("cart_items")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SbCartItemDto>()

            // Para cada item del carrito, cargar el producto asociado
            val cartProducts = items.mapNotNull { cartItem ->
                val product = ProductRepository.getById(cartItem.productId)
                if (product != null) {
                    CartProductItem(
                        cartItemId = cartItem.id ?: 0,
                        product = product,
                        size = cartItem.size,
                        color = cartItem.color,
                        quantity = cartItem.quantity,
                        priceAtAdd = cartItem.priceAtAdd
                    )
                } else null
            }

            _cartItems.value = cartProducts
            _cartCount.value = cartProducts.sumOf { it.quantity }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── Agregar producto al carrito ──────────────────────────────────────────
    suspend fun addItem(
        productId: Int,
        price: Double,
        size: String? = null,
        color: String? = "default",
        quantity: Int = 1
    ): Boolean {
        val userId = getCurrentUserId()
        android.util.Log.d("CartRepo", "addItem userId=$userId productId=$productId price=$price")
        if (userId == null) return false
        return try {
            // Insertar directo (sin verificar duplicados por ahora)
            client.from("cart_items")
                .insert(SbCartItemInsert(
                    userId = userId,
                    productId = productId,
                    size = size,
                    color = color,
                    quantity = quantity,
                    priceAtAdd = price
                ))
            android.util.Log.d("CartRepo", "INSERT OK")

            loadCart()
            android.util.Log.d("CartRepo", "loadCart OK, items=${_cartItems.value.size}")
            true
        } catch (e: Exception) {
            android.util.Log.e("CartRepo", "addItem FAILED: ${e.message}", e)
            false
        }
    }

    // ── Actualizar cantidad ─────────────────────────────────────────────────
    suspend fun updateQuantity(cartItemId: Int, newQuantity: Int): Boolean {
        return try {
            if (newQuantity <= 0) {
                removeItem(cartItemId)
                return true
            }

            client.from("cart_items")
                .update({
                    set("quantity", newQuantity)
                }) {
                    filter { eq("id", cartItemId) }
                }

            loadCart()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ── Eliminar item ───────────────────────────────────────────────────────
    suspend fun removeItem(cartItemId: Int): Boolean {
        return try {
            client.from("cart_items")
                .delete {
                    filter { eq("id", cartItemId) }
                }

            loadCart()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ── Vaciar carrito ──────────────────────────────────────────────────────
    suspend fun clearCart(): Boolean {
        val userId = getCurrentUserId() ?: return false
        return try {
            client.from("cart_items")
                .delete {
                    filter { eq("user_id", userId) }
                }

            _cartItems.value = emptyList()
            _cartCount.value = 0
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ── Calculos ────────────────────────────────────────────────────────────
    fun getSubtotal(): Double {
        return _cartItems.value.sumOf { it.priceAtAdd * it.quantity }
    }

    fun getShippingCost(): Double {
        val subtotal = getSubtotal()
        return if (subtotal >= 50.0) 0.0 else 5.99
    }

    fun getTotal(): Double {
        return getSubtotal() + getShippingCost()
    }
}