package com.iqoo.guardian.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GBorder
import com.iqoo.guardian.ui.theme.GCard
import com.iqoo.guardian.ui.theme.GCardElevated
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextSecondary

/**
 * The one card surface in the app.
 *
 * Two details do most of the work in making it read as expensive: a faint
 * top-to-bottom fill gradient, and a hairline that is brighter along the top edge
 * than the bottom. Together they suggest a light source above the screen, which
 * is what separates a panel from a coloured rectangle.
 */
@Composable
fun GuardianCard(
    modifier: Modifier = Modifier,
    background: Color = GCard,
    borderColor: Color = GBorder,
    cornerRadius: Dp = 20.dp,
    contentPadding: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(background.lift(0.045f), background, background.lift(-0.012f))
                )
            )
            .then(
                Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            borderColor.copy(alpha = 0.9f),
                            Color.Transparent
                        ),
                        endY = 2.2f
                    )
                )
            )
            .hairline(shape, borderColor)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

/** Border that fades from a lit top edge to a darker bottom one. */
private fun Modifier.hairline(shape: Shape, color: Color): Modifier =
    this.then(
        Modifier.border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.lift(0.30f),
                    color,
                    color.lift(-0.05f)
                )
            ),
            shape = shape
        )
    )

/** Nudges a colour toward white (positive) or black (negative). */
internal fun Color.lift(amount: Float): Color {
    val target = if (amount >= 0f) Color.White else Color.Black
    val t = kotlin.math.abs(amount).coerceIn(0f, 1f)
    return Color(
        red = red + (target.red - red) * t,
        green = green + (target.green - green) * t,
        blue = blue + (target.blue - blue) * t,
        alpha = alpha
    )
}

/**
 * Ambient depth behind a screen: a cool wash from the top and a soft accent bloom
 * anchored wherever the hero content sits. Deliberately low-contrast - it should
 * be felt rather than seen.
 */
@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    accent: Color,
    bloomCenterYFraction: Float = 0.26f,
    bloomStrength: Float = 0.085f
) {
    val transition = rememberInfiniteTransition(label = "grid")
    val gridOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "gridAnim"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GBackground)
            .drawBehind {
                val gridSize = 120f
                val yOffset = gridOffset * gridSize
                val gridColor = accent.copy(alpha = 0.04f) // Very subtle accent-tinted grid
                val strokeWidth = 3f

                // Draw horizontal lines moving downwards
                var y = yOffset - gridSize
                while (y < size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth)
                    y += gridSize
                }

                // Draw vertical static lines
                var x = 0f
                while (x < size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth)
                    x += gridSize
                }
            }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GCardElevated.copy(alpha = 0.85f),
                        Color.Transparent
                    ),
                    endY = 1200f
                )
            )
            .drawBloom(accent, bloomCenterYFraction, bloomStrength)
    )
}

private fun Modifier.drawBloom(accent: Color, centerY: Float, strength: Float): Modifier =
    this.drawBehind {
        val radius = size.width * 0.85f
        val origin = Offset(size.width / 2f, size.height * centerY)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = strength), Color.Transparent),
                center = origin,
                radius = radius
            ),
            radius = radius,
            center = origin
        )
    }

/** Small uppercase section label. Used above every block of content. */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GTextMuted
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * The breathing "LIVE" dot: a solid core with a halo that expands and fades, so
 * it reads as a heartbeat rather than a blink.
 */
@Composable
fun PulsingDot(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 7.dp,
    animate: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "dot")
    val halo by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotHalo"
    )

    Box(modifier = modifier.size(size * 2.4f), contentAlignment = Alignment.Center) {
        if (animate) {
            Box(
                modifier = Modifier
                    .size(size + (size * 1.3f * halo))
                    .alpha((1f - halo) * 0.45f)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color)
        )
    }
}

/** Dot + label, used for LIVE and for status rows. */
@Composable
fun StatusDotLabel(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        PulsingDot(color = color, animate = animate)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = GTextSecondary
        )
    }
}

/**
 * A soft coloured glow, used behind hero numbers on the alert screen. Rendered as
 * a blurred circle so it reads as light rather than as a shape.
 */
@Composable
fun GlowSpot(
    color: Color,
    modifier: Modifier = Modifier,
    diameter: Dp = 160.dp,
    strength: Float = 0.22f
) {
    Box(
        modifier = modifier
            .size(diameter)
            .blur(diameter / 2.2f)
            .clip(CircleShape)
            .background(color.copy(alpha = strength))
    )
}
