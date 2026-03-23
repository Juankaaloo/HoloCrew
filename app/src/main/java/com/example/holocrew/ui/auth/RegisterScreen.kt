/**
 * RegisterScreen.kt
 *
 * Pantalla de registro de nuevo usuario en HoloCrew.
 * Se accede desde el botón "Crear cuenta" de LoginScreen.
 * Diseño consistente con LoginScreen:
 *  - Logo de la marca
 *  - Campos: nombre, email, contraseña, confirmar contraseña
 *  - Botón "Crear cuenta" (navega al Home)
 *  - Enlace "Ya tengo cuenta" (vuelve al Login)
 *
 * Al ser solo front-end, no hay registro real.
 */
package com.example.holocrew.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {

    // ── Estados de los campos ──
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            // ═══ BOTÓN VOLVER ═══
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.Black)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ═══ LOGO ═══
            Image(
                painter = painterResource(id = R.drawable.logito),
                contentDescription = "Logo HoloCrew",
                modifier = Modifier.height(40.dp).width(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(32.dp))

            // ═══ TÍTULO ═══
            Text(
                text = "Crear cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Únete a la comunidad HoloCrew",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Spacer(Modifier.height(28.dp))

            // ═══ CAMPO NOMBRE ═══
            FieldLabel("NOMBRE COMPLETO")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Tu nombre", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // ═══ CAMPO EMAIL ═══
            FieldLabel("EMAIL")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("tu@email.com", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            // ═══ CAMPO CONTRASEÑA ═══
            FieldLabel("CONTRASEÑA")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Mínimo 8 caracteres", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = Color(0xFF9E9E9E)
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // ═══ CAMPO CONFIRMAR CONTRASEÑA ═══
            FieldLabel("CONFIRMAR CONTRASEÑA")
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Repite tu contraseña", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors(),
                singleLine = true,
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            if (confirmVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            if (confirmVisible) "Ocultar" else "Mostrar",
                            tint = Color(0xFF9E9E9E)
                        )
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            // ═══ BOTÓN CREAR CUENTA ═══
            Button(
                onClick = {
                    // Sin registro real — navegar directamente al Home
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(20.dp))

            // ═══ YA TENGO CUENTA ═══
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("¿Ya tienes cuenta? ", fontSize = 14.sp, color = Color(0xFF9E9E9E))
                Text(
                    "Iniciar sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.clickable { navController.navigateUp() }
                )
            }

            Spacer(Modifier.height(24.dp))

            // ═══ FOOTER ═══
            Text(
                text = "Al crear tu cuenta, aceptas los Términos de Servicio\ny la Política de Privacidad de HoloCrew",
                fontSize = 11.sp,
                color = Color(0xFFBDBDBD),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES REUTILIZABLES
// ══════════════════════════════════════════════════════════════════════════════

/** Label de campo de formulario */
@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF9E9E9E),
        letterSpacing = 1.sp,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    )
}

/** Colores reutilizables para OutlinedTextField */
@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Black,
    unfocusedBorderColor = Color(0xFFE0E0E0),
    cursorColor = Color.Black
)