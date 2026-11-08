package com.still.clock.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val ACTIVE_FRAME_INTERVAL_MS = 33L
private const val AMBIENT_FRAME_INTERVAL_MS = 100L
private const val BREATH_PERIOD_MS = 16_000L

@Composable
fun ClockCanvas(
    palette: ClockPalette,
    showSeconds: Boolean,
    modifier: Modifier = Modifier,
    animate: Boolean = true,
) {
    var animationTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(showSeconds, animate) {
        if (animate) {
            while (true) {
                animationTime = System.currentTimeMillis()
                delay(
                    if (showSeconds) ACTIVE_FRAME_INTERVAL_MS
                    else AMBIENT_FRAME_INTERVAL_MS,
                )
            }
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val width = with(density) { maxWidth.toPx() }
        val height = with(density) { maxHeight.toPx() }
        if (width <= 0f || height <= 0f) return@BoxWithConstraints

        val landscape = width > height
        val center = remember(width, height) {
            if (landscape) {
                Offset(width * 0.73f, height * 0.43f)
            } else {
                Offset(width * 0.68f, height * 0.30f)
            }
        }
        val radius = remember(width, height) {
            minOf(width, height) * if (landscape) 0.27f else 0.285f
        }
        val orbitRect = remember(center, radius) {
            Rect(
                left = center.x - radius * 1.34f,
                top = center.y - radius * 0.67f,
                right = center.x + radius * 1.34f,
                bottom = center.y + radius * 0.67f,
            )
        }
        val ambientBrush = remember(width, height, palette) {
            Brush.radialGradient(
                colorStops = arrayOf(
                    0f to palette.accent.copy(alpha = 0.075f),
                    0.48f to palette.accent.copy(alpha = 0.025f),
                    1f to Color.Transparent,
                ),
                center = center,
                radius = radius * 3.2f,
            )
        }
        val haloBrush = remember(width, height, palette) {
            Brush.radialGradient(
                colorStops = arrayOf(
                    0f to palette.accent.copy(alpha = 0.13f),
                    0.46f to palette.accent.copy(alpha = 0.055f),
                    1f to Color.Transparent,
                ),
                center = center,
                radius = radius * 1.95f,
            )
        }
        val orbBrush = remember(width, height, palette) {
            Brush.radialGradient(
                colorStops = arrayOf(
                    0f to palette.coreLight,
                    0.58f to palette.coreDark,
                    1f to palette.background,
                ),
                center = Offset(center.x - radius * 0.28f, center.y - radius * 0.30f),
                radius = radius * 1.35f,
            )
        }
        val rimBrush = remember(width, height, palette) {
            Brush.sweepGradient(
                colorStops = arrayOf(
                    0f to Color.Transparent,
                    0.14f to palette.accent.copy(alpha = 0.06f),
                    0.32f to palette.accent.copy(alpha = 0.85f),
                    0.46f to palette.accent.copy(alpha = 0.12f),
                    0.72f to Color.Transparent,
                    1f to Color.Transparent,
                ),
                center = center,
            )
        }
        val vignetteBrush = remember(width, height) {
            Brush.verticalGradient(
                colorStops = arrayOf(
                    0f to Color.Black.copy(alpha = 0.12f),
                    0.48f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.32f),
                ),
                startY = 0f,
                endY = height,
            )
        }
        val orbitStroke = with(density) { 0.65.dp.toPx() }
        val rimStroke = with(density) { 1.1.dp.toPx() }
        val secondStroke = with(density) { 1.15.dp.toPx() }
        val dotRadius = with(density) { 2.6.dp.toPx() }
        val dotHalo = with(density) { 12.dp.toPx() }
        val orbitStyle = remember(orbitStroke) { Stroke(width = orbitStroke) }
        val rimStyle = remember(rimStroke) { Stroke(width = rimStroke) }
        val secondStyle = remember(secondStroke) { Stroke(width = secondStroke) }
        val orbitColor = palette.secondary.copy(alpha = 0.14f)
        val foregroundOrbitColor = palette.accent.copy(alpha = 0.20f)
        val secondTrailColor = palette.accent.copy(alpha = 0.50f)
        val secondMidHaloColor = palette.accent.copy(alpha = 0.16f)
        val secondHighlightColor = Color.White.copy(alpha = 0.80f)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val breathPhase = (animationTime % BREATH_PERIOD_MS) /
                BREATH_PERIOD_MS.toDouble() * 2.0 * PI
            val breath = ((sin(breathPhase) + 1.0) / 2.0).toFloat()

            drawRect(brush = ambientBrush)
            drawCircle(
                brush = haloBrush,
                radius = radius * (1.94f + breath * 0.025f),
                center = center,
            )
            drawOval(
                color = orbitColor,
                topLeft = orbitRect.topLeft,
                size = orbitRect.size,
                style = orbitStyle,
            )
            drawCircle(
                brush = orbBrush,
                radius = radius,
                center = center,
            )
            drawCircle(
                brush = rimBrush,
                radius = radius,
                center = center,
                style = rimStyle,
            )
            drawArc(
                color = foregroundOrbitColor,
                startAngle = 8f,
                sweepAngle = 164f,
                useCenter = false,
                topLeft = orbitRect.topLeft,
                size = orbitRect.size,
                style = orbitStyle,
            )

            if (showSeconds) {
                val secondProgress = (animationTime % 60_000L) / 60_000f
                val secondAngle = secondProgress * 360f - 90f
                val angleRadians = Math.toRadians(secondAngle.toDouble())

                drawArc(
                    color = secondTrailColor,
                    startAngle = secondAngle - 20f,
                    sweepAngle = 20f,
                    useCenter = false,
                    topLeft = orbitRect.topLeft,
                    size = orbitRect.size,
                    style = secondStyle,
                )
                val secondPoint = Offset(
                    x = center.x + orbitRect.width / 2f * cos(angleRadians).toFloat(),
                    y = center.y + orbitRect.height / 2f * sin(angleRadians).toFloat(),
                )
                drawCircle(
                    color = palette.accent,
                    alpha = 0.07f + breath * 0.025f,
                    radius = dotHalo,
                    center = secondPoint,
                )
                drawCircle(
                    color = secondMidHaloColor,
                    radius = dotRadius * 2.1f,
                    center = secondPoint,
                )
                drawCircle(
                    color = palette.accent,
                    radius = dotRadius,
                    center = secondPoint,
                )
                drawCircle(
                    color = secondHighlightColor,
                    radius = dotRadius * 0.32f,
                    center = secondPoint,
                )
            }
            drawRect(brush = vignetteBrush)
        }
    }
}
