package com.example.ui.screens.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.TrendingUp
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
fun FinanceCalculatorScreen(viewModel: JsvViewModel) {
    var costPriceStr by remember { mutableStateOf("80.0") }
    var profitMarginStr by remember { mutableStateOf("35.0") }
    var bcvRateStr by remember { mutableStateOf("36.50") }

    val costPrice = costPriceStr.toDoubleOrNull() ?: 0.0
    val margin = profitMarginStr.toDoubleOrNull() ?: 0.0
    val bcvRate = bcvRateStr.toDoubleOrNull() ?: 1.0

    val salePriceUsd = costPrice * (1.0 + (margin / 100.0))
    val profitUsd = salePriceUsd - costPrice
    val salePriceVes = salePriceUsd * bcvRate

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
                        Icon(Icons.Default.AttachMoney, contentDescription = "Finanzas", tint = JsvGold, modifier = Modifier.size(24.dp))
                        Text(text = "CALCULADORA FINANCIERA & DIVISAS — JSV", style = FugazHeavyBrandStyle.copy(fontSize = 13.sp, color = JsvGold))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calcula de forma automática los márgenes de ganancia sobre costo de autopartes, precios de venta sugeridos en dólares y conversión oficial en bolívares (BCV/Paralelo).",
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
                    Text("Parámetros de Costo & Margen", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = costPriceStr,
                        onValueChange = { costPriceStr = it },
                        label = { Text("Costo de Adquisición (USD)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedLabelColor = JsvGold,
                            cursorColor = JsvGold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = profitMarginStr,
                        onValueChange = { profitMarginStr = it },
                        label = { Text("Margen de Ganancia Deseado (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedLabelColor = JsvGold,
                            cursorColor = JsvGold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = bcvRateStr,
                        onValueChange = { bcvRateStr = it },
                        label = { Text("Tasa Cambiaria (VES por USD)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedLabelColor = JsvGold,
                            cursorColor = JsvGold
                        )
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
                        Icon(Icons.Default.TrendingUp, contentDescription = "Resultados", tint = JsvGold, modifier = Modifier.size(20.dp))
                        Text("Resultados de Valuación Comercial", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    FinanceResultRow(label = "Costo Base:", value = "$${String.format("%.2f", costPrice)}")
                    FinanceResultRow(label = "Margen Aplicado:", value = "${String.format("%.1f", margin)}%")
                    FinanceResultRow(label = "Ganancia Neta Estimada:", value = "$${String.format("%.2f", profitUsd)}", isHighlight = true)
                    FinanceResultRow(label = "Precio de Venta Sugerido (USD):", value = "$${String.format("%.2f", salePriceUsd)}", isHighlight = true)
                    FinanceResultRow(label = "Precio Equivalente en VES:", value = "Bs. ${String.format("%.2f", salePriceVes)}")
                }
            }
        }
    }
}

@Composable
fun FinanceResultRow(label: String, value: String, isHighlight: Boolean = false) {
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
