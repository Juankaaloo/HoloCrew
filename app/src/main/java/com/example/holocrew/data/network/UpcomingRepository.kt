package com.example.holocrew.data.network

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

object UpcomingRepository {

    private val client get() = SupabaseClient.client

    suspend fun getAll(): List<SbUpcomingDto> {
        return try {
            client.from("upcoming_products")
                .select {
                    order("launch_date", Order.ASCENDING)
                }
                .decodeList<SbUpcomingDto>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getByStatus(status: String): List<SbUpcomingDto> {
        return try {
            client.from("upcoming_products")
                .select {
                    filter { eq("status", status) }
                    order("launch_date", Order.ASCENDING)
                }
                .decodeList<SbUpcomingDto>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}