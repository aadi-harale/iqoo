package com.iqoo.guardian.ui.screens.device

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqoo.guardian.ui.GuardianViewModel
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DeviceScreen(
    viewModel: GuardianViewModel,
    onOpenInsight: (String) -> Unit,
    onOpenPerformance: () -> Unit,
    onOpenAppUsage: () -> Unit,
    onOpenMemory: () -> Unit,
    onOpenBattery: () -> Unit,
    onOpenThermal: () -> Unit,
    onOpenStorage: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenNetwork: () -> Unit,
    onOpenSensors: () -> Unit,
    onOpenHealth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GAccent, bloomCenterYFraction = 0.30f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "DEVICE INTELLIGENCE",
                title = "Device Intelligence Hub",
                subtitle = "System signal correlation and analysis."
            )

            Spacer(Modifier.height(26.dp))
            DeviceDigitalTwin()

            Spacer(Modifier.height(32.dp))
            BentoGrid(onOpenPerformance, onOpenAppUsage, onOpenMemory, onOpenBattery, onOpenThermal, onOpenStorage, onOpenCamera, onOpenNetwork, onOpenSensors, onOpenHealth)
            
            Spacer(Modifier.height(24.dp))
            Text(
                text = "DATA MODE: PRESENTATION",
                style = MaterialTheme.typography.labelSmall,
                color = GTextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
fun DeviceDigitalTwin() {
    val infiniteTransition = rememberInfiniteTransition(label = "twin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "twinRot"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val phoneWidth = 70.dp.toPx()
            val phoneHeight = 140.dp.toPx()
            val radius = 100.dp.toPx()

            // Draw connecting lines
            val nodes = 10
            for (i in 0 until nodes) {
                val angle = (i * (360f / nodes) + rotation) * (Math.PI / 180f).toFloat()
                val nodeX = center.x + radius * cos(angle)
                val nodeY = center.y + radius * sin(angle)
                
                drawLine(
                    color = GAccent.copy(alpha = 0.15f),
                    start = center,
                    end = Offset(nodeX, nodeY),
                    strokeWidth = 1.dp.toPx()
                )
                
                // Draw intelligence nodes
                drawCircle(
                    color = GAccent.copy(alpha = 0.6f),
                    radius = 3.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )
            }

            // Draw Device Silhouette
            drawRoundRect(
                color = GBackground,
                topLeft = Offset(center.x - phoneWidth / 2, center.y - phoneHeight / 2),
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )
            drawRoundRect(
                color = GBorder,
                topLeft = Offset(center.x - phoneWidth / 2, center.y - phoneHeight / 2),
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Guardian AI Core in center
            drawCircle(
                color = GAccent.copy(alpha = 0.2f),
                radius = 16.dp.toPx(),
                center = center
            )
            drawCircle(
                color = GAccent,
                radius = 6.dp.toPx(),
                center = center
            )
        }
        
        Text(
            text = "DIGITAL TWIN",
            style = MaterialTheme.typography.labelSmall,
            color = GTextMuted,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
        )
    }
}

@Composable
fun BentoGrid(
    onOpenPerformance: () -> Unit,
    onOpenAppUsage: () -> Unit,
    onOpenMemory: () -> Unit,
    onOpenBattery: () -> Unit,
    onOpenThermal: () -> Unit,
    onOpenStorage: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenNetwork: () -> Unit,
    onOpenSensors: () -> Unit,
    onOpenHealth: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(110.dp)) {
            SectorCard(
                title = "BATTERY", value = "78%", subtitle = "GOOD", 
                modifier = Modifier.weight(1f),
                onClick = onOpenBattery
            ) {
                BatteryVisualization()
            }
            SectorCard(
                title = "THERMAL", value = "34°C", subtitle = "NORMAL", 
                modifier = Modifier.weight(1f),
                onClick = onOpenThermal
            ) {
                ThermalVisualization()
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(130.dp)) {
            SectorCard(
                title = "STORAGE", value = "214 GB", subtitle = "42 GB FREE", 
                modifier = Modifier.weight(1.5f),
                onClick = onOpenStorage
            ) {
                StorageVisualization()
            }
            SectorCard(
                title = "PERFORMANCE", value = "STABLE", subtitle = "No pressure", 
                modifier = Modifier.weight(1f), 
                onClick = onOpenPerformance
            ) {
                PerformanceVisualization()
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(110.dp)) {
            SectorCard(
                title = "APPS", value = "1 Anomaly", subtitle = "FlashDeals", 
                modifier = Modifier.weight(1f),
                onClick = onOpenAppUsage
            ) {
                Icon(Icons.Rounded.Warning, contentDescription = null, tint = GWarning, modifier = Modifier.size(32.dp))
            }
            SectorCard(
                title = "MEMORY", value = "NORMAL", subtitle = "5.4 GB Free", 
                modifier = Modifier.weight(1f),
                onClick = onOpenMemory
            ) {
                MemoryVisualization()
            }
            SectorCard(
                title = "CAMERA", value = "READY", subtitle = "Available", 
                modifier = Modifier.weight(1f),
                onClick = onOpenCamera
            ) {
                Icon(Icons.Rounded.Camera, contentDescription = null, tint = GTextSecondary, modifier = Modifier.size(32.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(80.dp)) {
            SectorCard(
                title = "NETWORK", value = "Wi-Fi", subtitle = "Connected", 
                modifier = Modifier.weight(1f),
                onClick = onOpenNetwork
            ) {
                Icon(Icons.Rounded.Wifi, contentDescription = null, tint = GTextSecondary, modifier = Modifier.size(24.dp))
            }
            SectorCard(
                title = "SENSORS", value = "12", subtitle = "Active", 
                modifier = Modifier.weight(1f),
                onClick = onOpenSensors
            ) {
                Icon(Icons.Rounded.Sensors, contentDescription = null, tint = GTextSecondary, modifier = Modifier.size(24.dp))
            }
            SectorCard(
                title = "HEALTH", value = "87", subtitle = "Index", 
                modifier = Modifier.weight(1f),
                onClick = onOpenHealth
            ) {
                Icon(Icons.Rounded.Favorite, contentDescription = null, tint = GSafe, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun SectorCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        androidx.compose.ui.Modifier.clickable { onClick() }
    } else {
        androidx.compose.ui.Modifier
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(GCard)
            .border(1.dp, GBorder, RoundedCornerShape(16.dp))
            .then(clickableModifier)
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = GTextMuted)
            Spacer(Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = GTextPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = GTextSecondary)
        }
        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            content()
        }
    }
}

@Composable
fun BatteryVisualization() {
    val transition = rememberInfiniteTransition(label = "batt")
    val pulse by transition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "battPulse"
    )
    Canvas(modifier = Modifier.size(40.dp)) {
        drawArc(color = GBorder, startAngle = 135f, sweepAngle = 270f, useCenter = false, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
        drawArc(color = GSafe.copy(alpha = pulse), startAngle = 135f, sweepAngle = 270f * 0.78f, useCenter = false, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun ThermalVisualization() {
    val transition = rememberInfiniteTransition(label = "therm")
    val pulse by transition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "thermPulse"
    )
    Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF4FC3F7).copy(alpha = 0.2f * pulse)))
        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFF4FC3F7).copy(alpha = 0.5f)))
    }
}

@Composable
fun PerformanceVisualization() {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom, modifier = Modifier.height(40.dp)) {
        val transition = rememberInfiniteTransition(label = "perf")
        for (i in 0..4) {
            val h by transition.animateFloat(
                initialValue = (10..20).random().toFloat(),
                targetValue = (20..40).random().toFloat(),
                animationSpec = infiniteRepeatable(tween((500..900).random()), RepeatMode.Reverse),
                label = "bar$i"
            )
            Box(modifier = Modifier.width(6.dp).height(h.dp).clip(RoundedCornerShape(3.dp)).background(GAccent))
        }
    }
}

@Composable
fun StorageVisualization() {
    Canvas(modifier = Modifier.width(100.dp).height(12.dp)) {
        drawRoundRect(color = GBorder, size = Size(size.width, size.height), cornerRadius = CornerRadius(6.dp.toPx()))
        drawRoundRect(color = GTextSecondary, size = Size(size.width * 0.83f, size.height), cornerRadius = CornerRadius(6.dp.toPx()))
    }
}

@Composable
fun MemoryVisualization() {
    Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = GBorder, style = Stroke(4.dp.toPx()))
            drawArc(color = GWarning, startAngle = -90f, sweepAngle = 360f * 0.55f, useCenter = false, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}
