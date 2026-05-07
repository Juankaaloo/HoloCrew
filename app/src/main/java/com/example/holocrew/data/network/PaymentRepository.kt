package com.example.holocrew.data.network

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

object PaymentRepository {

    private val client get() = SupabaseClient.client

    private fun getCurrentUserId(): String? = client.auth.currentUserOrNull()?.id

    suspend fun getPaymentMethods(): List<SbPaymentMethodDto> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            client.from("user_payment_methods")
                .select {
                    filter { eq("user_id", userId) }
                    order("is_default", Order.DESCENDING)
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<SbPaymentMethodDto>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addPaymentMethod(
        paymentType: String = "credit_card",
        cardLastFour: String? = null,
        cardBrand: String? = null,
        expiryMonth: Int? = null,
        expiryYear: Int? = null,
        isDefault: Boolean = false
    ): Boolean {
        val userId = getCurrentUserId() ?: return false
        return try {
            if (isDefault) {
                client.from("user_payment_methods")
                    .update({ set("is_default", false) }) {
                        filter { eq("user_id", userId) }
                    }
            }
            client.from("user_payment_methods")
                .insert(SbPaymentMethodInsert(
                    userId = userId,
                    paymentType = paymentType,
                    cardLastFour = cardLastFour,
                    cardBrand = cardBrand,
                    cardExpiryMonth = expiryMonth,
                    cardExpiryYear = expiryYear,
                    isDefault = isDefault
                ))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deletePaymentMethod(methodId: String): Boolean {
        return try {
            client.from("user_payment_methods")
                .delete { filter { eq("id", methodId) } }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}