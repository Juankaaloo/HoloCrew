@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.holocrew.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── products ─────────────────────────────────────────────────────────────────

@Serializable
data class SbProductDto(
    val id: Int, val name: String, val slug: String? = null, val sku: String? = null,
    @SerialName("short_description") val shortDescription: String? = null,
    val description: String? = null, val price: Double,
    @SerialName("original_price") val originalPrice: Double? = null,
    @SerialName("category_id") val categoryId: Int? = null,
    val gender: String? = null, val stock: Int = 0,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_new") val isNew: Boolean = false,
    @SerialName("is_featured") val isFeatured: Boolean = false,
    @SerialName("is_best_seller") val isBestSeller: Boolean = false,
    @SerialName("is_black_week") val isBlackWeek: Boolean = false,
    @SerialName("is_flash_sale") val isFlashSale: Boolean = false,
    @SerialName("is_app_exclusive") val isAppExclusive: Boolean = false,
    val collection: String? = null,
    @SerialName("avg_rating") val avgRating: Double = 0.0,
    @SerialName("review_count") val reviewCount: Int = 0,
    @SerialName("view_count") val viewCount: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val categories: SbCategoryEmbed? = null,
    @SerialName("product_images") val productImages: List<SbProductImageDto>? = null,
    @SerialName("product_sizes") val productSizes: List<SbProductSizeDto>? = null
)

@Serializable data class SbCategoryEmbed(val name: String)
@Serializable data class SbProductImageDto(@SerialName("image_url") val imageUrl: String)
@Serializable data class SbProductSizeDto(val size: String, val stock: Int = 0)

// ── cart_items ────────────────────────────────────────────────────────────────

@Serializable
data class SbCartItemDto(
    val id: Int? = null, @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: Int, val size: String? = null,
    val color: String? = null, val quantity: Int = 1,
    @SerialName("price_at_add") val priceAtAdd: Double,
    @SerialName("added_at") val addedAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class SbCartItemInsert(
    @SerialName("user_id") val userId: String, @SerialName("product_id") val productId: Int,
    val size: String? = null, val color: String? = null, val quantity: Int = 1,
    @SerialName("price_at_add") val priceAtAdd: Double
)

// ── wishlist_items ────────────────────────────────────────────────────────────

@Serializable
data class SbWishlistItemDto(
    val id: Int? = null, @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: Int,
    @SerialName("price_at_add") val priceAtAdd: Double,
    @SerialName("notify_on_sale") val notifyOnSale: Boolean = false,
    @SerialName("added_at") val addedAt: String? = null
)

@Serializable
data class SbWishlistItemInsert(
    @SerialName("user_id") val userId: String, @SerialName("product_id") val productId: Int,
    @SerialName("price_at_add") val priceAtAdd: Double,
    @SerialName("notify_on_sale") val notifyOnSale: Boolean = false
)

// ── orders ────────────────────────────────────────────────────────────────────

@Serializable
data class SbOrderDto(
    val id: Int, @SerialName("order_number") val orderNumber: String,
    @SerialName("user_id") val userId: String, val status: String,
    @SerialName("payment_status") val paymentStatus: String? = null,
    val subtotal: Double = 0.0, @SerialName("shipping_cost") val shippingCost: Double = 0.0,
    val total: Double = 0.0, @SerialName("shipping_method") val shippingMethod: String? = null,
    @SerialName("tracking_number") val trackingNumber: String? = null,
    @SerialName("order_date") val orderDate: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("order_items") val orderItems: List<SbOrderItemDto>? = null
)

@Serializable
data class SbOrderItemDto(
    val id: Int, @SerialName("order_id") val orderId: Int,
    @SerialName("product_id") val productId: Int,
    @SerialName("product_name") val productName: String,
    @SerialName("product_image_url") val productImageUrl: String? = null,
    val size: String? = null, val color: String? = null,
    @SerialName("unit_price") val unitPrice: Double, val quantity: Int, val total: Double
)

// ── addresses ─────────────────────────────────────────────────────────────────

@Serializable
data class SbAddressDto(
    val id: Int? = null, @SerialName("user_id") val userId: String? = null,
    val label: String? = null, @SerialName("full_name") val fullName: String,
    val phone: String? = null, val street: String,
    @SerialName("street_line2") val streetLine2: String? = null,
    val city: String, val state: String? = null,
    @SerialName("postal_code") val postalCode: String,
    val country: String? = null, @SerialName("country_code") val countryCode: String = "ES",
    @SerialName("is_default") val isDefault: Boolean = false,
    @SerialName("is_billing_default") val isBillingDefault: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SbAddressInsert(
    @SerialName("user_id") val userId: String, val label: String? = null,
    @SerialName("full_name") val fullName: String, val phone: String? = null,
    val street: String, @SerialName("street_line2") val streetLine2: String? = null,
    val city: String, val state: String? = null,
    @SerialName("postal_code") val postalCode: String,
    @SerialName("country_code") val countryCode: String = "ES",
    @SerialName("is_default") val isDefault: Boolean = false
)

// ── upcoming_products ─────────────────────────────────────────────────────────

@Serializable
data class SbUpcomingDto(
    val id: Int, val title: String, val subtitle: String? = null,
    @SerialName("launch_date") val launchDate: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val status: String = "upcoming", val price: Double? = null
)

// ── user_payment_methods ──────────────────────────────────────────────────────

@Serializable
data class SbPaymentMethodDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("payment_type") val paymentType: String = "credit_card",
    @SerialName("card_last_four") val cardLastFour: String? = null,
    @SerialName("card_brand") val cardBrand: String? = null,
    @SerialName("card_expiry_month") val cardExpiryMonth: Int? = null,
    @SerialName("card_expiry_year") val cardExpiryYear: Int? = null,
    @SerialName("is_default") val isDefault: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SbPaymentMethodInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("payment_type") val paymentType: String = "credit_card",
    @SerialName("card_last_four") val cardLastFour: String? = null,
    @SerialName("card_brand") val cardBrand: String? = null,
    @SerialName("card_expiry_month") val cardExpiryMonth: Int? = null,
    @SerialName("card_expiry_year") val cardExpiryYear: Int? = null,
    @SerialName("is_default") val isDefault: Boolean = false
)