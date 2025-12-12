package com.example.holocrew.ui.product

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holocrew.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(context: Context) : ViewModel() {

    private val repository = ProductRepository(context.applicationContext)

    private val _selectedProduct = MutableStateFlow<ProductDetail?>(null)
    val selectedProduct: StateFlow<ProductDetail?> = _selectedProduct.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allProducts = MutableStateFlow<List<ProductDetail>>(emptyList())
    val allProducts: StateFlow<List<ProductDetail>> = _allProducts.asStateFlow()

    init {
        loadAllProducts()
    }

    private fun loadAllProducts() {
        viewModelScope.launch {
            try {
                _allProducts.value = repository.getAllProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getProductById(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedProduct.value = repository.getProductById(productId)
            } catch (e: Exception) {
                // Manejar error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            val currentProduct = _selectedProduct.value
            if (currentProduct != null && currentProduct.id == productId) {
                _selectedProduct.value = currentProduct.copy(
                    isFavorite = !currentProduct.isFavorite
                )
            }
        }
    }

    // Esta función ahora es opcional, ya que los productos están en el StateFlow
    suspend fun getAllProductsDirect(): List<ProductDetail> {
        return repository.getAllProducts()
    }
}