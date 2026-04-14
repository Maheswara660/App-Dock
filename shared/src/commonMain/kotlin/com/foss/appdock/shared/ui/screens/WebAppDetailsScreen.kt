package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.ImagePicker
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun WebAppDetailsScreen(
    app: WebApp,
    availableCategories: List<String>,
    onBack: () -> Unit,
    onSave: (WebApp) -> Unit,
    onDelete: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    // ── Editable state ────────────────────────────────────────────────────────
    var name by remember { mutableStateOf(app.name) }
    var url by remember { mutableStateOf(app.url) }
    var customIconUrl by remember { mutableStateOf<String?>(app.iconPath) }
    var isStandalone by remember { mutableStateOf(app.isStandalone) }
    var isolatedProfile by remember { mutableStateOf(app.isolatedProfile) }
    var incognitoMode by remember { mutableStateOf(app.incognitoMode) }
    var selectedCategory by remember { mutableStateOf(app.category) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formValid = name.isNotBlank() && url.isNotBlank()
    val hasChanges = name != app.name || url != app.url || customIconUrl != app.iconPath ||
        isStandalone != app.isStandalone || isolatedProfile != app.isolatedProfile ||
        incognitoMode != app.incognitoMode || selectedCategory != app.category
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header bar ────────────────────────────────────────────────────
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        Text("App Details", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(app.name, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    // Unsaved changes indicator
                    if (hasChanges) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text("Unsaved", style = MaterialTheme.typography.labelSmall) },
                            icon = { Icon(Icons.Filled.Edit, null, modifier = Modifier.size(14.dp)) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            // ── Scrollable content ────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // ── Hero: icon + launch stats ─────────────────────────────
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Tappable icon
                            ImagePicker(onImagePicked = { path -> if (path != null) customIconUrl = path }) { launchPicker ->
                                val iconUrl = customIconUrl ?: run {
                                    try {
                                        val domain = io.ktor.http.Url(url).host
                                        if (domain.isNotBlank() && domain != "localhost")
                                            "https://www.google.com/s2/favicons?domain=$domain&sz=128" else ""
                                    } catch (e: Exception) { "" }
                                }
                                Box(
                                    modifier = Modifier.size(96.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { launchPicker() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (iconUrl.isNotBlank()) {
                                        val resolved = if (iconUrl.startsWith("http") ||
                                            iconUrl.startsWith("data:") || iconUrl.startsWith("file://"))
                                            iconUrl else "file:///${iconUrl.replace("\\", "/")}"
                                        io.kamel.image.KamelImage(
                                            resource = io.kamel.image.asyncPainterResource(resolved),
                                            contentDescription = "${app.name} icon",
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)),
                                            onLoading = {
                                                CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                                            },
                                            onFailure = {
                                                Text(app.name.take(1).uppercase(),
                                                    style = MaterialTheme.typography.headlineLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        )
                                    } else {
                                        Text(app.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.headlineLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    // Camera badge
                                    Box(
                                        modifier = Modifier.size(26.dp).align(Alignment.BottomEnd)
                                            .clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) { Icon(Icons.Filled.CameraAlt, "Change Icon",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(14.dp)) }
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(app.name, style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.height(4.dp))
                                // Stats row
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (app.launchCount > 0) {
                                        AssistChip(
                                            onClick = {},
                                            label = {
                                                AnimatedContent(
                                                    targetState = app.launchCount,
                                                    transitionSpec = {
                                                        (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
                                                    }
                                                ) { count ->
                                                    Text("$count launches", style = MaterialTheme.typography.labelSmall)
                                                }
                                            },
                                            leadingIcon = { Icon(Icons.Filled.RocketLaunch, null, modifier = Modifier.size(14.dp)) }
                                        )
                                    }
                                    if (!app.category.isNullOrBlank()) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(app.category.orEmpty(), style = MaterialTheme.typography.labelSmall) },
                                            leadingIcon = { Icon(Icons.Filled.Folder, null, modifier = Modifier.size(14.dp)) }
                                        )
                                    }
                                }
                                if (customIconUrl != null) {
                                    TextButton(
                                        onClick = { customIconUrl = null },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Clear custom icon", style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }

                    // ── Section: General ──────────────────────────────────────
                    DetailSection(title = "General Configuration") {
                        SolidTextField(label = "App Name", value = name,
                            onValueChange = { name = it }, placeholder = "e.g. Notion, GitHub")
                        Spacer(Modifier.height(2.dp))
                        SolidTextField(label = "Starting URL", value = url,
                            onValueChange = { url = it }, placeholder = "https://",
                            leadingIcon = Icons.Filled.Language)
                        Spacer(Modifier.height(2.dp))
                        SolidDropdownField(
                            label = "Category",
                            value = selectedCategory ?: "No Category",
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = it },
                            options = listOf("No Category") + availableCategories,
                            onOptionSelected = { cat ->
                                selectedCategory = if (cat == "No Category") null else cat
                                categoryExpanded = false
                            }
                        )
                    }

                    // ── Section: Preferences (desktop only) ───────────────────
                    if (!platformIsAndroid) {
                        DetailSection(title = "Preferences") {
                            listOf(
                                Triple("Native-like Window", "Hide browser UI and address bar", isStandalone),
                                Triple("Isolated Profile", "Separate browser profile for privacy", isolatedProfile),
                                Triple("Incognito Mode", "Always open in private browsing mode", incognitoMode),
                            ).forEachIndexed { idx, (t, s, checked) ->
                                AdvancedSettingToggle(title = t, subtitle = s, checked = checked,
                                    onCheckedChange = { v ->
                                        when (idx) {
                                            0 -> isStandalone = v
                                            1 -> isolatedProfile = v
                                            2 -> incognitoMode = v
                                        }
                                    })
                                if (idx < 2) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            }
                        }
                    }

                    // ── Danger zone ───────────────────────────────────────────
                    DetailSection(title = "Danger Zone") {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Delete App", style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                                Text("Permanently removes this app and its settings.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(38.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp)
                            ) {
                                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Delete", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    scrollState = scrollState
                )
            }

            // ── Sticky footer ─────────────────────────────────────────────────
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel", fontWeight = FontWeight.SemiBold) }

                Button(
                    onClick = {
                        if (formValid)
                            onSave(app.copy(name = name, url = url, isStandalone = isStandalone,
                                isolatedProfile = isolatedProfile, incognitoMode = incognitoMode,
                                category = selectedCategory, iconPath = customIconUrl))
                    },
                    enabled = formValid && hasChanges,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── Delete confirmation dialog ────────────────────────────────────────────
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("Delete \"${app.name}\"?", fontWeight = FontWeight.Bold) },
                text = { Text("This will permanently remove the app and all its settings. This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = { showDeleteDialog = false; onDelete() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Delete", fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

// ── Reusable detail section card ──────────────────────────────────────────────

@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title.uppercase(), style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                content()
            }
        }
    }
}
