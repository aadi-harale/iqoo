package com.iqoo.guardian.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GSafe
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary

/** One line of the analysis trace. */
data class ScanStep(
    val label: String,
    val state: ScanStepState
)

enum class ScanStepState { PENDING, RUNNING, DONE, ALERT }

/** Rotating arc used while a step is in flight. */
@Composable
fun ScanningPulse(
    modifier: Modifier = Modifier,
    color: Color = GAccent,
    size: androidx.compose.ui.unit.Dp = 18.dp
) {
    val transition = rememberInfiniteTransition(label = "scan")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(950, easing = LinearEasing), RepeatMode.Restart),
        label = "scanAngle"
    )
    Canvas(modifier = modifier.size(size)) {
        val stroke = 2.2.dp.toPx()
        drawArc(
            color = color.copy(alpha = 0.18f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = 96f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

/**
 * The analysis trace. Each line appears as the pipeline reaches it, so the
 * sequence the user watches matches the order the engine actually works in.
 */
@Composable
fun ScanStepRow(
    step: ScanStep,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = step.state != ScanStepState.PENDING,
        enter = fadeIn(tween(260)) + slideInVertically(tween(300)) { it / 3 }
    ) {
        Row(
            modifier = modifier.fillMaxWidth().padding(vertical = 0.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(18.dp)
            ) {
                Spacer(Modifier.height(7.dp))
                Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                    when (step.state) {
                        ScanStepState.RUNNING -> ScanningPulse()
                        ScanStepState.DONE -> Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = GSafe,
                            modifier = Modifier.size(16.dp)
                        )
                        ScanStepState.ALERT -> Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(com.iqoo.guardian.ui.theme.GCritical)
                        )
                        ScanStepState.PENDING -> Unit
                    }
                }
                if (!isLast) {
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(18.dp)
                            .background(GBorder)
                    )
                }
            }
            Spacer(Modifier.width(13.dp))
            Column {
                Spacer(Modifier.height(7.dp))
                Text(
                    text = step.label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = when (step.state) {
                        ScanStepState.RUNNING -> GTextPrimary
                        ScanStepState.DONE -> GTextSecondary
                        ScanStepState.ALERT -> com.iqoo.guardian.ui.theme.GCritical
                        ScanStepState.PENDING -> GTextMuted
                    }
                )
                if (!isLast) Spacer(Modifier.height(8.dp))
            }
        }
    }
}

/** Indeterminate sweep bar shown above the trace while analysis runs. */
@Composable
fun ScanningBar(modifier: Modifier = Modifier, color: Color = GAccent) {
    val transition = rememberInfiniteTransition(label = "bar")
    val position by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1250, easing = LinearEasing), RepeatMode.Restart),
        label = "barPos"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .clip(CircleShape)
            .background(GBorder)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
            val barWidth = size.width * 0.3f
            drawRoundRect(
                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, color, Color.Transparent)
                ),
                topLeft = androidx.compose.ui.geometry.Offset(position * size.width, 0f),
                size = androidx.compose.ui.geometry.Size(barWidth, size.height)
            )
        }
    }
}
