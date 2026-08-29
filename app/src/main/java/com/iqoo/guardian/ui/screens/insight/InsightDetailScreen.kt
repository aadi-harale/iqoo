package com.iqoo.guardian.ui.screens.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqoo.guardian.domain.model.GuardianEvent
import com.iqoo.guardian.ui.GuardianViewModel
import com.iqoo.guardian.ui.components.ContradictionCard
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.ReasonChip
import com.iqoo.guardian.ui.components.RecommendationCard
import com.iqoo.guardian.ui.components.RiskBadge
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.components.SimulatedNotificationCard
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCardElevated
import com.iqoo.guardian.ui.theme.GSafe
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.badgeColor
import com.iqoo.guardian.ui.theme.badgeLabel

/**
 * Every Guardian event is explainable. This screen is the contract: what
 * happened, why it was flagged, what was looked at, what to do, and what left
 * the device (nothing).
 */
@Composable
fun InsightDetailScreen(
    viewModel: GuardianViewModel,
    eventId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val event = events.firstOrNull { it.id == eventId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        if (event == null) {
            GuardianTopBar(title = "Not available", onBack = onBack)
            Spacer(Modifier.height(20.dp))
            GuardianCard {
                Text(
                    text = "This entry is no longer in the timeline.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GTextSecondary
                )
            }
        } else {
            InsightContent(event = event, onBack = onBack)
        }
        Spacer(Modifier.height(28.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InsightContent(event: GuardianEvent, onBack: () -> Unit) {
    val assessment = event.assessment
    val accent = badgeColor(event.severity, event.category)

    GuardianTopBar(
        eyebrow = "GUARDIAN INSIGHT",
        title = event.title,
        onBack = onBack
    )

    Spacer(Modifier.height(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        RiskBadge(badgeLabel(event.severity, event.category), accent)
        Spacer(Modifier.width(10.dp))
        Text(
            text = event.timestampLabel,
            style = MaterialTheme.typography.bodySmall,
            color = GTextMuted
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "RISK ${assessment.score}",
            style = MaterialTheme.typography.labelMedium,
            color = accent
        )
    }

    // ---- What happened ----
    Spacer(Modifier.height(24.dp))
    SectionLabel("WHAT HAPPENED")
    Spacer(Modifier.height(12.dp))
    event.signal?.let { signal ->
        SimulatedNotificationCard(signal = signal, visible = true)
        Spacer(Modifier.height(12.dp))
    }
    Text(
        text = event.subtitle,
        style = MaterialTheme.typography.bodyLarge,
        color = GTextSecondary
    )

    // ---- The contradiction, when there was one ----
    assessment.contradictions.firstOrNull()?.let { contradiction ->
        Spacer(Modifier.height(26.dp))
        SectionLabel("CROSS-SIGNAL CONTRADICTION")
        Spacer(Modifier.height(14.dp))
        ContradictionCard(contradiction = contradiction, animate = false)
        Spacer(Modifier.height(14.dp))
        GuardianCard(background = GCardElevated) {
            Text(
                text = contradiction.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = GTextSecondary
            )
        }
    }

    // ---- Why ----
    Spacer(Modifier.height(26.dp))
    SectionLabel("WHY GUARDIAN FLAGGED IT")
    Spacer(Modifier.height(12.dp))
    if (assessment.reasons.isEmpty()) {
        GuardianCard(borderColor = GSafe.copy(alpha = 0.24f)) {
            Text("Looks safe.", style = MaterialTheme.typography.titleLarge, color = GSafe)
            Spacer(Modifier.height(6.dp))
            Text(
                text = assessment.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = GTextSecondary
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            assessment.reasons.forEach { reason ->
                ReasonChip(
                    label = reason.label,
                    evidence = reason.evidence,
                    accent = accent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = assessment.explanation,
            style = MaterialTheme.typography.bodyLarge,
            color = GTextSecondary
        )
    }

    // ---- Signals used ----
    Spacer(Modifier.height(26.dp))
    SectionLabel("SIGNALS USED")
    Spacer(Modifier.height(12.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        assessment.signalsUsed.forEach { signal ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(GCardElevated)
                    .border(1.dp, GBorder, RoundedCornerShape(9.dp))
                    .padding(horizontal = 11.dp, vertical = 8.dp)
            ) {
                Text(
                    text = signal,
                    style = MaterialTheme.typography.bodySmall,
                    color = GTextSecondary
                )
            }
        }
    }

    // ---- Action ----
    Spacer(Modifier.height(26.dp))
    RecommendationCard(
        text = assessment.recommendedAction,
        accent = if (assessment.isThreat) accent else GSafe
    )

    // ---- Privacy ----
    Spacer(Modifier.height(20.dp))
    SectionLabel("PRIVACY")
    Spacer(Modifier.height(12.dp))
    GuardianCard {
        Text(
            text = "This analysis ran on the device. No part of the signal, the device state or " +
                "the result was uploaded anywhere.",
            style = MaterialTheme.typography.bodyMedium,
            color = GTextSecondary
        )
        if (event.signal?.simulated == true) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Signal source: simulated (Demo Lab). The analysis itself is the real " +
                    "engine.",
                style = MaterialTheme.typography.bodySmall,
                color = GTextMuted
            )
        }
    }

    if (event.handled) {
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RiskBadge("HANDLED", GSafe)
            Spacer(Modifier.width(10.dp))
            Text(
                text = "No further action is required.",
                style = MaterialTheme.typography.bodyMedium,
                color = GTextSecondary
            )
        }
    }
}
