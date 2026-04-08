package com.foss.appdock.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Light scheme ──────────────────────────────────────────────────────────────
private val LightColorScheme =
        lightColorScheme(
                primary = md_light_primary,
                onPrimary = md_light_onPrimary,
                primaryContainer = md_light_primaryContainer,
                onPrimaryContainer = md_light_onPrimaryContainer,
                secondary = md_light_secondary,
                onSecondary = md_light_onSecondary,
                secondaryContainer = md_light_secondaryContainer,
                onSecondaryContainer = md_light_onSecondaryContainer,
                error = md_light_error,
                onError = md_light_onError,
                errorContainer = md_light_errorContainer,
                onErrorContainer = md_light_onErrorContainer,
                background = md_light_background,
                onBackground = md_light_onBackground,
                surface = md_light_surface,
                onSurface = md_light_onSurface,
                surfaceVariant = md_light_surfaceVariant,
                onSurfaceVariant = md_light_onSurfaceVariant,
                outline = md_light_outline,
                outlineVariant = md_light_outlineVariant,
        )

// ── Dark scheme ───────────────────────────────────────────────────────────────
private val DarkColorScheme =
        darkColorScheme(
                primary = md_dark_primary,
                onPrimary = md_dark_onPrimary,
                primaryContainer = md_dark_primaryContainer,
                onPrimaryContainer = md_dark_onPrimaryContainer,
                secondary = md_dark_secondary,
                onSecondary = md_dark_onSecondary,
                secondaryContainer = md_dark_secondaryContainer,
                onSecondaryContainer = md_dark_onSecondaryContainer,
                error = md_dark_error,
                onError = md_dark_onError,
                errorContainer = md_dark_errorContainer,
                onErrorContainer = md_dark_onErrorContainer,
                background = md_dark_background,
                onBackground = md_dark_onBackground,
                surface = md_dark_surface,
                onSurface = md_dark_onSurface,
                surfaceVariant = md_dark_surfaceVariant,
                onSurfaceVariant = md_dark_onSurfaceVariant,
                outline = md_dark_outline,
                outlineVariant = md_dark_outlineVariant,
        )

// ── AMOLED scheme ─────────────────────────────────────────────────────────────
private val AmoledColorScheme =
        darkColorScheme(
                primary = md_amoled_primary,
                onPrimary = md_amoled_onPrimary,
                primaryContainer = md_amoled_primaryContainer,
                onPrimaryContainer = md_amoled_onPrimaryContainer,
                secondary = md_amoled_secondary,
                onSecondary = md_amoled_onSecondary,
                secondaryContainer = md_amoled_secondaryContainer,
                onSecondaryContainer = md_amoled_onSecondaryContainer,
                error = md_amoled_error,
                onError = md_amoled_onError,
                errorContainer = md_amoled_errorContainer,
                onErrorContainer = md_amoled_onErrorContainer,
                background = md_amoled_background,
                onBackground = md_amoled_onBackground,
                surface = md_amoled_surface,
                onSurface = md_amoled_onSurface,
                surfaceVariant = md_amoled_surfaceVariant,
                onSurfaceVariant = md_amoled_onSurfaceVariant,
                outline = md_amoled_outline,
                outlineVariant = md_amoled_outlineVariant,
        )

// ── Theme composable ──────────────────────────────────────────────────────────
@Composable
fun AppDockTheme(
        themeSelection: String = "System Default",
        darkTheme: Boolean = isSystemInDarkTheme(),
        accentColor: Color = PrimaryBlue,
        content: @Composable () -> Unit
) {
        val baseColorScheme =
                when (themeSelection) {
                        "Light" -> LightColorScheme
                        "Dark" -> DarkColorScheme
                        "AMOLED Black" -> AmoledColorScheme
                        else -> if (darkTheme) DarkColorScheme else LightColorScheme
                }

        val colorScheme =
                baseColorScheme.copy(
                        primary = accentColor,
                        primaryContainer = accentColor.copy(alpha = 0.2f),
                        secondary = accentColor,
                        secondaryContainer = accentColor.copy(alpha = 0.1f),
                        tertiary = accentColor,
                        outline = accentColor.copy(alpha = 0.5f)
                )

        val isDark =
                when (themeSelection) {
                        "Light" -> false
                        "Dark", "AMOLED Black" -> true
                        else -> darkTheme
                }

        PlatformStatusBarTheme(isDark)

        MaterialTheme(
                colorScheme = colorScheme,
                typography = AppDockTypography,
                shapes = AppDockShapes,
                content = content
        )
}

@Composable fun adaptiveOnSurface() = MaterialTheme.colorScheme.onSurface

@Composable fun adaptiveSurfaceVariantBackground() = MaterialTheme.colorScheme.surfaceVariant

@Composable fun adaptiveSurfaceVariantBorder() = MaterialTheme.colorScheme.outlineVariant
