package com.example.holocrew.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.CartRepository
import com.example.holocrew.data.network.SupabaseClient
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.auth.FieldLabel
import com.example.holocrew.ui.auth.holoTextFieldColors
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = HoloColors.Paper,
            title = { Text("Eliminar cuenta", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary) },
            text = { Text("Esta accion es irreversible. Se eliminaran todos tus datos, pedidos, direcciones y metodos de pago.", style = HoloType.BodyMedium, color = HoloColors.TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        showDeleteDialog = false
                        Toast.makeText(context, "Contacta soporte para eliminar tu cuenta: soporte@holocrew.com", Toast.LENGTH_LONG).show()
                    }
                }) { Text("ELIMINAR", style = HoloType.LabelLarge, color = HoloColors.Pulse) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("CANCELAR", style = HoloType.LabelLarge, color = HoloColors.TextPrimary) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper) }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink))
        },
        containerColor = HoloColors.Paper
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {

            Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800))).padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)) {
                Column {
                    Text("SEGURIDAD", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                    Spacer(Modifier.height(HoloSpacing.xxs))
                    Text("Privacidad y seguridad", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                    Spacer(Modifier.height(HoloSpacing.xxs))
                    Text("Gestiona tu contraseña y cuenta", style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted)
                }
            }

            Column(Modifier.padding(HoloSpacing.lg)) {

                // Cambiar contraseña
                Text("CAMBIAR CONTRASEÑA", style = HoloType.LabelMedium, color = HoloColors.TextTertiary)
                Spacer(Modifier.height(HoloSpacing.md))

                FieldLabel("NUEVA CONTRASEÑA")
                OutlinedTextField(
                    value = newPassword, onValueChange = { newPassword = it },
                    placeholder = { Text("Minimo 6 caracteres", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                    colors = holoTextFieldColors(), singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "Toggle", tint = HoloColors.TextTertiary)
                        }
                    }
                )
                Spacer(Modifier.height(HoloSpacing.md))

                FieldLabel("CONFIRMAR CONTRASEÑA")
                OutlinedTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it },
                    placeholder = { Text("Repite la contraseña", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                    colors = holoTextFieldColors(), singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(Modifier.height(HoloSpacing.xl))

                Button(
                    onClick = {
                        when {
                            newPassword.length < 6 -> Toast.makeText(context, "Minimo 6 caracteres", Toast.LENGTH_SHORT).show()
                            newPassword != confirmPassword -> Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            else -> {
                                scope.launch {
                                    isSaving = true
                                    try {
                                        SupabaseClient.client.auth.updateUser { password = newPassword }
                                        Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                        newPassword = ""; confirmPassword = ""
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    isSaving = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                    shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                    colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                    enabled = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && !isSaving
                ) { Text(if (isSaving) "GUARDANDO..." else "CAMBIAR CONTRASEÑA", style = HoloType.LabelLarge, color = HoloColors.Paper) }

                Spacer(Modifier.height(HoloSpacing.xxxl))

                // Zona peligrosa
                Text("ZONA PELIGROSA", style = HoloType.LabelMedium, color = HoloColors.Pulse)
                Spacer(Modifier.height(HoloSpacing.md))

                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                        .border(HoloSpacing.BorderHairline, HoloColors.Pulse.copy(alpha = 0.3f), RoundedCornerShape(HoloSpacing.RadiusMd))
                        .clickable { showDeleteDialog = true }
                        .padding(HoloSpacing.lg)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.DeleteForever, null, tint = HoloColors.Pulse, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(HoloSpacing.sm))
                        Column {
                            Text("Eliminar cuenta", style = HoloType.TitleMedium, color = HoloColors.Pulse)
                            Text("Esta accion no se puede deshacer", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
                        }
                    }
                }
            }
        }
    }
}