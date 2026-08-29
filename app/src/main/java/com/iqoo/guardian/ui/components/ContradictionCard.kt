package com.iqoo.guardian.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.domain.model.ContextContradiction
import com.iqoo.guardian.ui.theme.GBackgroundElevated
import com.iqoo.guardian.ui.theme.GCritical
import com.iqoo.guardian.ui.theme.GSafe
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary

/**
 * The moment the whole product exists for: what the message claimed, versus what
 * the device actually reports, with a hard inequality between them.
 *
 * Both sides and the explanation come from the [ContextContradiction] the
 * correlation engine produced. Nothing on this card is written into the UI.
 */
@Composable
fun ContradictionCard(
    contradiction: ContextContradiction,
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    var revealed by remember { mutableStateOf(!animate) }
    LaunchedEffect(Unit) { revealed = true }

    val symbolScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.3f,
        animationSpec = tween(520, delayMillis = 260, easing = EaseOutBack),
        label = "neqScale"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = revealed,
            enter = fadeIn(tween(360)) + slideInHorizontally(tween(420)) { -it / 2 },
            modifier = Modifier.weight(1f)
        ) {
            ClaimPanel(
                caption = "CLAIM",
                value = contradiction.claim,
                accentColor = GCritical,
                icon = Icons.Rounded.Campaign
            )
        }

        Box(
            modifier = Modifier
                .width(36.dp)
                .height(2.dp)
                .background(com.iqoo.guardian.ui.theme.GBorder),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .scale(symbolScale)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(GBackgroundElevated)
                    .border(1.dp, GTextMuted.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "≠",
                    style = MaterialTheme.typography.titleMedium,
                    color = GTextPrimary
                )
            }
        }

        AnimatedVisibility(
            visible = revealed,
            enter = fadeIn(tween(360, delayMillis = 420)) +
                slideInHorizontally(tween(420, delayMillis = 420)) { it / 2 },
            modifier = Modifier.weight(1f)
        ) {
            ClaimPanel(
                caption = "ACTUAL STATE",
                value = contradiction.actual,
                accentColor = GSafe,
                icon = Icons.Rounded.PhoneAndroid
            )
        }
    }
}

/**
 * One side of the comparison. A tinted gradient fill, a lit edge on the accent
 * side and a leading icon give each panel enough weight to be read across a room.
 */
@Composable
private fun ClaimPanel(
    caption: String,
    value: String,
    accentColor: androidx.compose.ui.graphics.Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(accentColor.copy(alpha = 0.12f), accentColor.copy(alpha = 0.04f))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(accentColor.copy(alpha = 0.45f), accentColor.copy(alpha = 0.10f))
                ),
                shape
            )
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(accentColor.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.height(10.dp))
        SectionLabel(caption, color = accentColor, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = GTextPrimary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * The explanation strip shown under the comparison, plus what correlation was
 * worth in points. Judges asked "how do you know it's lying" get the answer here.
 */
@Composable
fun CorrelationSummary(
    explanation: String,
    baselineScore: Int,
    finalScore: Int,
    modifier: Modifier = Modifier
) {
    GuardianCard(modifier = modifier, contentPadding = 18.dp) {
        SectionLabel("CROSS-SIGNAL CHECK")
        Spacer(Modifier.height(10.dp))
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = GTextSecondary
        )
        if (finalScore != baselineScore) {
            Spacer(Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ScorePip("TEXT ONLY", baselineScore, GTextMuted)
                Text("→", style = MaterialTheme.typography.bodyLarge, color = GTextMuted)
                ScorePip("WITH DEVICE STATE", finalScore, GCritical)
            }
        }
    }
}

@Composable
private fun ScorePip(
    caption: String,
    value: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = color
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = GTextMuted,
            textAlign = TextAlign.Center
        )
    }
}
