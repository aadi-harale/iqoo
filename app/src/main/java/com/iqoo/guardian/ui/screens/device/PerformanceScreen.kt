package com.iqoo.guardian.ui.screens.device

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.DeviceThermostat
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.theme.*

@Composable
fun PerformanceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GAccent, bloomCenterYFraction = 0.20f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "PERFORMANCE INTELLIGENCE",
                title = "Sustained Performance",
                onBack = onBack
            )

            Spacer(Modifier.height(26.dp))
            PerformanceHero()

            Spacer(Modifier.height(26.dp))
            PerformanceForecast()

            Spacer(Modifier.height(24.dp))
            PerformanceMetricsGrid()

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PerformanceHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = "STABLE", style = MaterialTheme.typography.displayMedium, color = GAccent)
        Spacer(Modifier.height(8.dp))
        Text(text = "88% GUARDIAN CONFIDENCE", style = MaterialTheme.typography.labelMedium, color = GTextSecondary)
        
        Spacer(Modifier.height(32.dp))
        // Animated Performance Waveform
        Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                val transition = rememberInfiniteTransition(label = "wave")
                for (i in 0 until 30) {
                    val height by transition.animateFloat(
                        initialValue = (20..40).random().toFloat(),
                        targetValue = (60..100).random().toFloat(),
                        animationSpec = infiniteRepeatable(
                            animation = tween((600..1200).random(), easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "wave_$i"
                    )
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(height.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(GAccent.copy(alpha = 0.8f))
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceForecast() {
    GuardianCard(contentPadding = 20.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("SUSTAINED PERFORMANCE")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "STABLE",
                    style = MaterialTheme.typography.titleMedium,
                    color = GSafe
                )
            }
            androidx.compose.material3.Button(
                onClick = { /* action */ },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GCardElevated),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("RUN DIAGNOSTIC", color = GAccent)
            }
        }
    }
}

@Composable
private fun PerformanceMetricsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricBox("Thermal Pressure", "Low", Icons.Rounded.DeviceThermostat, Modifier.weight(1f))
            MetricBox("Memory Pressure", "Normal", Icons.Rounded.Memory, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricBox("Battery Load", "Elevated", Icons.Rounded.BatteryChargingFull, Modifier.weight(1f), GWarning)
            MetricBox("Background", "1 anomaly", Icons.Rounded.Apps, Modifier.weight(1f), GWarning)
        }
    }
}

@Composable
private fun MetricBox(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, color: Color = GSafe) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GCard)
            .padding(16.dp)
    ) {
        Column {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = GTextMuted)
            Spacer(Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = color)
        }
    }
}
