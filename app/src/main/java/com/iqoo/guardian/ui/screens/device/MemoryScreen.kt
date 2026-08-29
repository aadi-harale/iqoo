package com.iqoo.guardian.ui.screens.device

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.theme.*

@Composable
fun MemoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GSafe, bloomCenterYFraction = 0.20f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "MEMORY INTELLIGENCE",
                title = "System RAM",
                onBack = onBack
            )

            Spacer(Modifier.height(26.dp))
            MemoryHero()

            Spacer(Modifier.height(24.dp))
            MemoryForecastCard()

            Spacer(Modifier.height(24.dp))
            SectionLabel("RECENT CONTRIBUTORS")
            Spacer(Modifier.height(12.dp))
            MemoryAppRow("System UI", "1.2 GB", GSafe)
            MemoryAppRow("Instagram", "840 MB", GSafe)
            MemoryAppRow("Camera", "620 MB", GSafe)
            MemoryAppRow("FlashDeals", "510 MB", GWarning, "Unusual background retention")

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MemoryHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = "NORMAL", style = MaterialTheme.typography.displayMedium, color = GSafe)
        Spacer(Modifier.height(8.dp))
        Text(text = "MEMORY PRESSURE", style = MaterialTheme.typography.labelMedium, color = GTextSecondary)
        
        Spacer(Modifier.height(32.dp))
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            val transition = rememberInfiniteTransition(label = "mem")
            val sweep by transition.animateFloat(
                initialValue = 180f,
                targetValue = 195f,
                animationSpec = infiniteRepeatable(tween(2500, easing = LinearOutSlowInEasing), RepeatMode.Reverse),
                label = "mem_sweep"
            )
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = GBorder,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = GSafe,
                    startAngle = 135f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("5.4 GB", style = MaterialTheme.typography.displayMedium, color = GTextPrimary)
                Text("AVAILABLE / 12 GB", style = MaterialTheme.typography.labelMedium, color = GTextMuted)
            }
        }
    }
}

@Composable
private fun MemoryForecastCard() {
    GuardianCard(contentPadding = 20.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("MEMORY FORECAST", color = GSafe)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "No pressure predicted.",
                    style = MaterialTheme.typography.titleMedium,
                    color = GTextPrimary
                )
            }
        }
    }
}

@Composable
private fun MemoryAppRow(name: String, usage: String, statusColor: Color, subtitle: String? = null) {
    GuardianCard(modifier = Modifier.padding(bottom = 8.dp), borderColor = if (statusColor != GSafe) statusColor.copy(alpha = 0.5f) else GBorder) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(GBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1), color = GTextPrimary, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium, color = GTextPrimary)
                if (subtitle != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = statusColor)
                }
            }
            if (statusColor == GWarning) {
                androidx.compose.material3.Button(
                    onClick = { /* action */ },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GWarning.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("FREEZE", color = GWarning, style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Text(text = usage, style = MaterialTheme.typography.titleMedium, color = statusColor)
            }
        }
    }
}
