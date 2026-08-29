package com.iqoo.guardian.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GBackgroundElevated
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.util.Haptics

/**
 * Four tabs, no labels shouting for attention. The active tab is marked by a lime
 * pip above the icon rather than a filled pill, which keeps the bar quiet.
 */
@Composable
fun GuardianBottomBar(
    currentRoute: String?,
    onSelect: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GBackgroundElevated, GBackground)
                )
            )
    ) {
        // Hairline that brightens under the active tab, so the bar reads as lit
        // from the selection rather than uniformly outlined.
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(GBorder, GAccent.copy(alpha = 0.22f), GBorder)
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab.entries.forEach { tab ->
                TabItem(
                    tab = tab,
                    selected = currentRoute == tab.route,
                    onClick = {
                        Haptics.tap(haptic)
                        onSelect(tab)
                    }
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        targetValue = if (selected) GAccent else GTextMuted,
        animationSpec = tween(220),
        label = "tabTint"
    )
    val pipAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(220),
        label = "tabPip"
    )
    val interaction = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 16.dp, height = 2.5.dp)
                .clip(CircleShape)
                .background(GAccent.copy(alpha = pipAlpha))
        )
        Spacer(Modifier.height(9.dp))
        Box(contentAlignment = Alignment.Center) {
            if (pipAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .blur(11.dp)
                        .clip(CircleShape)
                        .background(GAccent.copy(alpha = 0.20f * pipAlpha))
                )
            }
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = tint,
                modifier = Modifier.size(21.dp)
            )
        }
        Spacer(Modifier.height(5.dp))
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}
