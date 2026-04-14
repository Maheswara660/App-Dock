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
        sideBar: (@Composable () -> Unit)? = null,
        content: @Composable BoxScope.() -> Unit
) {
        val finalSnackbarHostState = snackbarHostState ?: remember { SnackbarHostState() }

        Scaffold(
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.systemBars
        ) { paddingValues ->
                Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        // ── Sidebar Layer (Desktop) ───────────────────────────────────────────
                        if (sideBar != null) {
                                sideBar()
                        }
                        
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                // ── Main Content Layer ────────────────────────────────────────────────
                                Box(modifier = Modifier.fillMaxSize()) { content() }

                                // ── Snackbar Layer ────────────────────────────────────────────────
                                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 80.dp)) {
                                        SnackbarHost(finalSnackbarHostState)
                                }

                                // Floating Bottom Bar Layer (Android)
                        }
                }
        }
}
