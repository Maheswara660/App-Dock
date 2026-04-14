package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid
import com.foss.appdock.shared.ui.theme.staggeredFadeIn

@Composable
@Suppress("UNUSED_PARAMETER")
fun SearchScreen(
    webApps: List<WebApp>,
    currentTab: BottomNavTab,
    onAppClick: (WebApp) -> Unit,
    onAppDetails: (WebApp) -> Unit,
    onDeleteApp: (WebApp) -> Unit,
    onTabSelected: (BottomNavTab) -> Unit,
    onFabClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var query by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val filtered = remember(webApps, query) {
        if (query.isBlank()) webApps
        else webApps.filter { app ->
            app.name.contains(query, ignoreCase = true) ||
            app.url.contains(query, ignoreCase = true) ||
            (app.category ?: "").contains(query, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Search Header ─────────────────────────────────────────────────
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text("Search", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search apps, URLs, categories…") },
                        leadingIcon = { Icon(Icons.Filled.Search, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                        trailingIcon = if (query.isNotBlank()) {
                            { IconButton(onClick = { query = "" }) {
                                Icon(Icons.Filled.Close, "Clear", modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }}
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            // ── Results ───────────────────────────────────────────────────────
            when {
                query.isBlank() -> {
                    // Idle state
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(
                            icon = Icons.Filled.Search,
                            title = "Search Your Apps",
                            subtitle = "Type to search by name, URL, or category"
                        )
                    }
                }
                filtered.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(
                            icon = Icons.Filled.SearchOff,
                            title = "No Results",
                            subtitle = "No apps match \"$query\""
                        )
                    }
                }
                else -> {
                    Row(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    "${filtered.size} result${if (filtered.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            items(filtered, key = { it.id }) { app ->
                                AppListCard(
                                    app = app,
                                    onClick = { onAppClick(app) },
                                    onDetails = { onAppDetails(app) },
                                    onDelete = { onDeleteApp(app) },
                                )
                            }
                        }
                        VerticalScrollbar(modifier = Modifier.fillMaxHeight(), listState = listState)
                    }
                }
            }
        }
    }
}
