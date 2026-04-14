package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ChangelogRelease(
    val version: String,
    val date: String,
    val changes: List<String>
)

private val changelogHistory = listOf(
    ChangelogRelease(
        version = "v1.2.0",
        date = "April 2024",
        changes = listOf(
            "Shifted architectural focus back to native OS shortcut generation.",
            "Restored direct browser launch workflow over embedded Chromium.",
            "Refined dynamic Android navigation restraints.",
            "Added chronological native Changelog feature.",
            "Replaced Context wipe-menus with a universal 3-dot dropdown.",
            "Visual standardization of the Icon preferences selector.",
            "Removed deprecated AppImage builder dependencies."
        )
    ),
    ChangelogRelease(
        version = "v1.1.0",
        date = "March 2024",
        changes = listOf(
            "Global UI restyling substituting strict colors for dynamic Material 3 tonal palettes.",
            "Implemented backup and restore capabilities (JSON).",
            "Enhanced sorting dropdowns spanning dashboards and category scopes.",
            "Enabled direct 'Add App' flows inside bounded categories."
        )
    ),
    ChangelogRelease(
        version = "v1.0.0",
        date = "February 2024",
        changes = listOf(
            "Initial Release.",
            "Privacy-first web application dock routing.",
            "Category grouping interface."
        )
    )
)

@Composable
fun ChangelogScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            DockPageHeader(title = "Changelog", onBack = onBack)
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(changelogHistory) { release ->
                    ReleaseCard(release)
                }
            }
        }
    }
}

@Composable
private fun ReleaseCard(release: ChangelogRelease) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = release.version,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = release.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                release.changes.forEach { change ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.padding(top = 6.dp)) {
                            Icon(
                                Icons.Filled.FiberManualRecord,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(6.dp)
                            )
                        }
                        Text(
                            text = change,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChangelogDialog(onDismiss: () -> Unit) {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "App-Dock is updated!",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                changelogHistory.forEach { release ->
                    ReleaseCard(release)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it!", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
