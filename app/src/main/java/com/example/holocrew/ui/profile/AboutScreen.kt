package com.example.holocrew.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.holocrew.R
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper) }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink))
        },
        containerColor = HoloColors.Fog
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {

            // Header
            Box(
                Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(HoloColors.Ink, HoloColors.Neutral800))).padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.xxl),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logito),
                        contentDescription = "Logo HoloCrew",
                        modifier = Modifier.height(60.dp).width(160.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                    )
                    Spacer(Modifier.height(HoloSpacing.sm))
                    Text("STREETWEAR EXCLUSIVO", style = HoloType.LabelSmall, color = HoloColors.Pulse)
                    Spacer(Modifier.height(HoloSpacing.xs))
                    Text("Version 1.0.0", style = HoloType.MonoSmall, color = HoloColors.TextOnDarkMuted)
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // Descripcion
            Card(Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusLg), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
                Column(Modifier.padding(HoloSpacing.lg)) {
                    Text("Sobre nosotros", style = HoloType.HeadlineMedium, color = HoloColors.TextPrimary)
                    Spacer(Modifier.height(HoloSpacing.sm))
                    Text(
                        "HoloCrew es una plataforma de streetwear exclusivo que conecta a los amantes de la moda urbana con colecciones limitadas, colaboraciones unicas y drops exclusivos.\n\nNuestra mision es ofrecer piezas de calidad con un diseño unico que refleje la cultura urbana contemporanea.",
                        style = HoloType.BodyMedium, color = HoloColors.TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))

            // Info cards
            AboutInfoCard(Icons.Outlined.Storefront, "Marca", "HoloCrew Streetwear")
            AboutInfoCard(Icons.Outlined.LocationOn, "Ubicacion", "Barcelona, España")
            AboutInfoCard(Icons.Outlined.Code, "Desarrollo", "LinkiaFP - DAM 2025/2026")
            AboutInfoCard(Icons.Outlined.Email, "Contacto", "soporte@holocrew.com")

            Spacer(Modifier.height(HoloSpacing.lg))

            // Legal
            Text(
                "© 2026 HoloCrew. Todos los derechos reservados.\nProyecto academico - LinkiaFP Barcelona",
                style = HoloType.BodySmall, color = HoloColors.TextDisabled,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.lg, vertical = HoloSpacing.md)
            )

            Spacer(Modifier.height(HoloSpacing.xl))
        }
    }
}

@Composable
fun AboutInfoCard(icon: ImageVector, label: String, value: String) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md, vertical = HoloSpacing.xxs),
        shape = RoundedCornerShape(HoloSpacing.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(Modifier.padding(HoloSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(HoloSpacing.RadiusSm)).background(HoloColors.Fog), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = HoloColors.Ink, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(HoloSpacing.sm))
            Column {
                Text(label, style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
                Text(value, style = HoloType.TitleMedium, color = HoloColors.TextPrimary)
            }
        }
    }
}