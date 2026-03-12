package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.ui.theme.adaptiveOnSurface
import com.foss.appdock.shared.ui.theme.adaptiveSurfaceVariantBackground
import com.foss.appdock.shared.ui.theme.adaptiveSurfaceVariantBorder
import kotlinx.coroutines.launch

// ── Shared Subscreen Header ───────────────────────────────────────────────────

@Composable
private fun SettingsSectionTitle(title: String) {
        Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = adaptiveOnSurface(),
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp, top = 24.dp)
        )
}

// ── 1. Appearance Screen ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearanceScreen(
        selectedTheme: String,
        onThemeSelected: (String) -> Unit,
        accentColor: String,
        onAccentColorSelected: (String) -> Unit,
        selectedIconSize: String,
        onIconSizeSelected: (String) -> Unit,
        selectedIconShape: String,
        onIconShapeSelected: (String) -> Unit,
        onBack: () -> Unit
) {
        val accentColors =
                listOf(
                        "#2B6CEE", // Royal Blue
                        "#3B82F6", // Blue
                        "#6366F1", // Indigo
                        "#8B5CF6", // Violet
                        "#D946EF", // Fuchsia
                        "#EC4899", // Pink
                        "#F43F5E", // Rose
                        "#EF4444", // Red
                        "#F97316", // Orange
                        "#F59E0B", // Amber
                        "#84CC16", // Lime
                        "#10B981", // Emerald
                        "#14B8A6", // Teal
                        "#06B6D4", // Cyan
                        "#0EA5E9" // Sky
                )

        AppScaffold(bottomBar = {}) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(horizontal = 24.dp, vertical = 24.dp)
                                        .verticalScroll(rememberScrollState()),
                ) {
                        DockPageHeader(title = "Appearance", onBack = onBack)

                        // ── 1. Theme Section ───────────────────────────────────
                        SettingsSectionTitle("Theme")
                        Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = adaptiveSurfaceVariantBackground(),
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .border(
                                                        1.dp,
                                                        adaptiveSurfaceVariantBorder(),
                                                        RoundedCornerShape(16.dp)
                                                )
                        ) {
                                Column {
                                        ThemeOptionRow(
                                                "Dark",
                                                Icons.Filled.DarkMode,
                                                selectedTheme == "Dark"
                                        ) { onThemeSelected("Dark") }
                                        HorizontalDivider(color = adaptiveSurfaceVariantBorder())
                                        ThemeOptionRow(
                                                "Light",
                                                Icons.Filled.LightMode,
                                                selectedTheme == "Light"
                                        ) { onThemeSelected("Light") }
                                        HorizontalDivider(color = adaptiveSurfaceVariantBorder())
                                        ThemeOptionRow(
                                                "AMOLED Black",
                                                Icons.Filled.BrightnessHigh,
                                                selectedTheme == "AMOLED Black"
                                        ) { onThemeSelected("AMOLED Black") }
                                        HorizontalDivider(color = adaptiveSurfaceVariantBorder())
                                        ThemeOptionRow(
                                                "System Adaptive",
                                                Icons.Filled.SettingsBrightness,
                                                selectedTheme == "System Adaptive"
                                        ) { onThemeSelected("System Adaptive") }
                                }
                        }

                        Spacer(Modifier.height(32.dp))

                        // ── 2. Personalization Section ─────────────────────────
                        SettingsSectionTitle("Personalization")
                        Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = adaptiveSurfaceVariantBackground(),
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .border(
                                                        1.dp,
                                                        adaptiveSurfaceVariantBorder(),
                                                        RoundedCornerShape(16.dp)
                                                )
                        ) {
                                Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                        // Accent Color
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Text(
                                                        "Accent Color",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .labelMedium,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                                Row(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .horizontalScroll(
                                                                                rememberScrollState()
                                                                        ),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        accentColors.forEach { colorStr ->
                                                                AccentColorCircle(
                                                                        color =
                                                                                Color(
                                                                                        colorStr.removePrefix(
                                                                                                        "#"
                                                                                                )
                                                                                                .toLong(
                                                                                                        16
                                                                                                ) or
                                                                                                0xFF000000
                                                                                ),
                                                                        isSelected =
                                                                                accentColor.equals(
                                                                                        colorStr,
                                                                                        ignoreCase =
                                                                                                true
                                                                                ),
                                                                        onClick = {
                                                                                onAccentColorSelected(
                                                                                        colorStr
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }

                        Spacer(Modifier.height(32.dp))

                        // ── 3. Icon Styling Section ────────────────────────────
                        SettingsSectionTitle("Icon Styling")
                        Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = adaptiveSurfaceVariantBackground(),
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .border(
                                                        1.dp,
                                                        adaptiveSurfaceVariantBorder(),
                                                        RoundedCornerShape(16.dp)
                                                )
                        ) {
                                Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                        // Icon Size
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Text(
                                                        "Icon Size",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .labelMedium,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                                SegmentedControl(
                                                        options =
                                                                listOf(
                                                                        "Small",
                                                                        "Medium",
                                                                        "Large",
                                                                        "System Adaptive"
                                                                ),
                                                        selectedOption = selectedIconSize,
                                                        onOptionSelected = onIconSizeSelected
                                                )
                                        }

                                        // App Icon Shape
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Text(
                                                        "App Icon Shape",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .labelMedium,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                                Row(
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        listOf(
                                                                        "Squircle",
                                                                        "Circle",
                                                                        "Square",
                                                                        "System Adaptive"
                                                                )
                                                                .forEach { shape ->
                                                                        IconShapeOption(
                                                                                name = shape,
                                                                                icon =
                                                                                        when (shape
                                                                                        ) {
                                                                                                "Squircle" ->
                                                                                                        Icons.Filled
                                                                                                                .DashboardCustomize
                                                                                                "Circle" ->
                                                                                                        Icons.Filled
                                                                                                                .AccountCircle
                                                                                                "Square" ->
                                                                                                        Icons.Filled
                                                                                                                .CropSquare
                                                                                                else ->
                                                                                                        Icons.Filled
                                                                                                                .SettingsSuggest
                                                                                        },
                                                                                isSelected =
                                                                                        selectedIconShape ==
                                                                                                shape,
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                onClick = {
                                                                                        onIconShapeSelected(
                                                                                                shape
                                                                                        )
                                                                                }
                                                                        )
                                                                }
                                                }
                                        }
                                }
                        }

                        Spacer(Modifier.height(40.dp))
                }
        }
}

@Composable
private fun AccentColorCircle(color: Color, isSelected: Boolean, onClick: () -> Unit) {
        Box(
                modifier =
                        Modifier.size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { onClick() }
                                .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color =
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else Color.Transparent,
                                        shape = CircleShape
                                ),
                contentAlignment = Alignment.Center
        ) {
                if (isSelected) {
                        Icon(
                                Icons.Filled.Check,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                        )
                }
        }
}

@Composable
private fun SegmentedControl(
        options: List<String>,
        selectedOption: String,
        onOptionSelected: (String) -> Unit
) {
        Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier =
                        Modifier.fillMaxWidth()
                                .height(44.dp)
                                .border(
                                        1.dp,
                                        adaptiveSurfaceVariantBorder(),
                                        RoundedCornerShape(8.dp)
                                )
        ) {
                Row(modifier = Modifier.padding(4.dp)) {
                        options.forEach { option ->
                                val isSelected = option == selectedOption
                                Box(
                                        modifier =
                                                Modifier.weight(1f)
                                                        .fillMaxHeight()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                                if (isSelected)
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                                else Color.Transparent
                                                        )
                                                        .clickable { onOptionSelected(option) },
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text =
                                                        if (option == "System Adaptive") "Adaptive"
                                                        else option,
                                                style = MaterialTheme.typography.labelMedium,
                                                color =
                                                        if (isSelected)
                                                                MaterialTheme.colorScheme.onPrimary
                                                        else
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun ThemeOptionRow(
        name: String,
        icon: ImageVector,
        isSelected: Boolean,
        onClick: () -> Unit
) {
        Row(
                modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = adaptiveOnSurface(),
                        modifier = Modifier.weight(1f)
                )
                RadioButton(
                        selected = isSelected,
                        onClick = null, // Handled by row click
                        colors =
                                RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                )
                )
        }
}

@Composable
private fun IconShapeOption(
        name: String,
        icon: ImageVector,
        isSelected: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
) {
        Column(
                modifier =
                        modifier.clip(RoundedCornerShape(12.dp))
                                .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else Color.Transparent
                                )
                                .border(
                                        width = 2.dp,
                                        color =
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onClick() }
                                .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .background(
                                                MaterialTheme.colorScheme.surfaceVariant,
                                                RoundedCornerShape(12.dp)
                                        ),
                        contentAlignment = Alignment.Center
                ) { Icon(imageVector = icon, contentDescription = null, tint = Color.White) }
                Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        color =
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
}

// ── 2. Privacy Screen ─────────────────────────────────────────────────────────

@Composable
fun SettingsPrivacyScreen(onBack: () -> Unit) {
        AppScaffold(bottomBar = {}) {
                Column(
                        modifier =
                                Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                        DockPageHeader(title = "Privacy", onBack = onBack)

                        Column(
                                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                        ) {
                                SettingsSectionTitle("Local Data Management")
                                Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = adaptiveSurfaceVariantBackground(),
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .border(
                                                                1.dp,
                                                                adaptiveSurfaceVariantBorder(),
                                                                RoundedCornerShape(16.dp)
                                                        )
                                ) {
                                        Column(
                                                modifier = Modifier.padding(20.dp),
                                                verticalArrangement = Arrangement.spacedBy(20.dp)
                                        ) {
                                                Button(
                                                        onClick = { /* Clear Data Action */},
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(48.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .errorContainer,
                                                                        contentColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .error
                                                                )
                                                ) {
                                                        Text(
                                                                "Clear All App Data",
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                                Text(
                                                        "This will delete all your configured web apps, categories, and settings from this device permanently.",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant,
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun ToggleOption(
        title: String,
        subtitle: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = adaptiveOnSurface()
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                                subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }
                Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors =
                                SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                )
        }
}

@Composable
fun SettingsBackupScreen(
        databaseHelper: com.foss.appdock.shared.data.DatabaseHelper,
        onExportClick: () -> Unit,
        onImportClick: () -> Unit,
        onBack: () -> Unit,
        snackbarHostState: SnackbarHostState
) {
        val history by databaseHelper.getAllBackupHistory().collectAsState(emptyList())
        val coroutineScope = rememberCoroutineScope()

        AppScaffold(snackbarHostState = snackbarHostState, bottomBar = {}) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(horizontal = 24.dp, vertical = 24.dp)
                                        .verticalScroll(rememberScrollState())
                ) {
                        DockPageHeader(title = "Backup & Restore", onBack = onBack)

                        Spacer(Modifier.height(32.dp))

                        // ── 1. Action Cards ──────────────────────────────────
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                BackupActionCard(
                                        title = "Export Data",
                                        description =
                                                "Create a local backup file of all your web apps, settings, and layout.",
                                        icon = Icons.Filled.FileDownload, // matching mockup icon
                                        // direction
                                        buttonText = "Create Backup File",
                                        modifier = Modifier.weight(1f),
                                        onClick = onExportClick
                                )
                                BackupActionCard(
                                        title = "Import Data",
                                        description =
                                                "Restore your setup from an existing backup file on your device.",
                                        icon = Icons.Filled.FileUpload,
                                        buttonText = "Select Backup File",
                                        iconColor = Color(0xFF10B981),
                                        modifier = Modifier.weight(1f),
                                        onClick = onImportClick
                                )
                        }

                        Spacer(Modifier.height(40.dp))

                        // ── 2. Previous Backups ──────────────────────────────
                        if (history.isNotEmpty()) {
                                SettingsSectionTitle("Previous Backups")
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        history.forEach { item ->
                                                BackupHistoryItem(
                                                        item = item,
                                                        onRestore = { /* TODO: Implement auto restore from local history */
                                                        },
                                                        onDelete = {
                                                                coroutineScope.launch {
                                                                        databaseHelper
                                                                                .deleteBackupHistory(
                                                                                        item.id
                                                                                )
                                                                }
                                                        }
                                                )
                                        }
                                }
                        } else {
                                // Empty state message
                                Box(
                                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                "No previous backups found.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.outline
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun BackupActionCard(
        title: String,
        description: String,
        icon: ImageVector,
        buttonText: String,
        modifier: Modifier = Modifier,
        iconColor: Color = MaterialTheme.colorScheme.primary,
        onClick: () -> Unit
) {
        Surface(
                shape = RoundedCornerShape(16.dp),
                color = adaptiveSurfaceVariantBackground(),
                modifier =
                        modifier.height(260.dp)
                                .border(
                                        1.dp,
                                        adaptiveSurfaceVariantBorder(),
                                        RoundedCornerShape(16.dp)
                                )
        ) {
                Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        Box(
                                modifier =
                                        Modifier.size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                contentAlignment = Alignment.Center
                        ) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp)) }

                        Text(
                                title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = adaptiveOnSurface()
                        )

                        Text(
                                description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
                                modifier = Modifier.weight(1f)
                        )

                        Button(
                                onClick = onClick,
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor =
                                                        if (iconColor ==
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                                                MaterialTheme.colorScheme.primary
                                                        else
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant,
                                                contentColor =
                                                        if (iconColor ==
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                                                Color.White
                                                        else
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                        )
                        ) {
                                Icon(icon, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                        buttonText,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }
                }
        }
}

@Composable
private fun BackupHistoryItem(
        item: com.foss.appdock.shared.domain.BackupHistory,
        onRestore: () -> Unit,
        onDelete: () -> Unit
) {
        Surface(
                shape = RoundedCornerShape(16.dp),
                color = adaptiveSurfaceVariantBackground(),
                modifier =
                        Modifier.fillMaxWidth()
                                .border(
                                        1.dp,
                                        adaptiveSurfaceVariantBorder(),
                                        RoundedCornerShape(16.dp)
                                )
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Box(
                                modifier =
                                        Modifier.size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.Filled.Description,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        item.filename,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = adaptiveOnSurface()
                                )
                                Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        val date =
                                                androidx.compose.ui.text.intl.Locale.current.let {
                                                        // Simple formatting for now
                                                        "Oct 24, 2023 at 14:30"
                                                }
                                        Text(
                                                "${date} • ${(item.sizeBytes / 1024f / 1024f).let { "%.1f".format(it) }} MB",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }

                        IconButton(onClick = onRestore) {
                                Icon(
                                        Icons.Filled.History,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                )
                        }

                        IconButton(onClick = onDelete) {
                                Icon(
                                        Icons.Filled.Delete,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                }
        }
}

// ── 4. App Management Screen ──────────────────────────────────────────────────

@Composable
fun SettingsAppManagementScreen(
        selectedBrowser: String,
        onBrowserSelected: (String) -> Unit,
        onBack: () -> Unit
) {
        AppScaffold(bottomBar = {}) {
                Column(
                        modifier =
                                Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                        DockPageHeader(title = "App Management", onBack = onBack)

                        Column(
                                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                        ) {
                                if (com.foss.appdock.shared.platform.platformIsDesktop) {
                                        SettingsSectionTitle("Default Browser")
                                        Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = adaptiveSurfaceVariantBackground(),
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .border(
                                                                        1.dp,
                                                                        adaptiveSurfaceVariantBorder(),
                                                                        RoundedCornerShape(16.dp)
                                                                )
                                        ) {
                                                Column {
                                                        val browsers = remember {
                                                                com.foss.appdock.shared.platform
                                                                        .queryInstalledBrowsers()
                                                        }
                                                        browsers.forEachIndexed { index, browser ->
                                                                val icon =
                                                                        if (browser ==
                                                                                        "System Default"
                                                                        )
                                                                                Icons.Filled
                                                                                        .Settings
                                                                        else Icons.Filled.Language
                                                                ThemeOptionRow(
                                                                        browser,
                                                                        icon,
                                                                        selectedBrowser == browser
                                                                ) { onBrowserSelected(browser) }

                                                                if (index < browsers.size - 1) {
                                                                        HorizontalDivider(
                                                                                color =
                                                                                        adaptiveSurfaceVariantBorder()
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                }

                                Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                                ) {
                                        Row(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Icon(
                                                        Icons.Filled.Info,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                        "If a selected browser is not installed, App Dock will automatically fall back to the system default.",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        }
                                }
                        }
                }
        }
}

// ── 5. About Screen ───────────────────────────────────────────────────────────

@Composable
fun SettingsAboutScreen(onOpenUrl: (String) -> Unit, onBack: () -> Unit) {
        AppScaffold(bottomBar = {}) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(horizontal = 24.dp, vertical = 24.dp)
                                        .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // ── Header ─────────────────────────────────────────────────────────────
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(40.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(
                                                                adaptiveSurfaceVariantBackground()
                                                        )
                                                        .clickable { onBack() },
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }

                                Text(
                                        text = "About",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = adaptiveOnSurface(),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.width(40.dp))
                        }

                        // ── App Branding ───────────────────────────────────────────────────────
                        Text(
                                "App Dock",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = adaptiveOnSurface()
                        )
                        Text(
                                "Version 1.0 (Stable)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(24.dp))

                        // ── Action Cards ───────────────────────────────────────────────────────
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                AboutActionCard(
                                        title = "GitHub Repository",
                                        description =
                                                "View source code, report issues, or contribute.",
                                        icon = Icons.Filled.Code,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                                onOpenUrl(
                                                        "https://github.com/Maheswara660/App-Dock"
                                                )
                                        }
                                )
                                AboutActionCard(
                                        title = "Support on Ko-fi",
                                        description =
                                                "Buy me a coffee to support the development of App Dock.",
                                        icon = Icons.Filled.Favorite,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onOpenUrl("https://ko-fi.com/Maheswara660/") }
                                )
                        }

                        Spacer(Modifier.height(32.dp))

                        // ── Privacy Manifesto ──────────────────────────────────────────────────
                        Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = adaptiveSurfaceVariantBackground(),
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .border(
                                                        width = 1.dp,
                                                        color = adaptiveSurfaceVariantBorder(),
                                                        shape = RoundedCornerShape(20.dp)
                                                )
                        ) {
                                Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                Icon(
                                                        Icons.Filled.VerifiedUser,
                                                        null,
                                                        tint = Color(0xFF10B981),
                                                        modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                        "Privacy Manifesto",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = adaptiveOnSurface()
                                                )
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        Text(
                                                "App Dock is built on a strict privacy-first, local-only philosophy. We believe your workspace and data belong entirely to you.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                        )

                                        Spacer(Modifier.height(24.dp))

                                        ManifestoItem(
                                                title = "No Telemetry",
                                                description =
                                                        "We do not track your usage, clicks, or the apps you dock.",
                                                icon = Icons.Filled.GppGood
                                        )

                                        Spacer(Modifier.height(16.dp))

                                        ManifestoItem(
                                                title = "Accountless & Local",
                                                description =
                                                        "No login required. All your settings are stored locally on your device.",
                                                icon = Icons.Filled.LockPerson
                                        )
                                }
                        }

                        Spacer(Modifier.height(48.dp))

                        // ── Footer ─────────────────────────────────────────────────────────────
                        Text(
                                "Released under the GPLv3 License.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                                "© From App Dock Contributor. All rights reserved.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(Modifier.height(32.dp))
                }
        }
}

@Composable
private fun AboutActionCard(
        title: String,
        description: String,
        icon: ImageVector,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
) {
        Surface(
                shape = RoundedCornerShape(16.dp),
                color = adaptiveSurfaceVariantBackground(),
                modifier =
                        modifier.fillMaxWidth()
                                .border(
                                        1.dp,
                                        adaptiveSurfaceVariantBorder(),
                                        RoundedCornerShape(16.dp)
                                )
                                .clickable { onClick() }
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                                modifier =
                                        Modifier.size(36.dp)
                                                .background(
                                                        MaterialTheme.colorScheme.surfaceVariant,
                                                        RoundedCornerShape(8.dp)
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = adaptiveOnSurface()
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                                text = description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight =
                                        androidx.compose.ui.unit.TextUnit.Unspecified // allow wrap
                        )
                }
        }
}

@Composable
private fun ManifestoItem(title: String, description: String, icon: ImageVector) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                )
                Column {
                        Text(
                                text = title,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = adaptiveOnSurface()
                        )
                        Text(
                                text = description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }
        }
}
