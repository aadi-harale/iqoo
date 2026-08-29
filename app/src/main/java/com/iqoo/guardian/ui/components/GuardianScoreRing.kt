package com.iqoo.guardian.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GSafe
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val TICK_COUNT = 60
private const val START_ANGLE = -90f

/**
 * The Guardian Score.
 *
 * Four layers, drawn back to front: a bloom, a graduated tick dial, the track,
 * and the progress arc with a lit cap at its head. It sweeps 0 -> target on first
 * composition and animates to any later value, so blocking a threat visibly moves
 * the number.
 */
@Composable
fun GuardianScoreRing(
    score: Int,
    statusLabel: String,
    modifier: Modifier = Modifier,
    diameter: Dp = 212.dp,
    strokeWidth: Dp = 10.dp,
    accent: Color = GAccent
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(score) {
        progress.animateTo(
            targetValue = score / 100f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    val breath = rememberInfiniteTransition(label = "breath")
    val bloom by breath.animateFloat(
        initialValue = 0.13f,
        targetValue = 0.26f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label = "bloom"
    )

    val orbitAngle by breath.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "orbit"
    )

    Box(modifier = modifier.size(diameter * 1.34f), contentAlignment = Alignment.Center) {

        // Layer 1 - the bloom. Blurred so it reads as emitted light.
        Box(
            modifier = Modifier
                .size(diameter * 0.92f)
                .blur(diameter / 3.4f)
                .drawRing(accent.copy(alpha = bloom), strokeWidth * 2.4f)
        )

        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(inset, inset)
            val radius = size.minDimension / 2f

            // Layer 2 - graduations. Lit up to the current value, dim beyond it.
            val tickOuter = radius - stroke * 1.55f
            val tickInner = tickOuter - stroke * 0.52f
            repeat(TICK_COUNT) { i ->
                val fraction = i / TICK_COUNT.toFloat()
                val angleRad = Math.toRadians((START_ANGLE + fraction * 360f).toDouble())
                val cosA = cos(angleRad).toFloat()
                val sinA = sin(angleRad).toFloat()
                val lit = fraction <= progress.value
                drawLine(
                    color = if (lit) accent.copy(alpha = 0.42f) else GBorder,
                    start = Offset(center.x + cosA * tickInner, center.y + sinA * tickInner),
                    end = Offset(center.x + cosA * tickOuter, center.y + sinA * tickOuter),
                    strokeWidth = 1.6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Layer 3 - the track.
            drawArc(
                color = GBorder,
                startAngle = START_ANGLE,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Layer 4 - progress. The sweep is rotated so the gradient starts at
            // the top of the ring rather than at 3 o'clock.
            val sweep = 360f * progress.value
            rotate(degrees = START_ANGLE, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(GSafe, accent, accent, GSafe, GSafe),
                        center = center
                    ),
                    startAngle = 0f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }

            // The lit head of the arc.
            if (progress.value > 0.02f) {
                val headRad = Math.toRadians((START_ANGLE + sweep).toDouble())
                val headRadius = (size.minDimension - stroke) / 2f
                val head = Offset(
                    center.x + cos(headRad).toFloat() * headRadius,
                    center.y + sin(headRad).toFloat() * headRadius
                )
                drawCircle(color = accent.copy(alpha = 0.22f), radius = stroke * 1.15f, center = head)
                drawCircle(color = Color.White, radius = stroke * 0.24f, center = head)
            }
            
            // Layer 5 - Orbiting high-tech particles
            val orbitRadius = (size.minDimension + stroke * 3f) / 2f
            for (i in 0..2) {
                val offsetAngle = orbitAngle + (i * 120f)
                val orbitRad = Math.toRadians(offsetAngle.toDouble())
                val particleOffset = Offset(
                    center.x + cos(orbitRad).toFloat() * orbitRadius,
                    center.y + sin(orbitRad).toFloat() * orbitRadius
                )
                drawCircle(color = accent, radius = stroke * 0.15f, center = particleOffset)
                drawCircle(color = accent.copy(alpha = 0.4f), radius = stroke * 0.4f, center = particleOffset)
            }

            // Layer 6 - Ambient Sparkles
            for (i in 0..15) {
                val xPhase = (i * 137.5f) % size.width
                val speed = 1f + (i % 3) * 0.5f
                val yProgress = (orbitAngle * speed + i * 45f) % 360f / 360f
                val yPos = size.height - (yProgress * size.height * 1.5f)
                val xPos = xPhase + kotlin.math.sin(yProgress * 10f + i) * 20f
                
                val alpha = (1f - kotlin.math.abs(yProgress - 0.5f) * 2f).coerceIn(0f, 1f) * 0.6f
                if (yPos > -20f && yPos < size.height + 20f) {
                    drawCircle(
                        color = accent.copy(alpha = alpha),
                        radius = (1f + (i % 3)).dp.toPx(),
                        center = Offset(xPos, yPos)
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = (progress.value * 100).roundToInt().coerceIn(0, 100).toString(),
                style = MaterialTheme.typography.displayLarge,
                color = GTextPrimary
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelMedium,
                color = GTextMuted
            )
        }
    }
}

/** Draws a plain ring, used as the blurred bloom source. */
private fun Modifier.drawRing(color: Color, width: Dp): Modifier =
    this.drawBehind {
        drawCircle(
            color = color,
            radius = (size.minDimension - width.toPx()) / 2f,
            style = Stroke(width = width.toPx())
        )
    }
