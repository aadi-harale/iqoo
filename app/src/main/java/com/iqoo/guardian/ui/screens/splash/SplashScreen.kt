package com.iqoo.guardian.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqoo.guardian.ui.components.GuardianMark
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import kotlinx.coroutines.delay

/**
 * Short by design: the mark draws itself, the wordmark fades in, and the app
 * moves on. Under two seconds so a live demo never waits on it.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val draw = remember { Animatable(0f) }
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        draw.animateTo(1f, tween(950, easing = FastOutSlowInEasing))
        showText = true
        delay(750)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GBackground),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(GAccent.copy(alpha = 0.06f), Color.Transparent),
                        radius = 620f
                    )
                )
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GuardianMark(progress = draw.value, size = 96.dp)
            Spacer(Modifier.height(30.dp))
            AnimatedVisibility(visible = showText, enter = fadeIn(tween(420))) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "iQOO GUARDIAN",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.5.sp),
                        color = GTextPrimary
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Device intelligence. On-device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GTextMuted
                    )
                }
            }
        }
    }
}
