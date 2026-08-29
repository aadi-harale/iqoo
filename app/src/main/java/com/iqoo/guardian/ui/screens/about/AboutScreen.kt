package com.iqoo.guardian.ui.screens.about

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.CardDivider
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianMark
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary

private val PIPELINE = listOf(
    "Signal" to "A notification, message, app behaviour record or device reading.",
    "Risk classifier" to "Deterministic local rules match urgency, credential, payment, " +
        "impersonation and link patterns, each with a weight.",
    "Context correlation" to "Factual claims in the signal are checked against the device's " +
        "own state. Disagreement becomes evidence.",
    "Risk assessment" to "Weights combine through a saturating curve into a 0-100 score with " +
        "a severity band.",
    "Guardian event" to "The result, its reasons and the inputs it used, kept explainable end " +
        "to end."
)

private val OEM_SURFACES = listOf(
    "Notification pipeline",
    "Security Center",
    "Storage Manager",
    "Permission Manager",
    "Battery Manager",
    "App installation protection",
    "System Settings",
    "SMS and Dialer protection"
)

@Composable
fun AboutScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GAccent, bloomCenterYFraction = 0.20f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        GuardianTopBar(
            eyebrow = "ABOUT",
            title = "iQOO Guardian",
            onBack = onBack
        )

        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GuardianMark(size = 76.dp)
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Your phone understands threats before you do.",
                style = MaterialTheme.typography.headlineSmall,
                color = GTextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(Modifier.height(26.dp))
        SectionLabel("HOW IT WORKS")
        Spacer(Modifier.height(12.dp))
        GuardianCard {
            PIPELINE.forEachIndexed { index, (title, body) ->
                if (index > 0) CardDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(GAccent)
                    )
                    Spacer(Modifier.width(13.dp))
                    Column {
                        Text(title, style = MaterialTheme.typography.titleLarge, color = GTextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(26.dp))
        SectionLabel("WHAT IS REAL IN THIS BUILD")
        Spacer(Modifier.height(12.dp))
        GuardianCard {
            HonestyLine(
                "Real",
                "The risk classifier, the context correlation engine, the scoring model and " +
                    "every explanation you see. Change the injected text and the verdict changes."
            )
            CardDivider()
            HonestyLine(
                "Simulated",
                "The signals themselves and the device state they are checked against. Guardian " +
                    "holds no notification, SMS or usage-stats permission in this build."
            )
            CardDivider()
            HonestyLine(
                "Not claimed",
                "There is no machine-learning model running, no cloud service and no iQOO " +
                    "system integration. The classifier is an interface so an on-device model " +
                    "can replace the rules later."
            )
        }

        Spacer(Modifier.height(26.dp))
        SectionLabel("OEM INTEGRATION VISION", color = GAccent)
        Spacer(Modifier.height(12.dp))
        GuardianCard {
            Text(
                text = "With privileged OEM integration, Guardian could read signals from:",
                style = MaterialTheme.typography.bodyMedium,
                color = GTextSecondary
            )
            Spacer(Modifier.height(14.dp))
            OEM_SURFACES.forEach { surface ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(GTextMuted)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = surface,
                        style = MaterialTheme.typography.bodyLarge,
                        color = GTextPrimary
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "None of these integrations exist today. Built as a prototype of how " +
                    "cross-signal intelligence could become a native device capability.",
                style = MaterialTheme.typography.bodySmall,
                color = GTextMuted
            )
        }

        Spacer(Modifier.height(28.dp))
        Text(
            text = "Prototype build - not a shipping iQOO feature.",
            style = MaterialTheme.typography.bodySmall,
            color = GTextMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
    }
    }
}

@Composable
private fun HonestyLine(label: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp)) {
        SectionLabel(label.uppercase())
        Spacer(Modifier.height(7.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
    }
}
