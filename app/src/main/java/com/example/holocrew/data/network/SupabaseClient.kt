package com.example.holocrew.data.network

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {

    private const val SUPABASE_URL = "https://unaptwklykdhbonuzhwm.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVuYXB0d2tseWtkaGJvbnV6aHdtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjYwMjU4MDEsImV4cCI6MjA4MTYwMTgwMX0.Otn5XzTXziMRH3Dwsa0wzvkpEZMJtWk06oxcog7MlwA"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}