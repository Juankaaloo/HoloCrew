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
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.LoginRequest
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloMotion
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

/**
 * LoginScreen — Pantalla de inicio de sesión.
 *
 * Primera pantalla visible al abrir la app. Diseño SNKRS sobre fondo blanco:
 *  - Logo centrado con tagline roja debajo
 *  - Línea roja separadora entre branding y formulario
 *  - Inputs con cursor rojo al hacer focus
 *  - Botón primario negro con press-scale feedback
 *  - Scroll habilitado para pantallas pequeñas / teclado abierto
 *
 * Flujo:
 *  1. Usuario introduce email + contraseña
 *  2. Se llama a POST /api/auth/login
 *  3. Si ok → guarda JWT en DataStore, carga carrito/favoritos, navega a Home
 *  4. Si ko → muestra Toast con mensaje de error
 *
 * @param navController Controlador de navegación para ir a Home o Register.
 */
@Composable
fun LoginScreen(navController: NavController) {

    // ── Estado del formulario ─────────────────────────────────────────────────
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── InteractionSource para efecto press-scale en botón principal ──────────
    val loginInteraction = remember { MutableInteractionSource() }
    val isPressed by loginInteraction.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = HoloMotion.smoothSpring(),
        label = "loginBtnScale"
    )

    // ── Layout principal con scroll (fix: pantallas pequeñas con teclado) ─────
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

            // ══════════════════════════════════════════════════════════════════
            // BRANDING — Logo + tagline roja
            // Fondo blanco limpio para que el logo se vea nítido.
            // ══════════════════════════════════════════════════════════════════
            Image(
                painter = painterResource(id = R.drawable.logito),
                contentDescription = "Logo HoloCrew",
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(HoloSpacing.sm))

            // Tagline con acento rojo — SNKRS style (uppercase, tracking alto)
            Text(
                text = "STREETWEAR EXCLUSIVO",
                style = HoloType.LabelSmall,
                color = HoloColors.Pulse
            )

            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Línea roja separadora (acento Supreme) ────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.BorderDefault)
                    .background(HoloColors.Pulse)
            )

            Spacer(Modifier.height(HoloSpacing.xxl))

            // ══════════════════════════════════════════════════════════════════
            // FORMULARIO — título, email, contraseña, botones
            // ══════════════════════════════════════════════════════════════════

            // ── Título de la sección ──────────────────────────────────────────
            Text(
                text = "Iniciar sesión",
                style = HoloType.HeadlineLarge,
                color = HoloColors.TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(HoloSpacing.xxs))

            Text(
                text = "Accede a tu cuenta HoloCrew",
                style = HoloType.BodyMedium,
                color = HoloColors.TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(HoloSpacing.xxl))

            // ── Campo EMAIL ───────────────────────────────────────────────────
            FieldLabel("EMAIL")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text("tu@email.com", style = HoloType.BodyMedium, color = HoloColors.TextDisabled)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(HoloSpacing.lg))

            // ── Campo CONTRASEÑA ──────────────────────────────────────────────
            FieldLabel("CONTRASEÑA")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text("Tu contraseña", style = HoloType.BodyMedium, color = HoloColors.TextDisabled)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(),
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    // Icono ojo para mostrar/ocultar contraseña
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña"
                            else
                                "Mostrar contraseña",
                            tint = HoloColors.TextTertiary
                        )
                    }
                }
            )

            Spacer(Modifier.height(HoloSpacing.xxl))

            // ══════════════════════════════════════════════════════════════════
            // BOTÓN PRINCIPAL — Iniciar sesión
            // Negro con press-scale feedback. Al pulsar se compacta 4%.
            // ══════════════════════════════════════════════════════════════════
            Button(
                onClick = {
                    // Validación básica antes de llamar a la API
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Petición de login al backend
                    scope.launch {
                        isLoading = true
                        try {
                            val response = RetrofitClient.api.login(
                                LoginRequest(email.trim(), password)
                            )

                            if (response.isSuccessful && response.body()?.success == true) {
                                val data = response.body()!!.data!!

                                // Guardar token JWT y datos del usuario en DataStore
                                TokenManager.saveToken(
                                    context,
                                    token = data.token,
                                    userId = data.user.id,
                                    userName = "${data.user.first_name} ${data.user.last_name}",
                                    userEmail = data.user.email,
                                    userTier = data.user.membership_tier
                                )

                                // Pre-cargar carrito y favoritos para que estén listos
                                CartManager.loadCart(context)
                                FavoritesManager.loadFavorites(context)

                                // Navegar a Home limpiando el backstack de auth
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    response.body()?.message ?: "Credenciales incorrectas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error de conexión. ¿Está el servidor encendido?",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.ButtonHeight)
                    .scale(buttonScale),           // Efecto press-scale SNKRS
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HoloColors.Ink,
                    disabledContainerColor = HoloColors.Neutral600
                ),
                interactionSource = loginInteraction,
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "CARGANDO..." else "INICIAR SESIÓN",
                    style = HoloType.LabelLarge,
                    color = HoloColors.Paper
                )
            }

            Spacer(Modifier.height(HoloSpacing.lg))

            // ── Separador "o" ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    thickness = HoloSpacing.BorderHairline,
                    color = HoloColors.BorderSubtle
                )
                Text(
                    text = "o",
                    style = HoloType.BodySmall,
                    color = HoloColors.TextTertiary,
                    modifier = Modifier.padding(horizontal = HoloSpacing.md)
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    thickness = HoloSpacing.BorderHairline,
                    color = HoloColors.BorderSubtle
                )
            }

            Spacer(Modifier.height(HoloSpacing.lg))

            // ══════════════════════════════════════════════════════════════════
            // BOTÓN SECUNDARIO — Crear cuenta
            // Borde 2dp negro (estilo Supreme). Sin relleno.
            // ══════════════════════════════════════════════════════════════════
            OutlinedButton(
                onClick = {
                    navController.navigate("register") { launchSingleTop = true }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.ButtonHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = HoloSpacing.BorderDefault
                )
            ) {
                Text(
                    text = "CREAR CUENTA",
                    style = HoloType.LabelLarge,
                    color = HoloColors.TextPrimary
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Footer legal ──────────────────────────────────────────────────
            Text(
                text = "Al continuar, aceptas los Términos de Servicio\ny la Política de Privacidad de HoloCrew",
                style = HoloType.BodySmall,
                color = HoloColors.TextDisabled,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = HoloSpacing.xl)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// UTILIDADES COMPARTIDAS — usadas también por RegisterScreen
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Colores personalizados para OutlinedTextField en todo el módulo auth.
 *
 * - Focus: borde negro (Ink) + cursor rojo (Pulse) → acento agresivo SNKRS
 * - Unfocused: borde gris sutil (Neutral200)
 * - Error: rojo Pulse
 */
@Composable
fun holoTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = HoloColors.Ink,
    unfocusedBorderColor = HoloColors.BorderSubtle,
    cursorColor = HoloColors.Pulse,                    // Cursor ROJO — detalle SNKRS
    focusedLabelColor = HoloColors.Ink,
    unfocusedLabelColor = HoloColors.TextTertiary
)

/**
 * Label reutilizable para campos de formulario.
 * Estilo SNKRS: uppercase, letter-spacing alto, gris medio.
 *
 * @param text Texto del label (se muestra en uppercase por el estilo LabelMedium).
 */
@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        style = HoloType.LabelMedium,
        color = HoloColors.TextTertiary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = HoloSpacing.xs)
    )
}