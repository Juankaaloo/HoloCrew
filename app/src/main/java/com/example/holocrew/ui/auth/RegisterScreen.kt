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
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloMotion
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import kotlinx.coroutines.launch

/**
 * RegisterScreen — Registro con Supabase Auth.
 *
 * Flujo:
 *  1. Usuario rellena nombre, apellidos, email, contraseña, confirmar
 *  2. TokenManager.signUp() → Supabase Auth crea usuario + trigger crea perfil
 *  3. Si ok → carga cart/favs, navega a Home
 *  4. Si ko → Toast con error
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {

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

    val registerInteraction = remember { MutableInteractionSource() }
    val isPressed by registerInteraction.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = HoloMotion.smoothSpring(),
        label = "registerBtnScale"
    )

    Box(modifier = Modifier.fillMaxSize().background(HoloColors.Paper)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = HoloSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(HoloSpacing.lg))

            // ── Botón volver ──────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Ink)
                }
            }
            Spacer(Modifier.height(HoloSpacing.md))

            // ── Branding ──────────────────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.logito), contentDescription = "Logo",
                modifier = Modifier.height(80.dp).width(200.dp), contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(HoloSpacing.xs))
            Text("ÚNETE AL CREW", style = HoloType.LabelSmall, color = HoloColors.Pulse)
            Spacer(Modifier.height(HoloSpacing.xl))

            Box(modifier = Modifier.fillMaxWidth().height(HoloSpacing.BorderDefault).background(HoloColors.Pulse))
            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Título ────────────────────────────────────────────────────────
            Text("Crear cuenta", style = HoloType.HeadlineLarge, color = HoloColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text("Únete a la comunidad HoloCrew", style = HoloType.BodyMedium, color = HoloColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Campos ────────────────────────────────────────────────────────
            FieldLabel("NOMBRE")
            OutlinedTextField(value = name, onValueChange = { name = it },
                placeholder = { Text("Tu nombre", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), singleLine = true)
            Spacer(Modifier.height(HoloSpacing.md))

            FieldLabel("APELLIDOS")
            OutlinedTextField(value = lastName, onValueChange = { lastName = it },
                placeholder = { Text("Tus apellidos", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), singleLine = true)
            Spacer(Modifier.height(HoloSpacing.md))

            FieldLabel("EMAIL")
            OutlinedTextField(value = email, onValueChange = { email = it },
                placeholder = { Text("tu@email.com", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(HoloSpacing.md))

            FieldLabel("CONTRASEÑA")
            OutlinedTextField(value = password, onValueChange = { password = it },
                placeholder = { Text("Mínimo 8 caracteres", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            if (passwordVisible) "Ocultar" else "Mostrar", tint = HoloColors.TextTertiary)
                    }
                })
            Spacer(Modifier.height(HoloSpacing.md))

            FieldLabel("CONFIRMAR CONTRASEÑA")
            OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it },
                placeholder = { Text("Repite tu contraseña", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.InputHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), singleLine = true,
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(if (confirmVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            if (confirmVisible) "Ocultar" else "Mostrar", tint = HoloColors.TextTertiary)
                    }
                })
            Spacer(Modifier.height(HoloSpacing.xxl))

            // ── Botón CREAR CUENTA (Supabase Auth) ────────────────────────────
            Button(
                onClick = {
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() ->
                            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        password != confirmPassword ->
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        password.length < 8 ->
                            Toast.makeText(context, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                        else -> scope.launch {
                            isLoading = true
                            // ✅ Registro directo con Supabase Auth
                            val result = TokenManager.signUp(email.trim(), password, name.trim(), lastName.trim())
                            result.onSuccess {
                                CartManager.loadCart(context)
                                FavoritesManager.loadFavorites(context)
                                navController.navigate("home") { popUpTo("login") { inclusive = true } }
                            }.onFailure { error ->
                                val message = when {
                                    error.message?.contains("already registered", ignoreCase = true) == true -> "Ya existe una cuenta con este email"
                                    error.message?.contains("password", ignoreCase = true) == true -> "La contraseña no cumple los requisitos"
                                    else -> "Error al registrarse: ${error.message}"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight).scale(buttonScale),
                shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink, disabledContainerColor = HoloColors.Neutral600),
                interactionSource = registerInteraction, enabled = !isLoading
            ) {
                Text(if (isLoading) "CARGANDO..." else "CREAR CUENTA", style = HoloType.LabelLarge, color = HoloColors.Paper)
            }

            Spacer(Modifier.height(HoloSpacing.lg))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("¿Ya tienes cuenta? ", style = HoloType.BodyMedium, color = HoloColors.TextSecondary)
                Text("Iniciar sesión", style = HoloType.TitleMedium, color = HoloColors.TextPrimary,
                    modifier = Modifier.clickable { navController.navigateUp() })
            }

            Spacer(Modifier.height(HoloSpacing.xl))

            Text("Al crear tu cuenta, aceptas los Términos de Servicio\ny la Política de Privacidad de HoloCrew",
                style = HoloType.BodySmall, color = HoloColors.TextDisabled, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = HoloSpacing.xl))
        }
    }
}