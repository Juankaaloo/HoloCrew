package com.example.holocrew.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
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
import com.example.holocrew.data.network.AddAddressRequest
import com.example.holocrew.data.network.AddressDto
import com.example.holocrew.data.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var addresses by remember { mutableStateOf<List<AddressDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddSheet by remember { mutableStateOf(false) }

    fun loadAddresses() {
        scope.launch {
            val token = TokenManager.getTokenOnce(context) ?: return@launch
            try {
                val response = RetrofitClient.api.getAddresses("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    addresses = response.body()!!.data ?: emptyList()
                }
            } catch (e: Exception) { e.printStackTrace() }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadAddresses() }

    if (showAddSheet) {
        AddAddressBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { request ->
                scope.launch {
                    val token = TokenManager.getTokenOnce(context) ?: return@launch
                    try {
                        val response = RetrofitClient.api.addAddress("Bearer $token", request)
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Dirección añadida", Toast.LENGTH_SHORT).show()
                            showAddSheet = false
                            isLoading = true
                            loadAddresses()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }, containerColor = Color.Black, contentColor = Color.White, shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Filled.Add, "Añadir dirección")
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(bottom = 80.dp)) {
            item {
                Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Black, Color(0xFF1A1A1A)))).padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Column {
                        Text("ENVÍO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 2.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Mis direcciones", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (addresses.isEmpty() && !isLoading) "Añade tu primera dirección" else "${addresses.size} dirección${if (addresses.size != 1) "es" else ""}",
                            fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            if (isLoading) {
                item { Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text("Cargando...", fontSize = 16.sp, color = Color(0xFF9E9E9E)) } }
            } else if (addresses.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(24.dp))
                        Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(80.dp))
                        Spacer(Modifier.height(20.dp))
                        Text("No tienes direcciones guardadas", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF666666))
                        Text("Añade una para agilizar tus compras", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 8.dp))
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { showAddSheet = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black), shape = RoundedCornerShape(50), modifier = Modifier.height(48.dp)) {
                            Text("AÑADIR DIRECCIÓN", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(16.dp)) }
                items(addresses) { address ->
                    AddressCard(address = address, onDelete = {
                        scope.launch {
                            val token = TokenManager.getTokenOnce(context) ?: return@launch
                            try {
                                RetrofitClient.api.deleteAddress("Bearer $token", address.id)
                                Toast.makeText(context, "Dirección eliminada", Toast.LENGTH_SHORT).show()
                                isLoading = true
                                loadAddresses()
                            } catch (e: Exception) { Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show() }
                        }
                    })
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun AddressCard(address: AddressDto, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.LocationOn, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(address.recipient_name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                if (address.is_default) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0xFF4CAF50).copy(alpha = 0.1f)).padding(horizontal = 12.dp, vertical = 5.dp)) {
                        Text("Principal", fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Column(modifier = Modifier.padding(start = 52.dp)) {
                Text(address.street_address, fontSize = 14.sp, color = Color(0xFF444444))
                address.apartment?.let { if (it.isNotEmpty()) Text(it, fontSize = 14.sp, color = Color(0xFF444444)) }
                Text("${address.postal_code} ${address.city}", fontSize = 14.sp, color = Color(0xFF444444))
                address.phone?.let { if (it.isNotEmpty()) Text(it, fontSize = 13.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp)) }
            }
            Spacer(Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)))
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDelete) { Text("Eliminar", color = Color(0xFFE53935), fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressBottomSheet(onDismiss: () -> Unit, onSave: (AddAddressRequest) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Text("Nueva dirección", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Rellena los datos de envío", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(24.dp))
            AddressField("DESTINATARIO", name) { name = it }
            Spacer(Modifier.height(16.dp))
            AddressField("TELÉFONO", phone) { phone = it }
            Spacer(Modifier.height(16.dp))
            AddressField("DIRECCIÓN", street) { street = it }
            Spacer(Modifier.height(16.dp))
            AddressField("PISO / PUERTA (OPCIONAL)", apartment) { apartment = it }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) { AddressField("CIUDAD", city) { city = it } }
                Column(Modifier.weight(1f)) { AddressField("C.P.", postalCode) { postalCode = it } }
            }
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    if (name.isNotBlank() && street.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank()) {
                        onSave(AddAddressRequest(name.trim(), phone.trim().ifEmpty { null }, street.trim(), apartment.trim().ifEmpty { null }, city.trim(), postal_code = postalCode.trim(), is_default = true))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = name.isNotBlank() && street.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank()
            ) { Text("GUARDAR DIRECCIÓN", fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun AddressField(label: String, value: String, onValueChange: (String) -> Unit) {
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color(0xFFE0E0E0), cursorColor = Color.Black),
        singleLine = true
    )
}