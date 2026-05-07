package com.example.holocrew.data.network

import com.example.holocrew.ui.product.ProductDetail
import com.example.holocrew.ui.product.toProductDetail
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

object ProductRepository {

    private const val SELECT_ALL =
        "*, categories(name), product_images(image_url), product_sizes(size, stock)"

    private val client get() = SupabaseClient.client

    suspend fun getAll(): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter { eq("is_active", true) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getById(productId: Int): ProductDetail? {
        return try {
            client.from("products")
                .select(Columns.raw(SELECT_ALL)) {
                    filter { eq("id", productId) }
                    limit(1)
                }
                .decodeSingleOrNull<SbProductDto>()
                ?.toProductDetail()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFeatured(limit: Int = 4): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    eq("is_featured", true)
                }
                order("created_at", Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getNew(limit: Int = 8): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    eq("is_new", true)
                }
                order("created_at", Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getBestSellers(limit: Int = 6): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    eq("is_best_seller", true)
                }
                order("view_count", Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getTrending(limit: Int = 4): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    gt("review_count", 0)
                }
                order("avg_rating", Order.DESCENDING)
                order("review_count", Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getOnSale(limit: Int = 6): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    gt("original_price", 0)
                }
                order("created_at", Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getFlashSale(): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    or {
                        eq("is_flash_sale", true)
                        eq("is_black_week", true)
                    }
                }
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getByCollection(collection: String): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    eq("collection", collection)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getByCategory(categoryId: Int): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    eq("category_id", categoryId)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun getRelated(productId: Int, categoryId: Int, limit: Int = 4): List<ProductDetail> {
        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    eq("category_id", categoryId)
                    neq("id", productId)
                }
                limit(limit.toLong())
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }

    suspend fun search(query: String): List<ProductDetail> {
        if (query.isBlank()) return emptyList()

        return client.from("products")
            .select(Columns.raw(SELECT_ALL)) {
                filter {
                    eq("is_active", true)
                    or {
                        ilike("name", "%$query%")
                        ilike("description", "%$query%")
                    }
                }
            }
            .decodeList<SbProductDto>()
            .map { it.toProductDetail() }
    }
}