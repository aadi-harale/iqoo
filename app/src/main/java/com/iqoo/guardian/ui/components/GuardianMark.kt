package com.iqoo.guardian.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GTextPrimary

/**
 * The Guardian mark: a shield outline with a signal pulse at its centre.
 * Drawn rather than shipped as an asset so it can animate on the splash.
 *
 * [progress] draws the shield (0..0.6) then the pulse (0.6..1).
 */
@Composable
fun GuardianMark(
    modifier: Modifier = Modifier,
    size: Dp = 92.dp,
    progress: Float = 1f,
    shieldColor: Color = GTextPrimary,
    accentColor: Color = GAccent
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val s = this.size.width
            val u = s / 108f
            val stroke = 3.2f * u

            val shieldPhase = (progress / 0.6f).coerceIn(0f, 1f)
            val pulsePhase = ((progress - 0.55f) / 0.45f).coerceIn(0f, 1f)

            if (shieldPhase > 0f) {
                val shield = Path().apply {
                    moveTo(54 * u, 26 * u)
                    lineTo(79 * u, 35 * u)
                    lineTo(79 * u, 54 * u)
                    cubicTo(79 * u, 69 * u, 68 * u, 79 * u, 54 * u, 84 * u)
                    cubicTo(40 * u, 79 * u, 29 * u, 69 * u, 29 * u, 54 * u)
                    lineTo(29 * u, 35 * u)
                    close()
                }
                drawPath(
                    path = shield,
                    color = shieldColor.copy(alpha = shieldPhase),
                    style = Stroke(width = stroke, join = StrokeJoin.Round, cap = StrokeCap.Round)
                )
            }

            if (pulsePhase > 0f) {
                val arcStroke = 3f * u
                drawArc(
                    color = accentColor.copy(alpha = pulsePhase),
                    startAngle = 120f,
                    sweepAngle = 120f * pulsePhase,
                    useCenter = false,
                    topLeft = Offset(38 * u, 33 * u),
                    size = androidx.compose.ui.geometry.Size(32 * u, 28 * u),
                    style = Stroke(width = arcStroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = accentColor.copy(alpha = pulsePhase),
                    startAngle = -60f,
                    sweepAngle = 120f * pulsePhase,
                    useCenter = false,
                    topLeft = Offset(38 * u, 33 * u),
                    size = androidx.compose.ui.geometry.Size(32 * u, 28 * u),
                    style = Stroke(width = arcStroke, cap = StrokeCap.Round)
                )
                drawCircle(
                    color = accentColor.copy(alpha = pulsePhase),
                    radius = 5.5f * u * pulsePhase,
                    center = Offset(54 * u, 47 * u)
                )
                drawLine(
                    color = shieldColor.copy(alpha = pulsePhase),
                    start = Offset(54 * u, 58 * u),
                    end = Offset(54 * u, 58 * u + 12 * u * pulsePhase),
                    strokeWidth = 3f * u,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
