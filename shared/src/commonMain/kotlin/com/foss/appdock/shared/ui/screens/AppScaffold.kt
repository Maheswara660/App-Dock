package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A global scaffold that provides the solid background described in the HTML designs. It features
 * an adaptive opaque background for all floating content.
 */
@Composable
fun AppScaffold(
        modifier: Modifier = Modifier.fillMaxSize(),
        snackbarHostState: SnackbarHostState? = null,
        bottomBar: @Composable () -> Unit = {},
        content: @Composable BoxScope.() -> Unit
) {
        val finalSnackbarHostState = snackbarHostState ?: remember { SnackbarHostState() }

        Scaffold(
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.systemBars
        ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        // ── Main Content Layer ────────────────────────────────────────────────
                        Box(modifier = Modifier.fillMaxSize()) { content() }

                        // ── Snackbar Layer ────────────────────────────────────────────────
                        Box(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()) {
                                SolidSnackbarHost(finalSnackbarHostState)
                        }

                        // Floating Bottom Bar Layer
                        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                                bottomBar()
                        }
                }
        }
}

/** Custom SnackbarHost that uses the SolidSnackbar layout. */
@Composable
fun SolidSnackbarHost(hostState: SnackbarHostState) {
        SnackbarHost(hostState = hostState) { snackbarData -> SolidSnackbar(snackbarData) }
}

/**
 * Custom Snackbar that matches the HTML "transient message" overlay. Floating, top-centered layout.
 */
@Composable
private fun SolidSnackbar(snackbarData: SnackbarData) {
        // Attempt to parse action type or use a default positive aesthetic
        val isError = snackbarData.visuals.message.contains("Delete", ignoreCase = true)

        val iconTint =
                if (isError) MaterialTheme.colorScheme.error else Color(0xFF10B981) // Emerald
        val iconBg = MaterialTheme.colorScheme.surfaceVariant
        val iconVec = if (isError) Icons.Filled.Info else Icons.Filled.CheckCircle

        Box(
                modifier = Modifier.fillMaxWidth().padding(top = 80.dp, end = 24.dp),
                contentAlignment = Alignment.TopEnd
        ) {
                Row(
                        modifier =
                                Modifier.clip(RoundedCornerShape(16.dp))
                                        .background(
                                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                        4.dp
                                                )
                                        )
                                        .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outlineVariant,
                                                RoundedCornerShape(16.dp)
                                        )
                                        .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Icon
                        Box(
                                modifier =
                                        Modifier.size(32.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(iconBg),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = iconVec,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier.size(20.dp)
                                )
                        }

                        // Message
                        Text(
                                text = snackbarData.visuals.message,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                        )

                        // Action Button (Optional)
                        if (snackbarData.visuals.actionLabel != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.width(1.dp)
                                                                .height(20.dp)
                                                                .background(
                                                                        MaterialTheme.colorScheme
                                                                                .outlineVariant
                                                                )
                                        )
                                        TextButton(
                                                onClick = { snackbarData.performAction() },
                                                contentPadding =
                                                        PaddingValues(
                                                                horizontal = 12.dp,
                                                                vertical = 4.dp
                                                        ),
                                                modifier =
                                                        Modifier.height(32.dp)
                                                                .clip(RoundedCornerShape(8.dp))
                                        ) {
                                                Text(
                                                        text = snackbarData.visuals.actionLabel!!,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .labelMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                )
                                        }
                                }
                        }
                }
        }
}
