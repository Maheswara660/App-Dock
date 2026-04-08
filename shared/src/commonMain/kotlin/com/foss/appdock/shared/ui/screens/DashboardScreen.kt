package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.VerticalScrollbar
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
                                        .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // Header
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                // Logo & Title
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
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
                                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                                        EmptyState(
                                                title = "Your Dock is Empty",
                                                subtitle =
                                                        "Start by adding your favorite web apps to the dock for instant access."
                                        )
                                }
                        } else {
                                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                        LazyColumn(
                                                state = listState,
                                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                                contentPadding = PaddingValues(bottom = 90.dp, start = 24.dp, end = 16.dp)
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

                                        VerticalScrollbar(
                                                modifier = Modifier.fillMaxHeight(),
                                                listState = listState
                                        )
                                }
                        }
                }
        }
}


@Composable
fun AppGridItem(
    app: WebApp,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable { onClick() },
        color = adaptiveSurfaceVariantBackground(),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, adaptiveSurfaceVariantBorder())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = app.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
