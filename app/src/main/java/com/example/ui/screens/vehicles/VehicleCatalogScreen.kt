package com.example.ui.screens.vehicles

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vehicle
import com.example.ui.components.JsvSectionHeader
import com.example.ui.components.JsvField
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarker
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvOnPrimary
import com.example.ui.theme.JsvRed
import com.example.ui.theme.JsvSurface
import com.example.ui.theme.JsvTextBody
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.viewmodel.JsvTab
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun VehicleCatalogScreen(
    viewModel: JsvViewModel,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.allVehicles.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var vehicleToDelete by remember { mutableStateOf<Vehicle?>(null) }

    val filteredVehicles = vehicles.filter { v ->
        searchQuery.isBlank() ||
                v.marca.contains(searchQuery, ignoreCase = true) ||
                v.modelo.contains(searchQuery, ignoreCase = true) ||
                v.anios.contains(searchQuery, ignoreCase = true) ||
                v.motor.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JsvDarker)
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        JsvSectionHeader(
            title = "🚗 Catálogo de Vehículos Donantes",
            trailingContent = {
                Text("${vehicles.size} registros", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar marca o modelo...", fontSize = 10.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JsvGold) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsvGold,
                    unfocusedBorderColor = JsvBorder,
                    focusedTextColor = JsvTextWhite,
                    unfocusedTextColor = JsvTextWhite
                ),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(52.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = JsvOnPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("NUEVO", color = JsvOnPrimary, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredVehicles, key = { it.id }) { v ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = JsvSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = JsvGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("${v.marca} ${v.modelo}", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Años: ${v.anios} • Motor: ${v.motor}", color = JsvTextMuted, fontSize = 9.sp)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = {
                                    viewModel.applyVehicle(v)
                                    viewModel.selectTab(JsvTab.EDITOR)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = JsvGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("USAR", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 8.5.sp)
                            }
                            IconButton(onClick = { vehicleToDelete = v }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = JsvRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Vehicle Dialog
    if (showAddDialog) {
        var marca by remember { mutableStateOf("") }
        var modelo by remember { mutableStateOf("") }
        var anios by remember { mutableStateOf("") }
        var motor by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = JsvSurface,
            title = { Text("Registrar Nuevo Vehículo Donante", color = JsvGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    JsvField(label = "Marca (ej. Nissan, Chevrolet)", value = marca, onValueChange = { marca = it })
                    JsvField(label = "Modelo (ej. Versa, Beat)", value = modelo, onValueChange = { modelo = it })
                    JsvField(label = "Rango de Años (ej. 2015-2019)", value = anios, onValueChange = { anios = it })
                    JsvField(label = "Motor (ej. 1.6L 4 CIL)", value = motor, onValueChange = { motor = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addVehicle(marca, modelo, anios, motor)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold)
                ) {
                    Text("GUARDAR VEHÍCULO", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }

    // Delete confirmation dialog
    vehicleToDelete?.let { v ->
        AlertDialog(
            onDismissRequest = { vehicleToDelete = null },
            containerColor = JsvSurface,
            title = { Text("Eliminar Vehículo", color = JsvRed, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar '${v.displayName}' del catálogo?", color = JsvTextBody) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteVehicle(v)
                        vehicleToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvRed)
                ) {
                    Text("ELIMINAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { vehicleToDelete = null }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }
}
