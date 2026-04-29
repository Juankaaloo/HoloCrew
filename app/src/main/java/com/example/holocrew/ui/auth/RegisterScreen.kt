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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.holocrew.data.network.RegisterRequest
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloMotion
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

/**
 * RegisterScreen — Pantalla de registro de nueva cuenta.
 *
 * Accesible desde LoginScreen. Diseño coherente con login:
 *  - Fondo blanco limpio con logo visible
 *  - Flecha atrás negra arriba a la izquierda
 *  - Tagline roja debajo del logo
 *  - Línea roja separadora entre branding y formulario
 *  - 5 campos con cursor rojo al hacer focus
 *  - Botón primario negro con press-scale
 *
 * Flujo:
 *  1. Usuario rellena nombre, apellidos, email, contraseña, confirmar
 *  2. Validación local: campos obligatorios, contraseñas coinciden, mín 8 chars
 *  3. POST /api/auth/register
 *  4. Si ok → guarda JWT, carga cart/favs, navega a Home
 *  5. Si ko → Toast con mensaje del backend
 *
 * @param navController Controlador de navegación para volver a Login o ir a Home.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {

    // ── Estado del formulario ─────────────────────────────────────────────────
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── Press-scale para el botón principal ────────────────────────────────────
    val registerInteraction = remember { MutableInteractionSource() }
    val isPressed by registerInteraction.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = HoloMotion.smoothSpring(),
        label = "registerBtnScale"
    )

    // ── Layout principal ──────────────────────────────────────────────────────
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

            Spacer(Modifier.height(HoloSpacing.lg))

            // ══════════════════════════════════════════════════════════════════
            // BOTÓN VOLVER — arriba a la izquierda
            // ══════════════════════════════════════════════════════════════════
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al login",
                        tint = HoloColors.Ink
                    )
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // ══════════════════════════════════════════════════════════════════
            // BRANDING — Logo + tagline roja
            // Mismo estilo que LoginScreen para coherencia visual.
            // ══════════════════════════════════════════════════════════════════
            Image(
                painter = painterResource(id = R.drawable.logito),
                contentDescription = "Logo HoloCrew",
                modifier = Modifier
                    .height(80.dp)
                    .width(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(HoloSpacing.xs))

            // Tagline roja — variante para registro
            Text(
                text = "ÚNETE AL CREW",
                style = HoloType.LabelSmall,
                color = HoloColors.Pulse
            )

            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Línea roja separadora (consistente con LoginScreen) ────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.BorderDefault)
                    .background(HoloColors.Pulse)
            )

            Spacer(Modifier.height(HoloSpacing.xl))

            // ══════════════════════════════════════════════════════════════════
            // FORMULARIO DE REGISTRO
            // ══════════════════════════════════════════════════════════════════

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text = "Crear cuenta",
                style = HoloType.HeadlineLarge,
                color = HoloColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(HoloSpacing.xxs))

            Text(
                text = "Únete a la comunidad HoloCrew",
                style = HoloType.BodyMedium,
                color = HoloColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Campo NOMBRE ──────────────────────────────────────────────────
            FieldLabel("NOMBRE")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text("Tu nombre", style = HoloType.BodyMedium, color = HoloColors.TextDisabled)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(),
                singleLine = true
            )

            Spacer(Modifier.height(HoloSpacing.md))

            // ── Campo APELLIDOS ───────────────────────────────────────────────
            FieldLabel("APELLIDOS")
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = {
                    Text("Tus apellidos", style = HoloType.BodyMedium, color = HoloColors.TextDisabled)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(),
                singleLine = true
            )

            Spacer(Modifier.height(HoloSpacing.md))

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

            Spacer(Modifier.height(HoloSpacing.md))

            // ── Campo CONTRASEÑA ──────────────────────────────────────────────
            FieldLabel("CONTRASEÑA")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text("Mínimo 8 caracteres", style = HoloType.BodyMedium, color = HoloColors.TextDisabled)
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

            Spacer(Modifier.height(HoloSpacing.md))

            // ── Campo CONFIRMAR CONTRASEÑA ────────────────────────────────────
            FieldLabel("CONFIRMAR CONTRASEÑA")
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {
                    Text("Repite tu contraseña", style = HoloType.BodyMedium, color = HoloColors.TextDisabled)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(),
                singleLine = true,
                visualTransformation = if (confirmVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            imageVector = if (confirmVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmVisible)
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
            // BOTÓN PRINCIPAL — Crear cuenta
            // Negro con press-scale. Misma estética que LoginScreen.
            // ══════════════════════════════════════════════════════════════════
            Button(
                onClick = {
                    // Validaciones locales antes de llamar al backend
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() ->
                            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()

                        password != confirmPassword ->
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()

                        password.length < 8 ->
                            Toast.makeText(context, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()

                        else -> scope.launch {
                            isLoading = true
                            try {
                                val response = RetrofitClient.api.register(
                                    RegisterRequest(
                                        name.trim(),
                                        lastName.trim(),
                                        email.trim(),
                                        password
                                    )
                                )

                                if (response.isSuccessful && response.body()?.success == true) {
                                    val data = response.body()!!.data!!

                                    // Guardar JWT y datos del usuario en DataStore
                                    TokenManager.saveToken(
                                        context,
                                        token = data.token,
                                        userId = data.user.id,
                                        userName = "${data.user.first_name} ${data.user.last_name}",
                                        userEmail = data.user.email,
                                        userTier = data.user.membership_tier
                                    )

                                    // Pre-cargar datos del carrito y favoritos
                                    CartManager.loadCart(context)
                                    FavoritesManager.loadFavorites(context)

                                    // Navegar a Home limpiando login del backstack
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        response.body()?.message ?: "Error al registrarse",
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
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HoloSpacing.ButtonHeight)
                    .scale(buttonScale),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HoloColors.Ink,
                    disabledContainerColor = HoloColors.Neutral600
                ),
                interactionSource = registerInteraction,
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "CARGANDO..." else "CREAR CUENTA",
                    style = HoloType.LabelLarge,
                    color = HoloColors.Paper
                )
            }

            Spacer(Modifier.height(HoloSpacing.lg))

            // ── Link "¿Ya tienes cuenta?" ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    style = HoloType.BodyMedium,
                    color = HoloColors.TextSecondary
                )
                Text(
                    text = "Iniciar sesión",
                    style = HoloType.TitleMedium,
                    color = HoloColors.TextPrimary,
                    modifier = Modifier.clickable { navController.navigateUp() }
                )
            }

            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Footer legal ──────────────────────────────────────────────────
            Text(
                text = "Al crear tu cuenta, aceptas los Términos de Servicio\ny la Política de Privacidad de HoloCrew",
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