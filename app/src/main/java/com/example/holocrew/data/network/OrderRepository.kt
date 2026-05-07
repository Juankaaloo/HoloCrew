package com.example.holocrew.data.network

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

object OrderRepository {

    private val client get() = SupabaseClient.client

    private fun getCurrentUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }

    suspend fun getOrders(): List<SbOrderDto> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            client.from("orders")
                .select(Columns.raw("*, order_items(*)")) {
                    filter { eq("user_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<SbOrderDto>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getOrderById(orderId: Int): SbOrderDto? {
        return try {
            client.from("orders")
                .select(Columns.raw("*, order_items(*)")) {
                    filter { eq("id", orderId) }
                    limit(1)
                }
                .decodeSingleOrNull<SbOrderDto>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}