package com.example.ui.screens.notifications

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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
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
import com.example.ui.viewmodel.JsvTab
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun NotificationsScreen(viewModel: JsvViewModel) {
    val allNotifications by viewModel.allNotifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

    var categoryFilter by remember { mutableStateOf("ALL") }
    var showSendPushDialog by remember { mutableStateOf(false) }

    val filteredNotifications = remember(allNotifications, categoryFilter) {
        if (categoryFilter == "ALL") allNotifications
        else allNotifications.filter { it.category.equals(categoryFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Hub Top Bar Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(JsvGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = JsvGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Centro de Alertas Push", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (unreadCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(JsvRed)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text("$unreadCount nuevas", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                        Text("Sincronización en memoria local SQLite", color = JsvTextMuted, fontSize = 9.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showSendPushDialog = true },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(JsvDarkElevated)
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = "Simular Alerta", tint = JsvGold, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.markAllNotificationsAsRead() },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(JsvDarkElevated)
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = "Marcar todas leídas", tint = JsvGreen, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.clearAllNotifications() },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(JsvDarkElevated)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Limpiar Todo", tint = JsvRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val categories = listOf(
                "ALL" to "Todas",
                "PEDIDO" to "📦 Pedidos",
                "GARANTIA" to "🛡️ Garantías",
                "OCR" to "🔍 OCR",
                "SEGURIDAD" to "🔒 Seguridad",
                "MODULOS" to "🧩 Módulos",
                "SISTEMA" to "⚙️ Sistema"
            )
            items(categories) { (catKey, catLabel) ->
                val selected = categoryFilter == catKey
                FilterChip(
                    selected = selected,
                    onClick = { categoryFilter = catKey },
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

        Spacer(modifier = Modifier.height(10.dp))

        // Notifications List
        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(JsvSurface)
                    .border(1.dp, JsvBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = JsvTextMuted, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay alertas pendientes en esta categoría", color = JsvTextMuted, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { item ->
                    NotificationCard(
                        item = item,
                        onMarkRead = { viewModel.markNotificationAsRead(item.id) },
                        onDelete = { viewModel.deleteNotification(item.id) },
                        onNavigate = { route ->
                            viewModel.markNotificationAsRead(item.id)
                            when (route.uppercase()) {
                                "EDITOR" -> viewModel.selectTab(JsvTab.EDITOR)
                                "SEGUIMIENTO" -> viewModel.selectTab(JsvTab.SEGUIMIENTO)
                                "GARANTIA" -> viewModel.selectTab(JsvTab.GARANTIA)
                                "SCANNER_OCR" -> viewModel.selectTab(JsvTab.SCANNER_OCR)
                                "AUDITORIA" -> viewModel.selectTab(JsvTab.AUDITORIA)
                                "MODULOS" -> viewModel.selectTab(JsvTab.MODULOS)
                                else -> viewModel.selectTab(JsvTab.EDITOR)
                            }
                        }
                    )
                }
            }
        }
    }

    // Push Notification Simulator Dialog
    if (showSendPushDialog) {
        var pushTitle by remember { mutableStateOf("⚡ Alerta de Stock Crítico") }
        var pushMessage by remember { mutableStateOf("El stock de Transmisión CVT Versa ha llegado a 1 unidad disponible.") }
        var pushCategory by remember { mutableStateOf("PEDIDO") }
        var pushPriority by remember { mutableStateOf("ALTA") }
        var pushRoute by remember { mutableStateOf("SEGUIMIENTO") }

        AlertDialog(
            onDismissRequest = { showSendPushDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddAlert, contentDescription = null, tint = JsvGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simular Push Notification", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Emite una notificación push en tiempo real sincronizada con la base de datos local SQLite.", color = JsvTextBody, fontSize = 10.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pushTitle,
                        onValueChange = { pushTitle = it },
                        label = { Text("Título de la Alerta", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedTextColor = JsvTextWhite,
                            unfocusedTextColor = JsvTextBody
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = pushMessage,
                        onValueChange = { pushMessage = it },
                        label = { Text("Cuerpo del Mensaje", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedTextColor = JsvTextWhite,
                            unfocusedTextColor = JsvTextBody
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("PEDIDO", "GARANTIA", "OCR", "SEGURIDAD").forEach { cat ->
                            val selected = pushCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) JsvGold else JsvDarkElevated)
                                    .clickable {
                                        pushCategory = cat
                                        pushRoute = when (cat) {
                                            "PEDIDO" -> "SEGUIMIENTO"
                                            "GARANTIA" -> "GARANTIA"
                                            "OCR" -> "SCANNER_OCR"
                                            else -> "AUDITORIA"
                                        }
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(cat, color = if (selected) JsvOnPrimary else JsvTextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerTestPushNotification(
                            category = pushCategory,
                            title = pushTitle,
                            message = pushMessage,
                            priority = pushPriority,
                            route = pushRoute
                        )
                        showSendPushDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("DISPARAR PUSH", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSendPushDialog = false }) {
                    Text("CANCELAR", color = JsvTextMuted, fontSize = 11.sp)
                }
            },
            containerColor = JsvSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItem,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val (catBg, catColor, catIcon) = when (item.category.uppercase()) {
        "PEDIDO" -> Triple(JsvBlue.copy(alpha = 0.15f), JsvBlue, Icons.Default.LocalShipping)
        "GARANTIA" -> Triple(JsvPurple.copy(alpha = 0.15f), JsvPurple, Icons.Default.VerifiedUser)
        "OCR" -> Triple(JsvGold.copy(alpha = 0.15f), JsvGold, Icons.Default.DocumentScanner)
        "SEGURIDAD" -> Triple(JsvRed.copy(alpha = 0.15f), JsvRed, Icons.Default.Security)
        else -> Triple(JsvGreen.copy(alpha = 0.15f), JsvGreen, Icons.Default.Notifications)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMarkRead() },
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) JsvSurface else JsvSurface.copy(alpha = 0.9f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!item.isRead) JsvGold.copy(alpha = 0.6f) else JsvBorder
        ),
        shape = RoundedCornerShape(14.dp)
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
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(catBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(catIcon, contentDescription = null, tint = catColor, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.title, color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            if (!item.isRead) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(JsvGold)
                                )
                            }
                        }
                        Text(item.timestamp, color = JsvTextMuted, fontSize = 8.5.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.priority == "ALTA" || item.priority == "CRITICA") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(JsvRed.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(item.priority, color = JsvRed, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = JsvTextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(item.message, color = JsvTextBody, fontSize = 10.sp)

            if (item.actionRoute.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onNavigate(item.actionRoute) },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("VER EN ${item.actionRoute}", color = JsvOnPrimary, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}
