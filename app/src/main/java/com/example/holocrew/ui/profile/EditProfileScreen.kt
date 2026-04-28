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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.data.network.UpdateProfileRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

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

    val initials = remember(firstName, lastName) {
        listOfNotNull(firstName.firstOrNull()?.uppercase(), lastName.firstOrNull()?.uppercase()).joinToString("").ifEmpty { "HC" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (!isLoading) {
                        IconButton(onClick = {
                            scope.launch {
                                isSaving = true
                                val token = TokenManager.getTokenOnce(context) ?: return@launch
                                try {
                                    val response = RetrofitClient.api.updateMe(
                                        "Bearer $token",
                                        UpdateProfileRequest(firstName.trim(), lastName.trim(), phone.trim().ifEmpty { null }, city.trim().ifEmpty { null }, username.trim().ifEmpty { null })
                                    )
                                    if (response.isSuccessful) {
                                        TokenManager.saveToken(context, token, "", "${firstName.trim()} ${lastName.trim()}", email, "")
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
                        }) {
                            Icon(Icons.Filled.Check, "Guardar", tint = Color(0xFFD4AF37))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Cargando...", fontSize = 16.sp, color = Color(0xFF9E9E9E))
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color.Black, Color(0xFF1A1A1A))))
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                                .background(Color(0xFF2D2D2D))
                                .border(2.dp, Color(0xFFD4AF37), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Editar perfil", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(email, fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    ProfileTextField("NOMBRE", firstName) { firstName = it }
                    Spacer(Modifier.height(20.dp))
                    ProfileTextField("APELLIDOS", lastName) { lastName = it }
                    Spacer(Modifier.height(20.dp))
                    ProfileTextField("USUARIO", username, placeholder = "@usuario") { username = it }
                    Spacer(Modifier.height(20.dp))
                    ProfileTextField("TELÉFONO", phone, placeholder = "+34 600 000 000") { phone = it }
                    Spacer(Modifier.height(20.dp))
                    ProfileTextField("CIUDAD", city, placeholder = "Tu ciudad") { city = it }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                val token = TokenManager.getTokenOnce(context) ?: return@launch
                                try {
                                    val response = RetrofitClient.api.updateMe(
                                        "Bearer $token",
                                        UpdateProfileRequest(firstName.trim(), lastName.trim(), phone.trim().ifEmpty { null }, city.trim().ifEmpty { null }, username.trim().ifEmpty { null })
                                    )
                                    if (response.isSuccessful) {
                                        TokenManager.saveToken(context, token, "", "${firstName.trim()} ${lastName.trim()}", email, "")
                                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                        navController.navigateUp()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                                }
                                isSaving = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        enabled = !isSaving
                    ) {
                        Text(if (isSaving) "Guardando..." else "Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("El email no se puede modificar por seguridad", fontSize = 13.sp, color = Color(0xFF9E9E9E))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, placeholder: String = "", onValueChange: (String) -> Unit) {
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = if (placeholder.isNotEmpty()) {{ Text(placeholder, color = Color(0xFFBDBDBD)) }} else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            cursorColor = Color.Black
        ),
        singleLine = true
    )
}