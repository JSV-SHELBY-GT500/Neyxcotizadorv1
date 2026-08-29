package com.example.ui.screens.autoparts

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutoPart
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvDarker
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvRed
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.viewmodel.JsvTab
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun AutoPartsCatalogScreen(viewModel: JsvViewModel) {
    val autoParts by viewModel.filteredAutoParts.collectAsState()
    val searchQuery by viewModel.autoPartSearchQuery.collectAsState()
    val selectedCategory by viewModel.autoPartSelectedCategory.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPart by remember { mutableStateOf<AutoPart?>(null) }

    val categories = listOf("Todas", "Transmisión", "Motor", "A/C", "Frenos", "Suspensión", "Electrónica")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDarker)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Info & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📦 CATÁLOGO DE AUTOPARTES",
                        color = JsvGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Inventario sincronizado Room • ${autoParts.size} piezas disponibles",
                        color = JsvTextMuted,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                    modifier = Modifier.testTag("btn_nueva_autoparte")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = JsvDarker)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Nueva Pieza", color = JsvDarker, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setAutoPartSearchQuery(it) },
                placeholder = { Text("Buscar por nombre, número OEM, categoría o vehículo...", color = JsvTextMuted) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = JsvGold) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsvGold,
                    unfocusedBorderColor = JsvBorder,
                    focusedTextColor = JsvTextWhite,
                    unfocusedTextColor = JsvTextWhite,
                    cursorColor = JsvGold,
                    focusedContainerColor = JsvDark,
                    unfocusedContainerColor = JsvDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_autoparts_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                    Surface(
                        color = if (isSelected) JsvGold else JsvDark,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) JsvGold else JsvBorder),
                        modifier = Modifier
                            .clickable { viewModel.setAutoPartCategory(cat) }
                            .testTag("chip_cat_$cat")
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) JsvDarker else JsvTextWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // List of Autoparts
            if (autoParts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = JsvTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No se encontraron autopartes",
                            color = JsvTextMuted,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("autoparts_list")
                ) {
                    items(autoParts, key = { it.id }) { part ->
                        AutoPartCard(
                            part = part,
                            onEdit = { editingPart = part },
                            onDelete = { viewModel.deleteAutoPart(part.id, part.nombre) },
                            onQuote = {
                                // Load part into quote editor and navigate
                                viewModel.updateQuoteState { state ->
                                    state.copy(
                                        partName = part.nombre,
                                        partOem = part.numeroParte,
                                        donorVehicle = part.displayVehicle.ifBlank { state.donorVehicle },
                                        breakdownItems = listOf(
                                            com.example.data.model.BreakdownItem(
                                                concept = part.nombre,
                                                qty = "1",
                                                condition = part.condicion,
                                                priceVenta = part.precio,
                                                priceCambio = part.precioCambio
                                            )
                                        )
                                    )
                                }
                                viewModel.selectTab(JsvTab.EDITOR)
                            }
                        )
                    }
                }
            }
        }
    }

    // Add or Edit Dialog
    if (showAddDialog || editingPart != null) {
        AutoPartFormDialog(
            partToEdit = editingPart,
            onDismiss = {
                showAddDialog = false
                editingPart = null
            },
            onSave = { newPart ->
                if (editingPart != null) {
                    viewModel.updateAutoPart(newPart)
                } else {
                    viewModel.addAutoPart(newPart)
                }
                showAddDialog = false
                editingPart = null
            }
        )
    }
}

@Composable
fun AutoPartCard(
    part: AutoPart,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuote: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = JsvDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("autopart_card_${part.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = JsvGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = part.categoria,
                                color = JsvGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OEM: ${part.numeroParte}",
                            color = JsvTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = part.nombre,
                        color = JsvTextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = JsvGold)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = JsvRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vehicle Compatibility & Location
            Text(
                text = "🚗 ${part.displayVehicle.ifBlank { "Compatible multimarca" }} • 📍 ${part.ubicacionAlmacen}",
                color = JsvTextMuted,
                fontSize = 12.sp
            )

            if (part.notas.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 ${part.notas}",
                    color = JsvTextMuted,
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = part.formattedPrice,
                        color = JsvGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (part.precioCambio > 0) {
                        Text(
                            text = "A cambio: \$${String.format(java.util.Locale.getDefault(), "%,.2f", part.precioCambio)} MXN",
                            color = JsvTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Stock badge
                    Surface(
                        color = if (part.stockDisponible > 0) JsvGreen.copy(alpha = 0.2f) else JsvRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Stock: ${part.stockDisponible}",
                            color = if (part.stockDisponible > 0) JsvGreen else JsvRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onQuote,
                        colors = ButtonDefaults.buttonColors(containerColor = JsvDarkElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JsvGold),
                        modifier = Modifier.testTag("btn_cotizar_part_${part.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = JsvGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Cotizar", color = JsvGold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AutoPartFormDialog(
    partToEdit: AutoPart?,
    onDismiss: () -> Unit,
    onSave: (AutoPart) -> Unit
) {
    var nombre by remember { mutableStateOf(partToEdit?.nombre ?: "") }
    var numeroParte by remember { mutableStateOf(partToEdit?.numeroParte ?: "") }
    var precioStr by remember { mutableStateOf(partToEdit?.precio?.toString() ?: "") }
    var precioCambioStr by remember { mutableStateOf(partToEdit?.precioCambio?.toString() ?: "0.0") }
    var stockStr by remember { mutableStateOf(partToEdit?.stockDisponible?.toString() ?: "1") }
    var categoria by remember { mutableStateOf(partToEdit?.categoria ?: "Transmisión") }
    var marcaVehiculo by remember { mutableStateOf(partToEdit?.marcaVehiculo ?: "Nissan") }
    var modeloVehiculo by remember { mutableStateOf(partToEdit?.modeloVehiculo ?: "Versa") }
    var aniosVehiculo by remember { mutableStateOf(partToEdit?.aniosVehiculo ?: "2015-2019") }
    var ubicacion by remember { mutableStateOf(partToEdit?.ubicacionAlmacen ?: "Bodega Central") }
    var notas by remember { mutableStateOf(partToEdit?.notas ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = JsvDark,
        title = {
            Text(
                text = if (partToEdit == null) "✨ Nueva Autoparte" else "✏️ Editar Autoparte",
                color = JsvGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de la Autoparte") },
                        singleLine = true,
                        colors = dialogFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = numeroParte,
                        onValueChange = { numeroParte = it },
                        label = { Text("Número de Parte (OEM / Ref)") },
                        singleLine = true,
                        colors = dialogFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = precioStr,
                            onValueChange = { precioStr = it },
                            label = { Text("Precio Venta ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = dialogFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = precioCambioStr,
                            onValueChange = { precioCambioStr = it },
                            label = { Text("Precio Cambio ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = dialogFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stockStr,
                            onValueChange = { stockStr = it },
                            label = { Text("Stock Disponible") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = dialogFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = { categoria = it },
                            label = { Text("Categoría") },
                            singleLine = true,
                            colors = dialogFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = marcaVehiculo,
                            onValueChange = { marcaVehiculo = it },
                            label = { Text("Marca Vehículo") },
                            singleLine = true,
                            colors = dialogFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = modeloVehiculo,
                            onValueChange = { modeloVehiculo = it },
                            label = { Text("Modelo Vehículo") },
                            singleLine = true,
                            colors = dialogFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = aniosVehiculo,
                            onValueChange = { aniosVehiculo = it },
                            label = { Text("Años (ej. 2015-2019)") },
                            singleLine = true,
                            colors = dialogFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ubicacion,
                            onValueChange = { ubicacion = it },
                            label = { Text("Ubicación Almacén") },
                            singleLine = true,
                            colors = dialogFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = { Text("Notas de Inspección / Estado") },
                        colors = dialogFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = precioStr.toDoubleOrNull() ?: 0.0
                    val pc = precioCambioStr.toDoubleOrNull() ?: 0.0
                    val st = stockStr.toIntOrNull() ?: 1
                    if (nombre.isNotBlank() && numeroParte.isNotBlank()) {
                        val updated = AutoPart(
                            id = partToEdit?.id ?: 0L,
                            nombre = nombre,
                            numeroParte = numeroParte,
                            precio = p,
                            precioCambio = pc,
                            stockDisponible = st,
                            categoria = categoria.ifBlank { "General" },
                            marcaVehiculo = marcaVehiculo,
                            modeloVehiculo = modeloVehiculo,
                            aniosVehiculo = aniosVehiculo,
                            ubicacionAlmacen = ubicacion,
                            notas = notas,
                            timestamp = partToEdit?.timestamp ?: System.currentTimeMillis()
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = JsvGold)
            ) {
                Text("Guardar Pieza", color = JsvDarker, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = JsvTextMuted)
            }
        }
    )
}

@Composable
fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = JsvGold,
    unfocusedBorderColor = JsvBorder,
    focusedTextColor = JsvTextWhite,
    unfocusedTextColor = JsvTextWhite,
    focusedLabelColor = JsvGold,
    unfocusedLabelColor = JsvTextMuted,
    cursorColor = JsvGold,
    focusedContainerColor = JsvDarkElevated,
    unfocusedContainerColor = JsvDarkElevated
)
