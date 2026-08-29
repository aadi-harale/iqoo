package com.iqoo.guardian.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.theme.GSafe

/**
 * Ring sweeps closed, then the tick draws itself. Two motions, ~900ms total -
 * long enough to register as a resolution, short enough not to hold up the demo.
 */
@Composable
fun SuccessCheck(
    modifier: Modifier = Modifier,
    size: Dp = 84.dp,
    color: Color = GSafe,
    onAnimationEnd: (() -> Unit)? = null
) {
    val ringProgress = remember { Animatable(0f) }
    val tickProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        ringProgress.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
        tickProgress.animateTo(1f, tween(340, easing = EaseOutBack))
        onAnimationEnd?.invoke()
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = 3.5.dp.toPx()
            val inset = stroke / 2f

            drawCircle(
                color = color.copy(alpha = 0.10f),
                radius = this.size.minDimension / 2f
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * ringProgress.value,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(
                    this.size.width - stroke,
                    this.size.height - stroke
                ),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            if (tickProgress.value > 0f) {
                val w = this.size.width
                val tick = Path().apply {
                    moveTo(w * 0.30f, w * 0.52f)
                    lineTo(w * 0.44f, w * 0.66f)
                    lineTo(w * 0.71f, w * 0.37f)
                }
                val measure = PathMeasure().apply { setPath(tick, false) }
                val drawn = Path()
                measure.getSegment(0f, measure.length * tickProgress.value, drawn, true)
                drawPath(
                    path = drawn,
                    color = color,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
        }
    }
}
