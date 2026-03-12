package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
        webApps: List<WebApp>,
        currentTab: BottomNavTab = BottomNavTab.APPS,
        onAddAppClick: () -> Unit,
        onAppClick: (WebApp) -> Unit,
        onAppDetails: (WebApp) -> Unit,
        onDeleteApp: (WebApp) -> Unit,
        onTabSelected: (BottomNavTab) -> Unit,
        snackbarHostState: SnackbarHostState
) {
        var sortOrder by remember { mutableStateOf(SortOrder.NEWEST) }

        val listState = rememberLazyListState()

        val sortedApps =
                remember(webApps, sortOrder) {
                        when (sortOrder) {
                                SortOrder.NAME_ASC -> webApps.sortedBy { it.name.lowercase() }
                                SortOrder.NAME_DESC ->
                                        webApps.sortedByDescending { it.name.lowercase() }
                                SortOrder.NEWEST -> webApps.sortedByDescending { it.createdAt }
                                SortOrder.OLDEST -> webApps.sortedBy { it.createdAt }
                                SortOrder.MOST_USED -> webApps.sortedByDescending { it.launchCount }
                                SortOrder.LEAST_USED -> webApps.sortedBy { it.launchCount }
                                SortOrder.DATE_ADDED_NEWEST ->
                                        webApps.sortedByDescending { it.createdAt }
                                SortOrder.DATE_ADDED_OLDEST -> webApps.sortedBy { it.createdAt }
                        }
                }

        AppScaffold(
                snackbarHostState = snackbarHostState,
                bottomBar = {
                        BottomNavBar(
                                selectedTab = currentTab,
                                onTabSelected = onTabSelected,
                                onFabClick = {
                                        if (currentTab == BottomNavTab.CATEGORY) {
                                                // In Category screen it should open "Create
                                                // Category"
                                                // But for now, routing logic falls outside this
                                                // specific view
                                                onAddAppClick()
                                        } else {
                                                onAddAppClick()
                                        }
                                }
                        )
                }
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // Header
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                // Logo & Title
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        // Header label
                                        Text(
                                                text = "App Dock",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = adaptiveOnSurface()
                                        )
                                }

                                // Sort Dropdown
                                SortDropdownList(
                                        currentSort = sortOrder,
                                        onSortChanged = { sortOrder = it }
                                )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (webApps.isEmpty()) {
                                // ── Empty State ──────────────────────────────────────────────────
                                EmptyState(
                                        title = "Your Dock is Empty",
                                        subtitle =
                                                "Start by adding your favorite web apps to the dock for instant access."
                                )
                        } else {
                                BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                        val maxWidth = maxWidth
                                        val columnCount =
                                                when {
                                                        maxWidth > 1200.dp -> 4
                                                        maxWidth > 900.dp -> 3
                                                        maxWidth > 600.dp -> 2
                                                        else -> 1
                                                }

                                        if (columnCount > 1) {
                                                androidx.compose.foundation.lazy.grid
                                                        .LazyVerticalGrid(
                                                                columns =
                                                                        androidx.compose.foundation
                                                                                .lazy.grid.GridCells
                                                                                .Fixed(columnCount),
                                                                modifier = Modifier.fillMaxSize(),
                                                                contentPadding =
                                                                        PaddingValues(
                                                                                bottom = 90.dp
                                                                        ),
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(16.dp),
                                                                verticalArrangement =
                                                                        Arrangement.spacedBy(16.dp)
                                                        ) {
                                                                items(
                                                                        sortedApps,
                                                                        key = { it.id }
                                                                ) { app ->
                                                                        SwipeableAppListItem(
                                                                                app = app,
                                                                                onClick = {
                                                                                        onAppClick(
                                                                                                app
                                                                                        )
                                                                                },
                                                                                onDetails = {
                                                                                        onAppDetails(
                                                                                                app
                                                                                        )
                                                                                },
                                                                                onDelete = {
                                                                                        onDeleteApp(
                                                                                                app
                                                                                        )
                                                                                }
                                                                        )
                                                                }
                                                        }
                                        } else {
                                                LazyColumn(
                                                        state = listState,
                                                        modifier = Modifier.fillMaxSize(),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(12.dp),
                                                        contentPadding =
                                                                PaddingValues(bottom = 90.dp)
                                                ) {
                                                        items(sortedApps, key = { it.id }) { app ->
                                                                SwipeableAppListItem(
                                                                        app = app,
                                                                        onClick = {
                                                                                onAppClick(app)
                                                                        },
                                                                        onDetails = {
                                                                                onAppDetails(app)
                                                                        },
                                                                        onDelete = {
                                                                                onDeleteApp(app)
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
