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
import com.example.holocrew.data.network.PaymentRepository
import com.example.holocrew.data.network.SbPaymentMethodDto
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.auth.holoTextFieldColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var methods by remember { mutableStateOf<List<SbPaymentMethodDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddSheet by remember { mutableStateOf(false) }

    fun loadMethods() {
        scope.launch {
            isLoading = true
            methods = PaymentRepository.getPaymentMethods()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadMethods() }

    if (showAddSheet) {
        AddCardBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { type, lastFour, brand, month, year, default_ ->
                scope.launch {
                    val ok = PaymentRepository.addPaymentMethod(
                        paymentType = type,
                        cardLastFour = lastFour,
                        cardBrand = brand,
                        expiryMonth = month,
                        expiryYear = year,
                        isDefault = default_
                    )
                    showAddSheet = false
                    if (ok) {
                        Toast.makeText(context, "Tarjeta agregada", Toast.LENGTH_SHORT).show()
                        loadMethods()
                    } else {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper) }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }, containerColor = HoloColors.Ink, contentColor = HoloColors.Paper, shape = RoundedCornerShape(HoloSpacing.RadiusLg)) {
                Icon(Icons.Filled.Add, "Agregar tarjeta")
            }
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->
        LazyColumn(Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(bottom = HoloSpacing.BottomNavHeight)) {

            item {
                Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800))).padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)) {
                    Column {
                        Text("PAGOS", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("Metodos de pago", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text(
                            if (methods.isEmpty() && !isLoading) "Agrega tu primera tarjeta"
                            else "${methods.size} metodo${if (methods.size != 1) "s" else ""} guardado${if (methods.size != 1) "s" else ""}",
                            style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted
                        )
                    }
                }
            }

            if (isLoading) {
                item { Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = HoloColors.Ink) } }
            } else if (methods.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(HoloSpacing.xxxl), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.CreditCard, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.huge))
                        Spacer(Modifier.height(HoloSpacing.lg))
                        Text("No tienes tarjetas guardadas", style = HoloType.TitleLarge, color = HoloColors.TextSecondary)
                        Text("Agrega una para pagar mas rapido", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xs))
                        Spacer(Modifier.height(HoloSpacing.xl))
                        Button(onClick = { showAddSheet = true }, colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink), shape = RoundedCornerShape(HoloSpacing.RadiusPill), modifier = Modifier.height(HoloSpacing.ButtonHeightSmall)) {
                            Text("AGREGAR TARJETA", style = HoloType.LabelLarge, color = HoloColors.Paper)
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(HoloSpacing.md)) }
                items(methods) { method ->
                    PaymentCard(method = method, onDelete = {
                        scope.launch {
                            if (method.id != null && PaymentRepository.deletePaymentMethod(method.id)) {
                                Toast.makeText(context, "Tarjeta eliminada", Toast.LENGTH_SHORT).show()
                                loadMethods()
                            }
                        }
                    })
                    Spacer(Modifier.height(HoloSpacing.sm))
                }
            }
        }
    }
}

@Composable
fun PaymentCard(method: SbPaymentMethodDto, onDelete: () -> Unit) {
    val brandColor = when (method.cardBrand?.lowercase()) { "visa" -> HoloColors.Info; "mastercard" -> HoloColors.Pulse; "amex" -> HoloColors.Info; else -> HoloColors.Neutral800 }
    val brandIcon = when (method.cardBrand?.lowercase()) { "visa" -> "VISA"; "mastercard" -> "MC"; "amex" -> "AMEX"; else -> "CARD" }
    val typeLabel = when (method.paymentType) { "credit_card" -> "Credito"; "debit_card" -> "Debito"; "paypal" -> "PayPal"; else -> method.paymentType }

    Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusXl), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(HoloSpacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(56.dp).height(36.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(brandColor), contentAlignment = Alignment.Center) {
                    Text(brandIcon, style = HoloType.LabelSmall, color = HoloColors.Paper)
                }
                Spacer(Modifier.width(HoloSpacing.sm))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(typeLabel, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
                        if (method.isDefault) {
                            Spacer(Modifier.width(HoloSpacing.xs))
                            Box(Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusPill)).background(HoloColors.Pulse.copy(alpha = 0.15f)).padding(horizontal = HoloSpacing.xs, vertical = 3.dp)) {
                                Text("Principal", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                            }
                        }
                    }
                    if (method.cardLastFour != null) {
                        Text("\u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022 ${method.cardLastFour}", style = HoloType.MonoSmall, color = HoloColors.TextSecondary, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            if (method.cardExpiryMonth != null && method.cardExpiryYear != null) {
                Spacer(Modifier.height(HoloSpacing.sm))
                Column {
                    Text("EXPIRA", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
                    Text("${String.format("%02d", method.cardExpiryMonth)}/${method.cardExpiryYear}", style = HoloType.MonoSmall, color = HoloColors.Neutral600)
                }
            }
            Spacer(Modifier.height(HoloSpacing.sm)); Divider(color = HoloColors.Neutral100); Spacer(Modifier.height(HoloSpacing.xs))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDelete) { Text("Eliminar", style = HoloType.TitleSmall, color = HoloColors.Pulse) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardBottomSheet(onDismiss: () -> Unit, onSave: (String, String?, String?, Int?, Int?, Boolean) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryMonth by remember { mutableStateOf("") }
    var expiryYear by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("credit_card") }
    var isDefault by remember { mutableStateOf(false) }

    val cardBrand = remember(cardNumber) {
        when { cardNumber.startsWith("4") -> "Visa"; cardNumber.startsWith("5") || cardNumber.startsWith("2") -> "Mastercard"; cardNumber.startsWith("3") -> "Amex"; else -> null }
    }
    val isFormValid = cardNumber.length >= 16 && cardHolder.isNotBlank() && expiryMonth.isNotBlank() && expiryYear.isNotBlank()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = HoloColors.Paper, shape = RoundedCornerShape(topStart = HoloSpacing.RadiusXl, topEnd = HoloSpacing.RadiusXl)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.lg).verticalScroll(rememberScrollState())) {
            Text("Nueva tarjeta", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
            Text("Tus datos estan protegidos", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xxs))
            Spacer(Modifier.height(HoloSpacing.xl))

            // Preview tarjeta
            Box(Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(HoloSpacing.RadiusLg)).background(Brush.horizontalGradient(listOf(HoloColors.Neutral800, HoloColors.Neutral700))).padding(HoloSpacing.lg)) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("HOLO CREW", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Text(cardBrand ?: "", style = HoloType.TitleSmall, color = HoloColors.TextOnDark)
                    }
                    Text(if (cardNumber.length >= 4) "\u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022 ${cardNumber.takeLast(4)}" else "\u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022", style = HoloType.MonoMedium, color = HoloColors.TextOnDark)
                }
            }
            Spacer(Modifier.height(HoloSpacing.xl))

            // Tipo
            Text("TIPO DE TARJETA", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
            Spacer(Modifier.height(HoloSpacing.xs))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HoloSpacing.xs)) {
                listOf("credit_card" to "Credito", "debit_card" to "Debito").forEach { (type, label) ->
                    val sel = selectedType == type
                    Box(Modifier.weight(1f).height(HoloSpacing.ButtonHeightSmall).clip(RoundedCornerShape(HoloSpacing.RadiusMd)).background(if (sel) HoloColors.Ink else HoloColors.Fog).clickable { selectedType = type }, contentAlignment = Alignment.Center) {
                        Text(label, style = HoloType.TitleMedium, color = if (sel) HoloColors.Paper else HoloColors.TextSecondary)
                    }
                }
            }
            Spacer(Modifier.height(HoloSpacing.lg))

            // Numero
            Text("NUMERO DE TARJETA", style = HoloType.LabelSmall, color = HoloColors.TextTertiary); Spacer(Modifier.height(HoloSpacing.xs))
            OutlinedTextField(value = cardNumber, onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it }, placeholder = { Text("1234 5678 9012 3456", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, trailingIcon = { Icon(Icons.Outlined.CreditCard, null, tint = HoloColors.TextTertiary) })
            Spacer(Modifier.height(HoloSpacing.md))

            // Titular
            Text("TITULAR", style = HoloType.LabelSmall, color = HoloColors.TextTertiary); Spacer(Modifier.height(HoloSpacing.xs))
            OutlinedTextField(value = cardHolder, onValueChange = { cardHolder = it.uppercase() }, placeholder = { Text("NOMBRE APELLIDO", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), singleLine = true)
            Spacer(Modifier.height(HoloSpacing.md))

            // Caducidad
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)) {
                Column(Modifier.weight(1f)) {
                    Text("MES", style = HoloType.LabelSmall, color = HoloColors.TextTertiary); Spacer(Modifier.height(HoloSpacing.xs))
                    OutlinedTextField(value = expiryMonth, onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) expiryMonth = it }, placeholder = { Text("MM", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
                Column(Modifier.weight(1f)) {
                    Text("ANO", style = HoloType.LabelSmall, color = HoloColors.TextTertiary); Spacer(Modifier.height(HoloSpacing.xs))
                    OutlinedTextField(value = expiryYear, onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) expiryYear = it }, placeholder = { Text("AAAA", style = HoloType.BodyMedium, color = HoloColors.TextDisabled) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = holoTextFieldColors(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
            }
            Spacer(Modifier.height(HoloSpacing.md))

            // Switch default
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("Tarjeta principal", style = HoloType.TitleMedium, color = HoloColors.TextPrimary); Text("Usar por defecto en tus compras", style = HoloType.BodySmall, color = HoloColors.TextTertiary) }
                Switch(checked = isDefault, onCheckedChange = { isDefault = it }, colors = SwitchDefaults.colors(checkedThumbColor = HoloColors.Paper, checkedTrackColor = HoloColors.Ink))
            }
            Spacer(Modifier.height(HoloSpacing.xxl))

            // Boton
            Button(
                onClick = { if (isFormValid) onSave(selectedType, cardNumber.takeLast(4), cardBrand, expiryMonth.toIntOrNull(), expiryYear.toIntOrNull(), isDefault) },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight), shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                colors = ButtonDefaults.buttonColors(containerColor = if (isFormValid) HoloColors.Ink else HoloColors.Neutral200, disabledContainerColor = HoloColors.Neutral200), enabled = isFormValid
            ) { Text("AGREGAR TARJETA", style = HoloType.LabelLarge, color = if (isFormValid) HoloColors.Paper else HoloColors.TextTertiary) }

            Spacer(Modifier.height(HoloSpacing.sm))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, null, tint = HoloColors.TextTertiary, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(HoloSpacing.xxs))
                Text("Datos encriptados y seguros", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
            }
            Spacer(Modifier.height(HoloSpacing.xxl))
        }
    }
}