package com.example.holocrew.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.AddPaymentMethodRequest
import com.example.holocrew.data.network.PaymentMethodDto
import com.example.holocrew.data.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var methods by remember { mutableStateOf<List<PaymentMethodDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddSheet by remember { mutableStateOf(false) }

    fun loadMethods() {
        scope.launch {
            val token = TokenManager.getTokenOnce(context) ?: return@launch
            try {
                val response = RetrofitClient.api.getPaymentMethods("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    methods = response.body()!!.data ?: emptyList()
                }
            } catch (e: Exception) { e.printStackTrace() }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadMethods() }

    if (showAddSheet) {
        AddCardBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { request ->
                scope.launch {
                    val token = TokenManager.getTokenOnce(context) ?: return@launch
                    try {
                        val response = RetrofitClient.api.addPaymentMethod("Bearer $token", request)
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Tarjeta añadida", Toast.LENGTH_SHORT).show()
                            showAddSheet = false
                            isLoading = true
                            loadMethods()
                        } else {
                            Toast.makeText(context, "Error al añadir", Toast.LENGTH_SHORT).show()
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
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Añadir tarjeta")
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header negro SNKRS
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color.Black, Color(0xFF1A1A1A))))
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text("PAGOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 2.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Métodos de pago", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (methods.isEmpty() && !isLoading) "Añade tu primera tarjeta"
                            else "${methods.size} método${if (methods.size != 1) "s" else ""} guardado${if (methods.size != 1) "s" else ""}",
                            fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Cargando...", fontSize = 16.sp, color = Color(0xFF9E9E9E))
                    }
                }
            } else if (methods.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(24.dp))
                        Icon(Icons.Outlined.CreditCard, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(80.dp))
                        Spacer(Modifier.height(20.dp))
                        Text("No tienes tarjetas guardadas", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF666666))
                        Text("Añade una para pagar más rápido", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 8.dp))
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { showAddSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("AÑADIR TARJETA", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(16.dp)) }
                items(methods) { method ->
                    PaymentCard(
                        method = method,
                        onDelete = {
                            scope.launch {
                                val token = TokenManager.getTokenOnce(context) ?: return@launch
                                try {
                                    RetrofitClient.api.deletePaymentMethod("Bearer $token", method.id)
                                    Toast.makeText(context, "Tarjeta eliminada", Toast.LENGTH_SHORT).show()
                                    isLoading = true
                                    loadMethods()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun PaymentCard(method: PaymentMethodDto, onDelete: () -> Unit) {
    val brandColor = when (method.card_brand?.lowercase()) {
        "visa" -> Color(0xFF1A1F71)
        "mastercard" -> Color(0xFFEB001B)
        "amex" -> Color(0xFF007BC1)
        else -> Color(0xFF1A1A1A)
    }
    val brandIcon = when (method.card_brand?.lowercase()) {
        "visa" -> "VISA"
        "mastercard" -> "MC"
        "amex" -> "AMEX"
        else -> "CARD"
    }
    val typeLabel = when (method.payment_type) {
        "credit_card" -> "Crédito"
        "debit_card" -> "Débito"
        "paypal" -> "PayPal"
        "apple_pay" -> "Apple Pay"
        "google_pay" -> "Google Pay"
        else -> method.payment_type
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Logo marca
                Box(
                    modifier = Modifier.width(56.dp).height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brandColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(brandIcon, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                }

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(typeLabel, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        if (method.is_default) {
                            Spacer(Modifier.width(8.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFD4AF37).copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                Text("Principal", fontSize = 10.sp, color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (method.card_last_four != null) {
                        Text("•••• •••• •••• ${method.card_last_four}", fontSize = 14.sp, color = Color(0xFF666666), modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }

            if (method.card_expiry_month != null && method.card_expiry_year != null) {
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("EXPIRA", fontSize = 10.sp, color = Color(0xFF9E9E9E), letterSpacing = 1.sp)
                        Text("${String.format("%02d", method.card_expiry_month)}/${method.card_expiry_year}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF444444))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)))
            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDelete) {
                    Text("Eliminar", color = Color(0xFFE53935), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardBottomSheet(onDismiss: () -> Unit, onSave: (AddPaymentMethodRequest) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryMonth by remember { mutableStateOf("") }
    var expiryYear by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("credit_card") }
    var isDefault by remember { mutableStateOf(false) }

    val cardBrand = remember(cardNumber) {
        when {
            cardNumber.startsWith("4") -> "Visa"
            cardNumber.startsWith("5") || cardNumber.startsWith("2") -> "Mastercard"
            cardNumber.startsWith("3") -> "Amex"
            else -> null
        }
    }

    val isFormValid = cardNumber.length >= 16 && cardHolder.isNotBlank() && expiryMonth.isNotBlank() && expiryYear.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
        ) {
            Text("Nueva tarjeta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Tus datos están protegidos", fontSize = 14.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))

            Spacer(Modifier.height(24.dp))

            // Preview de la tarjeta
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))))
                    .padding(20.dp)
            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("HOLO CREW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), letterSpacing = 1.sp)
                        Text(cardBrand ?: "", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text(
                        if (cardNumber.length >= 4) "•••• •••• •••• ${cardNumber.takeLast(4)}" else "•••• •••• •••• ••••",
                        fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White, letterSpacing = 2.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Tipo de tarjeta
            Text("TIPO DE TARJETA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("credit_card" to "Crédito", "debit_card" to "Débito").forEach { (type, label) ->
                    val isSelected = selectedType == type
                    Box(
                        modifier = Modifier.weight(1f).height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.Black else Color(0xFFF5F5F5))
                            .clickable { selectedType = type },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color(0xFF666666))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Número de tarjeta
            Text("NÚMERO DE TARJETA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it },
                placeholder = { Text("1234 5678 9012 3456", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color(0xFFE0E0E0), cursorColor = Color.Black),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                trailingIcon = { Icon(Icons.Outlined.CreditCard, null, tint = Color(0xFF9E9E9E)) }
            )

            Spacer(Modifier.height(16.dp))

            // Titular
            Text("TITULAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = cardHolder,
                onValueChange = { cardHolder = it.uppercase() },
                placeholder = { Text("NOMBRE APELLIDO", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color(0xFFE0E0E0), cursorColor = Color.Black),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Caducidad
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("MES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = expiryMonth,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) expiryMonth = it },
                        placeholder = { Text("MM", color = Color(0xFFBDBDBD)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color(0xFFE0E0E0), cursorColor = Color.Black),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text("AÑO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E), letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = expiryYear,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) expiryYear = it },
                        placeholder = { Text("AAAA", color = Color(0xFFBDBDBD)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color(0xFFE0E0E0), cursorColor = Color.Black),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Switch principal
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Tarjeta principal", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Text("Usar por defecto en tus compras", fontSize = 13.sp, color = Color(0xFF9E9E9E))
                }
                Switch(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color.Black)
                )
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    if (isFormValid) {
                        onSave(AddPaymentMethodRequest(
                            payment_type = selectedType,
                            card_last_four = cardNumber.takeLast(4),
                            card_brand = cardBrand,
                            card_expiry_month = expiryMonth.toIntOrNull(),
                            card_expiry_year = expiryYear.toIntOrNull(),
                            is_default = isDefault
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = if (isFormValid) Color.Black else Color(0xFFE0E0E0)),
                enabled = isFormValid
            ) {
                Text("AÑADIR TARJETA", fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                    color = if (isFormValid) Color.White else Color(0xFF9E9E9E))
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Datos encriptados y seguros", fontSize = 12.sp, color = Color(0xFF9E9E9E))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}