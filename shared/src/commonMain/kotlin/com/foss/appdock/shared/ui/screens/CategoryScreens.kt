package com.foss.appdock.shared.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.foss.appdock.shared.platform.platformIsDesktop
import com.foss.appdock.shared.ui.theme.adaptiveOnSurface
import kotlin.math.abs
import kotlinx.coroutines.launch

// ─── Manage Categories Screen ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(
        databaseHelper: DatabaseHelper,
        onCategoryClick: (Category) -> Unit,
        onTabSelected: (BottomNavTab) -> Unit,
        onBack: () -> Unit,
        snackbarHostState: SnackbarHostState
) {
        val categories by databaseHelper.getAllCategories().collectAsState(emptyList())
        val coroutineScope = rememberCoroutineScope()
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        var editingCategory by remember { mutableStateOf<Category?>(null) }
        var deletingCategory by remember { mutableStateOf<Category?>(null) }
        var showCreateDialog by remember { mutableStateOf(false) }
        var sortOrder by remember { mutableStateOf(SortOrder.NAME_ASC) }

        val webApps by databaseHelper.getAllWebApps().collectAsState(emptyList())

        AppScaffold(
                snackbarHostState = snackbarHostState,
                bottomBar = {
                        BottomNavBar(
                                selectedTab = BottomNavTab.CATEGORY,
                                onTabSelected = onTabSelected,
                                onFabClick = { showCreateDialog = true }
                        )
                }
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                                DockPageHeader(title = "Categories", onBack = onBack)

                                Spacer(Modifier.height(16.dp))

                                SortDropdownList(
                                        currentSort = sortOrder,
                                        onSortChanged = { sortOrder = it }
                                )

                                Spacer(Modifier.height(16.dp))
                        }

                        if (categories.isEmpty()) {
                                // ── Empty State ──────────────────────────────────────────────────
                                Column(
                                        modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(120.dp)
                                                                .clip(RoundedCornerShape(32.dp))
                                                                .background(
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant
                                                                )
                                                                .padding(32.dp),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Filled.FolderOpen,
                                                        contentDescription = null,
                                                        tint =
                                                                adaptiveOnSurface()
                                                                        .copy(
                                                                                alpha = 0.5f
                                                                        ), // adaptive slate-like
                                                        // color
                                                        modifier = Modifier.fillMaxSize()
                                                )
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                                "No Categories",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                "Organize your favorite apps into custom collections.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                }
                        } else {
                                // ── Category List
                                // ──────────────────────────────────────────────────────
                                // Dynamic colors for icons
                                val iconColors =
                                        listOf(
                                                Color(0xFFF87171), // red-400
                                                Color(0xFFC084FC), // purple-400
                                                Color(0xFF818CF8), // indigo-400
                                                Color(0xFF4ADE80), // green-400
                                                Color(0xFFF472B6), // pink-400
                                                Color(0xFFCBD5E1) // slate-300
                                        )

                                val sortedCategories =
                                        remember(categories, sortOrder) {
                                                when (sortOrder) {
                                                        SortOrder.NAME_ASC ->
                                                                categories.sortedBy {
                                                                        it.name.lowercase()
                                                                }
                                                        SortOrder.NAME_DESC ->
                                                                categories.sortedByDescending {
                                                                        it.name.lowercase()
                                                                }
                                                        else -> categories
                                                }
                                        }

                                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                        LazyColumn(
                                                state = listState,
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                                contentPadding = PaddingValues(bottom = 90.dp, start = 24.dp, end = 24.dp)
                                        ) {
                                                items(sortedCategories, key = { it.id }) { category ->
                                                        val cHash = category.name.hashCode()
                                                        val catColor =
                                                                iconColors[
                                                                        kotlin.math.abs(cHash) %
                                                                                iconColors.size]

                                                        val appCount =
                                                                webApps.count {
                                                                        it.category == category.name
                                                                }
                                                        SwipeableCategoryListItem(
                                                                category = category,
                                                                appCount = appCount,
                                                                iconColor = catColor,
                                                                onClick = { onCategoryClick(category) },
                                                                onEdit = { editingCategory = category },
                                                                onDelete = { deletingCategory = category }
                                                        )
                                                }
                                        }

                                        VerticalScrollbar(
                                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                                listState = listState
                                        )
                                }
                        }
                }

                // ── Dialogs ─────────────────────────────────────────────────────────────

                if (editingCategory != null) {
                        var editedName by remember { mutableStateOf(editingCategory!!.name) }
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(Unit) { focusRequester.requestFocus() }

                        AlertDialog(
                                onDismissRequest = { editingCategory = null },
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                title = {
                                        Text(
                                                "Rename Category",
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                },
                                text = {
                                        OutlinedTextField(
                                                value = editedName,
                                                onValueChange = { editedName = it },
                                                singleLine = true,
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .focusRequester(focusRequester),
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                unfocusedBorderColor =
                                                                        MaterialTheme.colorScheme
                                                                                .outlineVariant,
                                                                focusedTextColor =
                                                                        adaptiveOnSurface(),
                                                                unfocusedTextColor =
                                                                        adaptiveOnSurface()
                                                        )
                                        )
                                },
                                confirmButton = {
                                        Button(
                                                onClick = {
                                                        if (editedName.isNotBlank()) {
                                                                coroutineScope.launch {
                                                                        databaseHelper
                                                                                .updateCategory(
                                                                                        editingCategory!!
                                                                                                .copy(
                                                                                                        name =
                                                                                                                editedName
                                                                                                                        .trim()
                                                                                                )
                                                                                )
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        "Category renamed to $editedName"
                                                                                )
                                                                        editingCategory = null
                                                                }
                                                        }
                                                },
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                        ) { Text("Save") }
                                },
                                dismissButton = {
                                        TextButton(onClick = { editingCategory = null }) {
                                                Text("Cancel", color = adaptiveOnSurface())
                                        }
                                }
                        )
                }

                if (deletingCategory != null) {
                        AlertDialog(
                                onDismissRequest = { deletingCategory = null },
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                title = {
                                        Text(
                                                "Delete Category",
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                },
                                text = {
                                        Text(
                                                "Delete \"${deletingCategory!!.name}\"? Apps in this category will become uncategorized.",
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                },
                                confirmButton = {
                                        Button(
                                                onClick = {
                                                        coroutineScope.launch {
                                                                databaseHelper.deleteCategory(
                                                                        deletingCategory!!.id
                                                                )
                                                                snackbarHostState.showSnackbar(
                                                                        "Category deleted"
                                                                )
                                                                deletingCategory = null
                                                        }
                                                },
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .error
                                                        )
                                        ) { Text("Delete") }
                                },
                                dismissButton = {
                                        TextButton(onClick = { deletingCategory = null }) {
                                                Text("Cancel", color = adaptiveOnSurface())
                                        }
                                }
                        )
                }

                if (showCreateDialog) {
                        var newCategoryName by remember { mutableStateOf("") }
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(Unit) { focusRequester.requestFocus() }

                        AlertDialog(
                                onDismissRequest = { showCreateDialog = false },
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                title = {
                                        Text(
                                                "New Category",
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                },
                                text = {
                                        OutlinedTextField(
                                                value = newCategoryName,
                                                onValueChange = { newCategoryName = it },
                                                label = { Text("Category Name") },
                                                singleLine = true,
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .focusRequester(focusRequester),
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                unfocusedBorderColor =
                                                                        MaterialTheme.colorScheme
                                                                                .outlineVariant,
                                                                focusedTextColor =
                                                                        adaptiveOnSurface(),
                                                                unfocusedTextColor =
                                                                        adaptiveOnSurface()
                                                        )
                                        )
                                },
                                confirmButton = {
                                        Button(
                                                onClick = {
                                                        if (newCategoryName.isNotBlank()) {
                                                                coroutineScope.launch {
                                                                        databaseHelper.addCategory(
                                                                                newCategoryName
                                                                                        .trim()
                                                                        )
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        "Category \"$newCategoryName\" created"
                                                                                )
                                                                        showCreateDialog = false
                                                                }
                                                        }
                                                },
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                        ) { Text("Create") }
                                },
                                dismissButton = {
                                        TextButton(onClick = { showCreateDialog = false }) {
                                                Text("Cancel", color = adaptiveOnSurface())
                                        }
                                }
                        )
                }
        }
}

@Composable
fun SwipeableCategoryListItem(
        category: Category,
        appCount: Int,
        iconColor: Color,
        onClick: () -> Unit,
        onEdit: () -> Unit,
        onDelete: () -> Unit
) {
        var offsetX by remember { mutableStateOf(0f) }
        val animatedOffset by animateFloatAsState(targetValue = offsetX)
        val revealThreshold = 120f

        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))) {
                // ── Background Layers (Edit/Delete) ───────────────────────────────
                val swipeProgress =
                        (kotlin.math.abs(animatedOffset) / revealThreshold).coerceIn(0f, 1f)
                val density = LocalDensity.current
                val revealThresholdDp = with(density) { revealThreshold.toDp() }

                if (!platformIsDesktop) {
                        Box(modifier = Modifier.matchParentSize().alpha(swipeProgress)) {
                                Row(modifier = Modifier.matchParentSize()) {
                                        // Edit (Left Swipe Reveal)
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
                                                                        onEdit()
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
                                                                contentDescription =
                                                                        "Edit Category",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(26.dp)
                                                        )
                                                }
                                        }
                                        // Delete (Right Swipe Reveal)
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
                                                                contentDescription =
                                                                        "Delete Category",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(26.dp)
                                                        )
                                                }
                                        }
                                }
                        }
                }

                // ── Foreground Layer (Category Item) ──────────────────────────────────
                Surface(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .offset { IntOffset(animatedOffset.toInt(), 0) }
                                        .let {
                                                if (!platformIsDesktop) {
                                                        it.draggable(
                                                                state =
                                                                        rememberDraggableState {
                                                                                delta ->
                                                                                val newOffset =
                                                                                        offsetX +
                                                                                                delta
                                                                                offsetX =
                                                                                        newOffset
                                                                                                .coerceIn(
                                                                                                        -revealThreshold,
                                                                                                        revealThreshold
                                                                                                )
                                                                        },
                                                                orientation =
                                                                        Orientation.Horizontal,
                                                                onDragStopped = {
                                                                        offsetX =
                                                                                when {
                                                                                        offsetX >
                                                                                                revealThreshold /
                                                                                                        2 ->
                                                                                                revealThreshold
                                                                                        offsetX <
                                                                                                -revealThreshold /
                                                                                                        2 ->
                                                                                                -revealThreshold
                                                                                        else -> 0f
                                                                                }
                                                                }
                                                        )
                                                } else {
                                                        it
                                                }
                                        }
                                        .clickable { onClick() },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .border(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.outlineVariant,
                                                        RoundedCornerShape(16.dp)
                                                )
                        ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Category Icon
                                                Box(
                                                        modifier =
                                                                Modifier.size(48.dp)
                                                                        .clip(
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                        )
                                                                        .background(
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant
                                                                        )
                                                                        .border(
                                                                                1.dp,
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .outlineVariant,
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                Icons.Filled.FolderOpen,
                                                                contentDescription = "Folder",
                                                                tint = iconColor,
                                                                modifier = Modifier.size(24.dp)
                                                        )
                                                }

                                                Column {
                                                        Text(
                                                                text = category.name,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = adaptiveOnSurface()
                                                        )

                                                        Text(
                                                                text = "$appCount apps",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }
                                        }

                                        if (platformIsDesktop) {
                                                var expanded by remember { mutableStateOf(false) }
                                                Box {
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
                                                                        text = { Text("Rename") },
                                                                        onClick = {
                                                                                expanded = false
                                                                                onEdit()
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
