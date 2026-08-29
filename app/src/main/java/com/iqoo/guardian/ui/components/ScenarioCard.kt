package com.iqoo.guardian.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.domain.model.DemoScenario
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCard
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.color

/**
 * A Demo Lab entry.
 *
 * The accent tint hints at the kind of signal, but carries no verdict - the score
 * shown after tapping is whatever the engine returns for the injected text.
 */
@Composable
fun ScenarioCard(
    scenario: DemoScenario,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val accent = scenario.accentSeverity.color()
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(accent.copy(alpha = 0.12f), GCard.lift(0.03f), GCard)
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(accent.copy(alpha = 0.30f), GBorder)),
                shape
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.14f))
                .border(1.dp, accent.copy(alpha = 0.26f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = scenario.title,
                style = MaterialTheme.typography.labelLarge,
                color = GTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = scenario.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = GTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = GTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
