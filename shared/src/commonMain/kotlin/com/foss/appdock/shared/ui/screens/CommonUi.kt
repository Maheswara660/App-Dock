package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.ui.theme.*

@Composable
fun SolidTextField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String = "",
        leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        placeholder = { Text(placeholder) },
                        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = adaptiveSurfaceVariantBorder(),
                                        focusedContainerColor = adaptiveSurfaceVariantBackground(),
                                        unfocusedContainerColor =
                                                adaptiveSurfaceVariantBackground(),
                                        focusedTextColor = adaptiveOnSurface(),
                                        unfocusedTextColor = adaptiveOnSurface()
                                )
                )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolidDropdownField(
        label: String,
        value: String,
        expanded: Boolean,
        onExpandedChange: (Boolean) -> Unit,
        options: List<String>,
        onOptionSelected: (String) -> Unit
) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
                        OutlinedTextField(
                                value = value,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                        )
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp).menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor =
                                                        MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor =
                                                        adaptiveSurfaceVariantBorder(),
                                                focusedContainerColor =
                                                        adaptiveSurfaceVariantBackground(),
                                                unfocusedContainerColor =
                                                        adaptiveSurfaceVariantBackground(),
                                                focusedTextColor = adaptiveOnSurface(),
                                                unfocusedTextColor = adaptiveOnSurface()
                                        )
                        )
                        ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { onExpandedChange(false) },
                                modifier =
                                        Modifier.background(
                                                MaterialTheme.colorScheme.surfaceVariant
                                        )
                        ) {
                                options.forEach { option ->
                                        DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = { onOptionSelected(option) }
                                        )
                                }
                        }
                }
        }
}

@Composable
fun AdvancedSettingToggle(
        title: String,
        subtitle: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clickable { onCheckedChange(!checked) }
                                .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                        Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color =
                                        if (checked) MaterialTheme.colorScheme.primary
                                        else adaptiveOnSurface()
                        )
                        Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                        checked = checked,
                        onCheckedChange = null,
                        colors =
                                SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                                        uncheckedThumbColor =
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                        uncheckedTrackColor =
                                                MaterialTheme.colorScheme.surfaceVariant
                                )
                )
        }
}

@Composable
fun M3SectionHeader(title: String) {
        Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
        )
}
