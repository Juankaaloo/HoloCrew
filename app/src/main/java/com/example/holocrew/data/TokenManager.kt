package com.example.holocrew.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "holocrew_prefs")

object TokenManager {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_TIER_KEY = stringPreferencesKey("user_tier")

    fun getToken(context: Context): Flow<String?> =
        context.dataStore.data.map { it[TOKEN_KEY] }

    fun getUserName(context: Context): Flow<String?> =
        context.dataStore.data.map { it[USER_NAME_KEY] }

    fun getUserEmail(context: Context): Flow<String?> =
        context.dataStore.data.map { it[USER_EMAIL_KEY] }

    fun getUserTier(context: Context): Flow<String?> =
        context.dataStore.data.map { it[USER_TIER_KEY] }

    suspend fun saveToken(
        context: Context,
        token: String,
        userId: String,
        userName: String,
        userEmail: String,
        userTier: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[USER_NAME_KEY] = userName
            prefs[USER_EMAIL_KEY] = userEmail
            prefs[USER_TIER_KEY] = userTier
        }
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getTokenOnce(context: Context): String? =
        context.dataStore.data.map { it[TOKEN_KEY] }.first()
}