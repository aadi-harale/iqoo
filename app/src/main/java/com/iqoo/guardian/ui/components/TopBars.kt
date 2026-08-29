package com.iqoo.guardian.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCard
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary

/** Screen header: eyebrow label, title, optional subtitle, optional action. */
@Composable
fun GuardianTopBar(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        if (onBack != null) {
            IconTile(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onBack
            )
            Spacer(Modifier.width(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            if (eyebrow != null) {
                SectionLabel(eyebrow)
                Spacer(Modifier.height(9.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = GTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = GTextSecondary
                )
            }
        }
        if (action != null) {
            Spacer(Modifier.width(12.dp))
            action()
        }
    }
}

/** Square icon button used for back, Demo Lab and About. */
@Composable
fun IconTile(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = GTextSecondary
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(GCard)
            .border(1.dp, GBorder, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(19.dp)
        )
    }
}
