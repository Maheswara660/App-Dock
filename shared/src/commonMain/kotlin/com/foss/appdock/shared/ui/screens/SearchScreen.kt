package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid
import com.foss.appdock.shared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
        webApps: List<WebApp>,
        availableCategories: List<String>,
        onBack: () -> Unit,
        onAppClick: (WebApp) -> Unit,
        onAddAppClick: () -> Unit,
        onTabSelected: (BottomNavTab) -> Unit,
        snackbarHostState: SnackbarHostState
) {
        var query by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf<String?>(null) }
        val focusRequester = remember { FocusRequester() }
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        val results =
                webApps.filter { app ->
                        val matchesQuery =
                                query.isBlank() ||
                                        app.name.contains(query, ignoreCase = true) ||
                                        app.url.contains(query, ignoreCase = true)
                        val matchesCategory =
                                selectedCategory == null || app.category == selectedCategory
                        matchesQuery && matchesCategory
                }

        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        AppScaffold(
                snackbarHostState = snackbarHostState,
                bottomBar = {
                        BottomNavBar(
                                selectedTab = BottomNavTab.SEARCH,
                                onTabSelected = onTabSelected,
                                onFabClick = onAddAppClick
                        )
                }
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(vertical = 24.dp)
                ) {
                        Box(modifier = Modifier.padding(horizontal = if (platformIsAndroid) 16.dp else 24.dp)) {
                                DockPageHeader(title = "Search", onBack = onBack)
                        }
                        // M3 OutlinedTextField search input
                        OutlinedTextField(
                                value = query,
                                onValueChange = { query = it },
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = if (platformIsAndroid) 16.dp else 24.dp, vertical = 8.dp)
                                                .focusRequester(focusRequester),
                                placeholder = {
                                        Text(
                                                "Search apps...",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                },
                                leadingIcon = {
                                        Icon(
                                                Icons.Filled.Search,
                                                null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                },
                                trailingIcon =
                                        if (query.isNotEmpty())
                                                ({
                                                        IconButton(onClick = { query = "" }) {
                                                                Icon(
                                                                        Icons.Filled.Clear,
                                                                        null,
                                                                        tint =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                        }
                                                })
                                        else null,
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor =
                                                        MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor =
                                                        adaptiveSurfaceVariantBorder(),
                                                focusedContainerColor =
                                                        adaptiveSurfaceVariantBackground(),
                                                unfocusedContainerColor =
                                                        adaptiveSurfaceVariantBackground(),
                                                focusedTextColor = adaptiveOnSurface(),
                                                unfocusedTextColor = adaptiveOnSurface()
                                        )
                        )

                        // Category Filter Chips
                        if (availableCategories.isNotEmpty()) {
                                androidx.compose.foundation.lazy.LazyRow(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = if (platformIsAndroid) 16.dp else 24.dp,
                                                                vertical = 8.dp
                                                        ),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        item {
                                                FilterChip(
                                                        selected = selectedCategory == null,
                                                        onClick = { selectedCategory = null },
                                                        label = {
                                                                Text(
                                                                        "All",
                                                                        fontWeight =
                                                                                FontWeight.SemiBold
                                                                )
                                                        },
                                                        colors =
                                                                FilterChipDefaults.filterChipColors(
                                                                        selectedContainerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                        selectedLabelColor =
                                                                                Color.White
                                                                )
                                                )
                                        }
                                        items(availableCategories) { category ->
                                                FilterChip(
                                                        selected = selectedCategory == category,
                                                        onClick = { selectedCategory = category },
                                                        label = {
                                                                Text(
                                                                        category,
                                                                        fontWeight =
                                                                                FontWeight.SemiBold
                                                                )
                                                        },
                                                        colors =
                                                                FilterChipDefaults.filterChipColors(
                                                                        selectedContainerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                        selectedLabelColor =
                                                                                Color.White
                                                                )
                                                )
                                        }
                                }
                        }

                        if ((query.isNotEmpty() || selectedCategory != null) && results.isEmpty()) {
                                // No results empty state
                                Column(
                                        modifier =
                                                Modifier.fillMaxWidth().weight(1f).padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        Surface(
                                                shape = RoundedCornerShape(32.dp),
                                                color = SolidPanelBackground,
                                                modifier = Modifier.size(120.dp)
                                        ) {
                                                Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier.fillMaxSize()
                                                ) {
                                                        Icon(
                                                                Icons.Filled.Search,
                                                                null,
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .outline,
                                                                modifier = Modifier.size(48.dp)
                                                        )
                                                }
                                        }
                                        Spacer(Modifier.height(24.dp))
                                        Text(
                                                "No Results Found",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = adaptiveOnSurface()
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                                "No app matching \"$query\". Try a different query or add a new app.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                        )
                                        Spacer(Modifier.height(28.dp))
                                        Button(
                                                onClick = onAddAppClick,
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                contentColor =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimary
                                                        ),
                                                modifier = Modifier.fillMaxWidth().height(48.dp)
                                        ) { Text("Add New App", fontWeight = FontWeight.SemiBold) }
                                }
                        } else {
                                if (query.isNotEmpty() || selectedCategory != null) {
                                        Text(
                                                "RESULTS  •  ${results.size}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = if (platformIsAndroid) 16.dp else 24.dp, vertical = 8.dp)
                                        )
                                }
                                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                        LazyColumn(
                                                state = listState,
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp, start = if (platformIsAndroid) 16.dp else 24.dp, end = if (platformIsAndroid) 16.dp else 24.dp)
                                        ) {
                                                items(results) { app ->
                                                        AppListItem(
                                                                app = app,
                                                                onClick = { onAppClick(app) }
                                                        )
                                                }

                                                item {
                                                        Spacer(Modifier.height(16.dp))
                                                        Surface(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                shape = RoundedCornerShape(16.dp),
                                                                color = adaptiveSurfaceVariantBackground(),
                                                                border =
                                                                        androidx.compose.foundation
                                                                                .BorderStroke(
                                                                                        1.dp,
                                                                                        adaptiveSurfaceVariantBorder()
                                                                                )
                                                        ) {
                                                                Column(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .padding(24.dp),
                                                                        horizontalAlignment =
                                                                                Alignment
                                                                                        .CenterHorizontally,
                                                                        verticalArrangement =
                                                                                Arrangement.spacedBy(8.dp)
                                                                ) {
                                                                        Surface(
                                                                                shape =
                                                                                        RoundedCornerShape(
                                                                                                16.dp
                                                                                        ),
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .surfaceVariant,
                                                                                modifier =
                                                                                        Modifier.size(48.dp)
                                                                        ) {
                                                                                Box(
                                                                                        contentAlignment =
                                                                                                Alignment
                                                                                                        .Center,
                                                                                        modifier =
                                                                                                Modifier.fillMaxSize()
                                                                                ) {
                                                                                        Icon(
                                                                                                Icons.Filled
                                                                                                        .Add,
                                                                                                null,
                                                                                                tint =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary,
                                                                                                modifier =
                                                                                                        Modifier.size(
                                                                                                                24.dp
                                                                                                        )
                                                                                        )
                                                                                }
                                                                        }
                                                                        Text(
                                                                                "Can't find your app?",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .titleMedium,
                                                                                fontWeight =
                                                                                        FontWeight.SemiBold,
                                                                                color = adaptiveOnSurface()
                                                                        )
                                                                        Text(
                                                                                "Add a custom URL to dock any web service.",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant,
                                                                                textAlign = TextAlign.Center
                                                                        )
                                                                        Button(
                                                                                onClick = onAddAppClick,
                                                                                shape =
                                                                                        RoundedCornerShape(
                                                                                                12.dp
                                                                                        ),
                                                                                colors =
                                                                                        ButtonDefaults
                                                                                                .buttonColors(
                                                                                                        containerColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .primary
                                                                                                )
                                                                        ) { Text("Add Custom URL") }
                                                                }
                                                        }
                                                }
                                        }

                                        VerticalScrollbar(
                                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                                listState = listState
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun AppListItem(app: WebApp, onClick: () -> Unit) {
        Surface(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
                color = adaptiveSurfaceVariantBackground(),
                shape = RoundedCornerShape(16.dp),
                border =
                        androidx.compose.foundation.BorderStroke(
                                1.dp,
                                adaptiveSurfaceVariantBorder()
                        )
        ) {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(48.dp)
                        ) {
                                Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                ) {
                                        Text(
                                                app.name.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = adaptiveOnSurface()
                                        )
                                }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        app.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = adaptiveOnSurface()
                                )
                                Text(
                                        app.url,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                )
                        }
                }
        }
}
