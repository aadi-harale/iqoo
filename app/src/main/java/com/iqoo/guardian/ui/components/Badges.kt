package com.iqoo.guardian.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.domain.model.Severity
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.color

/** Severity pill. Tinted fill at low opacity so it reads without shouting. */
@Composable
fun RiskBadge(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.13f))
            .border(1.dp, color.copy(alpha = 0.32f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
fun RiskBadge(severity: Severity, modifier: Modifier = Modifier) =
    RiskBadge(severity.label, severity.color(), modifier)

/**
 * One matched pattern, with the evidence that triggered it. Chips carry the
 * engine's own labels - the UI never renames a finding.
 */
@Composable
fun ReasonChip(
    label: String,
    evidence: String?,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.07f))
            .border(1.dp, accent.copy(alpha = 0.24f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = accent
            )
            if (!evidence.isNullOrBlank()) {
                Spacer(Modifier.height(3.dp))
                Text(
                    text = evidence,
                    style = MaterialTheme.typography.bodySmall,
                    color = GTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Counts up to [value]. Used for the risk score and the dashboard metrics so a
 * number arriving on screen always feels measured rather than pasted in.
 */
@Composable
fun CountUpNumber(
    value: Int,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.displayMedium,
    color: Color = GTextPrimary,
    durationMillis: Int = 900,
    startDelayMillis: Long = 0L
) {
    var target by remember { mutableIntStateOf(0) }
    LaunchedEffect(value) {
        if (startDelayMillis > 0) kotlinx.coroutines.delay(startDelayMillis)
        target = value
    }
    val animated by animateIntAsState(
        targetValue = target,
        animationSpec = tween(durationMillis, easing = EaseOutCubic),
        label = "countUp"
    )
    Text(text = animated.toString(), style = style, color = color, modifier = modifier)
}

/** Compact dashboard metric: value on top, uppercase caption below. */
@Composable
fun MetricCard(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = GTextPrimary,
    startDelayMillis: Long = 0L
) {
    GuardianCard(modifier = modifier, contentPadding = 14.dp, cornerRadius = 16.dp) {
        CountUpNumber(
            value = value,
            style = MaterialTheme.typography.headlineMedium,
            color = valueColor,
            startDelayMillis = startDelayMillis
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = GTextMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
