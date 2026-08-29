package com.iqoo.guardian.ui.screens.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqoo.guardian.data.demo.DemoSignalProvider
import com.iqoo.guardian.ui.GuardianViewModel
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.ScenarioCard
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.components.StatusDotLabel
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCardElevated
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.GWarning
import com.iqoo.guardian.util.Haptics

/**
 * The presenter's control surface.
 *
 * Every card here injects a structured signal into the same pipeline that would
 * process a real one. The lab supplies inputs only - it never supplies verdicts.
 */
@Composable
fun DemoLabScreen(
    viewModel: GuardianViewModel,
    onBack: () -> Unit,
    onRunScenario: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val presentationMode by viewModel.presentationMode.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GAccent, bloomCenterYFraction = 0.16f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        GuardianTopBar(
            eyebrow = "GUARDIAN DEMO LAB",
            title = "Inject a signal",
            subtitle = "Simulated device signals, analysed by the Guardian engine.",
            onBack = onBack
        )

        Spacer(Modifier.height(22.dp))
        DemoModeBanner()

        Spacer(Modifier.height(20.dp))
        SectionLabel("SCENARIOS")
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoSignalProvider.scenarios.forEach { scenario ->
                ScenarioCard(
                    scenario = scenario,
                    icon = scenarioIcon(scenario.id)
                ) {
                    Haptics.tap(haptic)
                    onRunScenario(scenario.id)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        PresentationModeCard(
            enabled = presentationMode,
            onToggle = { viewModel.setPresentationMode(it) }
        )

        Spacer(Modifier.height(14.dp))
        TriggerNotificationCard()

        Spacer(Modifier.height(14.dp))
        ResetCard(
            onReset = {
                Haptics.confirm(haptic)
                viewModel.resetDemo()
            }
        )

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Guardian has no access to real notifications, SMS or restricted device " +
                "statistics in this build. Everything analysed here is a signal this screen " +
                "constructed.",
            style = MaterialTheme.typography.bodySmall,
            color = GTextMuted
        )
        Spacer(Modifier.height(20.dp))
        Spacer(Modifier.navigationBarsPadding())
    }
    }
}

@Composable
private fun DemoModeBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GWarning.copy(alpha = 0.07f))
            .border(1.dp, GWarning.copy(alpha = 0.24f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            tint = GWarning,
            modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("DEMO MODE", color = GWarning)
                Spacer(Modifier.width(10.dp))
                StatusDotLabel(label = "ACTIVE", color = GWarning)
            }
            Spacer(Modifier.height(7.dp))
            Text(
                text = "Simulated signals - analysed by the Guardian engine.",
                style = MaterialTheme.typography.bodyMedium,
                color = GTextSecondary
            )
        }
    }
}

@Composable
private fun PresentationModeCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    GuardianCard(background = GCardElevated) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("PRESENTATION MODE")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Paced analysis timings",
                    style = MaterialTheme.typography.titleLarge,
                    color = GTextPrimary
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Slows the analysis trace so each step is readable on a projector. " +
                        "Scores are unaffected - the engine is deterministic either way.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GTextSecondary
                )
            }
            Spacer(Modifier.width(14.dp))
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GBackground,
                    checkedTrackColor = GAccent,
                    uncheckedThumbColor = GTextMuted,
                    uncheckedTrackColor = GCardElevated,
                    uncheckedBorderColor = GBorder
                )
            )
        }
    }
}

@Composable
private fun ResetCard(onReset: () -> Unit) {
    GuardianCard {
        SectionLabel("DEMO STATE")
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Restores the starting dashboard, timeline and device state so the demo can be " +
                "run again immediately.",
            style = MaterialTheme.typography.bodyMedium,
            color = GTextSecondary
        )
        Spacer(Modifier.height(14.dp))
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GTextPrimary)
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )
            Spacer(Modifier.width(9.dp))
            Text("RESET DEMO", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun TriggerNotificationCard() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    GuardianCard {
        SectionLabel("SYSTEM NOTIFICATION")
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Fires a real Android system notification to simulate Guardian catching a threat in the background.",
            style = MaterialTheme.typography.bodyMedium,
            color = GTextSecondary
        )
        Spacer(Modifier.height(14.dp))
        OutlinedButton(
            onClick = {
                Haptics.confirm(haptic)
                val nm = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                val channelId = "guardian_demo"
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val channel = android.app.NotificationChannel(channelId, "Guardian Demo", android.app.NotificationManager.IMPORTANCE_HIGH)
                    nm.createNotificationChannel(channel)
                }
                
                // Create an intent to launch the app when the notification is tapped
                val intent = android.content.Intent(context, com.iqoo.guardian.MainActivity::class.java).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: android.app.PendingIntent = android.app.PendingIntent.getActivity(context, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE)

                val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("Guardian Alert: Threat Blocked")
                    .setContentText("A deceptive storage warning was intercepted.")
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                nm.notify(1001, builder.build())
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GTextPrimary)
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )
            Spacer(Modifier.width(9.dp))
            Text("TRIGGER NOTIFICATION", style = MaterialTheme.typography.labelMedium)
        }
    }
}
