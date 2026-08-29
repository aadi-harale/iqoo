package com.iqoo.guardian.ui.screens.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.theme.*

@Composable
fun SensorsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GSafe, bloomCenterYFraction = 0.20f)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "SENSOR INTELLIGENCE",
                title = "Hardware Sensors",
                subtitle = "Hardware availability diagnostics.",
                onBack = onBack
            )
            Spacer(Modifier.height(26.dp))
            SensorsHero()
            Spacer(Modifier.height(24.dp))
            SensorsDiagnostics()
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SensorsHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("12", style = MaterialTheme.typography.displayLarge, color = GSafe)
        Text("SENSORS ACTIVE", style = MaterialTheme.typography.labelMedium, color = GTextSecondary)
    }
}

@Composable
private fun SensorsDiagnostics() {
    GuardianCard(contentPadding = 20.dp) {
        SectionLabel("RESPONDING HARDWARE")
        Spacer(Modifier.height(16.dp))
        DiagRow("Accelerometer", "Responding", GSafe)
        DiagRow("Gyroscope", "Responding", GSafe)
        DiagRow("Proximity", "Responding", GSafe)
        DiagRow("Ambient Light", "Responding", GSafe)
        DiagRow("Magnetometer", "Responding", GSafe)
    }
}

@Composable
private fun DiagRow(title: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}
