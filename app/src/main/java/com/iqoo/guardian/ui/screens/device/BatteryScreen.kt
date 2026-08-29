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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.theme.*

@Composable
fun BatteryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GWarning, bloomCenterYFraction = 0.20f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "BATTERY INTELLIGENCE",
                title = "Power & Health",
                subtitle = "Local drain tracking and anomaly correlation.",
                onBack = onBack
            )

            Spacer(Modifier.height(26.dp))
            BatteryHero()

            Spacer(Modifier.height(24.dp))
            BatteryPredictionCard()

            Spacer(Modifier.height(24.dp))
            BatteryMetricsGrid()

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BatteryHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = "78%", style = MaterialTheme.typography.displayLarge, color = GAccent)
        Spacer(Modifier.height(8.dp))
        Text(text = "HEALTH: GOOD", style = MaterialTheme.typography.labelMedium, color = GSafe)
        
        Spacer(Modifier.height(32.dp))
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            val transition = rememberInfiniteTransition(label = "batt")
            val sweep by transition.animateFloat(
                initialValue = 270f * 0.78f,
                targetValue = 270f * 0.75f,
                animationSpec = infiniteRepeatable(tween(2000, easing = LinearOutSlowInEasing), RepeatMode.Reverse),
                label = "batt_sweep"
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
                    color = GAccent,
                    startAngle = 135f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Text("DISCHARGING", style = MaterialTheme.typography.labelMedium, color = GTextMuted)
        }
    }
}

@Composable
private fun BatteryPredictionCard() {
    GuardianCard(contentPadding = 20.dp, borderColor = GWarning.copy(alpha = 0.3f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("AI DRAIN FORECAST", color = GWarning)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Drain is 34% above your evening baseline.",
                    style = MaterialTheme.typography.titleMedium,
                    color = GTextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Predicted to hit 20% by 8:40 PM.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GWarning
                )
            }
            androidx.compose.material3.Button(
                onClick = { /* action */ },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GWarning.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("OPTIMIZE", color = GWarning)
            }
        }
    }
}

@Composable
private fun BatteryMetricsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricBox("Temperature", "34.2°C", Modifier.weight(1f))
            MetricBox("Voltage", "4.12 V", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricBox("Background", "FlashDeals", Modifier.weight(1f), GWarning)
            MetricBox("Confidence", "84%", Modifier.weight(1f), GAccent)
        }
    }
}

@Composable
private fun MetricBox(title: String, value: String, modifier: Modifier = Modifier, color: androidx.compose.ui.graphics.Color = GTextPrimary) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GCard)
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = GTextMuted)
            Spacer(Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = color)
        }
    }
}
