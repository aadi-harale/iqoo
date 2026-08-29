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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCardElevated
import com.iqoo.guardian.ui.theme.GSafe
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary

/** A row inside the Live Protection card: icon, subject, current status. */
@Composable
fun ProtectionStatusRow(
    icon: ImageVector,
    label: String,
    status: String,
    modifier: Modifier = Modifier,
    statusColor: Color = GSafe
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(GCardElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GTextSecondary,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = GTextPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            color = statusColor,
            maxLines = 1
        )
    }
}

/** Thin divider used inside cards. */
@Composable
fun CardDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(GBorder)
    )
}

/** Label / value pair used on the Device and Insight screens. */
@Composable
fun KeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = GTextPrimary
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = GTextSecondary)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = valueColor)
    }
}

/** Section heading with an optional trailing element. */
@Composable
fun SectionHeader(
    label: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionLabel(label, color = GTextMuted)
        trailing?.invoke()
    }
}
