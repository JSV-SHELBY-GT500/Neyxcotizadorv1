package com.example.ui.screens.system

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.components.JsvSectionHeader
import com.example.ui.theme.JsvAmber
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvDarker
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
fun AuditAndSyncScreen(
    viewModel: JsvViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val auditLogs by viewModel.allAuditLogs.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val vehicles by viewModel.allVehicles.collectAsState()
    val warranties by viewModel.allWarranties.collectAsState()
    val photos by viewModel.allPhotos.collectAsState()
    val quoteState by viewModel.quoteState.collectAsState()

    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var importContent by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(JsvDarker)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            JsvSectionHeader("⚡ Sincronización y Memoria Local Room")
            Spacer(modifier = Modifier.height(8.dp))

            // Local DB Stats
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = JsvGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Base de Datos SQLite / Room", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(JsvGreen)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("LOCAL PERSISTENTE", color = Color.Black, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DbStatItem("Pedidos", orders.size.toString(), JsvBlue)
                        DbStatItem("Vehículos", vehicles.size.toString(), JsvGold)
                        DbStatItem("Garantías", warranties.size.toString(), JsvGreen)
                        DbStatItem("Fotos", photos.size.toString(), JsvPurple)
                        DbStatItem("Eventos", auditLogs.size.toString(), JsvAmber)
                    }
                }
            }
        }

        // Fast Sync Hub
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🔄 Exportación e Importación de Alta Fidelidad", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Garantiza la sincronización inmediata entre dispositivos mediante archivos Markdown estructurados con JSON auditable embebido.",
                        color = JsvTextMuted,
                        fontSize = 9.5.sp,
                        lineHeight = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showExportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("EXPORTAR MD/JSON", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }

                        Button(
                            onClick = { showImportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = JsvBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("IMPORTAR MD/JSON", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // Live Audit Log
        item {
            Spacer(modifier = Modifier.height(6.dp))
            JsvSectionHeader(
                title = "📜 Registro de Auditoría y Trazabilidad",
                trailingContent = {
                    Text("${auditLogs.size} logs", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            )
        }

        if (auditLogs.isEmpty()) {
            item {
                Text(
                    text = "No hay registros de auditoría aún.",
                    color = JsvTextMuted,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(auditLogs, key = { it.id }) { log ->
                AuditLogCard(log = log)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Export Dialog
    if (showExportDialog) {
        val markdownText = viewModel.exportMarkdown()
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = JsvSurface,
            title = { Text("Exportar Cotización Completa", color = JsvGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Markdown estructurado compatible con JSV Desktop, Web y Android:", color = JsvTextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(JsvDarkElevated, RoundedCornerShape(6.dp))
                            .border(1.dp, JsvBorder, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = markdownText,
                            color = JsvTextWhite,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Cotización JSV", markdownText)
                            clipboard.setPrimaryClip(clip)
                            showExportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvGold)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("COPIAR", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, markdownText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Compartir Archivo Markdown JSV"))
                            showExportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvGreen)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("COMPARTIR", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("CERRAR", color = JsvTextMuted)
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            containerColor = JsvSurface,
            title = { Text("Importar Cotización Markdown / JSON", color = JsvBlue, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Pega el texto Markdown o JSON exportado de cualquier versión de JSV Studio:", color = JsvTextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = importContent,
                        onValueChange = { importContent = it },
                        label = { Text("Pega aquí el contenido...", fontSize = 10.sp) },
                        maxLines = 10,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvBlue,
                            unfocusedBorderColor = JsvBorder,
                            focusedTextColor = JsvTextWhite,
                            unfocusedTextColor = JsvTextWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.importMarkdownText(importContent)
                        if (success) {
                            showImportDialog = false
                            importContent = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvBlue)
                ) {
                    Text("IMPORTAR Y APLICAR", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }
}

@Composable
fun DbStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 14.sp)
        Text(label, color = JsvTextMuted, fontSize = 8.sp)
    }
}

@Composable
fun AuditLogCard(log: AuditLog) {
    val badgeColor = when (log.accion) {
        "PEDIDO_GUARDADO" -> JsvGreen
        "ESTADO_ACTUALIZADO" -> JsvBlue
        "GARANTIA_EMITIDA" -> JsvGold
        "IMPORTACION_MD" -> JsvPurple
        "VEHICULO_AGREGADO" -> JsvAmber
        else -> JsvTextMuted
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = JsvSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(badgeColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(log.accion, color = Color.Black, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                }
                Text(log.fecha, color = JsvTextMuted, fontSize = 8.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = log.detalles,
                color = JsvTextWhite,
                fontSize = 9.5.sp,
                lineHeight = 13.sp
            )

            if (log.entidadId.isNotBlank()) {
                Text(
                    text = "Ref: ${log.entidad} #${log.entidadId}",
                    color = JsvGold,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
