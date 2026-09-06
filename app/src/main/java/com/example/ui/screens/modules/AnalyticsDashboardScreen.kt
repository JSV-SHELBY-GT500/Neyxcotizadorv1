package com.example.ui.screens.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun AnalyticsDashboardScreen(viewModel: JsvViewModel) {
    val partsList by viewModel.filteredAutoParts.collectAsState()
    val ordersList by viewModel.allOrders.collectAsState()
    val warrantiesList by viewModel.allWarranties.collectAsState()

    val totalPartsCount = partsList.size
    val totalOrdersCount = ordersList.size
    val totalWarrantiesCount = warrantiesList.size
    val totalInventoryValueUsd = partsList.sumOf { it.precio * it.stockDisponible }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = BorderStroke(1.dp, JsvGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = "KPIs", tint = JsvGold, modifier = Modifier.size(24.dp))
                        Text(text = "DASHBOARD EJECUTIVO & ANALYTICS — JSV", style = FugazHeavyBrandStyle.copy(fontSize = 13.sp, color = JsvGold))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Métricas en tiempo real del inventario de autopartes, volumen de pedidos logísticos, pólizas de garantía activas y valoración global de la plataforma.",
                        color = JsvTextBody,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Autopartes",
                    value = "$totalPartsCount",
                    icon = Icons.Default.Inventory
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Pedidos",
                    value = "$totalOrdersCount",
                    icon = Icons.Default.LocalShipping
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Garantías",
                    value = "$totalWarrantiesCount",
                    icon = Icons.Default.VerifiedUser
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Valor Inventario",
                    value = "$${String.format("%.0f", totalInventoryValueUsd)}",
                    icon = Icons.Default.BarChart
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvDarkElevated),
                border = BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen de Rendimiento de Módulos", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    AnalyticsBarItem(label = "Catálogo de Autopartes & Stock", percentage = 0.85, color = JsvGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    AnalyticsBarItem(label = "Gestión de Pedidos & Logística", percentage = 0.70, color = JsvBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    AnalyticsBarItem(label = "Escáner OCR & IA Batch", percentage = 0.90, color = JsvGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    AnalyticsBarItem(label = "Pólizas de Garantía & Vales", percentage = 0.95, color = JsvGold)
                }
            }
        }
    }
}

@Composable
fun KpiCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = JsvDarkElevated),
        border = BorderStroke(1.dp, JsvBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = JsvTextBody, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = title, tint = JsvGold, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = JsvTextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AnalyticsBarItem(label: String, percentage: Double, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = JsvTextBody, fontSize = 10.sp)
            Text("${(percentage * 100).toInt()}%", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = JsvDarker
        )
    }
}
