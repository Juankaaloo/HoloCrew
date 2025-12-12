package com.example.holocrew.data

import android.content.Context
import com.example.holocrew.ui.product.ProductDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val context: Context) {

    data class ProductResponse(val products: List<ProductDetail>)

    private var cachedProducts: List<ProductDetail>? = null

    suspend fun getAllProducts(): List<ProductDetail> {
        return withContext(Dispatchers.IO) {
            if (cachedProducts == null) {
                cachedProducts = loadProductsFromJson()
            }
            cachedProducts ?: emptyList()
        }
    }

    suspend fun getProductById(productId: Int): ProductDetail? {
        return withContext(Dispatchers.IO) {
            getAllProducts().find { it.id == productId }
        }
    }

    private fun loadProductsFromJson(): List<ProductDetail> {
        return try {
            val jsonString = context.assets.open("products.json")
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()
            val responseType = object : TypeToken<ProductResponse>() {}.type
            val response = gson.fromJson<ProductResponse>(jsonString, responseType)

            response.products
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getProductsByCategory(category: String): List<ProductDetail> {
        return cachedProducts?.filter {
            it.category.equals(category, ignoreCase = true)
        } ?: emptyList()
    }
}