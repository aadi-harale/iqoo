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
fun HealthScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GSafe, bloomCenterYFraction = 0.20f)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "SYSTEM INTELLIGENCE",
                title = "Device Health",
                subtitle = "Overall hardware and intelligence overview.",
                onBack = onBack
            )
            Spacer(Modifier.height(26.dp))
            HealthHero()
            Spacer(Modifier.height(24.dp))
            HealthDiagnostics()
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HealthHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("87", style = MaterialTheme.typography.displayLarge, color = GSafe)
        Text("GUARDIAN SCORE", style = MaterialTheme.typography.labelMedium, color = GTextSecondary)
    }
}

@Composable
private fun HealthDiagnostics() {
    GuardianCard(contentPadding = 20.dp) {
        SectionLabel("SYSTEM INFORMATION")
        Spacer(Modifier.height(16.dp))
        DiagRow("Manufacturer", "iQOO (DEMO)", GTextPrimary)
        DiagRow("Model", "iQOO 12 (DEMO)", GTextPrimary)
        DiagRow("Memory", "12 GB RAM", GTextPrimary)
        DiagRow("Storage", "256 GB", GTextPrimary)
        DiagRow("Display", "120 Hz", GTextPrimary)
        Spacer(Modifier.height(16.dp))
        SectionLabel("SUBSYSTEM SCORES")
        Spacer(Modifier.height(16.dp))
        DiagRow("Security", "94", GSafe)
        DiagRow("Battery", "91", GSafe)
        DiagRow("Thermal", "96", GSafe)
        DiagRow("Performance", "88", GSafe)
        DiagRow("Apps", "82", GAccent)
    }
}

@Composable
private fun DiagRow(title: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}
