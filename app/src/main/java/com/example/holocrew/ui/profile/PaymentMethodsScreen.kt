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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.AddPaymentMethodRequest
import com.example.holocrew.data.network.PaymentMethodDto
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.auth.holoTextFieldColors
import kotlinx.coroutines.launch

/**
 * PaymentMethodsScreen — Gestión de métodos de pago del usuario.
 *
 * Diseño SNKRS:
 *  - Header negro con gradiente + label rojo "PAGOS"
 *  - Cards blancas con logo de marca (Visa/MC/Amex), tipo, últimos 4 dígitos
 *  - Badge "Principal" en rojo Pulse para la tarjeta por defecto
 *  - FAB negro para añadir nueva tarjeta (abre BottomSheet)
 *  - BottomSheet con preview de tarjeta + formulario completo
 *
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var methods by remember { mutableStateOf<List<PaymentMethodDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddSheet by remember { mutableStateOf(false) }

    // Función para cargar/recargar métodos de pago desde la API
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

    // ── BottomSheet para añadir nueva tarjeta ─────────────────────────────────
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = HoloColors.Ink,
                contentColor = HoloColors.Paper,
                shape = RoundedCornerShape(HoloSpacing.RadiusLg)
            ) {
                Icon(Icons.Filled.Add, "Añadir tarjeta")
            }
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.BottomNavHeight)
        ) {
            // ── Header negro con gradiente ────────────────────────────────────
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800)))
                        .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)
                ) {
                    Column {
                        Text("PAGOS", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("Métodos de pago", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text(
                            if (methods.isEmpty() && !isLoading) "Añade tu primera tarjeta"
                            else "${methods.size} método${if (methods.size != 1) "s" else ""} guardado${if (methods.size != 1) "s" else ""}",
                            style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted
                        )
                    }
                }
            }

            // ── Estado cargando ───────────────────────────────────────────────
            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Cargando...", style = HoloType.TitleLarge, color = HoloColors.TextTertiary)
                    }
                }
            }
            // ── Estado vacío ──────────────────────────────────────────────────
            else if (methods.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(HoloSpacing.xxxl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.CreditCard, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.huge))
                        Spacer(Modifier.height(HoloSpacing.lg))
                        Text("No tienes tarjetas guardadas", style = HoloType.TitleLarge, color = HoloColors.TextSecondary)
                        Text("Añade una para pagar más rápido", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xs))
                        Spacer(Modifier.height(HoloSpacing.xl))
                        Button(
                            onClick = { showAddSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                            shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                            modifier = Modifier.height(HoloSpacing.ButtonHeightSmall)
                        ) {
                            Text("AÑADIR TARJETA", style = HoloType.LabelLarge, color = HoloColors.Paper)
                        }
                    }
                }
            }
            // ── Lista de tarjetas ─────────────────────────────────────────────
            else {
                item { Spacer(Modifier.height(HoloSpacing.md)) }
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
                    Spacer(Modifier.height(HoloSpacing.sm))
                }
            }
        }
    }
}

/**
 * PaymentCard — Card individual de un método de pago guardado.
 * Logo de marca coloreado + tipo + últimos 4 dígitos + fecha expiración.
 */
@Composable
fun PaymentCard(method: PaymentMethodDto, onDelete: () -> Unit) {
    // Color del logo según la marca de la tarjeta
    val brandColor = when (method.card_brand?.lowercase()) {
        "visa" -> HoloColors.Info
        "mastercard" -> HoloColors.Pulse
        "amex" -> HoloColors.Info
        else -> HoloColors.Neutral800
    }
    val brandIcon = when (method.card_brand?.lowercase()) {
        "visa" -> "VISA"; "mastercard" -> "MC"; "amex" -> "AMEX"; else -> "CARD"
    }
    val typeLabel = when (method.payment_type) {
        "credit_card" -> "Crédito"; "debit_card" -> "Débito"; "paypal" -> "PayPal"
        "apple_pay" -> "Apple Pay"; "google_pay" -> "Google Pay"; else -> method.payment_type
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
        shape = RoundedCornerShape(HoloSpacing.RadiusXl),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(HoloSpacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Logo marca
                Box(
                    modifier = Modifier.width(56.dp).height(36.dp)
                        .clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(brandColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(brandIcon, style = HoloType.LabelSmall, color = HoloColors.Paper)
                }
                Spacer(Modifier.width(HoloSpacing.sm))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(typeLabel, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                        if (method.is_default) {
                            Spacer(Modifier.width(HoloSpacing.xs))
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusPill))
                                    .background(HoloColors.Pulse.copy(alpha = 0.15f))
                                    .padding(horizontal = HoloSpacing.xs, vertical = 3.dp)
                            ) {
                                Text("Principal", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                            }
                        }
                    }
                    if (method.card_last_four != null) {
                        Text("•••• •••• •••• ${method.card_last_four}", style = HoloType.MonoSmall, color = HoloColors.TextSecondary, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }

            if (method.card_expiry_month != null && method.card_expiry_year != null) {
                Spacer(Modifier.height(HoloSpacing.sm))
                Column {
                    Text("EXPIRA", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
                    Text("${String.format("%02d", method.card_expiry_month)}/${method.card_expiry_year}", style = HoloType.MonoSmall, color = HoloColors.Neutral600)
                }
            }

            Spacer(Modifier.height(HoloSpacing.sm))
            Divider(color = HoloColors.Neutral100)
            Spacer(Modifier.height(HoloSpacing.xs))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDelete) {
                    Text("Eliminar", style = HoloType.TitleSmall, color = HoloColors.Pulse)
                }
            }
        }
    }
}

/**
 * AddCardBottomSheet — BottomSheet para añadir una nueva tarjeta.
 * Preview de tarjeta en vivo + selector crédito/débito + campos + switch default.
 */
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

    // Detectar marca automáticamente por el primer dígito
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
        onDismissRequest = onDismiss, sheetState = sheetState,
        containerColor = HoloColors.Paper,
        shape = RoundedCornerShape(topStart = HoloSpacing.RadiusXl, topEnd = HoloSpacing.RadiusXl)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.lg).verticalScroll(rememberScrollState())
        ) {
            Text("Nueva tarjeta", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
            Text("Tus datos están protegidos", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xxs))

            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Preview de tarjeta en vivo ────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp)
                    .clip(RoundedCornerShape(HoloSpacing.RadiusLg))
                    .background(Brush.horizontalGradient(listOf(HoloColors.Neutral800, HoloColors.Neutral700)))
                    .padding(HoloSpacing.lg)
            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("HOLO CREW", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Text(cardBrand ?: "", style = HoloType.TitleSmall, color = HoloColors.TextOnDark)
                    }
                    Text(
                        if (cardNumber.length >= 4) "•••• •••• •••• ${cardNumber.takeLast(4)}" else "•••• •••• •••• ••••",
                        style = HoloType.MonoMedium, color = HoloColors.TextOnDark
                    )
                }
            }

            Spacer(Modifier.height(HoloSpacing.xl))

            // ── Selector crédito / débito ──────────────────────────────────────
            Text("TIPO DE TARJETA", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
            Spacer(Modifier.height(HoloSpacing.xs))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HoloSpacing.xs)) {
                listOf("credit_card" to "Crédito", "debit_card" to "Débito").forEach { (type, label) ->
                    val isSelected = selectedType == type
                    Box(
                        modifier = Modifier.weight(1f).height(HoloSpacing.ButtonHeightSmall)
                            .clip(RoundedCornerShape(HoloSpacing.RadiusMd))
                            .background(if (isSelected) HoloColors.Ink else HoloColors.Fog)
                            .clickable { selectedType = type },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, style = HoloType.TitleMedium, color = if (isSelected) HoloColors.Paper else HoloColors.TextSecondary)
                    }
                }
            }

            Spacer(Modifier.height(HoloSpacing.lg))

            // ── Número de tarjeta ─────────────────────────────────────────────
            Text("NÚMERO DE TARJETA", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
            Spacer(Modifier.height(HoloSpacing.xs))
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it },
                placeholder = { Text("1234 5678 9012 3456", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
                trailingIcon = { Icon(Icons.Outlined.CreditCard, null, tint = HoloColors.TextTertiary) }
            )

            Spacer(Modifier.height(HoloSpacing.md))

            // ── Titular ───────────────────────────────────────────────────────
            Text("TITULAR", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
            Spacer(Modifier.height(HoloSpacing.xs))
            OutlinedTextField(
                value = cardHolder, onValueChange = { cardHolder = it.uppercase() },
                placeholder = { Text("NOMBRE APELLIDO", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                colors = holoTextFieldColors(), singleLine = true
            )

            Spacer(Modifier.height(HoloSpacing.md))

            // ── Caducidad (mes + año) ─────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)) {
                Column(Modifier.weight(1f)) {
                    Text("MES", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
                    Spacer(Modifier.height(HoloSpacing.xs))
                    OutlinedTextField(
                        value = expiryMonth,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) expiryMonth = it },
                        placeholder = { Text("MM", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                        colors = holoTextFieldColors(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text("AÑO", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
                    Spacer(Modifier.height(HoloSpacing.xs))
                    OutlinedTextField(
                        value = expiryYear,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) expiryYear = it },
                        placeholder = { Text("AAAA", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd),
                        colors = holoTextFieldColors(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // ── Switch "tarjeta principal" ────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Tarjeta principal", style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                    Text("Usar por defecto en tus compras", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
                }
                Switch(
                    checked = isDefault, onCheckedChange = { isDefault = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = HoloColors.Paper, checkedTrackColor = HoloColors.Ink)
                )
            }

            Spacer(Modifier.height(HoloSpacing.xxl))

            // ── Botón confirmar ───────────────────────────────────────────────
            Button(
                onClick = {
                    if (isFormValid) {
                        onSave(AddPaymentMethodRequest(
                            payment_type = selectedType, card_last_four = cardNumber.takeLast(4),
                            card_brand = cardBrand, card_expiry_month = expiryMonth.toIntOrNull(),
                            card_expiry_year = expiryYear.toIntOrNull(), is_default = isDefault
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) HoloColors.Ink else HoloColors.Neutral200,
                    disabledContainerColor = HoloColors.Neutral200
                ),
                enabled = isFormValid
            ) {
                Text("AÑADIR TARJETA", style = HoloType.LabelLarge, color = if (isFormValid) HoloColors.Paper else HoloColors.TextTertiary)
            }

            Spacer(Modifier.height(HoloSpacing.sm))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, null, tint = HoloColors.TextTertiary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(HoloSpacing.xxs))
                Text("Datos encriptados y seguros", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
            }
            Spacer(Modifier.height(HoloSpacing.xxl))
        }
    }
}