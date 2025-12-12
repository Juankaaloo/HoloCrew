package com.example.holocrew.ui.product

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

data class ProductDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("category") val category: String,
    @SerializedName("imageRes") val imageRes: String,
    @SerializedName("status") val status: String,
    @SerializedName("price") val price: String,
    @SerializedName("originalPrice") val originalPrice: String? = null,
    @SerializedName("brand") val brand: String,
    @SerializedName("description") val description: String,
    @SerializedName("rating") val rating: Float = 0f,
    @SerializedName("reviewCount") val reviewCount: Int = 0,
    @SerializedName("sizes") val sizes: List<String> = emptyList(),
    @SerializedName("colors") val colors: List<ColorOption> = emptyList(),
    @SerializedName("features") val features: List<String> = emptyList(),
    @SerializedName("material") val material: String = "",
    @SerializedName("careInstructions") val careInstructions: String = "",
    @SerializedName("sku") val sku: String = "",
    @SerializedName("publishedDate") val publishedDate: String,
    @SerializedName("isFavorite") val isFavorite: Boolean = false
)

data class ColorOption(
    @SerializedName("name") val name: String,
    @SerializedName("color") val colorHex: String
) {
    val color: Color
        get() = Color(android.graphics.Color.parseColor(colorHex))
}