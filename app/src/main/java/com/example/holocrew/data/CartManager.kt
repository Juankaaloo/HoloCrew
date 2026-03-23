/**
 * CartManager.kt
 *
 * Gestor global del carrito de compras de la aplicación HoloCrew.
 * Implementado como un objeto singleton para que el estado del carrito
 * se comparta entre todas las pantallas sin necesidad de ViewModel.
 *
 * Al ser un proyecto solo front-end, el carrito vive en memoria.
 * Si el usuario cierra la app, se pierde. En una versión con backend
 * se persistiría en base de datos o servidor.
 *
 * Funcionalidades:
 *  - Añadir productos al carrito (con talla y color seleccionados)
 *  - Eliminar productos del carrito
 *  - Modificar cantidades (incrementar/decrementar)
 *  - Vaciar el carrito completo
 *  - Calcular subtotal, envío y total
 *  - Contar el número total de items
 *
 * Se usa MutableStateFlow para que los composables se recompongan
 * automáticamente cuando el carrito cambia.
 */
package com.example.holocrew.data

import com.example.holocrew.ui.product.ProductDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ══════════════════════════════════════════════════════════════════════════════
// MODELO DE DATOS DEL CARRITO
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Representa un item dentro del carrito de compras.
 *
 * @param product Referencia al producto completo (para acceder a imagen, título, etc.)
 * @param quantity Cantidad de unidades de este producto en el carrito
 * @param selectedSize Talla seleccionada por el usuario (ej: "M", "L", "42")
 * @param selectedColor Color seleccionado por el usuario (ej: "Negro", "Azul")
 */
data class CartItem(
    val product: ProductDetail,
    var quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = ""
)

// ══════════════════════════════════════════════════════════════════════════════
// SINGLETON DEL CARRITO
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Objeto singleton que gestiona el estado global del carrito.
 *
 * Se accede desde cualquier pantalla con: CartManager.items, CartManager.addItem(...), etc.
 * Los composables observan los cambios con collectAsState().
 */
object CartManager {

    // ── Estado observable del carrito ──
    // MutableStateFlow permite que los composables se recompongan al cambiar
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())

    /** Lista observable de items en el carrito */
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    // ── Operaciones del carrito ──

    /**
     * Añade un producto al carrito.
     *
     * Si el producto ya existe con la misma talla y color, incrementa la cantidad.
     * Si no, lo añade como un item nuevo.
     *
     * @param product Producto a añadir
     * @param size Talla seleccionada (por defecto vacío)
     * @param color Color seleccionado (por defecto vacío)
     */
    fun addItem(product: ProductDetail, size: String = "", color: String = "") {
        val currentItems = _items.value.toMutableList()

        // Buscar si ya existe un item con el mismo producto, talla y color
        val existingIndex = currentItems.indexOfFirst {
            it.product.id == product.id &&
                    it.selectedSize == size &&
                    it.selectedColor == color
        }

        if (existingIndex != -1) {
            // Si existe, incrementar la cantidad
            val existingItem = currentItems[existingIndex]
            currentItems[existingIndex] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // Si no existe, añadir como nuevo item
            currentItems.add(
                CartItem(
                    product = product,
                    quantity = 1,
                    selectedSize = size,
                    selectedColor = color
                )
            )
        }

        _items.value = currentItems
    }

    /**
     * Elimina un item del carrito por el ID del producto.
     *
     * @param productId ID del producto a eliminar
     * @param size Talla del item a eliminar (para diferenciar mismo producto con distinta talla)
     * @param color Color del item a eliminar
     */
    fun removeItem(productId: Int, size: String = "", color: String = "") {
        _items.value = _items.value.filter {
            !(it.product.id == productId &&
                    it.selectedSize == size &&
                    it.selectedColor == color)
        }
    }

    /**
     * Incrementa la cantidad de un item en el carrito.
     *
     * @param productId ID del producto
     * @param size Talla del item
     * @param color Color del item
     */
    fun increaseQuantity(productId: Int, size: String = "", color: String = "") {
        _items.value = _items.value.map { item ->
            if (item.product.id == productId &&
                item.selectedSize == size &&
                item.selectedColor == color
            ) {
                item.copy(quantity = item.quantity + 1)
            } else {
                item
            }
        }
    }

    /**
     * Decrementa la cantidad de un item en el carrito.
     * Si la cantidad llega a 0, elimina el item del carrito.
     *
     * @param productId ID del producto
     * @param size Talla del item
     * @param color Color del item
     */
    fun decreaseQuantity(productId: Int, size: String = "", color: String = "") {
        val updated = _items.value.map { item ->
            if (item.product.id == productId &&
                item.selectedSize == size &&
                item.selectedColor == color
            ) {
                item.copy(quantity = item.quantity - 1)
            } else {
                item
            }
        }
        // Filtrar items con cantidad 0 o menor
        _items.value = updated.filter { it.quantity > 0 }
    }

    /**
     * Vacía completamente el carrito.
     */
    fun clearCart() {
        _items.value = emptyList()
    }

    /**
     * Calcula el subtotal del carrito (suma de precio × cantidad de cada item).
     * Parsea el precio del string (ej: "$129.99" → 129.99).
     *
     * @return Subtotal como Double
     */
    fun getSubtotal(): Double {
        return _items.value.sumOf { item ->
            val price = item.product.price
                .replace("$", "")
                .replace(",", "")
                .toDoubleOrNull() ?: 0.0
            price * item.quantity
        }
    }

    /**
     * Calcula el coste de envío.
     * Envío gratis si el subtotal supera los $150, si no cuesta $9.99.
     *
     * @return Coste de envío como Double
     */
    fun getShipping(): Double {
        return if (getSubtotal() > 150.0) 0.0 else 9.99
    }

    /**
     * Calcula el total del pedido (subtotal + envío).
     *
     * @return Total como Double
     */
    fun getTotal(): Double {
        return getSubtotal() + getShipping()
    }

    /**
     * Cuenta el número total de items en el carrito
     * (sumando las cantidades de cada item).
     *
     * @return Número total de unidades en el carrito
     */
    fun getItemCount(): Int {
        return _items.value.sumOf { it.quantity }
    }
}