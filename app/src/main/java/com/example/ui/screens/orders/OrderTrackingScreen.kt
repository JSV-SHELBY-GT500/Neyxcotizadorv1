package com.example.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.components.JsvSectionHeader
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.JsvField
import com.example.ui.theme.JsvAmber
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvDarker
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvPurple
import com.example.ui.theme.JsvRed
import com.example.ui.theme.JsvSurface
import com.example.ui.theme.JsvTextBody
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun OrderTrackingScreen(
    viewModel: JsvViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    val metrics by viewModel.metrics.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("todos") }

    var orderToChangeStatus by remember { mutableStateOf<Order?>(null) }
    var orderToDelete by remember { mutableStateOf<Order?>(null) }

    val filteredOrders = orders.filter { order ->
        val matchesQuery = searchQuery.isBlank() ||
                order.id.contains(searchQuery, ignoreCase = true) ||
                order.vehiculo.contains(searchQuery, ignoreCase = true) ||
                order.pieza.contains(searchQuery, ignoreCase = true) ||
                order.clienteNombre.contains(searchQuery, ignoreCase = true)

        val matchesStatus = selectedStatusFilter == "todos" ||
                order.estado.equals(selectedStatusFilter, ignoreCase = true)

        matchesQuery && matchesStatus
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(JsvDarker)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            JsvSectionHeader("📊 Métricas y Rendimiento JSV")
            Spacer(modifier = Modifier.height(8.dp))

            // KPI Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricKpiCard(
                    title = "Total Cotizaciones",
                    value = metrics.totalQuotes.toString(),
                    subtitle = "Registros activos",
                    iconColor = JsvGold,
                    modifier = Modifier.weight(1f)
                )
                MetricKpiCard(
                    title = "Entregados",
                    value = "${metrics.deliveredCount} (${String.format("%.0f", metrics.deliveredPercent)}%)",
                    subtitle = "Conversión de venta",
                    iconColor = JsvGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricKpiCard(
                    title = "Modelo Top",
                    value = metrics.topModel.take(15),
                    subtitle = "Mayor demanda",
                    iconColor = JsvBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricKpiCard(
                    title = "Pieza Top",
                    value = metrics.topPiece.take(15),
                    subtitle = "Más cotizada",
                    iconColor = JsvAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Distribution by status bar
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Distribución de Estados:", color = JsvGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusMiniStat("Cotizado", metrics.statusCounts["cotizado"] ?: 0, JsvAmber)
                        StatusMiniStat("Pagado", metrics.statusCounts["pagado"] ?: 0, JsvGreen)
                        StatusMiniStat("En Envío", metrics.statusCounts["envio"] ?: 0, JsvBlue)
                        StatusMiniStat("Entregado", metrics.statusCounts["entregado"] ?: 0, JsvPurple)
                        StatusMiniStat("Cancelado", metrics.statusCounts["cancelado"] ?: 0, JsvRed)
                    }
                }
            }
        }

        // Search & Filter Row
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar pedido por Folio, Vehículo, Pieza o Cliente...", fontSize = 10.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JsvGold) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsvGold,
                    unfocusedBorderColor = JsvBorder,
                    focusedTextColor = JsvTextWhite,
                    unfocusedTextColor = JsvTextWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val filters = listOf("todos", "cotizado", "pagado", "envio", "entregado", "cancelado")
                items(filters) { f ->
                    FilterChip(
                        selected = selectedStatusFilter == f,
                        onClick = { selectedStatusFilter = f },
                        label = { Text(f.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JsvGold,
                            selectedLabelColor = Color.Black,
                            containerColor = JsvSurface,
                            labelColor = JsvTextMuted
                        )
                    )
                }
            }
        }

        // Orders List
        if (filteredOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron pedidos con los criterios actuales.\nGuarda cotizaciones desde el Editor para gestionarlas aquí.",
                        color = JsvTextMuted,
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredOrders, key = { it.id }) { order ->
                OrderItemCard(
                    order = order,
                    onLoadIntoEditor = { viewModel.loadOrderIntoEditor(order) },
                    onChangeStatus = { orderToChangeStatus = order },
                    onGenerateWarranty = {
                        viewModel.createWarrantyFromQuote(
                            clienteNombre = order.clienteNombre,
                            clienteTelefono = order.clienteTelefono,
                            clienteDireccion = order.clienteDireccion
                        )
                    },
                    onDelete = { orderToDelete = order }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Change Status Dialog
    orderToChangeStatus?.let { order ->
        var selectedStatus by remember { mutableStateOf(order.estado) }
        var trackingCode by remember { mutableStateOf(order.guiaRastreo) }
        var observations by remember { mutableStateOf(order.observaciones) }

        AlertDialog(
            onDismissRequest = { orderToChangeStatus = null },
            containerColor = JsvSurface,
            title = { Text("Actualizar Estado de Pedido", color = JsvGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pedido: ${order.id}", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("Selecciona el nuevo estatus en la cadena operativa:", color = JsvTextMuted, fontSize = 10.sp)

                    val statuses = listOf("cotizado", "pagado", "envio", "entregado", "cancelado")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        statuses.forEach { s ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedStatus == s) JsvGold else JsvDarkElevated)
                                    .clickable { selectedStatus = s }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    s.uppercase(),
                                    color = if (selectedStatus == s) Color.Black else JsvTextMuted,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    JsvField(
                        label = "Guía de Rastreo / Paquetería",
                        value = trackingCode,
                        onValueChange = { trackingCode = it }
                    )

                    JsvField(
                        label = "Observaciones de Entrega",
                        value = observations,
                        onValueChange = { observations = it },
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateOrderStatus(order.id, selectedStatus, trackingCode, observations)
                        orderToChangeStatus = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold)
                ) {
                    Text("GUARDAR ESTADO", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToChangeStatus = null }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            containerColor = JsvSurface,
            title = { Text("Eliminar Pedido", color = JsvRed, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar el pedido #${order.id}? Esta acción no se puede deshacer.", color = JsvTextBody) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteOrder(order.id)
                        orderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvRed)
                ) {
                    Text("ELIMINAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }
}

@Composable
fun OrderItemCard(
    order: Order,
    onLoadIntoEditor: () -> Unit,
    onChangeStatus: () -> Unit,
    onGenerateWarranty: () -> Unit,
    onDelete: () -> Unit
) {
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
                Column {
                    Text(order.id, color = JsvBlue, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    Text(order.fechaCreacion, color = JsvTextMuted, fontSize = 8.5.sp)
                }
                StatusBadge(order.estado)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(order.titulo, color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)

            if (order.clienteNombre.isNotBlank()) {
                Text("Cliente: ${order.clienteNombre} ${if (order.clienteTelefono.isNotBlank()) "(${order.clienteTelefono})" else ""}", color = JsvTextBody, fontSize = 9.5.sp)
            }

            if (order.guiaRastreo.isNotBlank()) {
                Text("Guía Rastreo: ${order.guiaRastreo}", color = JsvGold, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monto: $${String.format("%,.0f", order.montoTotal)} MXN",
                    color = JsvGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onLoadIntoEditor, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Cargar en Editor", tint = JsvGold, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onChangeStatus, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Assignment, contentDescription = "Cambiar Estado", tint = JsvGreen, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onGenerateWarranty, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Security, contentDescription = "Generar Garantía", tint = JsvBlue, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = JsvRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusMiniStat(name: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 24.dp, height = 4.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(count.toString(), color = JsvTextWhite, fontWeight = FontWeight.Black, fontSize = 11.sp)
        Text(name, color = JsvTextMuted, fontSize = 8.sp)
    }
}
