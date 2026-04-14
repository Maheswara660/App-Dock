package com.foss.appdock.shared.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.platformIsDesktop
import com.foss.appdock.shared.ui.theme.adaptiveOnSurface
import com.foss.appdock.shared.ui.theme.adaptiveSurfaceVariantBackground
import com.foss.appdock.shared.ui.theme.adaptiveSurfaceVariantBorder
import kotlin.math.abs
import kotlin.math.roundToInt

// ── Dock Page Header (consistent header used across all pages) ────────────────

@Composable
fun DockPageHeader(title: String, onBack: (() -> Unit)? = null) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                        Box(
                                modifier =
                                        Modifier.size(40.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .clickable { onBack() },
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                } else {
                        Spacer(modifier = Modifier.size(40.dp))
                }

                Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(40.dp)) // Balance the back button
        }
}

// ── M3 CenterAligned TopBar ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3TopBar(
        title: String,
        onBack: (() -> Unit)? = null,
        actions: @Composable RowScope.() -> Unit = {}
) {
        CenterAlignedTopAppBar(
                title = {
                        Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                        )
                },
                navigationIcon = {
                        if (onBack != null) {
                                IconButton(onClick = onBack) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }
                },
                actions = actions,
                colors =
                        TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                                actionIconContentColor = MaterialTheme.colorScheme.onBackground
                        )
        )
}

// ── M3 Section Header ─────────────────────────────────────────────────────────

@Composable
fun M3SectionHeader(text: String, modifier: Modifier = Modifier) {
        Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )
}

// ── M3 Settings Row (ListItem-based) ─────────────────────────────────────────

@Composable
fun M3SettingsRow(
        title: String,
        subtitle: String? = null,
        leadingIcon: ImageVector? = null,
        leadingIconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
        onClick: (() -> Unit)? = null,
        trailingContent: @Composable (() -> Unit)? = null
) {
        val modifier =
                if (onClick != null) Modifier.fillMaxWidth().clickable { onClick() }
                else Modifier.fillMaxWidth()

        ListItem(
                headlineContent = {
                        Text(
                                title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                        )
                },
                supportingContent =
                        if (subtitle != null)
                                ({
                                        Text(
                                                subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                })
                        else null,
                leadingContent =
                        if (leadingIcon != null)
                                ({
                                        Box(
                                                modifier =
                                                        Modifier.size(40.dp)
                                                                .clip(RoundedCornerShape(10.dp))
                                                                .background(
                                                                        leadingIconTint.copy(
                                                                                alpha = 0.12f
                                                                        )
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        imageVector = leadingIcon,
                                                        contentDescription = null,
                                                        tint = leadingIconTint,
                                                        modifier = Modifier.size(20.dp)
                                                )
                                        }
                                })
                        else null,
                trailingContent = trailingContent,
                modifier = modifier,
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
        )
}

// ── M3 Toggle Row ─────────────────────────────────────────────────────────────

@Composable
fun M3ToggleRow(
        title: String,
        subtitle: String? = null,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        leadingIcon: ImageVector? = null
) {
        M3SettingsRow(
                title = title,
                subtitle = subtitle,
                leadingIcon = leadingIcon,
                trailingContent = {
                        Switch(
                                checked = checked,
                                onCheckedChange = onCheckedChange,
                                colors =
                                        SwitchDefaults.colors(
                                                checkedThumbColor =
                                                        MaterialTheme.colorScheme.onPrimary,
                                                checkedTrackColor =
                                                        MaterialTheme.colorScheme.primary
                                        )
                        )
                }
        )
}

// ── M3 App Toggle Row (with emoji icon) ───────────────────────────────────────

@Composable
fun M3AppToggleRow(
        label: String,
        description: String? = null,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                        )
                        if (description != null) {
                                Text(
                                        description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                }
                Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors =
                                SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                )
        }
}

// ── M3 Danger AlertDialog ─────────────────────────────────────────────────────

@Composable
fun M3DangerDialog(
        title: String,
        text: String,
        confirmText: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
) {
        AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
                text = {
                        Text(
                                text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                },
                confirmButton = {
                        Button(
                                onClick = onConfirm,
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError
                                        )
                        ) { Text(confirmText, fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
        )
}

// ── Shared Data Types and UI Components ───────────────────────────────────────

enum class SortOrder {
        NAME_ASC,
        NAME_DESC,
        NEWEST,
        OLDEST,
        MOST_USED,
        LEAST_USED,
        DATE_ADDED_NEWEST,
        DATE_ADDED_OLDEST
}

@Composable
fun EmptyState(
        title: String,
        subtitle: String,
        icon: ImageVector? = null,
        action: (@Composable () -> Unit)? = null
) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                Box(
                        modifier = Modifier.size(104.dp),
                        contentAlignment = Alignment.Center
                ) {
                        Box(
                                modifier = Modifier.fillMaxSize()
                                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                        )
                        Box(
                                modifier = Modifier.size(80.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = icon ?: Icons.Filled.Apps,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(40.dp)
                                )
                        }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                )
                if (action != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        action()
                }
        }
}

@Composable
fun SortDropdownList(currentSort: SortOrder, onSortChanged: (SortOrder) -> Unit) {
        var menuExpanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.CenterEnd) {
                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { menuExpanded = true }
                        ) {
                                Row(
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 8.dp
                                                ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Icon(
                                                Icons.AutoMirrored.Filled.Sort,
                                                contentDescription = "Sort",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                                "Sort",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                                Icons.Filled.ExpandMore,
                                                contentDescription = "Expand",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                        )
                                }
                        }

                        DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                modifier =
                                        Modifier.background(
                                                MaterialTheme.colorScheme.surfaceVariant
                                        )
                        ) {
                                DropdownMenuItem(
                                        text = {
                                                Text(
                                                        "A → Z",
                                                        color =
                                                                if (currentSort ==
                                                                                SortOrder.NAME_ASC
                                                                )
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                                else MaterialTheme.colorScheme.onSurface
                                                )
                                        },
                                        onClick = {
                                                onSortChanged(SortOrder.NAME_ASC)
                                                menuExpanded = false
                                        }
                                )
                                DropdownMenuItem(
                                        text = {
                                                Text(
                                                        "Z → A",
                                                        color =
                                                                if (currentSort ==
                                                                                SortOrder.NAME_DESC
                                                                )
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                                else MaterialTheme.colorScheme.onSurface
                                                )
                                        },
                                        onClick = {
                                                onSortChanged(SortOrder.NAME_DESC)
                                                menuExpanded = false
                                        }
                                )
                                DropdownMenuItem(
                                        text = {
                                                Text(
                                                        "Newest First",
                                                        color =
                                                                if (currentSort == SortOrder.NEWEST)
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                                else MaterialTheme.colorScheme.onSurface
                                                )
                                        },
                                        onClick = {
                                                onSortChanged(SortOrder.NEWEST)
                                                menuExpanded = false
                                        }
                                )
                        }
                }
        }
}

@Composable
fun SwipeableAppListItem(
        app: WebApp,
        onClick: () -> Unit,
        onDetails: (() -> Unit)? = null,
        onEdit: (() -> Unit)? = null,
        onDelete: () -> Unit
) {
        var offsetX by remember { mutableStateOf(0f) }
        val animatedOffset by animateFloatAsState(targetValue = offsetX)
        val revealThreshold = 120f

        val hash = app.name.hashCode()
        val iconColors =
                listOf(
                        Color(0xFFF87171),
                        Color(0xFFC084FC),
                        Color(0xFF818CF8),
                        Color(0xFF4ADE80),
                        Color(0xFFF472B6),
                        Color(0xFFCBD5E1)
                )
        val itemColor = iconColors[kotlin.math.abs(hash) % iconColors.size]

        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))) {
                val swipeProgress = (abs(animatedOffset) / revealThreshold).coerceIn(0f, 1f)
                val density = LocalDensity.current
                val revealThresholdDp = with(density) { revealThreshold.toDp() }

                if (!platformIsDesktop) {
                        Box(modifier = Modifier.matchParentSize().alpha(swipeProgress)) {
                                Row(modifier = Modifier.matchParentSize()) {
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxHeight()
                                                                .weight(1f)
                                                                .background(
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer
                                                                )
                                                                .clickable {
                                                                        offsetX = 0f
                                                                        onDetails?.invoke()
                                                                                ?: onEdit?.invoke()
                                                                },
                                                contentAlignment = Alignment.CenterStart
                                        ) {
                                                Box(
                                                        modifier =
                                                                Modifier.width(revealThresholdDp)
                                                                        .fillMaxHeight(),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                Icons.Filled.Edit,
                                                                contentDescription = "Edit",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(26.dp)
                                                        )
                                                }
                                        }
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxHeight()
                                                                .weight(1f)
                                                                .background(
                                                                        MaterialTheme.colorScheme
                                                                                .errorContainer
                                                                )
                                                                .clickable {
                                                                        offsetX = 0f
                                                                        onDelete()
                                                                },
                                                contentAlignment = Alignment.CenterEnd
                                        ) {
                                                Box(
                                                        modifier =
                                                                Modifier.width(revealThresholdDp)
                                                                        .fillMaxHeight(),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                Icons.Filled.Delete,
                                                                contentDescription = "Delete",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(26.dp)
                                                        )
                                                }
                                        }
                                }
                        }
                }

                Box(
                        modifier =
                                Modifier.offset { IntOffset(animatedOffset.roundToInt(), 0) }.let {
                                        if (!platformIsDesktop) {
                                                it.pointerInput(Unit) {
                                                        detectHorizontalDragGestures(
                                                                onDragEnd = {
                                                                        offsetX =
                                                                                if (abs(offsetX) <
                                                                                                revealThreshold
                                                                                )
                                                                                        0f
                                                                                else if (offsetX > 0
                                                                                )
                                                                                        revealThreshold
                                                                                else
                                                                                        -revealThreshold
                                                                },
                                                                onDragCancel = { offsetX = 0f }
                                                        ) { _, dragAmount ->
                                                                offsetX =
                                                                        (offsetX + dragAmount)
                                                                                .coerceIn(
                                                                                        -revealThreshold,
                                                                                        revealThreshold
                                                                                )
                                                        }
                                                }
                                        } else {
                                                it
                                        }
                                }
                ) {
                        Surface(
                                modifier = Modifier.fillMaxWidth().clickable { onClick() },
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp),
                                border =
                                        androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outlineVariant
                                        )
                        ) {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 12.dp
                                                        ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(48.dp)
                                                                        .clip(
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                        )
                                                                        .background(
                                                                                Color(0xFF1E293B)
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.8f
                                                                                        )
                                                                        )
                                                                        .border(
                                                                                1.dp,
                                                                                Color(0xFF334155)
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.5f
                                                                                        ),
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        if (!app.iconPath.isNullOrBlank()) {
                                                                val iconResPath = app.iconPath
                                                                val resolvedPath = if (
                                                                        iconResPath.startsWith("http") || 
                                                                        iconResPath.startsWith("data:") || 
                                                                        iconResPath.startsWith("file://")
                                                                ) {
                                                                        iconResPath
                                                                } else {
                                                                        "file:///${iconResPath.replace("\\", "/")}"
                                                                }
                                                                io.kamel.image.KamelImage(
                                                                        resource =
                                                                                io.kamel.image
                                                                                        .asyncPainterResource(
                                                                                                resolvedPath
                                                                                        ),
                                                                        contentDescription =
                                                                                "${app.name} icon",
                                                                        modifier =
                                                                                Modifier.fillMaxSize()
                                                                                        .clip(
                                                                                                RoundedCornerShape(
                                                                                                        12.dp
                                                                                                )
                                                                                        ),
                                                                        onFailure = {
                                                                                Text(
                                                                                        text =
                                                                                                app.name
                                                                                                        .take(
                                                                                                                1
                                                                                                        )
                                                                                                        .uppercase(),
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .titleMedium,
                                                                                        color =
                                                                                                itemColor,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Bold
                                                                                )
                                                                        }
                                                                )
                                                        } else {
                                                                Text(
                                                                        text =
                                                                                app.name
                                                                                        .take(1)
                                                                                        .uppercase(),
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleLarge,
                                                                        color = itemColor,
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                        }
                                                }
                                                Column(modifier = Modifier) {
                                                        Text(
                                                                text = app.name,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                }
                                        }
                                        if (platformIsDesktop) {
                                                var expanded by remember { mutableStateOf(false) }
                                                Box(
                                                        modifier = Modifier.padding(8.dp),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        IconButton(onClick = { expanded = true }) {
                                                                Icon(
                                                                        Icons.Filled.MoreVert,
                                                                        contentDescription =
                                                                                "Options",
                                                                        tint =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                        }
                                                        DropdownMenu(
                                                                expanded = expanded,
                                                                onDismissRequest = {
                                                                        expanded = false
                                                                },
                                                                modifier =
                                                                        Modifier.background(
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surface
                                                                        )
                                                        ) {
                                                                DropdownMenuItem(
                                                                        text = {
                                                                                Text(
                                                                                        "Details / Edit"
                                                                                )
                                                                        },
                                                                        onClick = {
                                                                                expanded = false
                                                                                if (onDetails !=
                                                                                                null
                                                                                ) {
                                                                                        onDetails
                                                                                                .invoke()
                                                                                } else {
                                                                                        onEdit?.invoke()
                                                                                }
                                                                        },
                                                                        leadingIcon = {
                                                                                Icon(
                                                                                        Icons.Filled
                                                                                                .Edit,
                                                                                        contentDescription =
                                                                                                null
                                                                                )
                                                                        }
                                                                )
                                                                DropdownMenuItem(
                                                                        text = {
                                                                                Text(
                                                                                        "Delete",
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .error
                                                                                )
                                                                        },
                                                                        onClick = {
                                                                                expanded = false
                                                                                onDelete()
                                                                        },
                                                                        leadingIcon = {
                                                                                Icon(
                                                                                        Icons.Filled
                                                                                                .Delete,
                                                                                        contentDescription =
                                                                                                null,
                                                                                        tint =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .error
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}
