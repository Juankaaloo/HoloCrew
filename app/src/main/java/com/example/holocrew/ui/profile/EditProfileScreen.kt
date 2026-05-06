@file:OptIn(kotlinx.serialization.InternalSerializationApi::class, kotlinx.serialization.ExperimentalSerializationApi::class)

package com.example.holocrew.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.ProfileData
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.SupabaseClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.auth.FieldLabel
import com.example.holocrew.ui.auth.holoTextFieldColors
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

/**
 * EditProfileScreen — Edición de perfil con Supabase directo.
 * Lee y actualiza la tabla `profiles` via Postgrest.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val supabase = SupabaseClient.client

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Cargar perfil desde Supabase
    LaunchedEffect(Unit) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId != null) {
                val profile = supabase.postgrest
                    .from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<ProfileData>()
                firstName = profile.first_name
                lastName = profile.last_name
                email = profile.email
                phone = profile.phone ?: ""
                city = profile.city ?: ""
                username = profile.username ?: ""
            }
        } catch (e: Exception) {
            email = supabase.auth.currentUserOrNull()?.email ?: ""
            e.printStackTrace()
        }
        isLoading = false
    }

    val initials = remember(firstName, lastName) {
        listOfNotNull(firstName.firstOrNull()?.uppercase(), lastName.firstOrNull()?.uppercase())
            .joinToString("").ifEmpty { "HC" }
    }

    fun saveProfile() {
        scope.launch {
            isSaving = true
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                supabase.postgrest.from("profiles").update(
                    mapOf(
                        "first_name" to firstName.trim(),
                        "last_name" to lastName.trim(),
                        "phone" to phone.trim().ifEmpty { null },
                        "city" to city.trim().ifEmpty { null },
                        "username" to username.trim().ifEmpty { null }
                    )
                ) { filter { eq("id", userId) } }
                TokenManager.loadProfile()
                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                navController.navigateUp()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            isSaving = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper) } },
                actions = { if (!isLoading) { IconButton(onClick = { saveProfile() }) { Icon(Icons.Filled.Check, "Guardar", tint = HoloColors.Pulse) } } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink)
            )
        },
        containerColor = HoloColors.Paper
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Cargando...", style = HoloType.TitleLarge, color = HoloColors.TextTertiary)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800)))
                        .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(HoloColors.Neutral700).border(HoloSpacing.BorderDefault, HoloColors.Pulse, CircleShape), contentAlignment = Alignment.Center) {
                            Text(initials, style = HoloType.HeadlineLarge, color = HoloColors.Pulse)
                        }
                        Spacer(Modifier.height(HoloSpacing.sm))
                        Text("Editar perfil", style = HoloType.HeadlineMedium, color = HoloColors.TextOnDark)
                        Text(email, style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted, modifier = Modifier.padding(top = HoloSpacing.xxs))
                    }
                }
                Column(modifier = Modifier.padding(HoloSpacing.lg)) {
                    ProfileTextField("NOMBRE", firstName) { firstName = it }
                    Spacer(Modifier.height(HoloSpacing.lg))
                    ProfileTextField("APELLIDOS", lastName) { lastName = it }
                    Spacer(Modifier.height(HoloSpacing.lg))
                    ProfileTextField("USUARIO", username, placeholder = "@usuario") { username = it }
                    Spacer(Modifier.height(HoloSpacing.lg))
                    ProfileTextField("TELÉFONO", phone, placeholder = "+34 600 000 000") { phone = it }
                    Spacer(Modifier.height(HoloSpacing.lg))
                    ProfileTextField("CIUDAD", city, placeholder = "Tu ciudad") { city = it }
                    Spacer(Modifier.height(HoloSpacing.xxl))
                    Button(onClick = { saveProfile() }, modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                        shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                        colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink, disabledContainerColor = HoloColors.Neutral600), enabled = !isSaving
                    ) { Text(if (isSaving) "GUARDANDO..." else "GUARDAR CAMBIOS", style = HoloType.LabelLarge, color = HoloColors.Paper) }
                    Spacer(Modifier.height(HoloSpacing.md))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                        colors = CardDefaults.cardColors(containerColor = HoloColors.Fog), elevation = CardDefaults.cardElevation(0.dp)) {
                        Row(Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, null, tint = HoloColors.TextTertiary, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
                            Spacer(Modifier.width(HoloSpacing.sm))
                            Text("El email no se puede modificar por seguridad", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, placeholder: String = "", onValueChange: (String) -> Unit) {
    FieldLabel(label)
    OutlinedTextField(value = value, onValueChange = onValueChange,
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, style = HoloType.BodyMedium, color = HoloColors.TextDisabled) } } else null,
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), singleLine = true)
}