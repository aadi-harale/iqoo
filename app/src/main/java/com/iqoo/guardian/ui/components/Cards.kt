package com.iqoo.guardian.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.domain.model.EventCategory
import com.iqoo.guardian.domain.model.GuardianEvent
import com.iqoo.guardian.domain.model.Severity
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GCardElevated
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.badgeColor
import com.iqoo.guardian.ui.theme.badgeLabel

/**
 * A Guardian finding in list form. Used by "Needs attention" on Home and by the
 * Alerts timeline, so both stay identical.
 */
@Composable
fun InsightCard(
    event: GuardianEvent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accent = badgeColor(event.severity, event.category)
    GuardianCard(modifier = modifier, onClick = onClick, contentPadding = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = GTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = event.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = GTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RiskBadge(badgeLabel(event.severity, event.category), accent)
            Spacer(Modifier.width(10.dp))
            Text(
                text = event.timestampLabel,
                style = MaterialTheme.typography.bodySmall,
                color = GTextMuted
            )
            if (event.handled) {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Handled",
                    style = MaterialTheme.typography.bodySmall,
                    color = GTextMuted
                )
            }
        }
    }
}

/**
 * A row in the Alerts timeline: time on the left against a spine, content right.
 */
@Composable
fun TimelineItem(
    event: GuardianEvent,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accent = badgeColor(event.severity, event.category)
    Row(modifier = modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp).padding(top = 22.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(accent)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(96.dp)
                        .background(com.iqoo.guardian.ui.theme.GBorder)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.timestampLabel,
                style = MaterialTheme.typography.bodySmall,
                color = GTextMuted
            )
            Spacer(Modifier.height(7.dp))
            InsightCard(event = event, onClick = onClick)
            Spacer(Modifier.height(14.dp))
        }
    }
}

/**
 * The recommendation block. Primary action reads as the safe thing to do; the
 * card never offers a "proceed anyway" path.
 */
@Composable
fun RecommendationCard(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = GAccent
) {
    GuardianCard(
        modifier = modifier,
        background = GCardElevated,
        borderColor = accent.copy(alpha = 0.22f)
    ) {
        SectionLabel("RECOMMENDED ACTION", color = accent)
        Spacer(Modifier.height(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = GTextPrimary
        )
    }
}

/** Device Intelligence tile: caption, big value, supporting line. */
@Composable
fun DeviceMetricCard(
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
    valueColor: Color = GTextPrimary,
    icon: ImageVector? = null
) {
    GuardianCard(modifier = modifier, contentPadding = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionLabel(label, modifier = Modifier.weight(1f))
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = valueColor)
        Spacer(Modifier.height(5.dp))
        Text(
            text = caption,
            style = MaterialTheme.typography.bodySmall,
            color = GTextSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
