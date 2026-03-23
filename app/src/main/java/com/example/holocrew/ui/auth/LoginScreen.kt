/**
 * LoginScreen.kt
 *
 * Pantalla de inicio de sesión de HoloCrew.
 * Es la primera pantalla que ve el usuario al abrir la app.
 * Diseño premium con:
 *  - Logo de la marca en la parte superior
 *  - Campos de email y contraseña
 *  - Botón "Iniciar sesión" (navega al Home)
 *  - Enlace "¿Olvidaste tu contraseña?"
 *  - Botón "Crear cuenta" (navega a RegisterScreen)
 *
 * Al ser solo front-end, no hay autenticación real.
 * Cualquier dato introducido navega directamente al Home.
 */
package com.example.holocrew.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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

@Composable
fun LoginScreen(navController: NavController) {

    // ── Estados de los campos ──
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(80.dp))

            // ═══ LOGO ═══
            Image(
                painter = painterResource(id = R.drawable.logito),
                contentDescription = "Logo HoloCrew",
                modifier = Modifier.height(50.dp).width(140.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Streetwear exclusivo",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(48.dp))

            // ═══ TÍTULO ═══
            Text(
                text = "Iniciar sesión",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Accede a tu cuenta HoloCrew",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Spacer(Modifier.height(32.dp))

            // ═══ CAMPO EMAIL ═══
            Text(
                text = "EMAIL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9E9E9E),
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("tu@email.com", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = Color.Black
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(20.dp))

            // ═══ CAMPO CONTRASEÑA ═══
            Text(
                text = "CONTRASEÑA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9E9E9E),
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Tu contraseña", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = Color.Black
                ),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = Color(0xFF9E9E9E)
                        )
                    }
                }
            )

            // ¿Olvidaste tu contraseña?
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 13.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clickable { /* TODO */ },
                textAlign = TextAlign.End
            )

            Spacer(Modifier.height(32.dp))

            // ═══ BOTÓN INICIAR SESIÓN ═══
            Button(
                onClick = {
                    // Sin autenticación real — navegar directamente al Home
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            // ═══ SEPARADOR ═══
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(Modifier.weight(1f), color = Color(0xFFE0E0E0))
                Text(
                    "o",
                    fontSize = 13.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Divider(Modifier.weight(1f), color = Color(0xFFE0E0E0))
            }

            Spacer(Modifier.height(16.dp))

            // ═══ BOTÓN CREAR CUENTA ═══
            OutlinedButton(
                onClick = {
                    navController.navigate("register") { launchSingleTop = true }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFFE0E0E0), Color(0xFFE0E0E0)))
                )
            ) {
                Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(Modifier.weight(1f))

            // ═══ FOOTER ═══
            Text(
                text = "Al continuar, aceptas los Términos de Servicio\ny la Política de Privacidad de HoloCrew",
                fontSize = 11.sp,
                color = Color(0xFFBDBDBD),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}