package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
        onAppearanceClick: () -> Unit,
        onPrivacyClick: () -> Unit,
        onBackupClick: () -> Unit,
        onAppManagementClick: () -> Unit,
        onAboutClick: () -> Unit,
        onTabSelected: (BottomNavTab) -> Unit,
        onFabClick: () -> Unit,
        onBack: () -> Unit,
        snackbarHostState: SnackbarHostState
) {
        AppScaffold(
                snackbarHostState = snackbarHostState,
                bottomBar = {
                        BottomNavBar(
                                selectedTab = BottomNavTab.SETTINGS,
                                onTabSelected = onTabSelected,
                                onFabClick = onFabClick
                        )
                }
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                        DockPageHeader(title = "Settings", onBack = onBack)

                        Spacer(Modifier.height(32.dp))

                        // ── Settings Menu Items ────────────────────────────────────────────────
                        Column(
                                modifier =
                                        Modifier.weight(1f).verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                SettingsNavigationItem(
                                        title = "Appearance",
                                        subtitle =
                                                "Customize app theme, accent colors, and docked icon styling.",
                                        icon = Icons.Filled.Palette,
                                        iconTint = MaterialTheme.colorScheme.primary,
                                        onClick = onAppearanceClick
                                )

                                SettingsNavigationItem(
                                        title = "Privacy",
                                        subtitle =
                                                "Clear all locally saved web apps, categories, and settings.",
                                        icon = Icons.Filled.Security,
                                        iconTint = Color(0xFF10B981), // Emerald
                                        onClick = onPrivacyClick
                                )

                                SettingsNavigationItem(
                                        title = "Backup & Restore",
                                        subtitle =
                                                "Create and restore local backups of your dock setup.",
                                        icon = Icons.Filled.Backup,
                                        iconTint = Color(0xFFF59E0B), // Amber
                                        onClick = onBackupClick
                                )

                                SettingsNavigationItem(
                                        title = "App Management",
                                        subtitle =
                                                "Configure the default browser used to open fallback web apps.",
                                        icon = Icons.Filled.Apps,
                                        iconTint = Color(0xFFA855F7), // Purple
                                        onClick = onAppManagementClick
                                )

                                SettingsNavigationItem(
                                        title = "About",
                                        subtitle =
                                                "View version info, open source links, and our privacy manifesto.",
                                        icon = Icons.Filled.Info,
                                        iconTint = Color(0xFF64748B), // Slate
                                        onClick = onAboutClick
                                )

                                Spacer(
                                        modifier = Modifier.height(90.dp)
                                ) // padding for bottom nav space
                        }
                }
        }
}

@Composable
fun SettingsNavigationItem(
        title: String,
        subtitle: String,
        icon: ImageVector,
        iconTint: Color,
        onClick: () -> Unit
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
                                .clickable { onClick() }
        ) {
                Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                        // Icon Box
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .background(
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier.size(24.dp)
                                )
                        }

                        // Text content
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        // Arrow
                        Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                        )
                }
        }
}
