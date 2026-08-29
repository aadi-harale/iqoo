package com.iqoo.guardian.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.domain.model.Severity
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCard
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.color

/**
 * The verdict panel.
 *
 * Calm when the news is good, loud when it is not: the severity colour drives a
 * bloom behind the score, a tinted fill and a semicircular gauge. That contrast
 * between a quiet dashboard and a dramatic alert is the point.
 */
@Composable
fun VerdictHeader(
    score: Int,
    severity: Severity,
    labelOverride: String? = null,
    modifier: Modifier = Modifier
) {
    val accent = severity.color()
    val severe = severity >= Severity.HIGH

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val gauge by animateFloatAsState(
        targetValue = if (started) score / 100f else 0f,
        animationSpec = tween(1100, easing = EaseOutCubic),
        label = "gauge"
    )

    val pulse = rememberInfiniteTransition(label = "verdictPulse")
    val bloom by pulse.animateFloat(
        initialValue = if (severe) 0.16f else 0.08f,
        targetValue = if (severe) 0.30f else 0.13f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "verdictBloom"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(accent.copy(alpha = 0.13f), GCard, GCard)
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.45f), GBorder)),
                RoundedCornerShape(22.dp)
            )
            .padding(22.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(180.dp)
                .blur(60.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = bloom))
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                RiskBadge(labelOverride ?: "${severity.label} RISK", accent)
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    CountUpNumber(
                        value = score,
                        style = MaterialTheme.typography.displayLarge,
                        color = accent
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = "/100",
                        style = MaterialTheme.typography.bodyLarge,
                        color = GTextMuted,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                SectionLabel("RISK SCORE")
            }

            Spacer(Modifier.width(16.dp))
            SeverityGauge(progress = gauge, accent = accent, severity = severity)
        }
    }
}

/**
 * A dial showing where this score sits on the 0-100 scale, with the five severity
 * bands marked around the rim. It makes the number comparative rather than
 * arbitrary - a judge can see that 94 is deep into the top band.
 */
@Composable
private fun SeverityGauge(
    progress: Float,
    accent: Color,
    severity: Severity,
    size: androidx.compose.ui.unit.Dp = 96.dp
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .drawBehind {
                    val stroke = 7.dp.toPx()
                    val inset = stroke / 2f
                    val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
                    val topLeft = Offset(inset, inset)

                    // Band markers around the rim.
                    Severity.entries.forEach { band ->
                        val start = -90f + (band.range.first / 100f) * 360f
                        val sweep = ((band.range.last - band.range.first + 1) / 100f) * 360f - 4f
                        drawArc(
                            color = band.color().copy(alpha = 0.16f),
                            startAngle = start,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Butt)
                        )
                    }

                    drawArc(
                        color = accent,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = severity.label.take(4),
                style = MaterialTheme.typography.labelSmall,
                color = GTextSecondary
            )
        }
    }
}

/**
 * A hairline rule tinted by severity, used to separate the alert body from the
 * calmer sections beneath it.
 */
@Composable
fun AccentRule(accent: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(accent.copy(alpha = 0.5f), Color.Transparent)
                )
            )
    )
}
