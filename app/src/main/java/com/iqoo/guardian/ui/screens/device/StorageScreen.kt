package com.iqoo.guardian.ui.screens.device

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.theme.*

@Composable
fun StorageScreen(
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
                eyebrow = "STORAGE INTELLIGENCE",
                title = "Device Capacity",
                subtitle = "Monitored space for Contradiction Engine validation.",
                onBack = onBack
            )

            Spacer(Modifier.height(26.dp))
            StorageHero()

            Spacer(Modifier.height(24.dp))
            StorageBreakdown()

            Spacer(Modifier.height(24.dp))
            StoragePredictionCard()

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StorageHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = "42 GB", style = MaterialTheme.typography.displayLarge, color = GSafe)
        Spacer(Modifier.height(4.dp))
        Text(text = "FREE OF 256 GB", style = MaterialTheme.typography.labelMedium, color = GTextSecondary)
        
        Spacer(Modifier.height(24.dp))
        
        Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
            drawRoundRect(color = GBorder, size = Size(size.width, size.height), cornerRadius = CornerRadius(12.dp.toPx()))
            drawRoundRect(color = GSafe, size = Size(size.width * 0.83f, size.height), cornerRadius = CornerRadius(12.dp.toPx()))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0 GB", style = MaterialTheme.typography.labelSmall, color = GTextMuted)
            Text("256 GB", style = MaterialTheme.typography.labelSmall, color = GTextMuted)
        }
    }
}

@Composable
private fun StorageBreakdown() {
    GuardianCard(contentPadding = 20.dp) {
        SectionLabel("STORAGE BREAKDOWN")
        Spacer(Modifier.height(16.dp))
        
        StorageRowItem("Apps", "86 GB", GTextPrimary)
        StorageRowItem("Photos & Videos", "61 GB", GTextPrimary)
        StorageRowItem("System", "31 GB", GTextPrimary)
        StorageRowItem("Other", "36 GB", GTextMuted)
    }
}

@Composable
private fun StorageRowItem(title: String, amount: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
        Text(amount, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

@Composable
private fun StoragePredictionCard() {
    GuardianCard(contentPadding = 20.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("AI FORECAST")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Capacity threshold estimated in 11 days.",
                    style = MaterialTheme.typography.titleMedium,
                    color = GTextPrimary
                )
            }
        }
    }
}
