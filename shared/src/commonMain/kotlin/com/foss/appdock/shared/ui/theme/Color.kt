package com.foss.appdock.shared.ui.theme

import androidx.compose.ui.graphics.Color

// ── App Dock Brand Colors ─────────────────────────────────────────────────────
val PrimaryBlue = Color(0xFF2B6CEE) // #2b6cee
val SecondaryPurple = Color(0xFFA855F7)
val Danger = Color(0xFFEF4444) // Tailwind red-500
val SuccessGreen = Color(0xFF10B981) // Tailwind emerald-500

// ── Dark color scheme ─────────────────────────────────────────────────────────
val md_dark_primary = PrimaryBlue
val md_dark_onPrimary = Color.White
val md_dark_primaryContainer = Color(0xFF1E3A8A) // Tailwind blue-900
val md_dark_onPrimaryContainer = Color(0xFFDBEAFE) // Tailwind blue-100

val md_dark_secondary = SecondaryPurple
val md_dark_onSecondary = Color.White
val md_dark_secondaryContainer = Color(0xFF581C87) // Tailwind purple-900
val md_dark_onSecondaryContainer = Color(0xFFF3E8FF) // Tailwind purple-100

val md_dark_error = Color(0xFFEF4444) // Tailwind red-500
val md_dark_onError = Color.White
val md_dark_errorContainer = Color(0xFF7F1D1D) // Tailwind red-900
val md_dark_onErrorContainer = Color(0xFFFEE2E2) // Tailwind red-100

val md_dark_background = Color(0xFF0A0D14) // #0a0d14
val md_dark_onBackground = Color(0xFFF1F5F9) // Tailwind slate-100
val md_dark_surface = Color(0xFF101622) // #101622
val md_dark_onSurface = Color(0xFFF1F5F9)

val md_dark_surfaceVariant = Color(0xFF1E293B) // Tailwind slate-800
val md_dark_onSurfaceVariant = Color(0xFF94A3B8) // Tailwind slate-400
val md_dark_outline = Color(0xFF334155) // Tailwind slate-700
val md_dark_outlineVariant = Color(0xFF0F172A) // Tailwind slate-900

// Solid panel colors are defined in SolidStyle.kt (both static constants and
// adaptive @Composable helpers for dark/light mode switching).

// ── Light color scheme (Fallback, as design is dark-mode first) ─────────────
val md_light_primary = PrimaryBlue
val md_light_onPrimary = Color.White
val md_light_primaryContainer = Color(0xFFDBEAFE)
val md_light_onPrimaryContainer = Color(0xFF1E3A8A)

val md_light_secondary = SecondaryPurple
val md_light_onSecondary = Color.White
val md_light_secondaryContainer = Color(0xFFF3E8FF)
val md_light_onSecondaryContainer = Color(0xFF581C87)

val md_light_error = Color(0xFFEF4444)
val md_light_onError = Color.White
val md_light_errorContainer = Color(0xFFFEE2E2)
val md_light_onErrorContainer = Color(0xFF7F1D1D)

val md_light_background = Color(0xFFF6F6F8) // #f6f6f8
val md_light_onBackground = Color(0xFF0F172A)
val md_light_surface = Color(0xFFFFFFFF)
val md_light_onSurface = Color(0xFF0F172A)

val md_light_surfaceVariant = Color(0xFFE2E8F0)
val md_light_onSurfaceVariant = Color(0xFF475569)
val md_light_outline = Color(0xFF94A3B8)
val md_light_outlineVariant = Color(0xFFCBD5E1)

// ── AMOLED Black scheme ───────────────────────────────────────────────────────
val md_amoled_primary = PrimaryBlue
val md_amoled_onPrimary = Color.White
val md_amoled_primaryContainer = Color(0xFF1E3A8A)
val md_amoled_onPrimaryContainer = Color(0xFFDBEAFE)

val md_amoled_secondary = SecondaryPurple
val md_amoled_onSecondary = Color.White
val md_amoled_secondaryContainer = Color(0xFF581C87)
val md_amoled_onSecondaryContainer = Color(0xFFF3E8FF)

val md_amoled_error = Color(0xFFEF4444)
val md_amoled_onError = Color.White
val md_amoled_errorContainer = Color(0xFF7F1D1D)
val md_amoled_onErrorContainer = Color(0xFFFEE2E2)

val md_amoled_background = Color.Black
val md_amoled_onBackground = Color(0xFFF1F5F9)
val md_amoled_surface = Color.Black
val md_amoled_onSurface = Color(0xFFF1F5F9)

val md_amoled_surfaceVariant = Color(0xFF121212)
val md_amoled_onSurfaceVariant = Color(0xFF94A3B8)
val md_amoled_outline = Color(0xFF334155)
val md_amoled_outlineVariant = Color.Black
