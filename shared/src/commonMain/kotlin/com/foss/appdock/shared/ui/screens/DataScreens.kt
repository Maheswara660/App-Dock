package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.VerticalScrollbar
import com.foss.appdock.shared.platform.exportData
import com.foss.appdock.shared.platform.importData
import com.foss.appdock.shared.ui.theme.adaptiveOnSurface
import com.foss.appdock.shared.ui.theme.adaptiveSurfaceVariantBackground
import com.foss.appdock.shared.ui.theme.adaptiveSurfaceVariantBorder
import kotlinx.coroutines.launch

@Composable
fun ExportScreen(databaseHelper: DatabaseHelper, onBack: () -> Unit, snackbarHostState: SnackbarHostState) {
        val webApps by databaseHelper.getAllWebApps().collectAsState(emptyList())
        var selectedApps by remember { mutableStateOf(setOf<Long>()) }
        val scope = rememberCoroutineScope()

        AppScaffold(
                snackbarHostState = snackbarHostState,
                bottomBar = {
                        BottomNavBar(
                                selectedTab = BottomNavTab.SETTINGS,
                                onTabSelected = { /* Handle navigation if needed */},
                                onFabClick = {}
                        )
                }
        ) {
                val scrollState = rememberScrollState()
                Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .padding(horizontal = 24.dp, vertical = 24.dp)
                                                .verticalScroll(scrollState)
                        ) {
                                DockPageHeader(title = "Export Data", onBack = onBack)

                                Spacer(Modifier.height(32.dp))

                                Text(
                                        "Select the apps you want to export. The data will be saved as a JSON file.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(Modifier.height(24.dp))

                                // Select All / Deselect All
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                "Apps (${webApps.size})",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = adaptiveOnSurface()
                                        )

                                        TextButton(
                                                onClick = {
                                                        selectedApps =
                                                                if (selectedApps.size ==
                                                                                webApps.size
                                                                )
                                                                        emptySet()
                                                                else webApps.map { it.id }.toSet()
                                                }
                                        ) {
                                                Text(
                                                        if (selectedApps.size == webApps.size)
                                                                "Deselect All"
                                                        else "Select All"
                                                )
                                        }
                                }

                                Spacer(Modifier.height(8.dp))

                                Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = adaptiveSurfaceVariantBackground(),
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .border(
                                                                1.dp,
                                                                adaptiveSurfaceVariantBorder(),
                                                                RoundedCornerShape(20.dp)
                                                        )
                                ) {
                                        Column {
                                                webApps.forEachIndexed { index, app ->
                                                        val isSelected = app.id in selectedApps
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
                                                                                                                app.id
                                                                                                else
                                                                                                        selectedApps -
                                                                                                                app.id
                                                                                }
                                                                        )
                                                                },
                                                                colors =
                                                                        ListItemDefaults.colors(
                                                                                containerColor =
                                                                                        Color
                                                                                                .Transparent
                                                                        ),
                                                                modifier =
                                                                        Modifier.clickable {
                                                                                selectedApps =
                                                                                        if (isSelected
                                                                                        )
                                                                                                selectedApps -
                                                                                                        app.id
                                                                                        else
                                                                                                selectedApps +
                                                                                                        app.id
                                                                        }
                                                        )

                                                        if (index < webApps.size - 1) {
                                                                HorizontalDivider(
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        horizontal =
                                                                                                16.dp
                                                                                ),
                                                                        color =
                                                                                adaptiveSurfaceVariantBorder()
                                                                )
                                                        }
                                                }
                                        }
                                }

                                Spacer(Modifier.height(32.dp))

                                Button(
                                        onClick = {
                                                scope.launch {
                                                        val selectedList =
                                                                webApps.filter {
                                                                        it.id in selectedApps
                                                                }
                                                        if (selectedList.isEmpty()) {
                                                                snackbarHostState.showSnackbar(
                                                                        "Please select at least one app"
                                                                )
                                                                return@launch
                                                        }

                                                        val success =
                                                                exportData(selectedList)
                                                        if (success) {
                                                                snackbarHostState.showSnackbar(
                                                                        "Data exported successfully"
                                                                )
                                                        } else {
                                                                snackbarHostState.showSnackbar(
                                                                        "Failed to export data"
                                                                )
                                                        }
                                                }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        enabled = selectedApps.isNotEmpty()
                                ) {
                                        Icon(Icons.Filled.Download, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Export Selected Data", fontWeight = FontWeight.Bold)
                                }

                                Spacer(Modifier.height(100.dp))
                        }

                        VerticalScrollbar(
                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                scrollState = scrollState
                        )
                }
        }
}

@Composable
fun ImportScreen(databaseHelper: DatabaseHelper, onBack: () -> Unit, onImportComplete: () -> Unit, snackbarHostState: SnackbarHostState) {
        var parsedApps by remember { mutableStateOf(listOf<WebApp>()) }
        var selectedApps by remember { mutableStateOf(setOf<Int>()) }
        val scope = rememberCoroutineScope()
        var isParsing by remember { mutableStateOf(false) }

        AppScaffold(
                snackbarHostState = snackbarHostState,
                bottomBar = {
                        BottomNavBar(
                                selectedTab = BottomNavTab.SETTINGS,
                                onTabSelected = { /* Handle navigation if needed */},
                                onFabClick = {}
                        )
                }
        ) {
                Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .padding(vertical = 24.dp)
                        ) {
                                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                                        DockPageHeader(title = "Import Data", onBack = onBack)

                                        Spacer(Modifier.height(32.dp))

                                        if (parsedApps.isEmpty()) {
                                                Surface(
                                                        shape = RoundedCornerShape(24.dp),
                                                        color = adaptiveSurfaceVariantBackground(),
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(vertical = 16.dp)
                                                                        .clickable {
                                                                                scope.launch {
                                                                                        isParsing = true
                                                                                        val apps =
                                                                                                importData()
                                                                                        if (apps != null) {
                                                                                                parsedApps =
                                                                                                        apps
                                                                                                selectedApps =
                                                                                                        apps.indices
                                                                                                                .toSet()
                                                                                        } else {
                                                                                                snackbarHostState
                                                                                                        .showSnackbar(
                                                                                                                "Failed to import or no file selected"
                                                                                                        )
                                                                                        }
                                                                                        isParsing = false
                                                                                }
                                                                        }
                                                                        .border(
                                                                                1.dp,
                                                                                adaptiveSurfaceVariantBorder(),
                                                                                RoundedCornerShape(24.dp)
                                                                        )
                                                ) {
                                                        Column(
                                                                modifier = Modifier.padding(32.dp),
                                                                horizontalAlignment =
                                                                        Alignment.CenterHorizontally,
                                                                verticalArrangement =
                                                                        Arrangement.spacedBy(16.dp)
                                                        ) {
                                                                Box(
                                                                        modifier =
                                                                                Modifier.size(64.dp)
                                                                                        .clip(
                                                                                                RoundedCornerShape(
                                                                                                        20.dp
                                                                                                )
                                                                                        )
                                                                                        .background(
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primaryContainer
                                                                                        ),
                                                                        contentAlignment = Alignment.Center
                                                                ) {
                                                                        Icon(
                                                                                Icons.Filled.UploadFile,
                                                                                contentDescription = null,
                                                                                modifier =
                                                                                        Modifier.size(32.dp),
                                                                                tint =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        )
                                                                }

                                                                Text(
                                                                        "Select JSON File",
                                                                        style =
                                                                                MaterialTheme.typography
                                                                                        .titleMedium,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = adaptiveOnSurface()
                                                                )

                                                                Text(
                                                                        "Import your web apps from a previously exported App Dock JSON file.",
                                                                        style =
                                                                                MaterialTheme.typography
                                                                                        .bodySmall,
                                                                        color =
                                                                                MaterialTheme.colorScheme
                                                                                        .onSurfaceVariant,
                                                                        textAlign = TextAlign.Center
                                                                )

                                                                if (isParsing) {
                                                                        CircularProgressIndicator(
                                                                                modifier =
                                                                                        Modifier.size(24.dp)
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                }

                                if (parsedApps.isNotEmpty()) {
                                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
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
                                                                                else
                                                                                        parsedApps.indices
                                                                                                .toSet()
                                                                }
                                                        ) {
                                                                Text(
                                                                        if (selectedApps.size ==
                                                                                        parsedApps.size
                                                                        )
                                                                                "Deselect All"
                                                                        else "Select All"
                                                                )
                                                        }
                                                }
                                        }

                                        LazyColumn(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                                contentPadding = PaddingValues(horizontal = 24.dp)
                                        ) {
                                                items(parsedApps.indices.toList()) { i ->
                                                        val app = parsedApps[i]
                                                        val isSelected = i in selectedApps
                                                        Surface(
                                                                shape = RoundedCornerShape(14.dp),
                                                                color =
                                                                        if (isSelected)
                                                                                MaterialTheme
                                                                                        .colorScheme
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
                                                                                ListItemDefaults
                                                                                        .colors(
                                                                                                containerColor =
                                                                                                        Color
                                                                                                                .Transparent
                                                                                        )
                                                                )
                                                        }
                                                }
                                        }

                                        Spacer(Modifier.height(24.dp))

                                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                                                Button(
                                                        onClick = {
                                                                scope.launch {
                                                                        val selectedList =
                                                                                parsedApps.filterIndexed {
                                                                                        index,
                                                                                        _ ->
                                                                                        index in
                                                                                                selectedApps
                                                                                }
                                                                        selectedList.forEach {
                                                                                databaseHelper.insertWebApp(it)
                                                                        }
                                                                        snackbarHostState.showSnackbar(
                                                                                "Imported ${selectedList.size} apps"
                                                                        )
                                                                        onImportComplete()
                                                                        onBack()
                                                                }
                                                        },
                                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                                        shape = RoundedCornerShape(16.dp),
                                                        enabled = selectedApps.isNotEmpty()
                                                ) {
                                                        Icon(
                                                                Icons.Filled.SystemUpdateAlt,
                                                                contentDescription = null
                                                        )
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(
                                                                "Import Selected Apps",
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
