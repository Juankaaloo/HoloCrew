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
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.data.network.UpdateProfileRequest
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.auth.FieldLabel
import com.example.holocrew.ui.auth.holoTextFieldColors
import kotlinx.coroutines.launch

/**
 * EditProfileScreen — Pantalla de edición de datos personales.
 *
 * Diseño SNKRS:
 *  - TopBar negra con flecha atrás + botón check (guardar) en rojo Pulse
 *  - Header negro con avatar (iniciales) + título "Editar perfil" + email
 *  - Formulario con 5 campos: nombre, apellidos, usuario, teléfono, ciudad
 *  - Botón "GUARDAR CAMBIOS" negro pill
 *  - Nota de que el email no se puede modificar
 *
 * ⚠️ BUG FIX: La versión anterior llamaba a TokenManager.saveToken() pasando
 * userId="" y userTier="", lo cual BORRABA estos datos del DataStore y rompía
 * el tier en ProfileScreen. Ahora solo actualizamos userName y userEmail
 * sin tocar userId ni userTier.
 *
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── Estado del formulario ─────────────────────────────────────────────────
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Cargar datos actuales del usuario desde la API
    LaunchedEffect(Unit) {
        val token = TokenManager.getTokenOnce(context) ?: return@LaunchedEffect
        try {
            val response = RetrofitClient.api.getMe("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.data!!
                firstName = user.first_name
                lastName = user.last_name
                email = user.email
                phone = user.phone ?: ""
                city = user.city ?: ""
                username = user.username ?: ""
            }
        } catch (e: Exception) { e.printStackTrace() }
        isLoading = false
    }

    // Iniciales para el avatar
    val initials = remember(firstName, lastName) {
        listOfNotNull(firstName.firstOrNull()?.uppercase(), lastName.firstOrNull()?.uppercase())
            .joinToString("").ifEmpty { "HC" }
    }

    /**
     * Función para guardar los cambios del perfil.
     * Llama a PUT /api/auth/me y actualiza SOLO el nombre en el DataStore local.
     *
     * ⚠️ NO usa saveToken() para evitar borrar userId y userTier.
     * En su lugar, recarga los datos con getMe si es necesario.
     */
    fun saveProfile() {
        scope.launch {
            isSaving = true
            val token = TokenManager.getTokenOnce(context) ?: return@launch
            try {
                val response = RetrofitClient.api.updateMe(
                    "Bearer $token",
                    UpdateProfileRequest(
                        firstName.trim(),
                        lastName.trim(),
                        phone.trim().ifEmpty { null },
                        city.trim().ifEmpty { null },
                        username.trim().ifEmpty { null }
                    )
                )
                if (response.isSuccessful) {
                    // ✅ FIX: Solo actualizamos userName en DataStore, SIN tocar userId ni userTier
                    // Usamos saveToken con los valores actuales del DataStore para no perder datos
                    val currentToken = TokenManager.getTokenOnce(context) ?: token
                    // Recargar datos completos del usuario desde la API para no perder nada
                    val meResponse = RetrofitClient.api.getMe("Bearer $currentToken")
                    if (meResponse.isSuccessful && meResponse.body()?.success == true) {
                        val updatedUser = meResponse.body()!!.data!!
                        TokenManager.saveToken(
                            context,
                            token = currentToken,
                            userId = updatedUser.id,
                            userName = "${updatedUser.first_name} ${updatedUser.last_name}",
                            userEmail = updatedUser.email,
                            userTier = updatedUser.membership_tier
                        )
                    }
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                } else {
                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
            isSaving = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper)
                    }
                },
                actions = {
                    // Botón check para guardar (rojo Pulse como acento)
                    if (!isLoading) {
                        IconButton(onClick = { saveProfile() }) {
                            Icon(Icons.Filled.Check, "Guardar", tint = HoloColors.Pulse)
                        }
                    }
                },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Header negro con avatar ───────────────────────────────────
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800)))
                        .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Avatar con borde rojo Pulse
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                                .background(HoloColors.Neutral700)
                                .border(HoloSpacing.BorderDefault, HoloColors.Pulse, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, style = HoloType.HeadlineLarge, color = HoloColors.Pulse)
                        }
                        Spacer(Modifier.height(HoloSpacing.sm))
                        Text("Editar perfil", style = HoloType.HeadlineMedium, color = HoloColors.TextOnDark)
                        Text(email, style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted, modifier = Modifier.padding(top = HoloSpacing.xxs))
                    }
                }

                // ── Formulario ────────────────────────────────────────────────
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

                    // Botón guardar cambios
                    Button(
                        onClick = { saveProfile() },
                        modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                        shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloColors.Ink,
                            disabledContainerColor = HoloColors.Neutral600
                        ),
                        enabled = !isSaving
                    ) {
                        Text(
                            if (isSaving) "GUARDANDO..." else "GUARDAR CAMBIOS",
                            style = HoloType.LabelLarge, color = HoloColors.Paper
                        )
                    }

                    Spacer(Modifier.height(HoloSpacing.md))

                    // Nota sobre el email
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                        colors = CardDefaults.cardColors(containerColor = HoloColors.Fog),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(HoloSpacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

/**
 * ProfileTextField — Campo de texto reutilizable para el formulario de perfil.
 * Label uppercase + OutlinedTextField con cursor rojo Pulse.
 */
@Composable
fun ProfileTextField(label: String, value: String, placeholder: String = "", onValueChange: (String) -> Unit) {
    FieldLabel(label)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, style = HoloType.BodyMedium, color = HoloColors.TextDisabled) }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = holoTextFieldColors(),
        singleLine = true
    )
}