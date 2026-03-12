package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.domain.BackupHistory
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// ─── Export Screen ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
        databaseHelper: DatabaseHelper,
        onBack: () -> Unit,
        snackbarHostState: SnackbarHostState
) {
        val coroutineScope = rememberCoroutineScope()
        val clipboardManager = LocalClipboardManager.current
        var appCount by remember { mutableStateOf(0) }
        var categoryCount by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
                databaseHelper.getAllWebApps().collect { apps -> appCount = apps.size }
        }
        LaunchedEffect(Unit) {
                databaseHelper.getAllCategories().collect { cats -> categoryCount = cats.size }
        }

        AppScaffold(
                snackbarHostState = snackbarHostState,
                bottomBar = {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Button(
                                        onClick = {
                                                coroutineScope.launch {
                                                        try {
                                                                val apps =
                                                                        databaseHelper
                                                                                .getAllWebApps()
                                                                                .first()
                                                                val json = Json.encodeToString(apps)
                                                                clipboardManager.setText(
                                                                        AnnotatedString(json)
                                                                )

                                                                // Record History
                                                                databaseHelper.insertBackupHistory(
                                                                        BackupHistory(
                                                                                filename =
                                                                                        "backup-${System.currentTimeMillis()}.json",
                                                                                timestamp =
                                                                                        System.currentTimeMillis(),
                                                                                sizeBytes =
                                                                                        json.length
                                                                                                .toLong(),
                                                                                type = "manual"
                                                                        )
                                                                )

                                                                snackbarHostState.showSnackbar(
                                                                        "Exported to clipboard!"
                                                                )
                                                        } catch (e: Exception) {
                                                                snackbarHostState.showSnackbar(
                                                                        "Export failed."
                                                                )
                                                        }
                                                }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        contentColor = Color.White
                                                ),
                                        elevation = ButtonDefaults.buttonElevation(0.dp)
                                ) {
                                        Icon(
                                                Icons.Filled.Upload,
                                                null,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("Export as JSON", fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.height(8.dp))
                                TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                                "Cancel",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }
                }
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // Header
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
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
                                        text = "Export Config",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = adaptiveOnSurface(),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.width(40.dp))
                        }

                        // Summary Card
                        Column(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(adaptiveSurfaceVariantBackground())
                                                .border(
                                                        1.dp,
                                                        adaptiveSurfaceVariantBorder(),
                                                        RoundedCornerShape(16.dp)
                                                )
                        ) {
                                Text(
                                        "EXPORT SUMMARY",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 12.dp
                                                )
                                )
                                HorizontalDivider(color = adaptiveSurfaceVariantBorder())
                                ListItem(
                                        headlineContent = {
                                                Text(
                                                        "Web Applications",
                                                        color = adaptiveOnSurface()
                                                )
                                        },
                                        leadingContent = {
                                                Icon(
                                                        Icons.Filled.Apps,
                                                        null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        },
                                        trailingContent = {
                                                Surface(
                                                        shape = CircleShape,
                                                        color = adaptiveSurfaceVariantBackground(),
                                                        border =
                                                                androidx.compose.foundation
                                                                        .BorderStroke(
                                                                                1.dp,
                                                                                adaptiveSurfaceVariantBorder()
                                                                        )
                                                ) {
                                                        Text(
                                                                "$appCount",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color = adaptiveOnSurface(),
                                                                modifier =
                                                                        Modifier.padding(
                                                                                horizontal = 10.dp,
                                                                                vertical = 3.dp
                                                                        )
                                                        )
                                                }
                                        },
                                        colors =
                                                ListItemDefaults.colors(
                                                        containerColor = Color.Transparent
                                                )
                                )
                                HorizontalDivider(color = adaptiveSurfaceVariantBorder())
                                ListItem(
                                        headlineContent = {
                                                Text("Categories", color = adaptiveOnSurface())
                                        },
                                        leadingContent = {
                                                Icon(
                                                        Icons.Filled.Category,
                                                        null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        },
                                        trailingContent = {
                                                Surface(
                                                        shape = CircleShape,
                                                        color = adaptiveSurfaceVariantBackground(),
                                                        border =
                                                                androidx.compose.foundation
                                                                        .BorderStroke(
                                                                                1.dp,
                                                                                adaptiveSurfaceVariantBorder()
                                                                        )
                                                ) {
                                                        Text(
                                                                "$categoryCount",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color = adaptiveOnSurface(),
                                                                modifier =
                                                                        Modifier.padding(
                                                                                horizontal = 10.dp,
                                                                                vertical = 3.dp
                                                                        )
                                                        )
                                                }
                                        },
                                        colors =
                                                ListItemDefaults.colors(
                                                        containerColor = Color.Transparent
                                                )
                                )
                                HorizontalDivider(color = adaptiveSurfaceVariantBorder())
                                ListItem(
                                        headlineContent = {
                                                Text("Preferences", color = adaptiveOnSurface())
                                        },
                                        leadingContent = {
                                                Icon(
                                                        Icons.Filled.Settings,
                                                        null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        },
                                        trailingContent = {
                                                Text(
                                                        "Included",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        },
                                        colors =
                                                ListItemDefaults.colors(
                                                        containerColor = Color.Transparent
                                                )
                                )
                        }

                        Spacer(Modifier.height(32.dp))

                        // 2. Hero Icon & Message
                        Box(
                                modifier =
                                        Modifier.size(80.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(adaptiveSurfaceVariantBackground())
                                                .border(
                                                        1.dp,
                                                        adaptiveSurfaceVariantBorder(),
                                                        RoundedCornerShape(20.dp)
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.Filled.Upload,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                )
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                                "Ready to Export",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = adaptiveOnSurface()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                                "Your configuration will be saved as JSON to your clipboard. You can then paste it into a file or another device.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                        )
                }

                Spacer(Modifier.height(16.dp))

                // Security Note
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(adaptiveSurfaceVariantBackground())
                                        .border(
                                                1.dp,
                                                adaptiveSurfaceVariantBorder(),
                                                RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp)
                ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                        Icons.Filled.Security,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                )
                                Text(
                                        "Passwords and login cookies are not included in the export for security.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                }
        }
}

// ─── Import Screen ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
        databaseHelper: DatabaseHelper,
        onBack: () -> Unit,
        onImportComplete: () -> Unit,
        snackbarHostState: SnackbarHostState
) {
        var jsonText by remember { mutableStateOf("") }
        var parsedApps by remember { mutableStateOf<List<WebApp>>(emptyList()) }
        var selectedApps by remember { mutableStateOf<Set<Int>>(emptySet()) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(jsonText) {
                if (jsonText.isNotBlank()) {
                        try {
                                parsedApps = Json.decodeFromString<List<WebApp>>(jsonText)
                                selectedApps = parsedApps.indices.toSet()
                        } catch (_: Exception) {
                                parsedApps = emptyList()
                        }
                }
        }

        AppScaffold(
                snackbarHostState = snackbarHostState,
                bottomBar = {
                        if (parsedApps.isNotEmpty()) {
                                Box(modifier = Modifier.padding(16.dp)) {
                                        Button(
                                                onClick = {
                                                        coroutineScope.launch {
                                                                try {
                                                                        val toImport =
                                                                                parsedApps
                                                                                        .filterIndexed {
                                                                                                i,
                                                                                                _ ->
                                                                                                i in
                                                                                                        selectedApps
                                                                                        }
                                                                        toImport.forEach { app ->
                                                                                databaseHelper
                                                                                        .insertWebApp(
                                                                                                app.copy(
                                                                                                        id =
                                                                                                                0,
                                                                                                        createdAt =
                                                                                                                System.currentTimeMillis(),
                                                                                                        launchCount =
                                                                                                                0
                                                                                                )
                                                                                        )
                                                                        }
                                                                        // Record History
                                                                        databaseHelper
                                                                                .insertBackupHistory(
                                                                                        BackupHistory(
                                                                                                filename =
                                                                                                        "import-${System.currentTimeMillis()}.json",
                                                                                                timestamp =
                                                                                                        System.currentTimeMillis(),
                                                                                                sizeBytes =
                                                                                                        jsonText.length
                                                                                                                .toLong(),
                                                                                                type =
                                                                                                        "import"
                                                                                        )
                                                                                )

                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        "Imported ${toImport.size} apps successfully!"
                                                                                )
                                                                        onImportComplete()
                                                                } catch (e: Exception) {
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        "Import failed: ${e.message}"
                                                                                )
                                                                }
                                                        }
                                                },
                                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                                shape = RoundedCornerShape(14.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                contentColor = Color.White
                                                        ),
                                                elevation = ButtonDefaults.buttonElevation(0.dp)
                                        ) {
                                                Icon(
                                                        Icons.Filled.Download,
                                                        null,
                                                        modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                        "Import ${selectedApps.size} Apps",
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                        }
                                }
                        }
                }
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        // Header
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
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
                                        text = "Import Config",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = adaptiveOnSurface(),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.width(40.dp))
                        }
                        Text(
                                "Paste your exported JSON below to restore apps.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                                value = jsonText,
                                onValueChange = { jsonText = it },
                                modifier = Modifier.fillMaxWidth().height(140.dp),
                                placeholder = { Text("Paste JSON here...") },
                                shape = RoundedCornerShape(14.dp),
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
                                        ),
                                trailingIcon =
                                        if (jsonText.isNotEmpty())
                                                ({
                                                        IconButton(onClick = { jsonText = "" }) {
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
                                        else null
                        )

                        if (jsonText.isNotBlank() && parsedApps.isEmpty()) {
                                Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.errorContainer
                                ) {
                                        Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                Icon(
                                                        Icons.Filled.Error,
                                                        null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onErrorContainer,
                                                        modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                        "Invalid JSON format. Please check and try again.",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onErrorContainer
                                                )
                                        }
                                }
                        }

                        if (parsedApps.isNotEmpty()) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                "Found ${parsedApps.size} apps",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                        TextButton(
                                                onClick = {
                                                        selectedApps =
                                                                if (selectedApps.size ==
                                                                                parsedApps.size
                                                                )
                                                                        emptySet()
                                                                else parsedApps.indices.toSet()
                                                }
                                        ) {
                                                Text(
                                                        if (selectedApps.size == parsedApps.size)
                                                                "Deselect All"
                                                        else "Select All"
                                                )
                                        }
                                }

                                LazyColumn(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                        items(parsedApps.indices.toList()) { i ->
                                                val app = parsedApps[i]
                                                val isSelected = i in selectedApps
                                                Surface(
                                                        shape = RoundedCornerShape(14.dp),
                                                        color =
                                                                if (isSelected)
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant
                                                                else
                                                                        adaptiveSurfaceVariantBackground(),
                                                        border =
                                                                if (isSelected)
                                                                        androidx.compose.foundation
                                                                                .BorderStroke(
                                                                                        1.dp,
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                                )
                                                                else
                                                                        androidx.compose.foundation
                                                                                .BorderStroke(
                                                                                        1.dp,
                                                                                        adaptiveSurfaceVariantBorder()
                                                                                ),
                                                        tonalElevation = 0.dp
                                                ) {
                                                        ListItem(
                                                                headlineContent = {
                                                                        Text(
                                                                                app.name,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Medium,
                                                                                color =
                                                                                        adaptiveOnSurface()
                                                                        )
                                                                },
                                                                supportingContent = {
                                                                        Text(
                                                                                app.url,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                maxLines = 1,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                        )
                                                                },
                                                                trailingContent = {
                                                                        Checkbox(
                                                                                checked =
                                                                                        isSelected,
                                                                                onCheckedChange = {
                                                                                        checked ->
                                                                                        selectedApps =
                                                                                                if (checked
                                                                                                )
                                                                                                        selectedApps +
                                                                                                                i
                                                                                                else
                                                                                                        selectedApps -
                                                                                                                i
                                                                                },
                                                                                colors =
                                                                                        CheckboxDefaults
                                                                                                .colors(
                                                                                                        checkedColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .primary
                                                                                                )
                                                                        )
                                                                },
                                                                colors =
                                                                        ListItemDefaults.colors(
                                                                                containerColor =
                                                                                        Color.Transparent
                                                                        )
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
