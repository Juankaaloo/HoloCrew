package com.example.holocrew.data.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // AUTH
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthData>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthData>>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<ApiResponse<UserDto>>

    @PUT("auth/me")
    suspend fun updateMe(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<Unit>>

    // PRODUCTS
    @GET("products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<ProductDto>>

    @GET("products/featured")
    suspend fun getFeaturedProducts(@Query("limit") limit: Int = 10): Response<ApiResponse<List<ProductDto>>>

    @GET("products/new-arrivals")
    suspend fun getNewArrivals(@Query("limit") limit: Int = 10): Response<ApiResponse<List<ProductDto>>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<ApiResponse<ProductDto>>

    // CART
    @GET("cart")
    suspend fun getCart(@Header("Authorization") token: String): Response<ApiResponse<CartResponse>>

    @POST("cart/items")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): Response<ApiResponse<Unit>>

    @PUT("cart/items/{itemId}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String,
        @Body request: UpdateCartItemRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("cart/items/{itemId}")
    suspend fun removeCartItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String
    ): Response<ApiResponse<Unit>>

    @DELETE("cart")
    suspend fun clearCart(@Header("Authorization") token: String): Response<ApiResponse<Unit>>

    // FAVORITES
    @GET("favorites/ids")
    suspend fun getFavoriteIds(@Header("Authorization") token: String): Response<ApiResponse<List<String>>>

    @POST("favorites/{productId}")
    suspend fun toggleFavorite(
        @Header("Authorization") token: String,
        @Path("productId") productId: String
    ): Response<ApiResponse<Unit>>

    // ORDERS
    @GET("orders")
    suspend fun getOrders(@Header("Authorization") token: String): Response<ApiResponse<List<OrderDto>>>

    @GET("orders/{id}")
    suspend fun getOrderById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<OrderDetailDto>>

    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): Response<ApiResponse<OrderDto>>

    // PROFILE - ADDRESSES
    @GET("profile/addresses")
    suspend fun getAddresses(@Header("Authorization") token: String): Response<ApiResponse<List<AddressDto>>>

    @POST("profile/addresses")
    suspend fun addAddress(
        @Header("Authorization") token: String,
        @Body request: AddAddressRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("profile/addresses/{id}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    // PROFILE - PAYMENT METHODS
    @GET("profile/payment-methods")
    suspend fun getPaymentMethods(@Header("Authorization") token: String): Response<ApiResponse<List<PaymentMethodDto>>>

    @POST("profile/payment-methods")
    suspend fun addPaymentMethod(
        @Header("Authorization") token: String,
        @Body request: AddPaymentMethodRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("profile/payment-methods/{id}")
    suspend fun deletePaymentMethod(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    // PROFILE - MEMBERSHIP
    @GET("profile/membership")
    suspend fun getMembership(@Header("Authorization") token: String): Response<ApiResponse<MembershipDto>>
}
