package com.example.ui.screens.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.JsvViewModel

data class ObdCodeItem(
    val code: String,
    val category: String,
    val description: String,
    val suggestedPart: String
)

@Composable
fun Obd2DiagnosticScreen(viewModel: JsvViewModel) {
    var searchQuery by remember { mutableStateOf("") }

    val codesList = remember {
        listOf(
            ObdCodeItem("P0300", "Encendido", "Fallo de encendido aleatorio / múltiple en cilindros", "Bobinas de encendido y bujías originales"),
            ObdCodeItem("P0420", "Emisiones", "Eficiencia del sistema catalítico por debajo del umbral (Banco 1)", "Convertidor catalítico o sensor de oxígeno O2"),
            ObdCodeItem("P0171", "Combustible", "Sistema demasiado pobre (Banco 1)", "Bomba de gasolina, inyectores o sensor MAF"),
            ObdCodeItem("P0335", "Sensor CKP", "Fallo de circuito en sensor de posición del cigüeñal", "Sensor de cigüeñal OEM y cableado"),
            ObdCodeItem("P0500", "Transmisión", "Fallo en sensor de velocidad del vehículo VSS", "Sensor de velocidad / turbina de transmisión"),
            ObdCodeItem("P0700", "Transmisión", "Sistema de control de transmisión (Solicitud MIL encendida)", "Módulo TCM o solenoide de caja automática"),
            ObdCodeItem("P0115", "Refrigeración", "Fallo de circuito en sensor de temperatura del refrigerante ECT", "Termostato y sensor de temperatura original")
        )
    }

    val filteredList = codesList.filter {
        it.code.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true) ||
                it.suggestedPart.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
                        Icon(Icons.Default.Healing, contentDescription = "OBD2", tint = JsvGold, modifier = Modifier.size(24.dp))
                        Text(text = "DIAGNÓSTICO RÁPIDO OBD2 & AUTOPARTES", style = FugazHeavyBrandStyle.copy(fontSize = 13.sp, color = JsvGold))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Consulta rápida de códigos de falla OBD2 comunes, causas técnicas y la autoparte original recomendada para la reparación y cotización inmediata.",
                        color = JsvTextBody,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por código (ej. P0300) o síntoma...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = JsvGold) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JsvGold, unfocusedBorderColor = JsvBorder, focusedLabelColor = JsvGold, cursorColor = JsvGold)
            )
        }

        items(filteredList, key = { it.code }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvDarkElevated),
                border = BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(JsvBlue)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(item.code, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Text(item.category, color = JsvGold, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "🔍 Diagnóstico:", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text(text = item.description, color = JsvTextWhite, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "📦 Autoparte Recomendada:", color = JsvBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text(text = item.suggestedPart, color = JsvTextBody, fontSize = 11.sp)
                }
            }
        }
    }
}
