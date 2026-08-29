package com.iqoo.guardian.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.graphics.Color
import com.iqoo.guardian.ui.theme.GSafe
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.GWarning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary

@Composable
fun WeeklyThreatChart(modifier: Modifier = Modifier) {
    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animate = true }

    // Mock data for the last 7 days to make the chart look active and good
    val data = listOf(2, 5, 1, 0, 8, 3, 4)
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today")
    val max = data.maxOrNull()?.coerceAtLeast(1) ?: 1

    GuardianCard(modifier = modifier) {
        SectionLabel("WEEKLY THREAT ACTIVITY")
        Spacer(Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEachIndexed { index, value ->
                val progress by animateFloatAsState(
                    targetValue = if (animate) value.toFloat() / max else 0f,
                    animationSpec = tween(900, delayMillis = index * 70, easing = FastOutSlowInEasing),
                    label = "bar"
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                        Canvas(modifier = Modifier.fillMaxWidth(0.55f).height(120.dp)) {
                            val barHeight = size.height * progress
                            // Background track
                            drawRoundRect(
                                color = GBorder.copy(alpha = 0.4f),
                                topLeft = Offset(0f, 0f),
                                size = Size(size.width, size.height),
                                cornerRadius = CornerRadius(16f, 16f)
                            )
                            // Glow and Fill
                            if (barHeight > 0) {
                                val brush = Brush.verticalGradient(
                                    colors = listOf(GAccent, GAccent.copy(alpha = 0.1f)),
                                    startY = size.height - barHeight,
                                    endY = size.height
                                )
                                drawRoundRect(
                                    brush = brush,
                                    topLeft = Offset(0f, size.height - barHeight),
                                    size = Size(size.width, barHeight),
                                    cornerRadius = CornerRadius(16f, 16f)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = days[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = if (index == data.lastIndex) GTextPrimary else GTextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionsDonutChart(modifier: Modifier = Modifier) {
    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animate = true }

    // Mock permissions data (Camera, Microphone, Location, Contacts)
    val permissions = listOf(
        Pair("Location", 12f),
        Pair("Camera", 8f),
        Pair("Microphone", 6f),
        Pair("Contacts", 14f)
    )
    val total = permissions.sumOf { it.second.toDouble() }.toFloat()
    val colors = listOf(GAccent, GSafe, com.iqoo.guardian.ui.theme.GWarning, com.iqoo.guardian.ui.theme.GTextSecondary)

    val sweepProgress by animateFloatAsState(
        targetValue = if (animate) 1f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "donutSweep"
    )

    GuardianCard(modifier = modifier) {
        SectionLabel("APP PERMISSIONS BREAKDOWN")
        Spacer(Modifier.height(20.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    var startAngle = -90f
                    
                    permissions.forEachIndexed { index, data ->
                        val sweepAngle = (data.second / total) * 360f * sweepProgress
                        drawArc(
                            color = colors[index],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            ),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        )
                        startAngle += (data.second / total) * 360f
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${total.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GTextPrimary
                    )
                    Text(
                        text = "Apps",
                        style = MaterialTheme.typography.labelSmall,
                        color = GTextMuted
                    )
                }
            }
            
            Spacer(Modifier.width(24.dp))
            
            Column {
                permissions.forEachIndexed { index, data ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colors[index]))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = data.first,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GTextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${data.second.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkTrafficChart(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while(true) {
            withFrameNanos { time ->
                phase = (time / 10000000L % 360).toFloat()
            }
        }
    }

    GuardianCard(modifier = modifier) {
        SectionLabel("REAL-TIME NETWORK TRAFFIC")
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = androidx.compose.ui.graphics.Path()
                val width = size.width
                val height = size.height
                
                path.moveTo(0f, height)
                
                // Draw sine wave
                for (x in 0..width.toInt() step 5) {
                    val normalizedX = x / width
                    val wave1 = kotlin.math.sin(normalizedX * 10f + phase * 0.1f) * 0.3f
                    val wave2 = kotlin.math.cos(normalizedX * 20f - phase * 0.05f) * 0.2f
                    val y = height * 0.5f + (wave1 + wave2) * height
                    path.lineTo(x.toFloat(), y)
                }
                
                path.lineTo(width, height)
                path.close()
                
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(GAccent.copy(alpha = 0.5f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )
                
                // Draw top line
                val strokePath = androidx.compose.ui.graphics.Path()
                strokePath.moveTo(0f, height * 0.5f + (kotlin.math.sin(phase * 0.1f) * 0.3f + kotlin.math.cos(-phase * 0.05f) * 0.2f) * height)
                for (x in 0..width.toInt() step 5) {
                    val normalizedX = x / width
                    val wave1 = kotlin.math.sin(normalizedX * 10f + phase * 0.1f) * 0.3f
                    val wave2 = kotlin.math.cos(normalizedX * 20f - phase * 0.05f) * 0.2f
                    val y = height * 0.5f + (wave1 + wave2) * height
                    strokePath.lineTo(x.toFloat(), y)
                }
                drawPath(
                    path = strokePath,
                    color = GAccent,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )
            }
            
            // Random changing numbers
            var upSpeed by remember { mutableStateOf(0) }
            var downSpeed by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                while(true) {
                    kotlinx.coroutines.delay(800)
                    upSpeed = (12..450).random()
                    downSpeed = (20..1400).random()
                }
            }
            
            Row(modifier = Modifier.align(Alignment.TopEnd), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("DL", style = MaterialTheme.typography.labelSmall, color = GTextMuted)
                    Text("$downSpeed KB/s", style = MaterialTheme.typography.bodyMedium, color = GSafe)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("UL", style = MaterialTheme.typography.labelSmall, color = GTextMuted)
                    Text("$upSpeed KB/s", style = MaterialTheme.typography.bodyMedium, color = GAccent)
                }
            }
        }
    }
}
