package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.platform.platformIsAndroid
import com.foss.appdock.shared.ui.theme.fadeSlideIn
import com.foss.appdock.shared.utils.Constants

@Composable
@Suppress("UNUSED_PARAMETER")
fun SettingsScreen(
    onAppearanceClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onBackupClick: () -> Unit,
    onAppManagementClick: () -> Unit,
    onAboutClick: () -> Unit,
    onWhatNewClick: () -> Unit,
    onTabSelected: (BottomNavTab) -> Unit,
    onFabClick: () -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header ────────────────────────────────────────────────────────
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Customize App Dock to your workflow",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            // ── Menu ──────────────────────────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .fadeSlideIn(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                SettingsGroup(title = "Personalization") {
                    SettingsRow(
                        icon = Icons.Filled.Palette,
                        iconBg = Color(0xFF6366F1).copy(alpha = 0.12f),
                        iconTint = Color(0xFF6366F1),
                        title = "Appearance",
                        subtitle = "Theme, accent color, icon size & shape",
                        onClick = onAppearanceClick
                    )
                    SettingsRow(
                        icon = Icons.Filled.Apps,
                        iconBg = Color(0xFF8B5CF6).copy(alpha = 0.12f),
                        iconTint = Color(0xFF8B5CF6),
                        title = "App Management",
                        subtitle = "Default browser and launch settings",
                        onClick = onAppManagementClick
                    )
                }

                Spacer(Modifier.height(16.dp))

                SettingsGroup(title = "Data & Privacy") {
                    SettingsRow(
                        icon = Icons.Filled.Backup,
                        iconBg = Color(0xFF10B981).copy(alpha = 0.12f),
                        iconTint = Color(0xFF10B981),
                        title = "Backup & Restore",
                        subtitle = "Export and import your dock setup",
                        onClick = onBackupClick
                    )
                    SettingsRow(
                        icon = Icons.Filled.Security,
                        iconBg = Color(0xFFF59E0B).copy(alpha = 0.12f),
                        iconTint = Color(0xFFF59E0B),
                        title = "Privacy",
                        subtitle = "Clear locally stored data",
                        onClick = onPrivacyClick
                    )
                }

                Spacer(Modifier.height(16.dp))

                SettingsGroup(title = "About") {
                    SettingsRow(
                        icon = Icons.Filled.AutoAwesome,
                        iconBg = Color(0xFFF472B6).copy(alpha = 0.12f),
                        iconTint = Color(0xFFF472B6),
                        title = "What's New",
                        subtitle = "Changelog for v${Constants.VERSION}",
                        onClick = onWhatNewClick
                    )
                    SettingsRow(
                        icon = Icons.Filled.Info,
                        iconBg = MaterialTheme.colorScheme.surfaceVariant,
                        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                        title = "About App Dock",
                        subtitle = "Version, licenses, and developer info",
                        onClick = onAboutClick,
                        showDivider = false
                    )
                }

                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

// ── Settings Group container ──────────────────────────────────────────────────

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        ) {
            Column { content() }
        }
    }
}

// ── Single row inside a group ─────────────────────────────────────────────────

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 70.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}

// ── SettingsNavigationItem (kept for back-compat) ─────────────────────────────

@Composable
fun SettingsNavigationItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    SettingsRow(
        icon = icon,
        iconBg = iconTint.copy(alpha = 0.12f),
        iconTint = iconTint,
        title = title,
        subtitle = subtitle,
        onClick = onClick
    )
}
