package com.example.holocrew.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.holocrew.data.network.SupabaseClient
import com.example.holocrew.theme.HoloColors
import com.example.holocrew.theme.HoloSpacing
import com.example.holocrew.theme.HoloType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import coil.compose.AsyncImage

// DTO para membership_tiers_config
@Serializable
data class MembershipTierConfig(
    val tier: String,
    val name: String,
    @SerialName("min_points") val minPoints: Int,
    @SerialName("max_points") val maxPoints: Int? = null,
    @SerialName("points_per_euro") val pointsPerEuro: Double,
    @SerialName("early_access_hours") val earlyAccessHours: Int = 0,
    @SerialName("free_shipping_threshold") val freeShippingThreshold: Double = 0.0,
    @SerialName("birthday_bonus_credits") val birthdayBonusCredits: Int = 0,
    val benefits: JsonArray? = null,
    @SerialName("image_url") val imageUrl: String? = null
)

// Colores de los tiers
private val BronzeColor = Color(0xFFCD7F32)
private val SilverColor = Color(0xFFC0C0C0)
private val GoldColor = Color(0xFFD4AF37)
private val PlatinumColor = Color(0xFF7CB9E8)

private fun tierColor(tier: String) = when (tier.lowercase()) {
    "platinum" -> PlatinumColor; "gold" -> GoldColor; "silver" -> SilverColor; else -> BronzeColor
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(navController: NavController) {
    val supabase = SupabaseClient.client

    var tiers by remember { mutableStateOf<List<MembershipTierConfig>>(emptyList()) }
    var userTier by remember { mutableStateOf("bronze") }
    var userCredits by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar tiers y datos del usuario
    LaunchedEffect(Unit) {
        try {
            // Tiers desde membership_tiers_config
            tiers = supabase.from("membership_tiers_config")
                .select { order("min_points", Order.ASCENDING) }
                .decodeList<MembershipTierConfig>()

            // Datos del usuario
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId != null) {
                val profile = supabase.from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<JsonObject>()
                userTier = profile["membership_tier"]?.jsonPrimitive?.contentOrNull ?: "bronze"
                userCredits = profile["credits"]?.jsonPrimitive?.intOrNull ?: 0
            }
        } catch (e: Exception) { e.printStackTrace() }
        isLoading = false
    }

    // Calcular tier actual y siguiente
    val currentTierConfig = tiers.firstOrNull { it.tier == userTier }
    val nextTier = tiers.dropWhile { it.tier != userTier }.drop(1).firstOrNull()
    val progressToNext = if (nextTier != null && currentTierConfig != null) {
        val range = nextTier.minPoints - currentTierConfig.minPoints
        if (range > 0) ((userCredits - currentTierConfig.minPoints).toFloat() / range).coerceIn(0f, 1f) else 1f
    } else 1f

    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = HoloColors.Paper) }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = HoloColors.Ink))
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = HoloColors.Ink) }
            return@Scaffold
        }

        LazyColumn(Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(bottom = HoloSpacing.xxxl)) {

            // HERO
            item {
                Box(Modifier.fillMaxWidth().height(260.dp).background(HoloColors.Ink), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(HoloSpacing.xl)) {
                        Text("MEMBERS", style = HoloType.DisplayMedium, color = HoloColors.TextOnDark)
                        Text("CLUB", style = HoloType.DisplayMedium, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.md))
                        Text("JOIN HOLOCREW CLUB &\nGET REWARDED", style = HoloType.BodyMedium, color = HoloColors.TextOnDarkMuted, textAlign = TextAlign.Center, letterSpacing = 2.sp)
                        Spacer(Modifier.height(HoloSpacing.sm))
                        Box(Modifier.width(40.dp).height(2.dp).background(HoloColors.Pulse))
                    }
                }
            }

            // TU NIVEL + CREDITOS + PROGRESO
            item {
                val color = tierColor(userTier)
                Card(Modifier.fillMaxWidth().padding(HoloSpacing.md), shape = RoundedCornerShape(HoloSpacing.RadiusLg), colors = CardDefaults.cardColors(containerColor = HoloColors.Ink)) {
                    Column(Modifier.padding(HoloSpacing.lg)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(56.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)).border(2.dp, color, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Star, null, tint = color, modifier = Modifier.size(28.dp))
                            }
                            Spacer(Modifier.width(HoloSpacing.md))
                            Column(Modifier.weight(1f)) {
                                Text("TU NIVEL ACTUAL", style = HoloType.LabelSmall, color = HoloColors.TextOnDarkMuted)
                                Text(userTier.uppercase(), style = HoloType.HeadlineLarge, color = color)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("$userCredits", style = HoloType.HeadlineLarge, color = HoloColors.Pulse)
                                Text("creditos", style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted)
                            }
                        }

                        // Barra de progreso al siguiente tier
                        if (nextTier != null) {
                            Spacer(Modifier.height(HoloSpacing.md))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${currentTierConfig?.name ?: ""}", style = HoloType.LabelSmall, color = color)
                                Text("${nextTier.name}", style = HoloType.LabelSmall, color = tierColor(nextTier.tier))
                            }
                            Spacer(Modifier.height(HoloSpacing.xxs))
                            LinearProgressIndicator(
                                progress = progressToNext,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = color,
                                trackColor = HoloColors.Neutral700
                            )
                            Spacer(Modifier.height(HoloSpacing.xxs))
                            Text("${nextTier.minPoints - userCredits} puntos para ${nextTier.name}", style = HoloType.BodySmall, color = HoloColors.TextOnDarkMuted)
                        } else {
                            Spacer(Modifier.height(HoloSpacing.sm))
                            Text("Has alcanzado el nivel maximo!", style = HoloType.TitleMedium, color = color)
                        }
                    }
                }
            }

            // PERKS
            item {
                Column(Modifier.fillMaxWidth().padding(vertical = HoloSpacing.lg), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EXCLUSIVE PERKS", style = HoloType.HeadlineLarge, color = HoloColors.TextPrimary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(HoloSpacing.xs))
                    Box(Modifier.width(32.dp).height(2.dp).background(HoloColors.Ink))
                }
            }

            item {
                val perks = listOf(
                    PerkData(Icons.Outlined.CheckCircle, "200 CREDITOS", "Al registrarte"),
                    PerkData(Icons.Outlined.Star, "ACCESO EXCLUSIVO", "A nuevos productos"),
                    PerkData(Icons.Outlined.CardGiftcard, "BIRTHDAY REWARDS", "Regalo de cumpleaños"),
                    PerkData(Icons.Outlined.CreditCard, "PUNTOS POR COMPRA", "Por cada euro gastado"),
                    PerkData(Icons.Outlined.People, "EVENTOS VIP", "Solo para miembros"),
                    PerkData(Icons.Outlined.EmojiEvents, "TIERED REWARDS", "Sube de nivel")
                )
                Column(Modifier.padding(horizontal = HoloSpacing.md)) {
                    perks.chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HoloSpacing.sm)) {
                            row.forEach { perk -> PerkCard(perk, Modifier.weight(1f)) }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(HoloSpacing.sm))
                    }
                }
            }

            // HOW IT WORKS
            item {
                Box(Modifier.fillMaxWidth().padding(top = HoloSpacing.xl).background(HoloColors.Ink).padding(vertical = HoloSpacing.xxl, horizontal = HoloSpacing.md)) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("COMO FUNCIONA", style = HoloType.HeadlineLarge, color = HoloColors.TextOnDark)
                        Spacer(Modifier.height(HoloSpacing.xxs))
                        Text("PASOS SENCILLOS HACIA TUS RECOMPENSAS", style = HoloType.LabelMedium, color = HoloColors.TextOnDarkMuted, letterSpacing = 2.sp)
                        Spacer(Modifier.height(HoloSpacing.xxl))
                        StepCard("01", "REGISTRATE GRATIS", "Registrate y recibe 200 creditos de bienvenida al instante.")
                        Spacer(Modifier.height(HoloSpacing.md))
                        StepCard("02", "COMPRA Y GANA", "Gana puntos con cada compra para subir de nivel.")
                        Spacer(Modifier.height(HoloSpacing.md))
                        StepCard("03", "CANJEA RECOMPENSAS", "Usa tus puntos para descuentos y productos exclusivos.")
                    }
                }
            }

            // TIERS DESDE LA BDD
            item {
                Column(Modifier.fillMaxWidth().padding(vertical = HoloSpacing.xxl), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MEMBERSHIP TIERS", style = HoloType.HeadlineLarge, color = HoloColors.TextPrimary)
                    Spacer(Modifier.height(HoloSpacing.xxs))
                    Text("ESCALA TU ESTATUS", style = HoloType.BodySmall, color = HoloColors.TextTertiary, letterSpacing = 1.sp)
                }
            }

            tiers.forEach { tier ->
                item {
                    val benefitsList = tier.benefits?.map { it.jsonPrimitive.content } ?: emptyList()
                    TierCard(
                        name = tier.name.uppercase(),
                        points = if (tier.maxPoints != null) "${tier.minPoints} - ${tier.maxPoints}" else "${tier.minPoints}+",
                        color = tierColor(tier.tier),
                        isCurrentTier = tier.tier == userTier,
                        benefits = benefitsList,
                        imageUrl = tier.imageUrl,
                        pointsPerEuro = tier.pointsPerEuro,
                        earlyAccessHours = tier.earlyAccessHours,
                        freeShippingThreshold = tier.freeShippingThreshold,
                        birthdayCredits = tier.birthdayBonusCredits
                    )
                    Spacer(Modifier.height(HoloSpacing.md))
                }
            }

            item { Spacer(Modifier.height(HoloSpacing.xl)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES
// ══════════════════════════════════════════════════════════════════════════════

data class PerkData(val icon: ImageVector, val title: String, val subtitle: String)

@Composable
fun PerkCard(perk: PerkData, modifier: Modifier = Modifier) {
    Card(modifier.height(140.dp), shape = RoundedCornerShape(HoloSpacing.RadiusLg), colors = CardDefaults.cardColors(containerColor = HoloColors.Fog), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.fillMaxSize().padding(HoloSpacing.md), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(perk.icon, null, tint = HoloColors.Ink, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(HoloSpacing.sm))
            Text(perk.title, style = HoloType.TitleMedium, color = HoloColors.TextPrimary, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text(perk.subtitle, style = HoloType.BodySmall, color = HoloColors.TextTertiary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun StepCard(number: String, title: String, description: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(HoloSpacing.RadiusMd), colors = CardDefaults.cardColors(containerColor = HoloColors.Paper), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(HoloSpacing.lg), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(number, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = HoloColors.Neutral200)
            Spacer(Modifier.height(HoloSpacing.xs))
            Text(title, style = HoloType.TitleLarge, color = HoloColors.TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(HoloSpacing.xxs))
            Text(description, style = HoloType.BodyMedium, color = HoloColors.TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun TierCard(
    name: String, points: String, color: Color, isCurrentTier: Boolean,
    benefits: List<String>, imageUrl: String? = null,
    pointsPerEuro: Double = 0.0, earlyAccessHours: Int = 0,
    freeShippingThreshold: Double = 0.0, birthdayCredits: Int = 0
) {
    val fullImageUrl = imageUrl?.let {
        if (it.startsWith("http")) it else "https://holo-crew.vercel.app$it"
    }

    Card(
        Modifier.fillMaxWidth().padding(horizontal = HoloSpacing.md).then(if (isCurrentTier) Modifier.border(2.dp, color, RoundedCornerShape(HoloSpacing.RadiusLg)) else Modifier),
        shape = RoundedCornerShape(HoloSpacing.RadiusLg),
        colors = CardDefaults.cardColors(containerColor = HoloColors.Paper),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(HoloSpacing.lg)) {

            // Imagen del tier
            if (fullImageUrl != null) {
                Box(
                    Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(HoloSpacing.RadiusMd)).background(HoloColors.Fog),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = fullImageUrl,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize().padding(HoloSpacing.md),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(Modifier.height(HoloSpacing.md))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(name, style = HoloType.HeadlineMedium, color = color, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    Text("REQUIRED POINTS", style = HoloType.LabelSmall, color = HoloColors.TextTertiary)
                    Text(points, style = HoloType.TitleLarge, color = HoloColors.TextPrimary)
                }
                if (isCurrentTier) {
                    Box(Modifier.clip(RoundedCornerShape(HoloSpacing.RadiusPill)).background(color.copy(alpha = 0.15f)).padding(horizontal = HoloSpacing.sm, vertical = HoloSpacing.xxs)) {
                        Text("TU NIVEL", style = HoloType.LabelSmall, color = color)
                    }
                }
            }

            Spacer(Modifier.height(HoloSpacing.md))
            Divider(color = HoloColors.Neutral100)
            Spacer(Modifier.height(HoloSpacing.md))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TierStat("${pointsPerEuro.toInt()} pts", "por 1€")
                if (earlyAccessHours > 0) TierStat("${earlyAccessHours}h", "early access")
                if (freeShippingThreshold <= 0) TierStat("GRATIS", "envio") else TierStat("+${freeShippingThreshold.toInt()}€", "envio gratis")
                if (birthdayCredits > 0) TierStat("$birthdayCredits", "birthday")
            }

            Spacer(Modifier.height(HoloSpacing.md))
            Divider(color = HoloColors.Neutral100)
            Spacer(Modifier.height(HoloSpacing.md))

            benefits.forEach { benefit ->
                Row(Modifier.padding(vertical = HoloSpacing.xxs), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(color))
                    Spacer(Modifier.width(HoloSpacing.sm))
                    Text(benefit.uppercase(), style = HoloType.BodySmall, color = HoloColors.TextSecondary, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}

@Composable
fun TierStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = HoloType.TitleMedium, color = HoloColors.TextPrimary, fontWeight = FontWeight.Bold)
        Text(label, style = HoloType.BodySmall, color = HoloColors.TextTertiary, fontSize = 10.sp)
    }
}