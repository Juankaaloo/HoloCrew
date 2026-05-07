package com.example.holocrew.ui.product

import androidx.compose.ui.graphics.Color
import com.example.holocrew.data.network.SbProductDto

data class ColorOption(
    val name: String,
    val colorHex: String
) {
    val color: Color
        get() = Color(android.graphics.Color.parseColor(colorHex))
}

data class ProductDetail(
    val id: Int,
    val title: String,
    val subtitle: String,
    val category: String,
    val categoryId: Int? = null,
    val imageUrl: String,
    val extraImageUrls: List<String> = emptyList(),
    val status: String,
    val price: String,
    val priceRaw: Double = 0.0,
    val originalPrice: String? = null,
    val brand: String = "Holo Crew",
    val description: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val sizes: List<String> = emptyList(),
    val colors: List<ColorOption> = emptyList(),
    val features: List<String> = emptyList(),
    val material: String = "",
    val careInstructions: String = "",
    val sku: String = "",
    val stock: Int = 0,
    val collection: String = "",
    val gender: String = "",
    val isFavorite: Boolean = false
)

// Mapper: SbProductDto (Supabase) -> ProductDetail (UI)

private fun deriveStatus(dto: SbProductDto): String = when {
    dto.isFlashSale  -> "Flash Sale"
    dto.isBlackWeek  -> "Black Week"
    dto.isBestSeller -> "Mas vendido"
    dto.isFeatured   -> "Exclusivo"
    dto.isNew        -> "Nuevo"
    dto.originalPrice != null && dto.originalPrice > dto.price -> "En oferta"
    else             -> "Disponible"
}

private fun Double.toPriceString(): String = "%.2f\u20AC".format(this)

fun SbProductDto.toProductDetail(isFavorite: Boolean = false): ProductDetail {
    val images: List<String> = this.productImages?.map { it.imageUrl } ?: emptyList()
    val sizeList: List<String> = this.productSizes?.map { it.size } ?: emptyList()

    return ProductDetail(
        id = this.id,
        title = this.name,
        subtitle = this.shortDescription ?: "",
        category = this.categories?.name ?: "",
        categoryId = this.categoryId,
        imageUrl = images.firstOrNull() ?: "",
        extraImageUrls = images.drop(1),
        status = deriveStatus(this),
        price = this.price.toPriceString(),
        priceRaw = this.price,
        originalPrice = this.originalPrice?.toPriceString(),
        brand = "Holo Crew",
        description = this.description ?: "",
        rating = this.avgRating.toFloat(),
        reviewCount = this.reviewCount,
        sizes = sizeList,
        sku = this.sku ?: "",
        stock = this.stock,
        collection = this.collection ?: "",
        gender = this.gender ?: "",
        isFavorite = isFavorite
    )
}