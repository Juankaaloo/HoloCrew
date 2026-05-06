@file:OptIn(kotlinx.serialization.InternalSerializationApi::class, kotlinx.serialization.ExperimentalSerializationApi::class)

package com.example.holocrew.data

import android.content.Context
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import com.example.holocrew.data.network.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * TokenManager — Gestión de autenticación y datos de usuario con Supabase.
 *
 * ANTES: Guardaba el JWT manualmente en DataStore y lo enviaba con "Bearer $token".
 * AHORA: Usa Supabase Auth, que gestiona la sesión (JWT) internamente.
 *
 * El SDK refresca el token automáticamente cuando expira y lo incluye
 * en cada petición Postgrest/Storage sin intervención manual.
 */
object TokenManager {

    private val supabase = SupabaseClient.client

    // ── Cache de datos del perfil (observables por las pantallas) ──────────
    private val _userName = MutableStateFlow<String?>(null)
    private val _userEmail = MutableStateFlow<String?>(null)
    private val _userTier = MutableStateFlow<String?>(null)
    private val _userId = MutableStateFlow<String?>(null)

    // ═══════════════════════════════════════════════════════════════════════
    // API PÚBLICA — Flows para observar desde las pantallas
    // ═══════════════════════════════════════════════════════════════════════

    fun getUserName(context: Context): StateFlow<String?> = _userName
    fun getUserEmail(context: Context): StateFlow<String?> = _userEmail
    fun getUserTier(context: Context): StateFlow<String?> = _userTier
    fun getUserId(): String? = _userId.value
    fun isLoggedIn(): Boolean = supabase.auth.currentUserOrNull() != null

    // ═══════════════════════════════════════════════════════════════════════
    // REGISTRO
    // ═══════════════════════════════════════════════════════════════════════

    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<Unit> {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("first_name", firstName)
                    put("last_name", lastName)
                }
            }
            loadProfile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LOGIN
    // ═══════════════════════════════════════════════════════════════════════

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            loadProfile()
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return Result.success(Unit)
                supabase.postgrest.from("profiles")
                    .update(mapOf("last_login" to "now()")) {
                        filter { eq("id", userId) }
                    }
            } catch (_: Exception) { }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LOGOUT
    // ═══════════════════════════════════════════════════════════════════════

    suspend fun signOut() {
        try { supabase.auth.signOut() } catch (_: Exception) { }
        _userName.value = null
        _userEmail.value = null
        _userTier.value = null
        _userId.value = null
    }

    // ═══════════════════════════════════════════════════════════════════════
    // COMPATIBILIDAD — Para código que aún no se ha migrado
    // ═══════════════════════════════════════════════════════════════════════

    suspend fun clearToken(context: Context) = signOut()

    suspend fun getTokenOnce(context: Context): String? {
        return try { supabase.auth.currentAccessTokenOrNull() } catch (_: Exception) { null }
    }

    suspend fun saveToken(
        context: Context, token: String, userId: String,
        userName: String, userEmail: String, userTier: String
    ) {
        _userName.value = userName
        _userEmail.value = userEmail
        if (userTier.isNotBlank()) _userTier.value = userTier
    }

    fun getToken(context: Context): kotlinx.coroutines.flow.Flow<String?> {
        return flow {
            emit(try { supabase.auth.currentAccessTokenOrNull() } catch (_: Exception) { null })
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PERFIL — Cargar datos desde la tabla profiles
    // ═══════════════════════════════════════════════════════════════════════

    suspend fun loadProfile() {
        try {
            val user = supabase.auth.currentUserOrNull() ?: return
            val userId = user.id
            val profile = supabase.postgrest
                .from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeSingle<ProfileData>()
            _userId.value = userId
            _userName.value = "${profile.first_name} ${profile.last_name}".trim()
            _userEmail.value = profile.email
            _userTier.value = profile.membership_tier
        } catch (e: Exception) {
            val user = supabase.auth.currentUserOrNull()
            _userId.value = user?.id
            _userEmail.value = user?.email
            e.printStackTrace()
        }
    }

    suspend fun restoreSession() {
        try {
            if (supabase.auth.currentUserOrNull() != null) loadProfile()
        } catch (_: Exception) { }
    }
}

@Serializable
data class ProfileData(
    val id: String = "",
    val email: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val phone: String? = null,
    val username: String? = null,
    val city: String? = null,
    val avatar_url: String? = null,
    val membership_tier: String = "bronze",
    val total_credits: Int = 0,
    val lifetime_points: Int = 0
)