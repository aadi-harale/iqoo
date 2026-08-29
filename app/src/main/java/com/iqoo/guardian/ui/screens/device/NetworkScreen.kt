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
fun NetworkScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = Color(0xFF6C92FF), bloomCenterYFraction = 0.20f)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "NETWORK INTELLIGENCE",
                title = "Connectivity",
                subtitle = "Coarse connection state and stability.",
                onBack = onBack
            )
            Spacer(Modifier.height(26.dp))
            NetworkHero()
            Spacer(Modifier.height(24.dp))
            NetworkDiagnostics()
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NetworkHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("SECURE", style = MaterialTheme.typography.displayLarge, color = Color(0xFF6C92FF))
        Text("NO ANOMALIES DETECTED", style = MaterialTheme.typography.labelMedium, color = GTextSecondary)
    }
}

@Composable
private fun NetworkDiagnostics() {
    GuardianCard(contentPadding = 20.dp) {
        SectionLabel("STATUS")
        Spacer(Modifier.height(16.dp))
        DiagRow("Active Interface", "Wi-Fi", GTextPrimary)
        DiagRow("Internet Validation", "Validated", GSafe)
        DiagRow("Metered Connection", "No", GTextPrimary)
        DiagRow("VPN Active", "Off", GTextPrimary)
        DiagRow("Stability", "Normal", GSafe)
    }
}

@Composable
private fun DiagRow(title: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}
