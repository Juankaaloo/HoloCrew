package com.example.holocrew.ui.auth

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.holocrew.data.CartManager
import com.example.holocrew.data.FavoritesManager
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.LoginRequest
import com.example.holocrew.data.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            Image(
                painter = painterResource(id = R.drawable.logito),
                contentDescription = "Logo HoloCrew",
                modifier = Modifier.height(50.dp).width(140.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(12.dp))
            Text("Streetwear exclusivo", fontSize = 14.sp, color = Color(0xFF9E9E9E), letterSpacing = 1.sp)
            Spacer(Modifier.height(48.dp))

            Text("Iniciar sesión", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.fillMaxWidth())
            Text("Accede a tu cuenta HoloCrew", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

            Spacer(Modifier.height(32.dp))

            Text("EMAIL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("tu@email.com", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color(0xFFE0E0E0)),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(20.dp))

            Text("CONTRASEÑA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Tu contraseña", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color(0xFFE0E0E0)),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = Color(0xFF9E9E9E))
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        try {
                            val response = RetrofitClient.api.login(LoginRequest(email.trim(), password))
                            if (response.isSuccessful && response.body()?.success == true) {
                                val data = response.body()!!.data!!
                                TokenManager.saveToken(
                                    context,
                                    token = data.token,
                                    userId = data.user.id,
                                    userName = "${data.user.first_name} ${data.user.last_name}",
                                    userEmail = data.user.email,
                                    userTier = data.user.membership_tier
                                )
                                // Cargar carrito y favoritos tras login
                                CartManager.loadCart(context)
                                FavoritesManager.loadFavorites(context)
                                navController.navigate("home") { popUpTo("login") { inclusive = true } }
                            } else {
                                Toast.makeText(context, response.body()?.message ?: "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error de conexión. ¿Está el servidor encendido?", Toast.LENGTH_LONG).show()
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "Cargando..." else "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f), color = Color(0xFFE0E0E0))
                Text("o", fontSize = 13.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(horizontal = 16.dp))
                Divider(Modifier.weight(1f), color = Color(0xFFE0E0E0))
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("register") { launchSingleTop = true } },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(Modifier.weight(1f))

            Text(
                "Al continuar, aceptas los Términos de Servicio\ny la Política de Privacidad de HoloCrew",
                fontSize = 11.sp, color = Color(0xFFBDBDBD), textAlign = TextAlign.Center,
                lineHeight = 16.sp, modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}