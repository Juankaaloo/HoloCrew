package com.example.holocrew.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holocrew.data.network.ProductRepository
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.ui.product.ProductDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val featuredProducts: List<ProductDetail> = emptyList(),
    val exclusiveProducts: List<ProductDetail> = emptyList(),
    val onSaleProducts: List<ProductDetail> = emptyList(),
    val newProducts: List<ProductDetail> = emptyList(),
    val trendingProducts: List<ProductDetail> = emptyList()
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Cargar wishlist para marcar favoritos
                WishlistRepository.loadFavorites()

                // Cargar todas las secciones en paralelo
                val featured = ProductRepository.getFeatured(4)
                val exclusive = ProductRepository.getBestSellers(6)
                val onSale = ProductRepository.getOnSale(4)
                val new = ProductRepository.getNew(8)
                val trending = ProductRepository.getTrending(4)

                _uiState.value = HomeUiState(
                    isLoading = false,
                    featuredProducts = featured,
                    exclusiveProducts = exclusive,
                    onSaleProducts = onSale,
                    newProducts = new,
                    trendingProducts = trending
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar productos"
                )
            }
        }
    }

    fun refresh() {
        loadHome()
    }
}