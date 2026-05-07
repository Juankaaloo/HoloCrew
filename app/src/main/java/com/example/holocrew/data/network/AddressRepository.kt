package com.example.holocrew.data.network

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

object AddressRepository {

    private val client get() = SupabaseClient.client

    private fun getCurrentUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }

    suspend fun getAddresses(): List<SbAddressDto> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            client.from("addresses")
                .select {
                    filter { eq("user_id", userId) }
                    order("is_default", Order.DESCENDING)
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<SbAddressDto>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addAddress(
        fullName: String,
        phone: String? = null,
        street: String,
        streetLine2: String? = null,
        city: String,
        postalCode: String,
        countryCode: String = "ES",
        isDefault: Boolean = false,
        label: String? = null
    ): Boolean {
        val userId = getCurrentUserId() ?: return false
        return try {
            if (isDefault) {
                // Quitar default de las demas
                client.from("addresses")
                    .update({ set("is_default", false) }) {
                        filter { eq("user_id", userId) }
                    }
            }
            client.from("addresses")
                .insert(SbAddressInsert(
                    userId = userId,
                    label = label,
                    fullName = fullName,
                    phone = phone,
                    street = street,
                    streetLine2 = streetLine2,
                    city = city,
                    postalCode = postalCode,
                    countryCode = countryCode,
                    isDefault = isDefault
                ))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteAddress(addressId: Int): Boolean {
        return try {
            client.from("addresses")
                .delete { filter { eq("id", addressId) } }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}