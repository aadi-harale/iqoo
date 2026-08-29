package com.iqoo.guardian.ui.screens.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqoo.guardian.domain.model.EventCategory
import com.iqoo.guardian.ui.GuardianViewModel
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.TimelineItem
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCard
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary

private enum class AlertFilter(val label: String, val category: EventCategory?) {
    ALL("ALL", null),
    SCAMS("SCAMS", EventCategory.SCAM),
    DEVICE("DEVICE", EventCategory.DEVICE),
    PRIVACY("PRIVACY", EventCategory.PRIVACY)
}

@Composable
fun AlertsScreen(
    viewModel: GuardianViewModel,
    onOpenInsight: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    var filter by remember { mutableStateOf(AlertFilter.ALL) }

    val visible = remember(events, filter) {
        filter.category?.let { category -> events.filter { it.category == category } } ?: events
    }

    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GAccent, bloomCenterYFraction = 0.18f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        GuardianTopBar(
            eyebrow = "GUARDIAN TIMELINE",
            title = "Alerts",
            subtitle = "Everything Guardian has looked at, newest first."
        )

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AlertFilter.entries.forEach { option ->
                FilterChip(
                    label = option.label,
                    selected = filter == option,
                    onClick = { filter = option }
                )
            }
        }

        Spacer(Modifier.height(22.dp))
        if (visible.isEmpty()) {
            EmptyAlerts()
        } else {
            visible.forEachIndexed { index, event ->
                TimelineItem(
                    event = event,
                    isLast = index == visible.lastIndex,
                    onClick = { onOpenInsight(event.id) }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) GAccent.copy(alpha = 0.14f) else GCard)
            .border(1.dp, if (selected) GAccent.copy(alpha = 0.4f) else GBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) GAccent else GTextSecondary
        )
    }
}

@Composable
private fun EmptyAlerts() {
    GuardianCard(modifier = Modifier.fillMaxWidth()) {
        Text("All quiet.", style = MaterialTheme.typography.headlineSmall, color = GTextPrimary)
        Spacer(Modifier.height(7.dp))
        Text(
            text = "Guardian hasn't detected anything unusual.",
            style = MaterialTheme.typography.bodyLarge,
            color = GTextSecondary
        )
    }
}
