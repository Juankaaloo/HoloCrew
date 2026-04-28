package com.example.holocrew.ui.auth

import android.widget.Toast
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
import com.example.holocrew.data.network.RegisterRequest
import com.example.holocrew.data.network.RetrofitClient
import kotlinx.coroutines.launch

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

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.Black)
                }
            }

            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.logito),
                contentDescription = "Logo",
                modifier = Modifier.height(40.dp).width(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(32.dp))

            Text(
                "Crear cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Únete a la comunidad HoloCrew",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Spacer(Modifier.height(28.dp))

            // ── NOMBRE ──
            FieldLabel("NOMBRE")
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                placeholder = { Text("Tu nombre", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // ── APELLIDOS ──
            FieldLabel("APELLIDOS")
            OutlinedTextField(
                value = lastName, onValueChange = { lastName = it },
                placeholder = { Text("Tus apellidos", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // ── EMAIL ──
            FieldLabel("EMAIL")
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("tu@email.com", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            // ── CONTRASEÑA ──
            FieldLabel("CONTRASEÑA")
            OutlinedTextField(
                value = password, onValueChange = { password = it },
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
                            null, tint = Color(0xFF9E9E9E)
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // ── CONFIRMAR CONTRASEÑA ──
            FieldLabel("CONFIRMAR CONTRASEÑA")
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
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
                            null, tint = Color(0xFF9E9E9E)
                        )
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            // ── BOTÓN CREAR CUENTA ──
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
                            try {
                                val response = RetrofitClient.api.register(
                                    RegisterRequest(name.trim(), lastName.trim(), email.trim(), password)
                                )
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
                                    CartManager.loadCart(context)
                                    FavoritesManager.loadFavorites(context)
                                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                                } else {
                                    Toast.makeText(context, response.body()?.message ?: "Error al registrarse", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error de conexión. ¿Está el servidor encendido?", Toast.LENGTH_LONG).show()
                            }
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "Cargando..." else "Crear cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
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

            Text(
                "Al crear tu cuenta, aceptas los Términos de Servicio\ny la Política de Privacidad de HoloCrew",
                fontSize = 11.sp,
                color = Color(0xFFBDBDBD),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

// ── Funciones auxiliares ──────────────────────────────────────────────────────

@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF9E9E9E),
        letterSpacing = 1.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Black,
    unfocusedBorderColor = Color(0xFFE0E0E0),
    cursorColor = Color.Black
)