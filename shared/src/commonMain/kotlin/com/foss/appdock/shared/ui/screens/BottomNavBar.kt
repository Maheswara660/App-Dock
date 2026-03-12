package com.foss.appdock.shared.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.ui.theme.*

// ── Bottom Nav Tab definitions ─────────────────────────────────────────────────

enum class BottomNavTab {
        APPS,
        SEARCH,
        CATEGORY,
        SETTINGS
}

private data class NavItem(
        val tab: BottomNavTab,
        val label: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector
)

private val navItems =
        listOf(
                NavItem(
                        tab = BottomNavTab.APPS,
                        label = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home
                ),
                NavItem(
                        tab = BottomNavTab.SEARCH,
                        label = "Search",
                        selectedIcon = Icons.Filled.Search,
                        unselectedIcon = Icons.Outlined.Search
                ),
                NavItem(
                        tab = BottomNavTab.CATEGORY,
                        label = "Categories",
                        selectedIcon = Icons.Filled.Folder,
                        unselectedIcon = Icons.Outlined.Folder
                ),
                NavItem(
                        tab = BottomNavTab.SETTINGS,
                        label = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings
                )
        )

// ── BottomNavBar — Pure solid theme ─────────

@Composable
fun BottomNavBar(
        selectedTab: BottomNavTab,
        onTabSelected: (BottomNavTab) -> Unit,
        onFabClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        val isDark = isSystemInDarkTheme()
        val navBg =
                if (isDark) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surface
        val navBorder =
                if (isDark) MaterialTheme.colorScheme.outlineVariant
                else MaterialTheme.colorScheme.outline

        Box(
                modifier = modifier.fillMaxWidth().padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
        ) {
                // Main Dock Container
                Row(
                        modifier =
                                Modifier.clip(CircleShape)
                                        .background(navBg)
                                        .border(1.dp, navBorder, CircleShape)
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                        // First two items (Home, Search)
                        navItems.take(2).forEach { item ->
                                NavItemIcon(
                                        item = item,
                                        isSelected = selectedTab == item.tab,
                                        onClick = { onTabSelected(item.tab) },
                                        isDark = isDark
                                )
                        }

                        // Center FAB (Add App / Add Category)
                        Box(
                                modifier =
                                        Modifier.size(56.dp)
                                                .clip(CircleShape)
                                                .shadow(elevation = 8.dp, shape = CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                                .clickable { onFabClick() },
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                )
                        }

                        // Last two items (Folder, Settings)
                        navItems.takeLast(2).forEach { item ->
                                NavItemIcon(
                                        item = item,
                                        isSelected = selectedTab == item.tab,
                                        onClick = { onTabSelected(item.tab) },
                                        isDark = isDark
                                )
                        }
                }
        }
}

@Composable
private fun NavItemIcon(item: NavItem, isSelected: Boolean, onClick: () -> Unit, isDark: Boolean) {
        val scale by
                animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = tween(250)
                )

        val containerColor =
                if (isSelected) {
                        if (isDark) Color(0xFF334155) // Solid slate 700
                        else MaterialTheme.colorScheme.primaryContainer
                } else {
                        Color.Transparent
                }

        val iconColor =
                if (isSelected) {
                        if (isDark) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                }

        Box(
                modifier =
                        Modifier.size(48.dp)
                                .clip(CircleShape)
                                .background(containerColor)
                                .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
        ) {
                Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp).scale(scale)
                )
        }
}
