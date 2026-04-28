package com.example.holocrew.data.network

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

data class PaginatedResponse<T>(
    val success: Boolean,
    val data: List<T> = emptyList()
)

// ── Auth ──
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val first_name: String, val last_name: String, val email: String, val password: String)
data class AuthData(val token: String, val user: UserDto)
data class UserDto(
    val id: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val membership_tier: String,
    val avatar_url: String? = null,
    val total_credits: Int = 0,
    val lifetime_points: Int = 0,
    val phone: String? = null,
    val city: String? = null,
    val username: String? = null
)

// ── Update Profile ──
data class UpdateProfileRequest(
    val first_name: String,
    val last_name: String,
    val phone: String? = null,
    val city: String? = null,
    val username: String? = null
)

// ── Products ──
data class ProductDto(
    val id: String,
    val name: String,
    val slug: String? = null,
    val description: String? = null,
    val main_category: String,
    val price_eur: Double,
    val compare_at_price_eur: Double? = null,
    val is_featured: Boolean = false,
    val is_new_arrival: Boolean = false,
    val collection_name: String? = null,
    val primary_image: String? = null,
    val avg_rating: Double? = null,
    val review_count: Int = 0,
    val total_stock: Int = 0,
    val material: String? = null,
    val images: List<ProductImageDto> = emptyList(),
    val variants: List<VariantDto> = emptyList()
)

data class ProductImageDto(val id: String, val image_url: String, val is_primary: Boolean = false)
data class VariantDto(
    val id: String,
    val sku: String,
    val stock_quantity: Int,
    val color_name: String? = null,
    val hex_code: String? = null,
    val size_name: String? = null
)

data class CollectionDto(val id: String, val name: String, val slug: String)

// ── Cart ──
data class CartResponse(val cart_id: String, val items: List<CartItemDto>, val summary: CartSummaryDto)
data class CartItemDto(
    val id: String,
    val quantity: Int,
    val price_at_addition: Double,
    val product_id: String,
    val name: String,
    val price_eur: Double,
    val variant_id: String? = null,
    val color_name: String? = null,
    val size_name: String? = null,
    val image_url: String? = null
)
data class CartSummaryDto(val subtotal: Double, val shipping: Double, val total: Double, val item_count: Int)
data class AddToCartRequest(val product_id: String, val variant_id: String? = null, val quantity: Int = 1)
data class UpdateCartItemRequest(val quantity: Int)

// ── Orders ──
data class OrderDto(
    val id: String,
    val order_number: String,
    val status: String,
    val total_eur: String,
    val created_at: String,
    val item_count: Int = 0
)
data class OrderDetailDto(
    val id: String,
    val order_number: String,
    val status: String,
    val subtotal_eur: String,
    val shipping_eur: String,
    val total_eur: String,
    val created_at: String,
    val items: List<OrderItemDto> = emptyList()
)
data class OrderItemDto(
    val id: String,
    val product_name: String,
    val product_image_url: String? = null,
    val color_name: String? = null,
    val size_name: String? = null,
    val quantity: Int,
    val unit_price_eur: String,
    val total_price_eur: String
)
data class CreateOrderRequest(
    val shipping_address: ShippingAddressDto,
    val shipping_method: String = "standard",
    val payment_method: String = "credit_card"
)
data class ShippingAddressDto(
    val recipient_name: String,
    val street_address: String,
    val city: String,
    val postal_code: String,
    val country_code: String = "ES"
)

// ── Addresses ──
data class AddressDto(
    val id: String,
    val address_type: String? = null,
    val is_default: Boolean = false,
    val recipient_name: String,
    val phone: String? = null,
    val street_address: String,
    val apartment: String? = null,
    val city: String,
    val state_province: String? = null,
    val postal_code: String,
    val country_code: String = "ES"
)
data class AddAddressRequest(
    val recipient_name: String,
    val phone: String? = null,
    val street_address: String,
    val apartment: String? = null,
    val city: String,
    val state_province: String? = null,
    val postal_code: String,
    val country_code: String = "ES",
    val is_default: Boolean = false
)

// ── Payment Methods ──
data class PaymentMethodDto(
    val id: String,
    val payment_type: String,
    val is_default: Boolean = false,
    val card_last_four: String? = null,
    val card_brand: String? = null,
    val card_expiry_month: Int? = null,
    val card_expiry_year: Int? = null
)

// ── Membership ──
data class MembershipDto(
    val membership_tier: String,
    val total_credits: Int,
    val lifetime_points: Int,
    val points_to_next: Int
)

data class AddPaymentMethodRequest(
    val payment_type: String,
    val card_last_four: String? = null,
    val card_brand: String? = null,
    val card_expiry_month: Int? = null,
    val card_expiry_year: Int? = null,
    val is_default: Boolean = false
)
