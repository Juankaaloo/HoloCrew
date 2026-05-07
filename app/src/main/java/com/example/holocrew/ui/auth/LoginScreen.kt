package com.example.holocrew.ui.auth

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.R
import com.example.holocrew.data.network.CartRepository
import com.example.holocrew.data.network.WishlistRepository
import com.example.holocrew.data.TokenManager
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloMotion
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

/**
 * LoginScreen — Pantalla de inicio de sesión con Supabase Auth.
 *
 * Flujo:
 *  1. Usuario introduce email + contraseña
 *  2. TokenManager.signIn() → Supabase Auth directamente (sin backend Node.js)
 *  3. Si ok → carga carrito/favoritos, navega a Home
 *  4. Si ko → Toast con mensaje de error
 */
@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val loginInteraction = remember { MutableInteractionSource() }
    val isPressed by loginInteraction.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = HoloMotion.smoothSpring(),
        label = "loginBtnScale"
    )

    // Al principio del LaunchedEffect(Unit) que ya tengas, o añade uno nuevo:
    LaunchedEffect(Unit) {
        // Si ya hay sesión activa, saltar al home directamente
        if (TokenManager.isLoggedIn()) {
            TokenManager.loadProfile()
            navController.navigate("home") { popUpTo(0) { inclusive = true } }
            return@LaunchedEffect
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HoloColors.Paper)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HoloSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(HoloSpacing.huge))

            // ── Branding ──────────────────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.logito),
                contentDescription = "Logo HoloCrew",
                modifier = Modifier.height(80.dp).width(200.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(HoloSpacing.sm))
            Text("STREETWEAR EXCLUSIVO", style = HoloType.LabelSmall, color = HoloColors.Pulse)
            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Línea roja ────────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth().height(HoloSpacing.BorderDefault).background(HoloColors.Pulse))
            Spacer(Modifier.height(HoloSpacing.xxl))

            // ── Título ────────────────────────────────────────────────────────
            Text("Iniciar sesión", style = HoloType.HeadlineLarge, color = HoloColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text("Accede a tu cuenta HoloCrew", style = HoloType.BodyMedium, color = HoloColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(HoloSpacing.xxl))

            // ── Campo EMAIL ───────────────────────────────────────────────────
            FieldLabel("EMAIL")
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("tu@email.com", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(HoloSpacing.lg))

            // ── Campo CONTRASEÑA ──────────────────────────────────────────────
            FieldLabel("CONTRASEÑA")
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Tu contraseña", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(), singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = HoloColors.TextTertiary
                        )
                    }
                }
            )
            Spacer(Modifier.height(HoloSpacing.xxl))

            // ── Botón INICIAR SESIÓN (Supabase Auth) ──────────────────────────
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        // ✅ Login directo con Supabase Auth
                        val result = TokenManager.signIn(email.trim(), password)
                        result.onSuccess {
                            CartRepository.loadCart()
                            WishlistRepository.loadFavorites()
                            navController.navigate("home") { popUpTo("login") { inclusive = true } }
                        }.onFailure { error ->
                            val message = when {
                                error.message?.contains("Invalid login", ignoreCase = true) == true -> "Credenciales incorrectas"
                                error.message?.contains("Email not confirmed", ignoreCase = true) == true -> "Confirma tu email antes de iniciar sesión"
                                else -> "Error al iniciar sesión: ${error.message}"
                            }
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight).scale(buttonScale),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink, disabledContainerColor = HoloColors.Neutral600),
                interactionSource = loginInteraction, enabled = !isLoading
            ) {
                Text(if (isLoading) "CARGANDO..." else "INICIAR SESIÓN", style = HoloType.LabelLarge, color = HoloColors.Paper)
            }

            Spacer(Modifier.height(HoloSpacing.lg))

            // ── Separador "o" ─────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f), thickness = HoloSpacing.BorderHairline, color = HoloColors.BorderSubtle)
                Text("o", style = HoloType.BodySmall, color = HoloColors.TextTertiary, modifier = Modifier.padding(horizontal = HoloSpacing.md))
                Divider(Modifier.weight(1f), thickness = HoloSpacing.BorderHairline, color = HoloColors.BorderSubtle)
            }
            Spacer(Modifier.height(HoloSpacing.lg))

            // ── Botón CREAR CUENTA ────────────────────────────────────────────
            OutlinedButton(
                onClick = { navController.navigate("register") { launchSingleTop = true } },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = HoloSpacing.BorderDefault)
            ) {
                Text("CREAR CUENTA", style = HoloType.LabelLarge, color = HoloColors.TextPrimary)
            }

            Spacer(Modifier.weight(1f))

            // ── Footer legal ──────────────────────────────────────────────────
            Text(
                "Al continuar, aceptas los Términos de Servicio\ny la Política de Privacidad de HoloCrew",
                style = HoloType.BodySmall, color = HoloColors.TextDisabled, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = HoloSpacing.xl)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// UTILIDADES COMPARTIDAS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun holoTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = HoloColors.Ink,
    unfocusedBorderColor = HoloColors.BorderSubtle,
    cursorColor = HoloColors.Pulse,
    focusedLabelColor = HoloColors.Ink,
    unfocusedLabelColor = HoloColors.TextTertiary
)

@Composable
fun FieldLabel(text: String) {
    Text(text, style = HoloType.LabelMedium, color = HoloColors.TextTertiary,
        modifier = Modifier.fillMaxWidth().padding(bottom = HoloSpacing.xs))
}