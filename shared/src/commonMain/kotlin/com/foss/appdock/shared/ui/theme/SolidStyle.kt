package com.foss.appdock.shared.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Static fallback constants (kept for non-composable contexts) ──────────────
// These are the dark-mode values; prefer the @Composable helpers above
// when inside a Composable function.
val SolidPanelBackground = Color(0xFF1E293B) // Solid
val SolidPanelBorder = Color(0xFF334155) // Solid
val SolidPanelHover = Color(0xFF334155) // Solid

// ── Modifier helper ───────────────────────────────────────────────────────────

@Composable
fun Modifier.surfaceVariantPanel(
        shape: Shape = RoundedCornerShape(16.dp),
        borderWidth: Dp = 1.dp,
): Modifier {
        val bg = adaptiveSurfaceVariantBackground()
        val border = adaptiveSurfaceVariantBorder()
        return this.clip(shape).background(bg).border(borderWidth, border, shape)
}

// ── Reusable SolidPanel composable ───────────────────────────────────────────

@Composable
fun SolidPanelModifier(
        shape: Shape = RoundedCornerShape(16.dp),
        backgroundColor: Color = SolidPanelBackground,
        borderColor: Color = SolidPanelBorder,
        borderWidth: Float = 1f,
): Modifier {
        return Modifier.clip(shape)
                .background(backgroundColor)
                .border(width = borderWidth.dp, color = borderColor, shape = shape)
}

@Composable
fun SolidPanel(
        modifier: Modifier = Modifier,
        shape: Shape = RoundedCornerShape(16.dp),
        backgroundColor: Color = SolidPanelBackground,
        borderColor: Color = SolidPanelBorder,
        borderWidth: Float = 1f,
        content: @Composable BoxScope.() -> Unit,
) {
        Box(
                modifier =
                        modifier.then(
                                SolidPanelModifier(shape, backgroundColor, borderColor, borderWidth)
                        ),
                content = content,
        )
}
