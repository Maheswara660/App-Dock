package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.foss.appdock.shared.platform.ImagePicker
import com.foss.appdock.shared.platform.platformIsDesktop
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.platformIsAndroid
import com.foss.appdock.shared.platform.queryInstalledBrowsers
import com.foss.appdock.shared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWebAppScreen(
        availableCategories: List<String>,
        onBack: () -> Unit,
        onSave:
                (
                        name: String,
                        url: String,
                        category: String,
                        browser: String,
                        isStandalone: Boolean,
                        notificationsEnabled: Boolean,
                        isolatedProfile: Boolean,
                        incognitoMode: Boolean,
                        customIconUrl: String?) -> Unit
) {
        var name by remember { mutableStateOf("") }
        var url by remember { mutableStateOf("") }
        var customIconUrl by remember { mutableStateOf<String?>(null) }
        var showIconUrlDialog by remember { mutableStateOf(false) }

        var selectedCategory by remember { mutableStateOf("") }
        var selectedBrowser by remember { mutableStateOf("System Default") }

        var isStandalone by remember { mutableStateOf(false) }
        var isolatedProfile by remember { mutableStateOf(true) }
        var incognitoMode by remember { mutableStateOf(false) }
        var createShortcut by remember { mutableStateOf(true) }

        var categoryExpanded by remember { mutableStateOf(false) }
        var browserExpanded by remember { mutableStateOf(false) }

        val defaultCategories = listOf("Productivity", "Social", "Development", "Entertainment")
        val categories =
                if (availableCategories.isEmpty()) defaultCategories else availableCategories
        val browsers = queryInstalledBrowsers(null)

        val formValid = name.isNotBlank() && url.isNotBlank() && url != "https://"

        AppScaffold(bottomBar = {}) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
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
                                        text = "Create New Web App",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = adaptiveOnSurface(),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.width(40.dp)) // Balance the back button
                        }

                        val scrollState = rememberScrollState()
                        // ── Scrollable Form ───────────────────────────────────────────────────
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                Column(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .verticalScroll(scrollState)
                                                        .padding(
                                                                top = 16.dp, 
                                                                bottom = 16.dp, 
                                                                end = if (platformIsDesktop) 16.dp else 0.dp
                                                        ),
                                        verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                // ── Basic Info ──────────────────────────────────
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        SolidTextField(
                                                label = "App Name",
                                                value = name,
                                                onValueChange = { name = it },
                                                placeholder = "e.g. Notion, Discord"
                                        )

                                        SolidTextField(
                                                label = "URL",
                                                value = url,
                                                onValueChange = { url = it },
                                                placeholder = "https://",
                                                leadingIcon = Icons.Filled.Link
                                        )

                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                Box(modifier = Modifier.weight(1f)) {
                                                        SolidDropdownField(
                                                                label = "Category",
                                                                value =
                                                                        selectedCategory.ifEmpty {
                                                                                "Select..."
                                                                        },
                                                                expanded = categoryExpanded,
                                                                onExpandedChange = {
                                                                        categoryExpanded = it
                                                                },
                                                                options = categories,
                                                                onOptionSelected = {
                                                                        selectedCategory = it
                                                                        categoryExpanded = false
                                                                }
                                                        )
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                        SolidDropdownField(
                                                                label = "Browser",
                                                                value = selectedBrowser,
                                                                expanded = browserExpanded,
                                                                onExpandedChange = {
                                                                        browserExpanded = it
                                                                },
                                                                options = browsers,
                                                                onOptionSelected = {
                                                                        selectedBrowser = it
                                                                        browserExpanded =
                                                                                false
                                                                }
                                                        )
                                                }
                                        }
                                }

                                // ── App Icon ─────────────────────────────────────
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                                "App Icon",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = adaptiveOnSurface()
                                        )
                                        Row(
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
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Icon Preview
                                                Box(
                                                        modifier =
                                                                Modifier.size(64.dp)
                                                                        .clip(
                                                                                RoundedCornerShape(
                                                                                        16.dp
                                                                                )
                                                                        )
                                                                        .background(
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        val derivedIconUrl =
                                                                customIconUrl
                                                                        ?: run {
                                                                                val domain =
                                                                                        try {
                                                                                                io.ktor
                                                                                                        .http
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
                                                                                "App Icon",
                                                                        modifier =
                                                                                Modifier.fillMaxSize()
                                                                )
                                                        } else {
                                                                Icon(
                                                                        Icons.Filled.Image,
                                                                        contentDescription = null,
                                                                        tint =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.5f
                                                                                        ),
                                                                        modifier =
                                                                                Modifier.size(32.dp)
                                                                )
                                                        }
                                                }

                                                // Icon Actions
                                                Column(
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(8.dp)
                                                ) {
                                                        Text(
                                                                "Enter a URL above to auto-fetch an icon, or set a custom one.",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                        ImagePicker(
                                                                onImagePicked = { path: String? ->
                                                                        if (path != null)
                                                                                customIconUrl = path
                                                                }
                                                        ) { launchPicker: () -> Unit ->
                                                                Surface(
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.5f
                                                                                        ),
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        8.dp
                                                                                ),
                                                                        modifier =
                                                                                Modifier.clickable {
                                                                                        // showIconUrlDialog = true
                                                                                        launchPicker()
                                                                                }
                                                                ) {
                                                                        Row(
                                                                                modifier =
                                                                                        Modifier.padding(
                                                                                                horizontal =
                                                                                                        12.dp,
                                                                                                vertical =
                                                                                                        6.dp
                                                                                        ),
                                                                                verticalAlignment =
                                                                                        Alignment
                                                                                                .CenterVertically,
                                                                                horizontalArrangement =
                                                                                        Arrangement
                                                                                                .spacedBy(
                                                                                                        6.dp
                                                                                                )
                                                                        ) {
                                                                                Icon(
                                                                                        imageVector =
                                                                                                Icons.Filled
                                                                                                        .Search,
                                                                                        contentDescription =
                                                                                                null,
                                                                                        modifier =
                                                                                                Modifier.size(
                                                                                                        16.dp
                                                                                                )
                                                                                )
                                                                                Text(
                                                                                        "Set Custom Icon",
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .labelMedium
                                                                                )
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }

                                // ── Advanced Settings ────────────────────────────
                                if (!platformIsAndroid) {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text(
                                                        "Advanced Settings",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = adaptiveOnSurface()
                                                )
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
                                                                title = "Isolation Mode (Sandboxing)",
                                                                subtitle =
                                                                        "Run this app in an isolated container for enhanced privacy.",
                                                                checked = isolatedProfile,
                                                                onCheckedChange = { isolatedProfile = it }
                                                        )
                                                        AdvancedSettingToggle(
                                                                title = "Full Screen Mode",
                                                                subtitle =
                                                                        "Hide browser address bar and UI.",
                                                                checked = isStandalone,
                                                                onCheckedChange = { isStandalone = it }
                                                        )
                                                        AdvancedSettingToggle(
                                                                title = "Incognito Mode",
                                                                subtitle = "Launch in a private window.",
                                                                checked = incognitoMode,
                                                                onCheckedChange = { incognitoMode = it }
                                                        )
                                                        AdvancedSettingToggle(
                                                                title = "Create Desktop Shortcut",
                                                                subtitle =
                                                                        "Add a launcher to your desktop for quick access.",
                                                                checked = createShortcut,
                                                                onCheckedChange = { createShortcut = it }
                                                        )
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(30.dp))
                                }

                                VerticalScrollbar(
                                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                        scrollState = scrollState
                                )
                        }

                        // ── Footer Actions ────────────────────────────────────────────────────
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(vertical = 16.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                Button(
                                        onClick = onBack,
                                        modifier = Modifier.weight(1f).height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                adaptiveSurfaceVariantBackground(),
                                                        contentColor =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                ) { Text("Cancel", fontWeight = FontWeight.SemiBold) }

                                Button(
                                        onClick = {
                                                if (formValid) {
                                                        onSave(
                                                                name,
                                                                url,
                                                                selectedCategory,
                                                                selectedBrowser,
                                                                isStandalone,
                                                                false,
                                                                isolatedProfile,
                                                                incognitoMode,
                                                                customIconUrl
                                                        )
                                                }
                                        },
                                        enabled = formValid,
                                        modifier = Modifier.weight(1f).height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        contentColor =
                                                                MaterialTheme.colorScheme.onPrimary
                                                )
                                ) {
                                        Icon(
                                                Icons.Filled.Add,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Create App", fontWeight = FontWeight.Bold)
                                }
                        }
                }

                // Custom Icon Dialog
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
}
