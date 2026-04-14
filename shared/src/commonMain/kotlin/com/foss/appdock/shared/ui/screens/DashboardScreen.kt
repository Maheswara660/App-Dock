package com.foss.appdock.shared.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid
import com.foss.appdock.shared.ui.theme.fadeSlideIn

// ── Dashboard ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun DashboardScreen(
    webApps: List<WebApp>,
    currentTab: BottomNavTab = BottomNavTab.APPS,
    onAddAppClick: () -> Unit,
    onAppClick: (WebApp) -> Unit,
    onAppDetails: (WebApp) -> Unit,
    onDeleteApp: (WebApp) -> Unit,
    onTabSelected: (BottomNavTab) -> Unit,
    snackbarHostState: SnackbarHostState,
    onShowFlyout: (String, androidx.compose.ui.graphics.vector.ImageVector) -> Unit = { _, _ -> },
    showChangelog: Boolean = false,
    onChangelogDismiss: () -> Unit = {}
) {
    var sortOrder by remember { mutableStateOf(SortOrder.NEWEST) }
    var isGridView by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    if (showChangelog) {
        ChangelogDialog(onDismiss = onChangelogDismiss)
    }

    val sortedApps = remember(webApps, sortOrder) {
        when (sortOrder) {
            SortOrder.NAME_ASC         -> webApps.sortedBy { it.name.lowercase() }
            SortOrder.NAME_DESC        -> webApps.sortedByDescending { it.name.lowercase() }
            SortOrder.NEWEST,          
            SortOrder.DATE_ADDED_NEWEST-> webApps.sortedByDescending { it.createdAt }
            SortOrder.OLDEST,          
            SortOrder.DATE_ADDED_OLDEST-> webApps.sortedBy { it.createdAt }
            SortOrder.MOST_USED        -> webApps.sortedByDescending { it.launchCount }
            SortOrder.LEAST_USED       -> webApps.sortedBy { it.launchCount }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ───────────────────────────────────────────────────────
            DashboardTopBar(
                appCount = webApps.size,
                isGridView = isGridView,
                sortOrder = sortOrder,
                onToggleView = { isGridView = !isGridView },
                onSortChanged = { sortOrder = it },
                onAddClick = onAddAppClick
            )

            // ── Content ───────────────────────────────────────────────────────
            if (webApps.isEmpty()) {
                DashboardEmptyState(onAddClick = onAddAppClick, modifier = Modifier.fillMaxSize())
            } else {
                AnimatedContent(
                    targetState = isGridView,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "viewToggle"
                ) { grid ->
                    if (grid) {
                        // Grid view
                        Row(modifier = Modifier.fillMaxSize()) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 140.dp),
                                state = gridState,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                contentPadding = PaddingValues(
                                    start = 16.dp, end = 16.dp,
                                    top = 8.dp, bottom = 100.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(sortedApps, key = { it.id }) { app ->
                                    AppGridCard(
                                        app = app,
                                        onClick = { onAppClick(app) },
                                        onLongClick = { onAppDetails(app) }
                                    )
                                }
                            }
                        }
                    } else {
                        // List view
                        Row(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                contentPadding = PaddingValues(
                                    start = 16.dp, end = 16.dp,
                                    top = 8.dp, bottom = 100.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(sortedApps, key = { it.id }) { app ->
                                    AppListCard(
                                        app = app,
                                        onClick = { onAppClick(app) },
                                        onDetails = { onAppDetails(app) },
                                        onDelete = { onDeleteApp(app) }
                                    )
                                }
                            }
                            VerticalScrollbar(
                                modifier = Modifier.fillMaxHeight(),
                                listState = listState
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Dashboard Top Bar ─────────────────────────────────────────────────────────

@Composable
private fun DashboardTopBar(
    appCount: Int,
    isGridView: Boolean,
    sortOrder: SortOrder,
    onToggleView: () -> Unit,
    onSortChanged: (SortOrder) -> Unit,
    onAddClick: () -> Unit
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "My Apps",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (appCount > 0) {
                    Text(
                        "$appCount app${if (appCount != 1) "s" else ""} docked",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // View toggle
            IconButton(onClick = onToggleView) {
                Icon(
                    imageVector = if (isGridView) Icons.AutoMirrored.Outlined.ViewList else Icons.Outlined.GridView,
                    contentDescription = "Toggle view",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sort
            Box {
                IconButton(onClick = { sortMenuExpanded = true }) {
                    Icon(
                        Icons.Filled.FilterList,
                        contentDescription = "Sort",
                        tint = if (sortOrder != SortOrder.NEWEST) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    SortMenuItems.forEach { (label, order) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    label,
                                    color = if (sortOrder == order) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (sortOrder == order) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            onClick = { onSortChanged(order); sortMenuExpanded = false },
                            trailingIcon = if (sortOrder == order) {
                                { Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            // FAB (mobile only - shown inline in top bar)
            if (platformIsAndroid) {
                FilledIconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(20.dp))
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

private val SortMenuItems = listOf(
    "Newest first"     to SortOrder.NEWEST,
    "Oldest first"     to SortOrder.OLDEST,
    "A → Z"            to SortOrder.NAME_ASC,
    "Z → A"            to SortOrder.NAME_DESC,
    "Most used"        to SortOrder.MOST_USED,
    "Least used"       to SortOrder.LEAST_USED,
)

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun DashboardEmptyState(onAddClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp).fadeSlideIn(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(96.dp).clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Apps,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Your Dock is Empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Add your favourite web apps for instant\none-click access from any device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add Your First App", fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── App List Card (list view) ─────────────────────────────────────────────────

@Composable
fun AppListCard(
    app: WebApp,
    onClick: () -> Unit,
    onDetails: (() -> Unit)? = null,
    onDelete: () -> Unit
) {
    val iconColors = listOf(
        Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF06B6D4),
        Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444)
    )
    val accentColor = iconColors[kotlin.math.abs(app.name.hashCode()) % iconColors.size]

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Accent bar
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(accentColor))
            
            Row(
                modifier = Modifier.weight(1f)
                    .clickable { onClick() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // App icon
                AppIcon(app = app, size = 44.dp, cornerRadius = 12.dp, accentColor = accentColor)

                // App info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = app.url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Badges + overflow
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (app.incognitoMode) {
                        Icon(Icons.Filled.VisibilityOff, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp))
                    }
                    if (app.isolatedProfile) {
                        Icon(Icons.Filled.Shield, null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp))
                    }
                    if (app.launchCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text("${app.launchCount}×", style = MaterialTheme.typography.labelSmall,
                                 modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                 color = MaterialTheme.colorScheme.onSecondaryContainer,
                                 fontWeight = FontWeight.Bold)
                        }
                    }

                    // universal 3-dot menu (replaces swipe actions)
                    var menuExpanded by remember { mutableStateOf(false) }
                    Box {
                            IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.MoreVert, null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = { menuExpanded = false; onDetails?.invoke() },
                                    leadingIcon = { Icon(Icons.Filled.Edit, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                    onClick = { menuExpanded = false; onDelete() },
                                    leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
                                )
                            }
                    }
                }
            }
        }
    }
}

// ── App Grid Card (grid view) ─────────────────────────────────────────────────

@Composable
@Suppress("UNUSED_PARAMETER")
fun AppGridCard(app: WebApp, onClick: () -> Unit, onLongClick: () -> Unit) {
    val iconColors = listOf(
        Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF06B6D4),
        Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444)
    )
    val accentColor = iconColors[kotlin.math.abs(app.name.hashCode()) % iconColors.size]

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Box(modifier = Modifier.clickable { onClick() }) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppIcon(app = app, size = 72.dp, cornerRadius = 20.dp, accentColor = accentColor)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!app.category.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = app.category.orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Shared App Icon ───────────────────────────────────────────────────────────

@Composable
fun AppIcon(app: WebApp, size: androidx.compose.ui.unit.Dp, cornerRadius: androidx.compose.ui.unit.Dp, accentColor: Color) {
    Box(
        modifier = Modifier.size(size).clip(RoundedCornerShape(cornerRadius))
            .background(accentColor.copy(alpha = 0.12f))
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        val iconUrl = remember(app.iconPath, app.url) {
            app.iconPath ?: run {
                try {
                    val domain = io.ktor.http.Url(app.url).host
                    if (domain.isNotBlank() && domain != "localhost")
                        "https://www.google.com/s2/favicons?domain=$domain&sz=128" else ""
                } catch (e: Exception) { "" }
            }
        }
        
        if (iconUrl.isNotBlank()) {
            val resolvedPath = if (iconUrl.startsWith("http") || iconUrl.startsWith("data:") || iconUrl.startsWith("file://"))
                iconUrl else "file:///${iconUrl.replace("\\", "/")}"
            io.kamel.image.KamelImage(
                resource = io.kamel.image.asyncPainterResource(resolvedPath),
                contentDescription = "${app.name} icon",
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(cornerRadius)),
                onFailure = { AppIconFallback(app.name, accentColor, size) }
            )
        } else {
            AppIconFallback(app.name, accentColor, size)
        }
    }
}

@Composable
private fun AppIconFallback(name: String, accentColor: Color, size: androidx.compose.ui.unit.Dp) {
    val style = when {
        size >= 56.dp -> MaterialTheme.typography.titleLarge
        size >= 44.dp -> MaterialTheme.typography.titleMedium
        else          -> MaterialTheme.typography.titleSmall
    }
    Text(
        text = name.take(1).uppercase(),
        style = style,
        fontWeight = FontWeight.Bold,
        color = accentColor
    )
}
