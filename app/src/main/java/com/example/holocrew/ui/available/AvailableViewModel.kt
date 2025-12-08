// ui/available/AvailableViewModel.kt
package com.example.holocrew.ui.available

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel // <-- USAR ANDROIDVIEWMODEL
import com.example.holocrew.R // <-- IMPORTAR R AQUÍ
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 1. Definición del Modelo de Datos (Mantenemos la misma clase)
data class AvailableProduct(
    val id: Int,
    val title: String,
    val subtitle: String,
    val category: String,
    val imageRes: Int, // <-- ESTO ES UN INT
    val status: String,
    val price: String,
    val isFavorite: Boolean = false,
    val inCart: Boolean = false
)

// 2. Definición del ViewModel (Ahora extiende de AndroidViewModel)
// Al extender de AndroidViewModel, tiene acceso al 'Application' Context
class AvailableViewModel(application: Application) : AndroidViewModel(application) {

    // --- Estado Observable de la UI (MutableStateFlow/StateFlow) ---

    // La lista de todos los productos (AHORA R.drawable.xxx ESTÁ DISPONIBLE)
    private val allProducts = listOf(
        AvailableProduct(id = 1, title = "Holo Pannel Hoodie", subtitle = "Hoodie - Hombre", category = "Ropa", imageRes = R.drawable.pannels_hoodie, status = "Nuevo", price = "$129.99"),
        AvailableProduct(id = 2, title = "Denim Bison Holo", subtitle = "Denim premium edición limitada", category = "Denim", imageRes = R.drawable.bison_denim_holo, status = "Exclusivo", price = "$89.99"),
        AvailableProduct(id = 3, title = "Boxer Holo White", subtitle = "Boxer Holo - Corte ajustado", category = "Ropa Interior", imageRes = R.drawable.boxer_holo_white, status = "Más vendido", price = "$30.99"),
        AvailableProduct(id = 4, title = "Glory Holo Polo", subtitle = "Polo", category = "Ropa", imageRes = R.drawable.glory_holo_polo, status = "En oferta", price = "$199.99"),
        AvailableProduct(id = 5, title = "Shoulder Bag", subtitle = "Bag Holo BlackLeather", category = "Accesorios", imageRes = R.drawable.shoulder_bag_holo_blackleather, status = "Exclusivo", price = "$249.99"),
        AvailableProduct(id = 6, title = "Racing Cap", subtitle = "Exclusive cap - Solo 500 unidades", category = "Accesorios", imageRes = R.drawable.offroad_racing_cap, status = "Limitado", price = "$299.99"),
        AvailableProduct(id = 7, title = "Holo Crew T-Shirt", subtitle = "Camiseta básica logo HOLO", category = "Ropa", imageRes = R.drawable.tops, status = "Básico", price = "$49.99"),
        AvailableProduct(id = 8, title = "Premium Denim Jacket", subtitle = "Chaqueta denim premium", category = "Denim", imageRes = R.drawable.newdenims, status = "Premium", price = "$189.99")
    )

    // Lista de categorías únicas para los filtros (se obtiene una vez)
    val categories: List<String> = listOf("Todos") + allProducts.map { it.category }.distinct()

    // Estado del filtro seleccionado
    var selectedFilter by mutableStateOf("Todos")
        private set

    private val _filteredProducts = MutableStateFlow(allProducts)
    val filteredProducts: StateFlow<List<AvailableProduct>> = _filteredProducts.asStateFlow()

    // --- Lógica de Negocio (Manejo de Eventos) ---

    fun selectFilter(category: String) {
        selectedFilter = category
        filterProducts()
    }

    private fun filterProducts() {
        val newFilteredList = if (selectedFilter == "Todos") {
            allProducts
        } else {
            allProducts.filter { it.category == selectedFilter }
        }
        _filteredProducts.value = newFilteredList
    }

    // Nota: Aquí irían funciones para togglear favorito/carrito, actualizar la lista, etc.
}