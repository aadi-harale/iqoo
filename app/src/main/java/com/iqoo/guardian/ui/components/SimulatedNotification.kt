package com.iqoo.guardian.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.ui.theme.GBorderStrong
import com.iqoo.guardian.ui.theme.GCardElevated
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.GWarning

/**
 * An in-app rendering of the signal being analysed.
 *
 * Styled like a notification so the demo reads instantly, but explicitly marked
 * as a simulated signal: this is not, and does not claim to be, a real
 * notification posted by an installed app.
 */
@Composable
fun SimulatedNotificationCard(
    signal: DeviceSignal,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(tween(420)) { -it } + fadeIn(tween(300)),
        exit = slideOutVertically(tween(260)) { -it / 2 } + fadeOut(tween(200)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(GCardElevated)
                .border(1.dp, GBorderStrong, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(GWarning.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = GWarning,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(Modifier.width(9.dp))
                Text(
                    text = signal.sourceLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = GTextSecondary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (signal.simulated) {
                    Text(
                        text = "SIMULATED SIGNAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = GTextMuted
                    )
                }
            }
            Spacer(Modifier.height(11.dp))
            Text(
                text = signal.title,
                style = MaterialTheme.typography.titleLarge,
                color = GTextPrimary
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = signal.body,
                style = MaterialTheme.typography.bodyMedium,
                color = GTextSecondary
            )
            if (signal.url != null) {
                Spacer(Modifier.height(11.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Link,
                        contentDescription = null,
                        tint = GTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = signal.url,
                        style = MaterialTheme.typography.bodySmall,
                        color = GTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
