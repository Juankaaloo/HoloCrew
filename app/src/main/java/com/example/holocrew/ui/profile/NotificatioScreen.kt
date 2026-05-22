package com.example.holocrew.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.data.network.SupabaseClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val supabase = SupabaseClient.client

    var emailOrders by remember { mutableStateOf(true) }
    var emailPromotions by remember { mutableStateOf(true) }
    var pushOrders by remember { mutableStateOf(true) }
    var pushPromotions by remember { mutableStateOf(false) }
    var pushDrops by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Cargar preferencias desde Supabase
    LaunchedEffect(Unit) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId != null) {
                val result = supabase.from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<JsonObject>()

                val prefs = result["notification_preferences"]?.jsonObject
                if (prefs != null) {
                    emailOrders = prefs["email_orders"]?.jsonPrimitive?.booleanOrNull ?: true
                    emailPromotions = prefs["email_promotions"]?.jsonPrimitive?.booleanOrNull ?: true
                    pushOrders = prefs["push_orders"]?.jsonPrimitive?.booleanOrNull ?: true
                    pushPromotions = prefs["push_promotions"]?.jsonPrimitive?.booleanOrNull ?: false
                    pushDrops = prefs["push_drops"]?.jsonPrimitive?.booleanOrNull ?: true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoading = false
    }

    fun savePreferences() {
        scope.launch {
            isSaving = true
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val prefs = buildJsonObject {
                    put("email_orders", emailOrders)
                    put("email_promotions", emailPromotions)
                    put("push_orders", pushOrders)
                    put("push_promotions", pushPromotions)
                    put("push_drops", pushDrops)
                }
                supabase.from("profiles")
                    .update(buildJsonObject {
                        put("notification_preferences", prefs)
                    }) { filter { eq("id", userId) } }
                Toast.makeText(context, "Preferencias guardadas", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            isSaving = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper) }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink))
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->

        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HoloColors.Ink)
            }
            return@Scaffold
        }

        Column(Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {

            // Header
            Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800))).padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)) {
                Column {
                    Text("ALERTAS", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                    Spacer(Modifier.height(HoloSpacing.xxs))
                    Text("Notificaciones", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                    Spacer(Modifier.height(HoloSpacing.xxs))
                    Text("Elige que quieres recibir", style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted)
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // Email notifications
            NotificationSection(
                title = "EMAIL",
                icon = Icons.Outlined.Email
            ) {
                NotificationToggle(
                    title = "Pedidos y envios",
                    subtitle = "Confirmacion, seguimiento y entrega",
                    checked = emailOrders,
                    onCheckedChange = { emailOrders = it }
                )
                NotificationToggle(
                    title = "Ofertas y promociones",
                    subtitle = "Descuentos, rebajas y codigos exclusivos",
                    checked = emailPromotions,
                    onCheckedChange = { emailPromotions = it }
                )
            }

            Spacer(Modifier.height(HoloSpacing.sm))

            // Push notifications
            NotificationSection(
                title = "NOTIFICACIONES PUSH",
                icon = Icons.Outlined.Notifications
            ) {
                NotificationToggle(
                    title = "Pedidos y envios",
                    subtitle = "Alertas en tiempo real de tus pedidos",
                    checked = pushOrders,
                    onCheckedChange = { pushOrders = it }
                )
                NotificationToggle(
                    title = "Nuevos drops",
                    subtitle = "Se el primero en enterarte de los lanzamientos",
                    checked = pushDrops,
                    onCheckedChange = { pushDrops = it }
                )
                NotificationToggle(
                    title = "Ofertas y promociones",
                    subtitle = "Flash sales, Black Week y descuentos",
                    checked = pushPromotions,
                    onCheckedChange = { pushPromotions = it }
                )
            }

            Spacer(Modifier.height(HoloSpacing.xl))

            // Boton guardar
            Button(
                onClick = { savePreferences() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md).height(HoloSpacing.ButtonHeight),
                shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink),
                enabled = !isSaving
            ) {
                Text(if (isSaving) "GUARDANDO..." else "GUARDAR PREFERENCIAS", style = HoloType.LabelLarge, color = HoloColors.Paper)
            }

            Spacer(Modifier.height(HoloSpacing.sm))

            // Info
            Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
                Row(Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Info, null, tint = HoloColors.TextTertiary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(HoloSpacing.sm))
                    Text("Puedes cambiar estas preferencias en cualquier momento", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
                }
            }

            Spacer(Modifier.height(HoloSpacing.xxl))
        }
    }
}

@Composable
fun NotificationSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(horizontal = HoloSpacing.md)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = HoloSpacing.xs)) {
            Icon(icon, null, tint = HoloColors.TextTertiary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(HoloSpacing.xs))
            Text(title, style = HoloType.LabelMedium, color = HoloColors.TextTertiary)
        }
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusLg), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
            Column(Modifier.padding(vertical = HoloSpacing.xs)) {
                content()
            }
        }
    }
}

@Composable
fun NotificationToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f).padding(end = HoloSpacing.md)) {
            Text(title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
            Text(subtitle, style = HoloType.BodySmall, color = HoloColors.TextTertiary, modifier = Modifier.padding(top = 2.dp))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = HoloColors.Paper,
                checkedTrackColor = HoloColors.Ink,
                uncheckedThumbColor = HoloColors.Neutral400,
                uncheckedTrackColor = HoloColors.Neutral200
            )
        )
    }
}