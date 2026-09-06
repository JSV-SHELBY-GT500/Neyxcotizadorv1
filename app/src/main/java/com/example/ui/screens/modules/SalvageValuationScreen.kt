package com.example.ui.screens.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun SalvageValuationScreen(viewModel: JsvViewModel) {
    var originalMSRPStr by remember { mutableStateOf("300.0") }
    var mileageKmStr by remember { mutableStateOf("120000") }
    var conditionRating by remember { mutableStateOf("85") } // %

    val msrp = originalMSRPStr.toDoubleOrNull() ?: 0.0
    val mileage = mileageKmStr.toDoubleOrNull() ?: 0.0
    val condition = conditionRating.toDoubleOrNull() ?: 50.0

    // Algoritmo de valuación de salvamento
    val mileageFactor = when {
        mileage < 50000 -> 0.70
        mileage < 100000 -> 0.55
        mileage < 150000 -> 0.40
        else -> 0.25
    }
    val conditionFactor = condition / 100.0
    val estimatedSalvageValue = msrp * mileageFactor * conditionFactor

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        Icon(Icons.Default.Calculate, contentDescription = "Valuación", tint = JsvGold, modifier = Modifier.size(24.dp))
                        Text(text = "VALUACIÓN Y DESGASTE DE PIEZAS DE DESGUACE", style = FugazHeavyBrandStyle.copy(fontSize = 13.sp, color = JsvGold))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calcula el valor justo de mercado de autopartes recuperadas de vehículos donantes en función del MSRP original, kilometraje del donante y porcentaje de vida útil remanente.",
                        color = JsvTextBody,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
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
                    Text("Parámetros del Vehículo Donante y Pieza", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = originalMSRPStr,
                        onValueChange = { originalMSRPStr = it },
                        label = { Text("Precio Original OEM Nuevo (MSRP USD)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JsvGold, unfocusedBorderColor = JsvBorder, focusedLabelColor = JsvGold, cursorColor = JsvGold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = mileageKmStr,
                        onValueChange = { mileageKmStr = it },
                        label = { Text("Kilometraje del Vehículo Donante (Km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JsvGold, unfocusedBorderColor = JsvBorder, focusedLabelColor = JsvGold, cursorColor = JsvGold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = conditionRating,
                        onValueChange = { conditionRating = it },
                        label = { Text("Vida Útil / Estado Estimado (0 - 100%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JsvGold, unfocusedBorderColor = JsvBorder, focusedLabelColor = JsvGold, cursorColor = JsvGold)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvDarkElevated),
                border = BorderStroke(1.dp, JsvBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = "Resultado", tint = JsvGold, modifier = Modifier.size(20.dp))
                        Text("Resultado de Valuación de Salvamento", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    SalvageResultRow(label = "MSRP Referencia:", value = "$${String.format("%.2f", msrp)}")
                    SalvageResultRow(label = "Factor Kilometraje:", value = "${String.format("%.2f", mileageFactor * 100)}% de retención")
                    SalvageResultRow(label = "Factor Condición:", value = "${String.format("%.1f", condition)}%")
                    SalvageResultRow(label = "Valor Sugerido de Venta (Salvamento):", value = "$${String.format("%.2f", estimatedSalvageValue)}", isHighlight = true)
                }
            }
        }
    }
}

@Composable
fun SalvageResultRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = JsvTextBody, fontSize = 11.sp)
        Text(text = value, color = if (isHighlight) JsvGold else JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
