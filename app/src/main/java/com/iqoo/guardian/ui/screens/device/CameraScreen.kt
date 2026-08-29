package com.iqoo.guardian.ui.screens.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun CameraScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GSafe, bloomCenterYFraction = 0.20f)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "CAMERA INTELLIGENCE",
                title = "Camera Diagnostics",
                subtitle = "Hardware availability and capability checks.",
                onBack = onBack
            )
            Spacer(Modifier.height(26.dp))
            CameraHero()
            Spacer(Modifier.height(24.dp))
            CameraDiagnostics()
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CameraHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("READY", style = MaterialTheme.typography.displayLarge, color = GSafe)
        Text("ALL SENSORS RESPONDING", style = MaterialTheme.typography.labelMedium, color = GTextSecondary)
    }
}

@Composable
private fun CameraDiagnostics() {
    GuardianCard(contentPadding = 20.dp) {
        SectionLabel("DIAGNOSTICS")
        Spacer(Modifier.height(16.dp))
        DiagRow("Rear Cameras", "3", GTextPrimary)
        DiagRow("Front Cameras", "1", GTextPrimary)
        DiagRow("Flash Availability", "Ready", GSafe)
        DiagRow("Hardware Health", "Passed", GSafe)
    }
}

@Composable
private fun DiagRow(title: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}
