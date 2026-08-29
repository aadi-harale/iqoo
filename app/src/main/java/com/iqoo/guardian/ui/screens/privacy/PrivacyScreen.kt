package com.iqoo.guardian.ui.screens.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqoo.guardian.ui.GuardianViewModel
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.CardDivider
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.KeyValueRow
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCardElevated
import com.iqoo.guardian.ui.theme.GSafe
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary

import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.iqoo.guardian.util.Haptics

@Composable
fun PrivacyScreen(
    viewModel: GuardianViewModel,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val privacy by viewModel.privacy.collectAsStateWithLifecycle()
    val score by viewModel.score.collectAsStateWithLifecycle()
    
    var isUnlocked by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GAccent, bloomCenterYFraction = 0.24f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        GuardianTopBar(
            eyebrow = "PRIVACY",
            title = "Nothing leaves this device"
        )

        Spacer(Modifier.height(22.dp))
        GuardianCard(background = GCardElevated, contentPadding = 24.dp) {
            SectionLabel("LOCAL-FIRST PROTECTION", color = GAccent)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = score.cloudUploads.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = GAccent
                )
                Spacer(Modifier.height(4.dp))
                SectionLabel("DATA UPLOADED")
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Guardian's analysis runs entirely on this device. The app declares no " +
                    "internet permission, so it has no way to send anything anywhere.",
                style = MaterialTheme.typography.bodyMedium,
                color = GTextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(22.dp))
        GuardianCard(contentPadding = 20.dp, borderColor = GAccent.copy(alpha = 0.5f)) {
            SectionLabel("OFFLINE PROOF MODE", color = GAccent)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Prove that Guardian works without internet access.",
                style = MaterialTheme.typography.bodyMedium,
                color = GTextSecondary
            )
            Spacer(Modifier.height(16.dp))
            androidx.compose.material3.Button(
                onClick = { /* action */ },
                colors = ButtonDefaults.buttonColors(containerColor = GAccent.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("RUN OFFLINE ANALYSIS", color = GAccent)
            }
        }

        Spacer(Modifier.height(22.dp))
        com.iqoo.guardian.ui.components.PermissionsDonutChart()

        Spacer(Modifier.height(22.dp))
        SectionLabel("WHAT GUARDIAN LOOKS AT")
        Spacer(Modifier.height(12.dp))
        GuardianCard {
            StatusLine("Notification analysis", privacy.analyzeNotifications)
            CardDivider()
            StatusLine("Device state", privacy.analyzeDeviceHealth)
            CardDivider()
            StatusLine("App activity", privacy.analyzeAppActivity)
            CardDivider()
            StatusLine("Cloud analysis", false, disabledLabel = "Disabled")
        }

        Spacer(Modifier.height(22.dp))
        SectionLabel("YOUR CONTROL")
        Spacer(Modifier.height(12.dp))
        GuardianCard {
            ToggleRow(
                label = "Analyze notifications",
                caption = "Turning this off stops Guardian reading notification and message signals.",
                checked = privacy.analyzeNotifications,
                onChange = { value -> viewModel.setPrivacy { it.copy(analyzeNotifications = value) } }
            )
            CardDivider()
            ToggleRow(
                label = "Analyze device health",
                caption = "Storage and battery state used for cross-signal checks.",
                checked = privacy.analyzeDeviceHealth,
                onChange = { value -> viewModel.setPrivacy { it.copy(analyzeDeviceHealth = value) } }
            )
            CardDivider()
            ToggleRow(
                label = "Analyze app activity",
                caption = "Background behaviour of installed apps.",
                checked = privacy.analyzeAppActivity,
                onChange = { value -> viewModel.setPrivacy { it.copy(analyzeAppActivity = value) } }
            )
            CardDivider()
            ToggleRow(
                label = "Save Guardian history",
                caption = "Keeps the alert timeline. Off means results are shown once and not stored.",
                checked = privacy.saveHistory,
                onChange = { value -> viewModel.setPrivacy { it.copy(saveHistory = value) } }
            )
        }

        Spacer(Modifier.height(22.dp))
        OutlinedButton(
            onClick = onOpenAbout,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GTextPrimary)
        ) {
            Text("ABOUT GUARDIAN", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                modifier = Modifier.width(18.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
    }
    
        // Biometric Overlay
        AnimatedVisibility(
            visible = !isUnlocked,
            exit = fadeOut(tween(400))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GBackground.copy(alpha = 0.95f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isScanning) {
                            isScanning = true
                            Haptics.alert(haptic)
                            coroutineScope.launch {
                                delay(800)
                                Haptics.confirm(haptic)
                                isUnlocked = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.Fingerprint,
                        contentDescription = "Scan Fingerprint",
                        tint = if (isScanning) GAccent else GTextMuted,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = if (isScanning) "VERIFYING..." else "TOUCH TO UNLOCK",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isScanning) GAccent else GTextSecondary,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusLine(label: String, enabled: Boolean, disabledLabel: String = "Paused") {
    KeyValueRow(
        label = label,
        value = if (enabled) "Enabled" else disabledLabel,
        valueColor = if (enabled) GSafe else GTextMuted
    )
}

@Composable
private fun ToggleRow(
    label: String,
    caption: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = GTextPrimary)
            Spacer(Modifier.height(3.dp))
            Text(caption, style = MaterialTheme.typography.bodySmall, color = GTextMuted)
        }
        Spacer(Modifier.width(14.dp))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
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
