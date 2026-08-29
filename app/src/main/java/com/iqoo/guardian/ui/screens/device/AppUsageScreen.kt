package com.iqoo.guardian.ui.screens.device

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
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
fun AppUsageScreen(
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
                eyebrow = "APP INTELLIGENCE",
                title = "Behavior Analysis",
                subtitle = "Local usage tracking and anomaly detection.",
                onBack = onBack
            )

            Spacer(Modifier.height(26.dp))
            UsageGraphCard()

            Spacer(Modifier.height(24.dp))
            PredictionCard()

            Spacer(Modifier.height(24.dp))
            SectionLabel("TODAY'S SCREEN TIME")
            Spacer(Modifier.height(12.dp))
            
            AppUsageRow(name = "Instagram", time = "2h 12m", status = "+48% above baseline", statusColor = GWarning)
            AppUsageRow(name = "YouTube", time = "1h 31m", status = "Normal", statusColor = GTextSecondary)
            AppUsageRow(name = "WhatsApp", time = "51m", status = "Normal", statusColor = GTextSecondary)
            AppUsageRow(name = "Maps", time = "24m", status = "Normal", statusColor = GTextSecondary)
            
            Spacer(Modifier.height(16.dp))
            SectionLabel("AI ANOMALIES DETECTED")
            Spacer(Modifier.height(12.dp))
            
            GuardianCard(borderColor = GCritical.copy(alpha = 0.5f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(GBackground), contentAlignment = Alignment.Center) {
                        Text("F", color = GTextPrimary, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "FlashDeals", style = MaterialTheme.typography.titleMedium, color = GTextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text(text = "38m background activity", style = MaterialTheme.typography.bodySmall, color = GCritical)
                    }
                    androidx.compose.material3.Button(
                        onClick = { /* action */ },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GCritical.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("RESTRICT", color = GCritical)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun UsageGraphCard() {
    GuardianCard {
        SectionLabel("7-DAY USAGE TREND")
        Spacer(Modifier.height(24.dp))
        
        val transition = rememberInfiniteTransition(label = "usage_graph")
        val heights = listOf(0.4f, 0.6f, 0.5f, 0.8f, 0.7f, 0.9f, 0.6f)
        
        Row(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            heights.forEachIndexed { index, targetFraction ->
                val fraction by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = targetFraction,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, delayMillis = index * 100, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bar_$index"
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight(fraction)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(if (index == 5) GWarning else GAccent)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = listOf("M", "T", "W", "T", "F", "S", "S")[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = GTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun PredictionCard() {
    GuardianCard(contentPadding = 20.dp, borderColor = GWarning.copy(alpha = 0.3f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("FORECAST", color = GWarning)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Social usage projected to exceed baseline (+18%).",
                    style = MaterialTheme.typography.titleMedium,
                    color = GTextPrimary
                )
            }
            androidx.compose.material3.Button(
                onClick = { /* action */ },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GWarning.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("SET LIMIT", color = GWarning)
            }
        }
    }
}

@Composable
private fun AppUsageRow(name: String, time: String, status: String, statusColor: Color) {
    GuardianCard(modifier = Modifier.padding(bottom = 8.dp)) {
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
                Spacer(Modifier.height(4.dp))
                Text(text = status, style = MaterialTheme.typography.bodySmall, color = statusColor)
            }
            Text(text = time, style = MaterialTheme.typography.titleLarge, color = GTextPrimary)
        }
    }
}
