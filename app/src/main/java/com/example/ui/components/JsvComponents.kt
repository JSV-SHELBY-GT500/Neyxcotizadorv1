package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.KeyboardType
import com.example.data.model.BreakdownItem
import com.example.data.model.QuoteOption
import com.example.ui.theme.JsvAmber
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
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

@Composable
fun JsvSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(JsvGold)
            .border(width = 1.dp, color = JsvBorder, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = 16.dp)
                        .background(JsvOnPrimary, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title.uppercase(),
                    color = JsvOnPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
            trailingContent?.invoke()
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status.lowercase()) {
        "pagado" -> Triple(JsvGreen, JsvOnPrimary, Icons.Default.Payment)
        "envio" -> Triple(JsvBlue, JsvOnPrimary, Icons.Default.LocalShipping)
        "entregado" -> Triple(JsvPurple, JsvOnPrimary, Icons.Default.CheckCircle)
        "cancelado" -> Triple(JsvRed, Color.White, Icons.Default.Warning)
        else -> Triple(JsvAmber, JsvOnPrimary, Icons.Default.Build)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun MetricKpiCard(
    title: String,
    value: String,
    subtitle: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(JsvSurface)
            .border(1.dp, JsvBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            color = iconColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = title,
            color = JsvTextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            color = JsvTextMuted,
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
    }
}

fun JsvGoldDarker(): Color = Color(0xFF4F378B)

@Composable
fun BreakdownItemEditor(
    index: Int,
    item: BreakdownItem,
    onUpdate: (BreakdownItem) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(JsvDarkElevated)
            .border(1.dp, JsvBorder, RoundedCornerShape(6.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Partida #$index", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = JsvRed)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        JsvField(
            label = "Concepto / Detalle",
            value = item.concept,
            onValueChange = { onUpdate(item.copy(concept = it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            JsvField(
                label = "Cant.",
                value = item.qty,
                onValueChange = { onUpdate(item.copy(qty = it)) },
                modifier = Modifier.weight(0.8f)
            )
            JsvField(
                label = "Condición",
                value = item.condition,
                onValueChange = { onUpdate(item.copy(condition = it)) },
                modifier = Modifier.weight(1.2f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            JsvField(
                label = "Al Cambio ($)",
                value = if (item.priceCambio == 0.0) "" else item.priceCambio.toInt().toString(),
                onValueChange = { onUpdate(item.copy(priceCambio = it.toDoubleOrNull() ?: 0.0)) },
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            JsvField(
                label = "Venta Directa ($)",
                value = if (item.priceVenta == 0.0) "" else item.priceVenta.toInt().toString(),
                onValueChange = { onUpdate(item.copy(priceVenta = it.toDoubleOrNull() ?: 0.0)) },
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuoteOptionEditor(
    index: Int,
    option: QuoteOption,
    onUpdate: (QuoteOption) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(JsvDarkElevated)
            .border(1.dp, JsvBorder, RoundedCornerShape(6.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Opción Proveedor #$index", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = JsvRed)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            JsvField(
                label = "Título Opción",
                value = option.title,
                onValueChange = { onUpdate(option.copy(title = it)) },
                modifier = Modifier.weight(1.3f)
            )
            JsvField(
                label = "Precio Base ($)",
                value = if (option.price == 0.0) "" else option.price.toInt().toString(),
                onValueChange = { onUpdate(option.copy(price = it.toDoubleOrNull() ?: 0.0)) },
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        JsvField(
            label = "Descripción",
            value = option.description,
            onValueChange = { onUpdate(option.copy(description = it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            JsvField(
                label = "Condición",
                value = option.condition,
                onValueChange = { onUpdate(option.copy(condition = it)) },
                modifier = Modifier.weight(1f)
            )
            JsvField(
                label = "Entrega",
                value = option.delivery,
                onValueChange = { onUpdate(option.copy(delivery = it)) },
                modifier = Modifier.weight(1f)
            )
            JsvField(
                label = "Garantía",
                value = option.warranty,
                onValueChange = { onUpdate(option.copy(warranty = it)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun JsvField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 10.sp) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JsvGold,
            unfocusedBorderColor = JsvBorder,
            focusedLabelColor = JsvGold,
            unfocusedLabelColor = JsvTextMuted,
            focusedTextColor = JsvTextWhite,
            unfocusedTextColor = JsvTextWhite,
            cursorColor = JsvGold
        ),
        modifier = modifier
    )
}
