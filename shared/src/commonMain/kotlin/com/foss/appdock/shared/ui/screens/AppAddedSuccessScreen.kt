package com.foss.appdock.shared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.ui.theme.*

@Composable
fun AppAddedSuccessScreen(app: WebApp, onGoToDashboard: () -> Unit, onAddAnother: () -> Unit) {
        AppScaffold(bottomBar = {}) {
                Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // Header
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(top = 48.dp, bottom = 16.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        "App Dock",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = adaptiveOnSurface()
                                )
                        }

                        // Main content
                        Column(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
                                // Success icon with glow
                                Box(
                                        modifier = Modifier.size(160.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.size(112.dp)
                                        ) {
                                                Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier.fillMaxSize()
                                                ) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.Filled.CheckCircle,
                                                                contentDescription = null,
                                                                tint = SuccessGreen,
                                                                modifier = Modifier.size(64.dp)
                                                        )
                                                }
                                        }
                                }

                                Spacer(Modifier.height(28.dp))

                                Text(
                                        "App Added Successfully!",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = adaptiveOnSurface()
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                        "${app.name} is now available in your dock.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 48.dp)
                                )

                                Spacer(Modifier.height(32.dp))

                                // Mock dock preview card
                                Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(horizontal = 32.dp)
                                ) {
                                        Row(
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 16.dp,
                                                                vertical = 14.dp
                                                        ),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                                        ) {
                                                Surface(
                                                        shape = RoundedCornerShape(12.dp),
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant,
                                                        modifier = Modifier.size(44.dp)
                                                ) {
                                                        Box(
                                                                contentAlignment = Alignment.Center,
                                                                modifier = Modifier.fillMaxSize()
                                                        ) {
                                                                if (!app.iconPath.isNullOrBlank()) {
                                                                        io.kamel.image.KamelImage(
                                                                                resource =
                                                                                        io.kamel
                                                                                                .image
                                                                                                .asyncPainterResource(
                                                                                                        app.iconPath
                                                                                                ),
                                                                                contentDescription =
                                                                                        "App Icon",
                                                                                modifier =
                                                                                        Modifier.fillMaxSize()
                                                                        )
                                                                } else {
                                                                        Text(
                                                                                app.name
                                                                                        .take(1)
                                                                                        .uppercase(),
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .titleMedium,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold,
                                                                                color = SuccessGreen
                                                                        )
                                                                }
                                                        }
                                                }
                                                Column(
                                                        modifier = Modifier.weight(1f),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(4.dp)
                                                ) {
                                                        Text(
                                                                app.name,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = adaptiveOnSurface()
                                                        )
                                                        Text(
                                                                "Created just now",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }
                                                Icon(
                                                        Icons.Filled.CheckCircle,
                                                        null,
                                                        tint = SuccessGreen,
                                                        modifier = Modifier.size(20.dp)
                                                )
                                        }
                                }
                        }

                        // Footer buttons
                        Column(
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                Button(
                                        onClick = onGoToDashboard,
                                        shape = CircleShape,
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = SuccessGreen,
                                                        contentColor = Color.White
                                                ),
                                        elevation = ButtonDefaults.buttonElevation(0.dp),
                                        modifier = Modifier.fillMaxWidth().height(56.dp)
                                ) {
                                        Text(
                                                "Go to Dashboard",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                        )
                                }

                                TextButton(onClick = onAddAnother) {
                                        Icon(
                                                Icons.Filled.Add,
                                                null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                                "Add Another",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }
                }
        }
}
