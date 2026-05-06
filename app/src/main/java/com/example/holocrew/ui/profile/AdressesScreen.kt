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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.TokenManager
import com.example.holocrew.data.network.AddAddressRequest
import com.example.holocrew.data.network.AddressDto
import com.example.holocrew.data.network.RetrofitClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.auth.FieldLabel
import com.example.holocrew.ui.auth.holoTextFieldColors
import kotlinx.coroutines.launch

/**
 * AddressesScreen — Gestión de direcciones de envío del usuario.
 *
 * Diseño SNKRS:
 *  - Header negro con gradiente + label rojo "ENVÍO"
 *  - Cards blancas con icono de ubicación, nombre, dirección completa
 *  - Badge verde "Principal" para la dirección por defecto
 *  - FAB negro para añadir nueva dirección (BottomSheet)
 *  - Estado vacío con icono grande y CTA
 *
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var addresses by remember { mutableStateOf<List<AddressDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddSheet by remember { mutableStateOf(false) }

    // Función para cargar/recargar direcciones desde la API
    fun loadAddresses() {
        isLoading = false
    }

    LaunchedEffect(Unit) { loadAddresses() }

    // ── BottomSheet para añadir nueva dirección ───────────────────────────────
    if (showAddSheet) {
        AddAddressBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { request ->
                scope.launch {
                    addresses = addresses + AddressDto(
                        id = "addr_${System.currentTimeMillis()}",
                        recipient_name = request.recipient_name,
                        street_address = request.street_address,
                        apartment = request.apartment,
                        city = request.city,
                        postal_code = request.postal_code,
                        country_code = request.country_code,
                        phone = request.phone,
                        is_default = request.is_default
                    )
                    showAddSheet = false
                    Toast.makeText(context, "Dirección añadida", Toast.LENGTH_SHORT).show()
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
                Icon(Icons.Filled.Add, "Añadir dirección")
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
                        Text("ENVÍO", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("Mis direcciones", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text(
                            if (addresses.isEmpty() && !isLoading) "Añade tu primera dirección"
                            else "${addresses.size} dirección${if (addresses.size != 1) "es" else ""}",
                            style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Cargando...", style = HoloType.TitleLarge, color = HoloColors.TextTertiary)
                    }
                }
            } else if (addresses.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(HoloSpacing.xxxl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.huge))
                        Spacer(Modifier.height(HoloSpacing.lg))
                        Text("No tienes direcciones guardadas", style = HoloType.TitleLarge, color = HoloColors.TextSecondary)
                        Text("Añade una para agilizar tus compras", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xs))
                        Spacer(Modifier.height(HoloSpacing.xl))
                        Button(
                            onClick = { showAddSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                            shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                            modifier = Modifier.height(HoloSpacing.ButtonHeightSmall)
                        ) {
                            Text("AÑADIR DIRECCIÓN", style = HoloType.LabelLarge, color = HoloColors.Paper)
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(HoloSpacing.md)) }
                items(addresses) { address ->
                    AddressCard(
                        address = address,
                        onDelete = {
                            addresses = addresses.filter { it.id != address.id }
                            Toast.makeText(context, "Dirección eliminada", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(Modifier.height(HoloSpacing.sm))
                }
            }
        }
    }
}

/**
 * AddressCard — Card individual de una dirección guardada.
 */
@Composable
fun AddressCard(address: AddressDto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(HoloSpacing.lg)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icono de ubicación en cuadrado gris
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(HoloColors.Fog),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
                    }
                    Spacer(Modifier.width(HoloSpacing.sm))
                    Text(address.recipient_name, style = HoloType.TitleLarge, color = HoloColors.TextPrimary)
                }
                // Badge "Principal" en verde
                if (address.is_default) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusPill))
                            .background(HoloColors.Success.copy(alpha = 0.1f))
                            .padding(horizontal = HoloSpacing.sm, vertical = HoloSpacing.xxs)
                    ) {
                        Text("Principal", style = HoloType.LabelSmall, color = HoloColors.Success)
                    }
                }
            }

            Spacer(Modifier.height(HoloSpacing.sm))

            // Detalles de la dirección (indentados bajo el icono)
            Column(modifier = Modifier.padding(start = 52.dp)) {
                Text(address.street_address, style = HoloType.BodyMedium, color = HoloColors.Neutral600)
                address.apartment?.let { if (it.isNotEmpty()) Text(it, style = HoloType.BodyMedium, color = HoloColors.Neutral600) }
                Text("${address.postal_code} ${address.city}", style = HoloType.BodyMedium, color = HoloColors.Neutral600)
                address.phone?.let { if (it.isNotEmpty()) Text(it, style = HoloType.BodySmall, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xxs)) }
            }

            Spacer(Modifier.height(HoloSpacing.sm))
            Divider(color = HoloColors.Neutral100)
            Spacer(Modifier.height(HoloSpacing.xs))

            // Botón eliminar en rojo Pulse
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDelete) {
                    Text("Eliminar", style = HoloType.TitleSmall, color = HoloColors.Pulse)
                }
            }
        }
    }
}

/**
 * AddAddressBottomSheet — BottomSheet para añadir una nueva dirección.
 */
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

    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState,
        containerColor = HoloColors.Paper,
        shape = RoundedCornerShape(topStart = HoloSpacing.RadiusXl, topEnd = HoloSpacing.RadiusXl)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.lg).verticalScroll(rememberScrollState())
        ) {
            Text("Nueva dirección", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
            Text("Rellena los datos de envío", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xxs))

            Spacer(Modifier.height(HoloSpacing.xl))

            // Campos del formulario usando FieldLabel + holoTextFieldColors
            AddressField("DESTINATARIO", name) { name = it }
            Spacer(Modifier.height(HoloSpacing.md))
            AddressField("TELÉFONO", phone) { phone = it }
            Spacer(Modifier.height(HoloSpacing.md))
            AddressField("DIRECCIÓN", street) { street = it }
            Spacer(Modifier.height(HoloSpacing.md))
            AddressField("PISO / PUERTA (OPCIONAL)", apartment) { apartment = it }
            Spacer(Modifier.height(HoloSpacing.md))

            // Ciudad + C.P. en la misma fila
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)) {
                Column(Modifier.weight(1f)) { AddressField("CIUDAD", city) { city = it } }
                Column(Modifier.weight(1f)) { AddressField("C.P.", postalCode) { postalCode = it } }
            }

            Spacer(Modifier.height(HoloSpacing.xxl))

            // Botón guardar
            Button(
                onClick = {
                    if (name.isNotBlank() && street.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank()) {
                        onSave(AddAddressRequest(name.trim(), phone.trim().ifEmpty { null }, street.trim(), apartment.trim().ifEmpty { null }, city.trim(), postal_code = postalCode.trim(), is_default = true))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                enabled = name.isNotBlank() && street.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank()
            ) {
                Text("GUARDAR DIRECCIÓN", style = HoloType.LabelLarge, color = HoloColors.Paper)
            }
            Spacer(Modifier.height(HoloSpacing.xxl))
        }
    }
}

/**
 * AddressField — Campo de texto individual para el formulario de dirección.
 */
@Composable
fun AddressField(label: String, value: String, onValueChange: (String) -> Unit) {
    FieldLabel(label)
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = holoTextFieldColors(),
        singleLine = true
    )
}