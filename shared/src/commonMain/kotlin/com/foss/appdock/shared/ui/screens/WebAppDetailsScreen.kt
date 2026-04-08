package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.ImagePicker
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid
import com.foss.appdock.shared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebAppDetailsScreen(
        app: WebApp,
        availableCategories: List<String>,
        onBack: () -> Unit,
        onSave: (WebApp) -> Unit,
        onDelete: () -> Unit,
        snackbarHostState: SnackbarHostState
) {
        var name by remember { mutableStateOf(app.name) }
        var url by remember { mutableStateOf(app.url) }
        var customIconUrl by remember { mutableStateOf<String?>(app.iconPath) }
        var isStandalone by remember { mutableStateOf(app.isStandalone) }
        var isolatedProfile by remember { mutableStateOf(app.isolatedProfile) }
        var incognitoMode by remember { mutableStateOf(app.incognitoMode) }
        var selectedCategory by remember { mutableStateOf(app.category) }
        var categoryMenuExpanded by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var showIconUrlDialog by remember { mutableStateOf(false) }

        val scrollState = rememberScrollState()

        AppScaffold(snackbarHostState = snackbarHostState, bottomBar = {}) {
                Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .padding(horizontal = 16.dp)
                                                .verticalScroll(scrollState)
                        ) {
                        // ── Header ─────────────────────────────────────────────────────────────
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(40.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(
                                                                adaptiveSurfaceVariantBackground()
                                                        )
                                                        .clickable { onBack() },
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }

                                Text(
                                        text = "App Details",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = adaptiveOnSurface(),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.width(40.dp))
                        }
                        // ── Icon + Name Hero ─────────────────────────────────────────────
                        Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                ImagePicker(
                                        onImagePicked = { path ->
                                                if (path != null) customIconUrl = path
                                        }
                                ) { launchPicker ->
                                        Box(
                                                modifier =
                                                        Modifier.size(112.dp)
                                                                .clip(RoundedCornerShape(28.dp))
                                                                .background(
                                                                        adaptiveSurfaceVariantBackground()
                                                                )
                                                                .border(
                                                                        1.dp,
                                                                        adaptiveSurfaceVariantBorder(),
                                                                        RoundedCornerShape(28.dp)
                                                                )
                                                                .clickable { launchPicker() },
                                                contentAlignment = Alignment.Center
                                        ) {
                                                val derivedIconUrl =
                                                        customIconUrl
                                                                ?: run {
                                                                        val domain =
                                                                                try {
                                                                                        io.ktor.http
                                                                                                .Url(
                                                                                                        url
                                                                                                )
                                                                                                .host
                                                                                } catch (
                                                                                        e:
                                                                                                Exception) {
                                                                                        ""
                                                                                }
                                                                        if (domain.isNotBlank() &&
                                                                                        domain !=
                                                                                                "localhost"
                                                                        )
                                                                                "https://www.google.com/s2/favicons?domain=$domain&sz=128"
                                                                        else ""
                                                                }
                                                if (derivedIconUrl.isNotBlank()) {
                                                        val resolvedPath = if (
                                                                derivedIconUrl.startsWith("http") || 
                                                                derivedIconUrl.startsWith("data:") || 
                                                                derivedIconUrl.startsWith("file://")
                                                        ) {
                                                                derivedIconUrl
                                                        } else {
                                                                "file:///${derivedIconUrl.replace("\\", "/")}"
                                                        }

                                                        io.kamel.image.KamelImage(
                                                                resource =
                                                                        io.kamel.image
                                                                                .asyncPainterResource(
                                                                                        resolvedPath
                                                                                ),
                                                                contentDescription =
                                                                        "${app.name} icon",
                                                                modifier =
                                                                        Modifier.fillMaxSize()
                                                                                .clip(
                                                                                        RoundedCornerShape(
                                                                                                28.dp
                                                                                        )
                                                                                ),
                                                                onLoading = {
                                                                        CircularProgressIndicator(
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                32.dp
                                                                                        ),
                                                                                strokeWidth = 2.dp,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        )
                                                                },
                                                                onFailure = {
                                                                        Text(
                                                                                app.name
                                                                                        .take(1)
                                                                                        .uppercase(),
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .displaySmall,
                                                                                color =
                                                                                        adaptiveOnSurface(),
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        )
                                                                }
                                                        )
                                                } else {
                                                        Text(
                                                                app.name.take(1).uppercase(),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .displaySmall,
                                                                color = adaptiveOnSurface(),
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                        }
                                }
                                Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                        Text(
                                                app.name,
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = adaptiveOnSurface()
                                        )
                                        if (app.launchCount > 0) {
                                                Surface(
                                                        shape = CircleShape,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant
                                                ) {
                                                        Text(
                                                                "${app.launchCount} launches",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = adaptiveOnSurface(),
                                                                modifier =
                                                                        Modifier.padding(
                                                                                horizontal = 10.dp,
                                                                                vertical = 4.dp
                                                                        )
                                                        )
                                                }
                                        }
                                }
                        }

                        Spacer(Modifier.height(4.dp))

                        // ── General Config ───────────────────────────────────────────────
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                                M3SectionHeader("General Configuration")
                                Spacer(Modifier.height(8.dp))

                                SolidTextField(
                                        label = "App Name",
                                        value = name,
                                        onValueChange = { name = it },
                                        placeholder = "e.g. Notion, Discord"
                                )
                                Spacer(Modifier.height(16.dp))

                                SolidTextField(
                                        label = "Starting URL",
                                        value = url,
                                        onValueChange = { url = it },
                                        placeholder = "https://",
                                        leadingIcon = Icons.Filled.Language
                                )
                                Spacer(Modifier.height(16.dp))

                                SolidDropdownField(
                                        label = "Category",
                                        value = selectedCategory ?: "No Category",
                                        expanded = categoryMenuExpanded,
                                        onExpandedChange = { categoryMenuExpanded = it },
                                        options = listOf("No Category") + availableCategories,
                                        onOptionSelected = { cat ->
                                                selectedCategory =
                                                        if (cat == "No Category") null else cat
                                                categoryMenuExpanded = false
                                        }
                                )
                        }

                        Spacer(Modifier.height(20.dp))

                        // ── Preferences ──────────────────────────────────────────────────
                        if (!platformIsAndroid) {
                                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                                        M3SectionHeader("Preferences")
                                        Spacer(Modifier.height(8.dp))
                                        Column(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clip(RoundedCornerShape(16.dp))
                                                                .background(
                                                                        adaptiveSurfaceVariantBackground()
                                                                )
                                                                .border(
                                                                        1.dp,
                                                                        adaptiveSurfaceVariantBorder(),
                                                                        RoundedCornerShape(16.dp)
                                                                )
                                                                .padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                AdvancedSettingToggle(
                                                        title = "Native-like Window",
                                                        subtitle = "Hide browser address bar and controls",
                                                        checked = isStandalone,
                                                        onCheckedChange = { isStandalone = it }
                                                )
                                                AdvancedSettingToggle(
                                                        title = "Isolated Profile",
                                                        subtitle = "Separate browser profile for this app",
                                                        checked = isolatedProfile,
                                                        onCheckedChange = { isolatedProfile = it }
                                                )
                                                AdvancedSettingToggle(
                                                        title = "Incognito Mode",
                                                        subtitle = "Open app in a private browsing window",
                                                        checked = incognitoMode,
                                                        onCheckedChange = { incognitoMode = it }
                                                )
                                        }
                                }
                        }

                        Spacer(Modifier.height(24.dp))

                        // ── Actions ──────────────────────────────────────────────────────
                        Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                Button(
                                        onClick = {
                                                if (name.isNotBlank() && url.isNotBlank()) {
                                                        onSave(
                                                                app.copy(
                                                                        name = name,
                                                                        url = url,
                                                                        isStandalone = isStandalone,
                                                                        isolatedProfile =
                                                                                isolatedProfile,
                                                                        incognitoMode =
                                                                                incognitoMode,
                                                                        category = selectedCategory,
                                                                        iconPath = customIconUrl
                                                                )
                                                        )
                                                }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        contentColor =
                                                                MaterialTheme.colorScheme.onPrimary
                                                )
                                ) { Text("Save Changes", fontWeight = FontWeight.Bold) }

                                Button(
                                        onClick = { showDeleteDialog = true },
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                adaptiveSurfaceVariantBackground(),
                                                        contentColor = Danger
                                                )
                                ) {
                                        Icon(
                                                Icons.Filled.Delete,
                                                null,
                                                tint = Danger,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("Delete App", fontWeight = FontWeight.Bold)
                                }
                        }

                        Spacer(Modifier.height(32.dp))
                        }

                        VerticalScrollbar(
                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                scrollState = scrollState
                        )
                }
        }

        // Delete confirmation dialog
        if (showDeleteDialog) {
                AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        icon = {
                                Icon(
                                        Icons.Filled.Delete,
                                        null,
                                        tint = MaterialTheme.colorScheme.error
                                )
                        },
                        title = { Text("Delete App") },
                        text = {
                                Text(
                                        "Are you sure you want to delete \"${app.name}\"? This cannot be undone."
                                )
                        },
                        confirmButton = {
                                Button(
                                        onClick = {
                                                showDeleteDialog = false
                                                onDelete()
                                        },
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.error,
                                                        contentColor =
                                                                MaterialTheme.colorScheme.onError
                                                )
                                ) { Text("Delete", fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                        Text("Cancel")
                                }
                        }
                )
        }

        if (showIconUrlDialog) {
                var tempUrl by remember { mutableStateOf(customIconUrl ?: "") }
                AlertDialog(
                        onDismissRequest = { showIconUrlDialog = false },
                        containerColor = MaterialTheme.colorScheme.surface,
                        title = { Text("Custom Icon URL") },
                        text = {
                                SolidTextField(
                                        label = "Image URL",
                                        value = tempUrl,
                                        onValueChange = { tempUrl = it },
                                        placeholder = "https://example.com/icon.png"
                                )
                        },
                        confirmButton = {
                                Button(
                                        onClick = {
                                                customIconUrl = tempUrl.ifBlank { null }
                                                showIconUrlDialog = false
                                        }
                                ) { Text("Save") }
                        },
                        dismissButton = {
                                TextButton(onClick = { showIconUrlDialog = false }) {
                                        Text("Cancel")
                                }
                        }
                )
        }
}
