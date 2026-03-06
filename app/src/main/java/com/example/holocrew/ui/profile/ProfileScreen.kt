package com.example.holocrew.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar y datos del usuario
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEEEEE))
                            .border(2.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Avatar",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Holo User",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "holocrew@example.com",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Estadísticas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(value = "12", label = "Pedidos")
                        Divider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = Color(0xFFEEEEEE)
                        )
                        StatItem(value = "5", label = "Favoritos")
                        Divider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = Color(0xFFEEEEEE)
                        )
                        StatItem(value = "2", label = "Pendientes")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Sección Pedidos recientes
            item {
                ProfileSectionTitle("PEDIDOS RECIENTES")
                ProfileMenuItem(
                    icon = Icons.Filled.ShoppingBag,
                    title = "Holo Pannel Hoodie",
                    subtitle = "Entregado · 15 Nov 2024",
                    showChevron = true
                )
                ProfileMenuItem(
                    icon = Icons.Filled.ShoppingBag,
                    title = "Denim Bison Holo",
                    subtitle = "En camino · 20 Nov 2024",
                    showChevron = true,
                    badgeText = "En camino",
                    badgeColor = Color(0xFF2196F3)
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Sección Métodos de pago
            item {
                ProfileSectionTitle("MÉTODOS DE PAGO")
                ProfileMenuItem(
                    icon = Icons.Filled.CreditCard,
                    title = "Tarjeta terminada en 4242",
                    subtitle = "Visa · Expira 12/26",
                    showChevron = true
                )
                ProfileMenuItem(
                    icon = Icons.Filled.Add,
                    title = "Añadir método de pago",
                    subtitle = "",
                    showChevron = false,
                    titleColor = Color(0xFF2196F3)
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Sección Dirección de envío
            item {
                ProfileSectionTitle("DIRECCIÓN DE ENVÍO")
                ProfileMenuItem(
                    icon = Icons.Filled.LocationOn,
                    title = "Casa",
                    subtitle = "Calle Ejemplo 123, Madrid, 28001",
                    showChevron = true
                )
                ProfileMenuItem(
                    icon = Icons.Filled.Add,
                    title = "Añadir dirección",
                    subtitle = "",
                    showChevron = false,
                    titleColor = Color(0xFF2196F3)
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Sección Ajustes
            item {
                ProfileSectionTitle("AJUSTES")
                ProfileMenuItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notificaciones",
                    subtitle = "Activadas",
                    showChevron = true
                )
                ProfileMenuItem(
                    icon = Icons.Filled.Lock,
                    title = "Privacidad y seguridad",
                    subtitle = "",
                    showChevron = true
                )
                ProfileMenuItem(
                    icon = Icons.Filled.Info,
                    title = "Sobre HoloCrew",
                    subtitle = "Versión 1.0.0",
                    showChevron = true
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Cerrar sesión
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { /* placeholder */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Cerrar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE53935),
                            modifier = Modifier.padding(start = 14.dp)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF9E9E9E),
        modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showChevron: Boolean,
    titleColor: Color = Color.Black,
    badgeText: String? = null,
    badgeColor: Color = Color.Transparent
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clickable { /* placeholder */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con fondo
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Badge opcional
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (showChevron) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFCCCCCC),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}