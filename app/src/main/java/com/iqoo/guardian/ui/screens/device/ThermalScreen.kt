package com.iqoo.guardian.ui.screens.device

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.theme.*

@Composable
fun ThermalScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = Color(0xFF4FC3F7), bloomCenterYFraction = 0.20f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "THERMAL INTELLIGENCE",
                title = "Device Temperature",
                subtitle = "Hardware thermal status and pressure forecasting.",
                onBack = onBack
            )

            Spacer(Modifier.height(26.dp))
            ThermalHero()

            Spacer(Modifier.height(24.dp))
            ThermalForecastCard()

            Spacer(Modifier.height(24.dp))
            ThermalMetricsGrid()

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThermalHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = "NORMAL", style = MaterialTheme.typography.displayLarge, color = Color(0xFF4FC3F7))
        Spacer(Modifier.height(8.dp))
        Text(text = "THERMAL PRESSURE: LOW", style = MaterialTheme.typography.labelMedium, color = GTextSecondary)
        
        Spacer(Modifier.height(32.dp))
        Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
            val transition = rememberInfiniteTransition(label = "therm")
            val pulse by transition.animateFloat(
                initialValue = 0.6f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
                label = "thermPulse"
            )
            Box(modifier = Modifier.size(160.dp).clip(CircleShape).background(Color(0xFF4FC3F7).copy(alpha = 0.2f * pulse)))
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFF4FC3F7).copy(alpha = 0.4f * pulse)))
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF4FC3F7).copy(alpha = 0.7f)))
        }
    }
}

@Composable
private fun ThermalForecastCard() {
    GuardianCard(contentPadding = 20.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("AI FORECAST", color = Color(0xFF4FC3F7))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "No immediate thermal performance pressure predicted.",
                    style = MaterialTheme.typography.titleMedium,
                    color = GTextPrimary
                )
            }
        }
    }
}

@Composable
private fun ThermalMetricsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricBox("Thermal Status", "NONE", Modifier.weight(1f))
            MetricBox("Demo Headroom", "0.38", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricBox("Thermal Trend", "Stable", Modifier.weight(1f), Color(0xFF4FC3F7))
            MetricBox("Confidence", "91%", Modifier.weight(1f), GAccent)
        }
    }
}

@Composable
private fun MetricBox(title: String, value: String, modifier: Modifier = Modifier, color: Color = GTextPrimary) {
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
