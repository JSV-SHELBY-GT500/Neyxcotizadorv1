package com.example.ui.screens.audit

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLog
import com.example.ui.theme.JsvAmber
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvOnPrimary
import com.example.ui.theme.JsvPurple
import com.example.ui.theme.JsvRed
import com.example.ui.theme.JsvSurface
import com.example.ui.theme.JsvTextBody
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun AuditControlHubScreen(viewModel: JsvViewModel) {
    val context = LocalContext.current
    val allLogs by viewModel.allAuditLogs.collectAsState()
    val totalCount by viewModel.totalAuditLogsCount.collectAsState()
    val memoryCount by viewModel.memoryAccessCount.collectAsState()
    val searchQuery by viewModel.auditSearchQuery.collectAsState()
    val categoryFilter by viewModel.auditCategoryFilter.collectAsState()
    val severityFilter by viewModel.auditSeverityFilter.collectAsState()

    var selectedLogForDetails by remember { mutableStateOf<AuditLog?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    // Filter logs based on search, category and severity
    val filteredLogs = remember(allLogs, searchQuery, categoryFilter, severityFilter) {
        allLogs.filter { log ->
            val matchesQuery = searchQuery.isBlank() ||
                    log.accion.contains(searchQuery, ignoreCase = true) ||
                    log.entidad.contains(searchQuery, ignoreCase = true) ||
                    log.detalles.contains(searchQuery, ignoreCase = true) ||
                    log.hashIntegridad.contains(searchQuery, ignoreCase = true)

            val matchesCategory = categoryFilter == "ALL" || log.categoria.equals(categoryFilter, ignoreCase = true)
            val matchesSeverity = severityFilter == "ALL" || log.severidad.equals(severityFilter, ignoreCase = true)

            matchesQuery && matchesCategory && matchesSeverity
        }
    }

    val warningCount = remember(allLogs) { allLogs.count { it.severidad == "WARNING" || it.severidad == "SECURITY" || it.severidad == "CRITICAL" } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Top Header Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuditMetricCard(
                modifier = Modifier.weight(1f),
                title = "Total Eventos",
                value = "$totalCount",
                icon = Icons.Default.Security,
                color = JsvGold
            )
            AuditMetricCard(
                modifier = Modifier.weight(1f),
                title = "Accesos Memoria",
                value = "$memoryCount",
                icon = Icons.Default.Storage,
                color = JsvBlue
            )
            AuditMetricCard(
                modifier = Modifier.weight(1f),
                title = "Alertas / Sec",
                value = "$warningCount",
                icon = Icons.Default.Warning,
                color = if (warningCount > 0) JsvAmber else JsvGreen
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar & Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setAuditSearchQuery(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Buscar acción, entidad o hash...", color = JsvTextMuted, fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JsvGold, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsvGold,
                    unfocusedBorderColor = JsvBorder,
                    focusedTextColor = JsvTextWhite,
                    unfocusedTextColor = JsvTextBody,
                    focusedContainerColor = JsvSurface,
                    unfocusedContainerColor = JsvSurface
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    val report = viewModel.exportAuditReport()
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, report)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Exportar Reporte Forense JSV"))
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(JsvSurface)
                    .border(1.dp, JsvBorder, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Share, contentDescription = "Exportar Reporte", tint = JsvGold, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(JsvSurface)
                    .border(1.dp, JsvBorder, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Limpiar Logs", tint = JsvRed, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val categories = listOf(
                "ALL" to "Todos",
                "MEMORIA_LOCAL" to "💾 Memoria Local",
                "USUARIO" to "👤 Usuario",
                "MODULOS" to "🧩 Módulos",
                "NOTIFICACIONES" to "🔔 Alertas",
                "OCR" to "🔍 OCR",
                "SISTEMA" to "⚙️ Sistema"
            )
            items(categories) { (catKey, catLabel) ->
                val selected = categoryFilter == catKey
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.setAuditCategoryFilter(catKey) },
                    label = { Text(catLabel, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JsvGold,
                        selectedLabelColor = JsvOnPrimary,
                        containerColor = JsvSurface,
                        labelColor = JsvTextBody
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (selected) JsvGold else JsvBorder,
                        selectedBorderColor = JsvGold,
                        enabled = true,
                        selected = selected
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Audit Logs List
        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(JsvSurface)
                    .border(1.dp, JsvBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = JsvTextMuted, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No se encontraron registros de auditoría", color = JsvTextMuted, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(filteredLogs, key = { it.id }) { log ->
                    AuditLogRowCard(
                        log = log,
                        onClick = { selectedLogForDetails = log }
                    )
                }
            }
        }
    }

    // Detail Inspector Dialog
    selectedLogForDetails?.let { log ->
        AlertDialog(
            onDismissRequest = { selectedLogForDetails = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DataObject, contentDescription = null, tint = JsvGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detalle Forense #${log.id}", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    InspectorFieldRow("Acción", log.accion)
                    InspectorFieldRow("Entidad / ID", "${log.entidad} (#${log.entidadId})")
                    InspectorFieldRow("Categoría", log.categoria)
                    InspectorFieldRow("Severidad", log.severidad)
                    InspectorFieldRow("Memoria Local", log.memoriaTipo)
                    InspectorFieldRow("Fecha / Hora", log.fecha)
                    InspectorFieldRow("Dispositivo", log.dispositivo)
                    InspectorFieldRow("Latencia", "${log.tiempoEjecucionMs} ms")
                    InspectorFieldRow("Hash Integridad", log.hashIntegridad)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Detalles de Operación:", color = JsvGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(JsvDarkElevated)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = log.detalles,
                            color = JsvTextWhite,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedLogForDetails = null },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CERRAR", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            containerColor = JsvSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Clear Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = JsvRed, modifier = Modifier.size(32.dp)) },
            title = { Text("¿Purgar Auditoría?", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = { Text("Esta acción eliminará todos los registros forenses de la memoria local SQLite.", color = JsvTextBody, fontSize = 12.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAuditLogs()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("PURGAR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("CANCELAR", color = JsvTextMuted, fontSize = 11.sp)
                }
            },
            containerColor = JsvSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun AuditMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = JsvSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = JsvTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = JsvTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun AuditLogRowCard(
    log: AuditLog,
    onClick: () -> Unit
) {
    val (badgeBg, badgeText, badgeIcon) = when (log.severidad) {
        "MEMORY_ACCESS" -> Triple(JsvBlue.copy(alpha = 0.2f), JsvBlue, Icons.Default.Storage)
        "WARNING" -> Triple(JsvAmber.copy(alpha = 0.2f), JsvAmber, Icons.Default.Warning)
        "SECURITY" -> Triple(JsvPurple.copy(alpha = 0.2f), JsvPurple, Icons.Default.Security)
        "CRITICAL" -> Triple(JsvRed.copy(alpha = 0.2f), JsvRed, Icons.Default.Warning)
        else -> Triple(JsvGold.copy(alpha = 0.2f), JsvGold, Icons.Default.Code)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = JsvSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(badgeIcon, contentDescription = null, tint = badgeText, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(log.accion, color = badgeText, fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    if (log.memoriaTipo != "N/A") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(JsvDarkElevated)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(log.memoriaTipo, color = JsvTextMuted, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Text(log.fecha.takeLast(8), color = JsvTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(log.detalles, color = JsvTextWhite, fontSize = 10.5.sp, maxLines = 2)

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Entidad: ${log.entidad} (#${log.entidadId})", color = JsvTextBody, fontSize = 8.5.sp)
                Text("SHA: ${log.hashIntegridad} • ${log.tiempoEjecucionMs}ms", color = JsvTextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun InspectorFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = JsvTextMuted, fontSize = 10.sp)
        Text(value, color = JsvTextWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
