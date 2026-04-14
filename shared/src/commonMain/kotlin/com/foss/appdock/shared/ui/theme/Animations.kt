package com.foss.appdock.shared.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

// ── Press-scale animation ─────────────────────────────────────────────────────
// iOS/macOS-style spring rebound for interactive elements.
@Composable
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.96f,
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessMediumLow
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = dampingRatio, stiffness = stiffness),
        label = "pressScale"
    )
    return this.graphicsLayer { scaleX = scale; scaleY = scale }
}

// ── Fade + slide-up entrance ──────────────────────────────────────────────────
@Composable
fun Modifier.fadeSlideIn(
    delayMillis: Int = 0,
    durationMillis: Int = 400
): Modifier {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (appeared) 0f else 24f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "slideUp"
    )
    return this.graphicsLayer { this.alpha = alpha; translationY = offsetY }
}

// ── Staggered list entrance ───────────────────────────────────────────────────
@Composable
fun Modifier.staggeredFadeIn(index: Int, staggerDelay: Int = 40, baseDuration: Int = 350): Modifier =
    this.fadeSlideIn(delayMillis = index * staggerDelay, durationMillis = baseDuration)
