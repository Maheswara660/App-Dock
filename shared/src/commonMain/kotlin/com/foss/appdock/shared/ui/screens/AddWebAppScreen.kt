package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.platform.ImagePicker
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid
import com.foss.appdock.shared.platform.platformIsDesktop
import com.foss.appdock.shared.platform.queryInstalledBrowsers
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWebAppScreen(
    availableCategories: List<String>,
    onBack: () -> Unit,
    onSave: (
        name: String,
        url: String,
        category: String,
        browser: String,
        isStandalone: Boolean,
        notificationsEnabled: Boolean,
        isolatedProfile: Boolean,
        incognitoMode: Boolean,
        customIconUrl: String?
    ) -> Unit
) {
    // ── State ──────────────────────────────────────────────────────────────────
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var customIconUrl by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedBrowser by remember { mutableStateOf("System Default") }
    var isStandalone by remember { mutableStateOf(false) }
    var isolatedProfile by remember { mutableStateOf(true) }
    var incognitoMode by remember { mutableStateOf(false) }
    var createShortcut by remember { mutableStateOf(true) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var browserExpanded by remember { mutableStateOf(false) }

    val defaultCategories = listOf("Productivity", "Social", "Development", "Entertainment", "Finance", "News")
    val categories = if (availableCategories.isEmpty()) defaultCategories else availableCategories
    val browsers = queryInstalledBrowsers(null)
    val formValid = name.isNotBlank() && url.isNotBlank() && url != "https://"
    val scrollState = rememberScrollState()

    // ── Layout ─────────────────────────────────────────────────────────────────
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
                        Text("Add Web App", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Fill in the details below", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }


            // ── Scrollable form ───────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // ── Section: Basic Info ───────────────────────────────────
                    FormSection(title = "Basic Info") {
                        // Name
                        SolidTextField(
                            label = "App Name",
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "e.g. Notion, GitHub, Discord"
                        )

                        SolidTextField(
                            label = "URL",
                            value = url,
                            onValueChange = { url = it },
                            placeholder = "https://",
                            leadingIcon = Icons.Filled.Link
                        )

                        // Category + Browser dropdowns side by side
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                SolidDropdownField(
                                    label = "Category",
                                    value = selectedCategory.ifEmpty { "None" },
                                    expanded = categoryExpanded,
                                    onExpandedChange = { categoryExpanded = it },
                                    options = listOf("None") + categories,
                                    onOptionSelected = {
                                        selectedCategory = if (it == "None") "" else it
                                        categoryExpanded = false
                                    }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                SolidDropdownField(
                                    label = "Browser",
                                    value = selectedBrowser,
                                    expanded = browserExpanded,
                                    onExpandedChange = { browserExpanded = it },
                                    options = browsers,
                                    onOptionSelected = { selectedBrowser = it; browserExpanded = false }
                                )
                            }
                        }
                    }

                    // ── Section: App Icon ─────────────────────────────────────
                    FormSection(title = "App Icon") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon preview
                            val previewIconUrl = customIconUrl ?: run {
                                try {
                                    val domain = io.ktor.http.Url(url).host
                                    if (domain.isNotBlank() && domain != "localhost")
                                        "https://www.google.com/s2/favicons?domain=$domain&sz=128"
                                    else ""
                                } catch (e: Exception) { "" }
                            }
                            Box(
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (previewIconUrl.isNotBlank()) {
                                    io.kamel.image.KamelImage(
                                        resource = io.kamel.image.asyncPainterResource(previewIconUrl),
                                        contentDescription = "App Icon",
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                                        onFailure = {
                                            Icon(Icons.Filled.Image, null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                                modifier = Modifier.size(32.dp))
                                        }
                                    )
                                } else {
                                    Icon(Icons.Filled.Image, null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(32.dp))
                                }
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    if (customIconUrl != null) "Custom icon set" else "Enter a URL above to auto-fetch",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ImagePicker(onImagePicked = { path ->
                                        if (path != null) customIconUrl = path
                                    }) { launchPicker ->
                                        OutlinedButton(
                                            onClick = { launchPicker() },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp)
                                        ) {
                                            Icon(Icons.Filled.FileOpen, null, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Browse", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                    if (customIconUrl != null) {
                                        OutlinedButton(
                                            onClick = { customIconUrl = null },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Text("Clear", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Section: Advanced ─────────────────────────────────────
                    FormSection(title = "Advanced") {
                        listOf(
                            Triple("Isolation Mode", "Run in a sandboxed container for privacy", isolatedProfile),
                            Triple("Full Screen / Standalone", "Hide browser address bar and chrome UI", isStandalone),
                            Triple("Incognito Mode", "Always launch in a private browsing window", incognitoMode),
                        ).forEachIndexed { idx, (title, subtitle, checked) ->
                            AdvancedSettingToggle(
                                title = title, subtitle = subtitle, checked = checked,
                                onCheckedChange = { v ->
                                    when (idx) {
                                        0 -> isolatedProfile = v
                                        1 -> isStandalone = v
                                        2 -> incognitoMode = v
                                    }
                                }
                            )
                            if (idx < 2) HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        }
                        if (platformIsDesktop) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            AdvancedSettingToggle(
                                title = "Create Desktop Shortcut",
                                subtitle = "Add a launcher icon to your desktop for quick access",
                                checked = createShortcut,
                                onCheckedChange = { createShortcut = it }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    scrollState = scrollState
                )
            }

            // ── Footer actions ────────────────────────────────────────────────
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel", fontWeight = FontWeight.SemiBold) }

                Button(
                    onClick = {
                        if (formValid) {
                            onSave(name, url, selectedCategory, selectedBrowser,
                                isStandalone, false, isolatedProfile, incognitoMode, customIconUrl)
                            onBack()
                        }
                    },
                    enabled = formValid,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Create App", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Reusable form section card ─────────────────────────────────────────────────

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                content()
            }
        }
    }
}
