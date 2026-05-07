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
import com.example.holocrew.data.network.AddressRepository
import com.example.holocrew.data.network.SbAddressDto
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import com.example.holocrew.ui.auth.FieldLabel
import com.example.holocrew.ui.auth.holoTextFieldColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var addresses by remember { mutableStateOf<List<SbAddressDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddSheet by remember { mutableStateOf(false) }

    fun loadAddresses() {
        scope.launch {
            isLoading = true
            addresses = AddressRepository.getAddresses()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadAddresses() }

    if (showAddSheet) {
        AddAddressBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { fullName, phone, street, streetLine2, city, postalCode ->
                scope.launch {
                    val ok = AddressRepository.addAddress(
                        fullName = fullName,
                        phone = phone,
                        street = street,
                        streetLine2 = streetLine2,
                        city = city,
                        postalCode = postalCode,
                        isDefault = addresses.isEmpty()
                    )
                    showAddSheet = false
                    if (ok) {
                        Toast.makeText(context, "Direccion agregada", Toast.LENGTH_SHORT).show()
                        loadAddresses()
                    } else {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
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
            ) { Icon(Icons.Filled.Add, "Agregar direccion") }
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = HoloSpacing.BottomNavHeight)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800)))
                        .padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)
                ) {
                    Column {
                        Text("ENVIO", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("Mis direcciones", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text(
                            if (addresses.isEmpty() && !isLoading) "Agrega tu primera direccion"
                            else "${addresses.size} direccion${if (addresses.size != 1) "es" else ""}",
                            style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = HoloColors.Ink)
                    }
                }
            } else if (addresses.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(HoloSpacing.xxxl), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.LocationOn, null, tint = HoloColors.Neutral300, modifier = Modifier.size(HoloSpacing.huge))
                        Spacer(Modifier.height(HoloSpacing.lg))
                        Text("No tienes direcciones guardadas", style = HoloType.TitleLarge, color = HoloColors.TextSecondary)
                        Text("Agrega una para agilizar tus compras", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xs))
                        Spacer(Modifier.height(HoloSpacing.xl))
                        Button(
                            onClick = { showAddSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                            shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                            modifier = Modifier.height(HoloSpacing.ButtonHeightSmall)
                        ) { Text("AGREGAR DIRECCION", style = HoloType.LabelLarge, color = HoloColors.Paper) }
                    }
                }
            } else {
                item { Spacer(Modifier.height(HoloSpacing.md)) }
                items(addresses) { address ->
                    AddressCard(
                        address = address,
                        onDelete = {
                            scope.launch {
                                if (address.id != null && AddressRepository.deleteAddress(address.id)) {
                                    Toast.makeText(context, "Direccion eliminada", Toast.LENGTH_SHORT).show()
                                    loadAddresses()
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

@Composable
fun AddressCard(address: SbAddressDto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md),
        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(HoloSpacing.lg)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(HoloColors.Fog), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.LocationOn, null, tint = HoloColors.Ink, modifier = Modifier.size(HoloSpacing.IconSizeDefault))
                    }
                    Spacer(Modifier.width(HoloSpacing.sm))
                    Text(address.fullName, style = HoloType.TitleLarge, color = HoloColors.TextPrimary)
                }
                if (address.isDefault) {
                    Box(Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusPill)).background(HoloColors.Success.copy(alpha = 0.1f)).padding(horizontal = HoloSpacing.sm, vertical = HoloSpacing.xxs)) {
                        Text("Principal", style = HoloType.LabelSmall, color = HoloColors.Success)
                    }
                }
            }
            Spacer(Modifier.height(HoloSpacing.sm))
            Column(modifier = Modifier.padding(start = 52.dp)) {
                Text(address.street, style = HoloType.BodyMedium, color = HoloColors.Neutral600)
                address.streetLine2?.let { if (it.isNotEmpty()) Text(it, style = HoloType.BodyMedium, color = HoloColors.Neutral600) }
                Text("${address.postalCode} ${address.city}", style = HoloType.BodyMedium, color = HoloColors.Neutral600)
                address.phone?.let { if (it.isNotEmpty()) Text(it, style = HoloType.BodySmall, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xxs)) }
            }
            Spacer(Modifier.height(HoloSpacing.sm))
            Divider(color = HoloColors.Neutral100)
            Spacer(Modifier.height(HoloSpacing.xs))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDelete) { Text("Eliminar", style = HoloType.TitleSmall, color = HoloColors.Pulse) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressBottomSheet(onDismiss: () -> Unit, onSave: (String, String?, String, String?, String, String) -> Unit) {
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
        Column(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.lg).verticalScroll(rememberScrollState())) {
            Text("Nueva direccion", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
            Text("Rellena los datos de envio", style = HoloType.BodyMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = HoloSpacing.xxs))
            Spacer(Modifier.height(HoloSpacing.xl))
            AddressField("DESTINATARIO", name) { name = it }
            Spacer(Modifier.height(HoloSpacing.md))
            AddressField("TELEFONO", phone) { phone = it }
            Spacer(Modifier.height(HoloSpacing.md))
            AddressField("DIRECCION", street) { street = it }
            Spacer(Modifier.height(HoloSpacing.md))
            AddressField("PISO / PUERTA (OPCIONAL)", apartment) { apartment = it }
            Spacer(Modifier.height(HoloSpacing.md))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)) {
                Column(Modifier.weight(1f)) { AddressField("CIUDAD", city) { city = it } }
                Column(Modifier.weight(1f)) { AddressField("C.P.", postalCode) { postalCode = it } }
            }
            Spacer(Modifier.height(HoloSpacing.xxl))
            Button(
                onClick = {
                    if (name.isNotBlank() && street.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank()) {
                        onSave(name.trim(), phone.trim().ifEmpty { null }, street.trim(), apartment.trim().ifEmpty { null }, city.trim(), postalCode.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                enabled = name.isNotBlank() && street.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank()
            ) { Text("GUARDAR DIRECCION", style = HoloType.LabelLarge, color = HoloColors.Paper) }
            Spacer(Modifier.height(HoloSpacing.xxl))
        }
    }
}

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