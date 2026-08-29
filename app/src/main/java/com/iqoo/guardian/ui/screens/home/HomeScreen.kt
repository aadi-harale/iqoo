package com.iqoo.guardian.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqoo.guardian.domain.model.GuardianEvent
import com.iqoo.guardian.ui.GuardianViewModel
import com.iqoo.guardian.ui.components.*
import com.iqoo.guardian.ui.theme.*
import java.time.LocalTime

@Composable
fun HomeScreen(
    viewModel: GuardianViewModel,
    onOpenHub: () -> Unit,
    onOpenAlerts: () -> Unit,
    onOpenDemoLab: () -> Unit,
    onOpenInsight: (String) -> Unit,
    onRunAutopilot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val score by viewModel.score.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val protection by viewModel.protection.collectAsStateWithLifecycle()

    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }

    val attention = events.filter { !it.handled }

    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GAccent, bloomCenterYFraction = 0.30f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            HomeHeader(onOpenDemoLab = onOpenDemoLab)

            Spacer(Modifier.height(28.dp))
            ScoreBlock(
                score = score.value,
                statusLabel = score.statusLabel,
                lastCheckLabel = score.lastCheckLabel
            )

            Spacer(Modifier.height(26.dp))
            EntranceBlock(entered, delayMillis = 120) {
                LiveIntelligenceRow()
            }
            
            Spacer(Modifier.height(20.dp))
            EntranceBlock(entered, delayMillis = 140) {
                androidx.compose.material3.Button(
                    onClick = onRunAutopilot,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GAccent.copy(alpha = 0.15f)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = GAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("START AUTOPILOT PRESENTATION", color = GAccent, style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(Modifier.height(20.dp))
            EntranceBlock(entered, delayMillis = 160) {
                com.iqoo.guardian.ui.components.WeeklyThreatChart()
            }

            Spacer(Modifier.height(14.dp))
            EntranceBlock(entered, delayMillis = 200) {
                LiveProtectionCard(
                    allNormal = protection.allNormal,
                    notifications = protection.notifications,
                    deviceState = protection.deviceState,
                    appActivity = protection.appActivity,
                    storage = protection.storage,
                    privacy = protection.privacy
                )
            }

            Spacer(Modifier.height(24.dp))
            EntranceBlock(entered, delayMillis = 280) {
                NeedsAttentionSection(events = attention, onOpenInsight = onOpenInsight)
            }

            Spacer(Modifier.height(26.dp))
            Text(
                text = "Protection runs locally",
                style = MaterialTheme.typography.bodySmall,
                color = GTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EntranceBlock(visible: Boolean, delayMillis: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(420, delayMillis)) + slideInVertically(tween(460, delayMillis)) { it / 8 }
    ) { content() }
}

@Composable
private fun HomeHeader(onOpenDemoLab: () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            SectionLabel("GUARDIAN INTELLIGENCE")
            Spacer(Modifier.height(10.dp))
            Text(text = "Good evening.", style = MaterialTheme.typography.headlineLarge, color = GTextPrimary)
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDotLabel(label = "AI ACTIVE", color = GSafe)
                Spacer(Modifier.width(8.dp))
                Text("•", color = GTextMuted)
                Spacer(Modifier.width(8.dp))
                Text("ON DEVICE", style = MaterialTheme.typography.labelMedium, color = GTextMuted)
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(horizontalAlignment = Alignment.End) {
            Spacer(Modifier.height(12.dp))
            IconTile(icon = Icons.Rounded.Science, contentDescription = "Demo Lab", onClick = onOpenDemoLab, tint = GAccent)
        }
    }
}

@Composable
private fun ScoreBlock(score: Int, statusLabel: String, lastCheckLabel: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        GuardianScoreRing(score = score, statusLabel = "GUARDIAN SCORE")
        Spacer(Modifier.height(20.dp))
        Text(text = statusLabel, style = MaterialTheme.typography.titleLarge, color = GSafe)
        Spacer(Modifier.height(6.dp))
        Text(text = "14,028 signals analyzed today", style = MaterialTheme.typography.bodySmall, color = GTextSecondary)
    }
}

@Composable
private fun LiveIntelligenceRow() {
    Column {
        SectionLabel("LIVE INTELLIGENCE")
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PulseIndicator("Notifications")
            PulseIndicator("Storage")
            PulseIndicator("Battery")
            PulseIndicator("Apps")
        }
    }
}

@Composable
private fun PulseIndicator(label: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_$label")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GAccent.copy(alpha = alpha)))
        Spacer(Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = GTextSecondary)
    }
}

@Composable
private fun LiveProtectionCard(
    allNormal: Boolean, notifications: String, deviceState: String, appActivity: String, storage: String, privacy: String
) {
    GuardianCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionLabel("LIVE PROTECTION", modifier = Modifier.weight(1f))
            StatusDotLabel(label = if (allNormal) "ALL SYSTEMS NORMAL" else "NEEDS REVIEW", color = if (allNormal) GSafe else GWarning)
        }
        Spacer(Modifier.height(14.dp))
        Text(text = "Guardian is monitoring device signals locally.", style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
        Spacer(Modifier.height(14.dp))
        CardDivider()
        Spacer(Modifier.height(4.dp))
        ProtectionStatusRow(icon = Icons.Rounded.Notifications, label = "Notifications", status = notifications, statusColor = statusColor(notifications))
        ProtectionStatusRow(icon = Icons.Rounded.PhoneAndroid, label = "Device state", status = deviceState, statusColor = statusColor(deviceState))
        ProtectionStatusRow(icon = Icons.Rounded.Bolt, label = "App activity", status = appActivity, statusColor = statusColor(appActivity))
        ProtectionStatusRow(icon = Icons.Rounded.Storage, label = "Storage", status = storage, statusColor = statusColor(storage))
        ProtectionStatusRow(icon = Icons.Rounded.VisibilityOff, label = "Privacy", status = privacy, statusColor = statusColor(privacy))
    }
}

private fun statusColor(status: String) = when (status) {
    "Protected", "Normal", "Healthy" -> GSafe
    "Paused" -> GTextMuted
    else -> GWarning
}

@Composable
private fun NeedsAttentionSection(events: List<GuardianEvent>, onOpenInsight: (String) -> Unit) {
    Column {
        SectionHeader("NEEDS ATTENTION")
        Spacer(Modifier.height(12.dp))
        if (events.isEmpty()) {
            GuardianCard {
                Text(text = "All quiet.", style = MaterialTheme.typography.titleLarge, color = GTextPrimary)
                Spacer(Modifier.height(5.dp))
                Text(text = "Guardian hasn't detected anything unusual.", style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                events.take(3).forEach { event ->
                    InsightCard(event = event, onClick = { onOpenInsight(event.id) })
                }
            }
        }
    }
}
