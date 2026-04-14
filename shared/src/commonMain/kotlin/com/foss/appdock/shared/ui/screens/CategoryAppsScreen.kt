package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.domain.Category
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid

@Composable
@Suppress("UNUSED_PARAMETER")
fun CategoryAppsScreen(
    category: Category,
    databaseHelper: DatabaseHelper,
    onAppClick: (WebApp) -> Unit,
    onAppDelete: (WebApp) -> Unit,
    onAppEdit: (WebApp) -> Unit,
    onAddWebApp: () -> Unit,
    onBack: () -> Unit,
    onTabSelected: (BottomNavTab) -> Unit,
    snackbarHostState: SnackbarHostState,
    onShowFlyout: (String, androidx.compose.ui.graphics.vector.ImageVector) -> Unit = { _, _ -> }
) {
    val allApps by databaseHelper.getAllWebApps().collectAsState(emptyList())
    var sortOrder by remember { mutableStateOf(SortOrder.NAME_ASC) }
    val listState = rememberLazyListState()

    val categoryApps = remember(allApps, category, sortOrder) {
        val apps = allApps.filter { it.category == category.name }
        when (sortOrder) {
            SortOrder.NAME_ASC, SortOrder.NEWEST    -> apps.sortedBy { it.name.lowercase() }
            SortOrder.NAME_DESC, SortOrder.OLDEST   -> apps.sortedByDescending { it.name.lowercase() }
            SortOrder.DATE_ADDED_NEWEST             -> apps.sortedByDescending { it.createdAt }
            SortOrder.DATE_ADDED_OLDEST             -> apps.sortedBy { it.createdAt }
            SortOrder.MOST_USED                     -> apps.sortedByDescending { it.launchCount }
            SortOrder.LEAST_USED                    -> apps.sortedBy { it.launchCount }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        Text(
                            category.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            "${categoryApps.size} app${if (categoryApps.size != 1) "s" else ""} in this collection",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SortDropdownList(currentSort = sortOrder, onSortChanged = { sortOrder = it })
                    if (!platformIsAndroid) {
                        Spacer(Modifier.width(8.dp))
                        FilledIconButton(onClick = onAddWebApp, modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary)) {
                            Icon(Icons.Filled.Add, "Add", modifier = Modifier.size(18.dp))
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            if (categoryApps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Filled.Folder,
                        title = "Empty Category",
                        subtitle = "No apps assigned to \"${category.name}\" yet.",
                        action = {
                            Button(onClick = onAddWebApp, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
                                Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Add App Here")
                            }
                        }
                    )
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categoryApps, key = { it.id }) { app ->
                            AppListCard(
                                app = app,
                                onClick = { onAppClick(app) },
                                onDetails = { onAppEdit(app) },
                                onDelete = { onAppDelete(app) }
                            )
                        }
                    }
                    VerticalScrollbar(modifier = Modifier.fillMaxHeight(), listState = listState)
                }
            }
        }
    }
}
