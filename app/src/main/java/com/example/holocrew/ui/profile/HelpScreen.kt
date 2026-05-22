package com.example.holocrew.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper) }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink))
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {

            Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800))).padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.lg)) {
                Column {
                    Text("SOPORTE", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                    Spacer(Modifier.height(HoloSpacing.xxs))
                    Text("Centro de ayuda", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                    Spacer(Modifier.height(HoloSpacing.xxs))
                    Text("Estamos aqui para ayudarte", style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted)
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // Contacto por email
            Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusLg), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
                Column(Modifier.padding(HoloSpacing.lg)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).clip(RoundedCornerShape(HoloSpacing.RadiusMd)).background(HoloColors.Pulse.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Email, null, tint = HoloColors.Pulse, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(HoloSpacing.md))
                        Column {
                            Text("Contacto por email", style = HoloType.TitleLarge, color = HoloColors.TextPrimary)
                            Text("Respuesta en 24-48h", style = HoloType.BodySmall, color = HoloColors.TextTertiary)
                        }
                    }
                    Spacer(Modifier.height(HoloSpacing.md))
                    Text("Si tienes algun problema con tu pedido, cuenta o cualquier duda, escribenos y te ayudaremos lo antes posible.", style = HoloType.BodyMedium, color = HoloColors.TextSecondary)
                    Spacer(Modifier.height(HoloSpacing.lg))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:soporte@holocrew.com")
                                putExtra(Intent.EXTRA_SUBJECT, "HoloCrew App - Soporte")
                            }
                            context.startActivity(Intent.createChooser(intent, "Enviar email"))
                        },
                        modifier = Modifier.fillMaxWidth().height(HoloSpacing.ButtonHeight),
                        shape = RoundedCornerShape(HoloSpacing.RadiusPill),
                        colors = ButtonDefaults.buttonColors(containerColor = HoloColors.Ink)
                    ) { Text("ENVIAR EMAIL A SOPORTE", style = HoloType.LabelLarge, color = HoloColors.Paper) }
                    Spacer(Modifier.height(HoloSpacing.xs))
                    Text("soporte@holocrew.com", style = HoloType.MonoSmall, color = HoloColors.TextTertiary, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // FAQ
            Text("PREGUNTAS FRECUENTES", style = HoloType.LabelMedium, color = HoloColors.TextTertiary, modifier = Modifier.padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.xs))

            FaqItem("¿Cuanto tarda mi pedido?", "Los pedidos estandar se entregan en 3-5 dias laborables. El envio express llega en 1-2 dias laborables.")
            FaqItem("¿Puedo cancelar un pedido?", "Puedes cancelar tu pedido dentro de las primeras 2 horas. Despues, contacta con soporte.")
            FaqItem("¿Como hago una devolucion?", "Tienes 30 dias para devolver cualquier producto sin usar. Contacta soporte para iniciar el proceso.")
            FaqItem("¿Los precios incluyen IVA?", "Si, todos los precios mostrados incluyen IVA.")
            FaqItem("¿Como funciona el programa de miembros?", "Al registrarte recibes 200 creditos. Acumula puntos con cada compra para subir de nivel y desbloquear beneficios exclusivos.")

            Spacer(Modifier.height(HoloSpacing.xxl))
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.xxs),
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.clickable { expanded = !expanded }.padding(HoloSpacing.md)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(question, style = HoloType.TitleMedium, color = HoloColors.TextPrimary, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null, tint = HoloColors.TextTertiary)
            }
            if (expanded) {
                Spacer(Modifier.height(HoloSpacing.sm))
                Text(answer, style = HoloType.BodyMedium, color = HoloColors.TextSecondary)
            }
        }
    }
}