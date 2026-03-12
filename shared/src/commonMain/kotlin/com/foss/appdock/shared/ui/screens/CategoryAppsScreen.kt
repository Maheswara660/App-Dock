package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.domain.Category
import com.foss.appdock.shared.domain.WebApp

@Composable
fun CategoryAppsScreen(
        category: Category,
        databaseHelper: DatabaseHelper,
        onAppClick: (WebApp) -> Unit,
        onAppDelete: (WebApp) -> Unit,
        onAppEdit: (WebApp) -> Unit,
        onAddWebApp: () -> Unit,
        onBack: () -> Unit,
        onTabSelected: (BottomNavTab) -> Unit,
        snackbarHostState: SnackbarHostState
) {
    val allApps by databaseHelper.getAllWebApps().collectAsState(emptyList())
    var sortOrder by remember { mutableStateOf(SortOrder.NAME_ASC) }

    val categoryApps =
            remember(allApps, category, sortOrder) {
                val apps = allApps.filter { it.category == category.name }
                when (sortOrder) {
                    SortOrder.NAME_ASC -> apps.sortedBy { it.name.lowercase() }
                    SortOrder.NAME_DESC -> apps.sortedByDescending { it.name.lowercase() }
                    SortOrder.DATE_ADDED_NEWEST -> apps.sortedByDescending { it.createdAt }
                    SortOrder.DATE_ADDED_OLDEST -> apps.sortedBy { it.createdAt }
                    else -> apps
                }
            }

    AppScaffold(
            snackbarHostState = snackbarHostState,
            bottomBar = {
                BottomNavBar(
                        selectedTab = BottomNavTab.CATEGORY,
                        onTabSelected = onTabSelected,
                        onFabClick = onAddWebApp
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)) {
            DockPageHeader(title = category.name, onBack = onBack)

            Spacer(Modifier.height(24.dp))

            SortDropdownList(currentSort = sortOrder, onSortChanged = { sortOrder = it })

            Spacer(Modifier.height(16.dp))

            if (categoryApps.isEmpty()) {
                EmptyState(
                        title = "No apps in this category",
                        subtitle = "Add web apps here to keep your collection organized."
                )
            } else {
                LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(categoryApps, key = { it.id }) { app ->
                        SwipeableAppListItem(
                                app = app,
                                onClick = { onAppClick(app) },
                                onDelete = { onAppDelete(app) },
                                onEdit = { onAppEdit(app) }
                        )
                    }
                }
            }
        }
    }
}
