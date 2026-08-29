package com.example.ui.screens.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DynamicModule
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
fun DynamicModuleManagerScreen(viewModel: JsvViewModel) {
    val modules by viewModel.allDynamicModules.collectAsState()
    var selectedModuleForInfo by remember { mutableStateOf<DynamicModule?>(null) }
    var isSyncingRemote by remember { mutableStateOf(false) }

    val activeCount = remember(modules) { modules.count { it.isEnabled && it.isInstalled } }
    val totalInstalledSize = remember(modules) { modules.filter { it.isInstalled }.sumOf { it.sizeKb } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Module Manager Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(JsvGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Extension, contentDescription = null, tint = JsvGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Gestor Dinámico de Módulos", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Hot-Swap y carga remota de plugins", color = JsvTextMuted, fontSize = 9.sp)
                        }
                    }

                    Button(
                        onClick = {
                            isSyncingRemote = true
                            viewModel.triggerTestPushNotification(
                                category = "MODULOS",
                                title = "🧩 Repositorio Sincronizado",
                                message = "11 módulos verificados y actualizados desde el CDN central."
                            )
                            isSyncingRemote = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SYNC CDN", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModuleStatPill(modifier = Modifier.weight(1f), label = "Activos", value = "$activeCount / ${modules.size}", color = JsvGreen)
                    ModuleStatPill(modifier = Modifier.weight(1f), label = "Memoria RAM", value = "${totalInstalledSize / 1024} MB", color = JsvBlue)
                    ModuleStatPill(modifier = Modifier.weight(1f), label = "Estado Core", value = "HOT-SWAP OK", color = JsvGold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Modules List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(modules, key = { it.id }) { module ->
                DynamicModuleRowCard(
                    module = module,
                    onToggleEnabled = { enabled -> viewModel.toggleDynamicModule(module.id, enabled) },
                    onInstallToggle = { installed -> viewModel.installOrUninstallModule(module.id, installed) },
                    onInfoClick = { selectedModuleForInfo = module }
                )
            }
        }
    }

    // Module Info Modal
    selectedModuleForInfo?.let { mod ->
        AlertDialog(
            onDismissRequest = { selectedModuleForInfo = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(JsvGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(getModuleIcon(mod.iconName), contentDescription = null, tint = JsvGold, modifier = Modifier.size(24.dp))
                }
            },
            title = {
                Text(mod.name, color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(mod.description, color = JsvTextBody, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    ModuleInfoRow("Identificador", mod.id)
                    ModuleInfoRow("Versión", mod.version)
                    ModuleInfoRow("Categoría", mod.category)
                    ModuleInfoRow("Tamaño", "${mod.sizeKb} KB")
                    ModuleInfoRow("Permisos", mod.permissions)
                    ModuleInfoRow("Dependencias", mod.dependencies)
                    ModuleInfoRow("Endpoint CDN", mod.remoteEndpoint)
                    ModuleInfoRow("Última Sincronización", mod.lastSync)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedModuleForInfo = null },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ENTENDIDO", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            containerColor = JsvSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun DynamicModuleRowCard(
    module: DynamicModule,
    onToggleEnabled: (Boolean) -> Unit,
    onInstallToggle: (Boolean) -> Unit,
    onInfoClick: () -> Unit
) {
    val isEnabled = module.isEnabled && module.isInstalled

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) JsvSurface else JsvSurface.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled) JsvBorder else JsvBorder.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isEnabled) JsvGold.copy(alpha = 0.15f) else JsvDarkElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        getModuleIcon(module.iconName),
                        contentDescription = null,
                        tint = if (isEnabled) JsvGold else JsvTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(module.name, color = if (isEnabled) JsvTextWhite else JsvTextMuted, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        if (module.isCore) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(JsvPurple.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("CORE", color = JsvPurple, fontSize = 7.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(module.description, color = JsvTextBody, fontSize = 9.5.sp, maxLines = 1)

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("v${module.version}", color = JsvGold, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text(" • ", color = JsvTextMuted, fontSize = 8.sp)
                        Text("${module.sizeKb} KB", color = JsvTextMuted, fontSize = 8.sp)
                        Text(" • ", color = JsvTextMuted, fontSize = 8.sp)
                        Text(module.category, color = JsvBlue, fontSize = 8.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onInfoClick, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Info, contentDescription = "Detalles", tint = JsvTextMuted, modifier = Modifier.size(16.dp))
                }

                if (!module.isInstalled) {
                    Button(
                        onClick = { onInstallToggle(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DESCARGAR", color = JsvOnPrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Switch(
                        checked = module.isEnabled,
                        onCheckedChange = { onToggleEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JsvOnPrimary,
                            checkedTrackColor = JsvGold,
                            uncheckedThumbColor = JsvTextMuted,
                            uncheckedTrackColor = JsvDarkElevated
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleStatPill(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(JsvDarkElevated)
            .padding(vertical = 4.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = JsvTextMuted, fontSize = 8.sp)
            Text(value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ModuleInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = JsvTextMuted, fontSize = 9.5.sp)
        Text(value, color = JsvTextWhite, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun getModuleIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "edit" -> Icons.Default.Edit
        "local_shipping" -> Icons.Default.LocalShipping
        "verified_user" -> Icons.Default.VerifiedUser
        "directions_car" -> Icons.Default.DirectionsCar
        "photo_camera" -> Icons.Default.PhotoCamera
        "public" -> Icons.Default.Public
        "document_scanner" -> Icons.Default.DocumentScanner
        "security" -> Icons.Default.Security
        "notifications" -> Icons.Default.Notifications
        "memory" -> Icons.Default.Memory
        "receipt_long" -> Icons.Default.ReceiptLong
        else -> Icons.Default.Extension
    }
}
