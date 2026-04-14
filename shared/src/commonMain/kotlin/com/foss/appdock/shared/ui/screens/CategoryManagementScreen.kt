package com.foss.appdock.shared.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.domain.Category
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid
import com.foss.appdock.shared.platform.platformIsDesktop
import kotlinx.coroutines.launch

// ── Manage Categories Screen ──────────────────────────────────────────────────

@Composable
@Suppress("UNUSED_PARAMETER")
fun ManageCategoriesScreen(
    databaseHelper: DatabaseHelper,
    onCategoryClick: (Category) -> Unit,
    onTabSelected: (BottomNavTab) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onShowFlyout: (String, androidx.compose.ui.graphics.vector.ImageVector) -> Unit = { _, _ -> }
) {
    val categories by databaseHelper.getAllCategories().collectAsState(emptyList())
    val webApps by databaseHelper.getAllWebApps().collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(SortOrder.NAME_ASC) }

    val sortedCategories = remember(categories, sortOrder) {
        when (sortOrder) {
            SortOrder.NAME_ASC  -> categories.sortedBy { it.name.lowercase() }
            SortOrder.NAME_DESC -> categories.sortedByDescending { it.name.lowercase() }
            else                -> categories
        }
    }

    // ── Palette for category icons ────────────────────────────────────────────
    val iconColors = listOf(
        Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF06B6D4),
        Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444),
        Color(0xFFF472B6), Color(0xFF4ADE80)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Categories", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        if (categories.isNotEmpty()) {
                            Text("${categories.size} collection${if (categories.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    SortDropdownList(currentSort = sortOrder, onSortChanged = { sortOrder = it })
                    FilledIconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary)
                    ) { Icon(Icons.Filled.Add, "New category", modifier = Modifier.size(18.dp)) }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            // ── Content ───────────────────────────────────────────────────────
            if (categories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Filled.FolderOpen,
                        title = "No Categories Yet",
                        subtitle = "Organize your apps into custom collections.",
                        action = {
                            Button(onClick = { showCreateDialog = true },
                                shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Create First Category")
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
                        items(sortedCategories, key = { it.id }) { category ->
                            val color = iconColors[kotlin.math.abs(category.name.hashCode()) % iconColors.size]
                            val appCount = webApps.count { it.category == category.name }
                            CategoryListCard(
                                category = category,
                                appCount = appCount,
                                iconColor = color,
                                onClick = { onCategoryClick(category) },
                                onEdit = { editingCategory = category },
                                onDelete = { deletingCategory = category }
                            )
                        }
                    }
                    VerticalScrollbar(modifier = Modifier.fillMaxHeight(), listState = listState)
                }
            }
        }

        // ── Dialogs ───────────────────────────────────────────────────────────

        // Create category
        if (showCreateDialog) {
            var newName by remember { mutableStateOf("") }
            val fr = remember { FocusRequester() }
            LaunchedEffect(Unit) { fr.requestFocus() }
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                icon = { Icon(Icons.Filled.CreateNewFolder, null, tint = MaterialTheme.colorScheme.primary) },
                title = { Text("New Category", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = newName, onValueChange = { newName = it }, singleLine = true,
                        label = { Text("Category Name") },
                        modifier = Modifier.fillMaxWidth().focusRequester(fr),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant)
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (newName.isNotBlank()) {
                            scope.launch {
                                databaseHelper.addCategory(newName.trim())
                                showCreateDialog = false
                            }
                        }
                    }) { Text("Create", fontWeight = FontWeight.SemiBold) }
                },
                dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") } }
            )
        }

        // Rename category
        editingCategory?.let { cat ->
            var editedName by remember { mutableStateOf(cat.name) }
            val fr = remember { FocusRequester() }
            LaunchedEffect(Unit) { fr.requestFocus() }
            AlertDialog(
                onDismissRequest = { editingCategory = null },
                icon = { Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.primary) },
                title = { Text("Rename Category", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = editedName, onValueChange = { editedName = it }, singleLine = true,
                        modifier = Modifier.fillMaxWidth().focusRequester(fr),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant)
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (editedName.isNotBlank()) {
                            scope.launch {
                                databaseHelper.updateCategory(cat.copy(name = editedName.trim()))
                                // silenced
                                editingCategory = null
                            }
                        }
                    }) { Text("Save", fontWeight = FontWeight.SemiBold) }
                },
                dismissButton = { TextButton(onClick = { editingCategory = null }) { Text("Cancel") } }
            )
        }

        // Delete category
        deletingCategory?.let { cat ->
            AlertDialog(
                onDismissRequest = { deletingCategory = null },
                icon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("Delete \"${cat.name}\"?", fontWeight = FontWeight.Bold) },
                text = { Text("Apps in this category will become uncategorized. This cannot be undone.") },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            databaseHelper.deleteCategory(cat.id)
                            deletingCategory = null
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = { TextButton(onClick = { deletingCategory = null }) { Text("Cancel") } }
            )
        }
    }
}

// ── Category list card ─────────────────────────────────────────────────────────

@Composable
fun CategoryListCard(
    category: Category,
    appCount: Int,
    iconColor: Color,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column {
            // Gradient accent strip
            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(
                androidx.compose.ui.graphics.Brush.horizontalGradient(
                    colors = listOf(iconColor.copy(alpha = 0.5f), iconColor)
                )
            ))
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onClick() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                        .background(iconColor.copy(alpha = 0.12f))
                        .border(1.dp, iconColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.FolderOpen, null, tint = iconColor, modifier = Modifier.size(22.dp)) }

                Column(modifier = Modifier.weight(1f)) {
                    Text(category.name, style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }

                // App count badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        "$appCount app${if (appCount != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                // Context Menu (Universal)
                var menuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.MoreVert, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            onClick = { menuExpanded = false; onEdit() },
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
