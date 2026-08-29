package com.iqoo.guardian.ui.screens.demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqoo.guardian.domain.model.RiskAssessment
import com.iqoo.guardian.domain.model.Severity
import com.iqoo.guardian.ui.components.ContradictionCard
import com.iqoo.guardian.ui.components.CorrelationSummary
import com.iqoo.guardian.ui.components.CountUpNumber
import com.iqoo.guardian.ui.components.GuardianCard
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.components.ReasonChip
import com.iqoo.guardian.ui.components.RecommendationCard
import com.iqoo.guardian.ui.components.RiskBadge
import com.iqoo.guardian.ui.components.ScanStepRow
import com.iqoo.guardian.ui.components.ScanningBar
import com.iqoo.guardian.ui.components.SectionLabel
import com.iqoo.guardian.ui.components.SimulatedNotificationCard
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GSafe
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary
import com.iqoo.guardian.ui.theme.color
import com.iqoo.guardian.util.Haptics

/**
 * The full demo loop on one screen: signal arrives, Guardian narrates its
 * analysis, the verdict is revealed, the user acts.
 *
 * Keeping it on one route means the back stack stays trivial and the presenter
 * can never land somewhere unexpected mid-demo.
 */
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel,
    onFinished: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val scenario = state.scenario

    // A critical verdict gets a stronger haptic than the rest.
    LaunchedEffect(state.phase, state.result?.assessment?.severity) {
        if (state.phase == AnalysisPhase.RESULT) {
            when (state.result?.assessment?.severity) {
                Severity.CRITICAL, Severity.HIGH -> Haptics.alert(haptic)
                null -> Unit
                else -> Haptics.tap(haptic)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))

        when (state.phase) {
            AnalysisPhase.BLOCKED -> BlockedState(
                severity = state.result?.assessment?.severity ?: Severity.MEDIUM,
                onDone = onFinished
            )

            else -> {
                GuardianTopBar(
                    eyebrow = if (state.phase == AnalysisPhase.RESULT) "GUARDIAN ALERT"
                    else "INCOMING SIGNAL",
                    title = when (state.phase) {
                        AnalysisPhase.INCOMING -> "Signal received"
                        AnalysisPhase.SCANNING -> "Analysing"
                        else -> state.result?.assessment?.headline ?: "Analysis complete"
                    },
                    onBack = onBack
                )
                Spacer(Modifier.height(20.dp))

                if (scenario != null) {
                    SimulatedNotificationCard(
                        signal = scenario.signal,
                        visible = true
                    )
                    Spacer(Modifier.height(18.dp))
                }

                if (state.analysisSkipped) {
                    AnalysisPausedCard()
                } else {
                    AnalysisBody(state = state, onBlock = {
                        Haptics.confirm(haptic)
                        viewModel.block()
                    }, onDismiss = {
                        viewModel.dismiss()
                        onFinished()
                    })
                }
            }
        }

        Spacer(Modifier.height(26.dp))
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
fun Modifier.glitch(enabled: Boolean): Modifier {
    if (!enabled) return this
    var isGlitching by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "glitch")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween<Float>(40, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "glitchX"
    )
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(450)
        isGlitching = false
    }
    
    return this.then(if (isGlitching) Modifier.offset(x = offsetX.toInt().dp) else Modifier)
}

@Composable
private fun AnalysisBody(
    state: AnalysisUiState,
    onBlock: () -> Unit,
    onDismiss: () -> Unit
) {
    val assessment = state.result?.assessment

    // The trace stays on screen after the verdict so the reasoning remains visible.
    if (state.steps.isNotEmpty()) {
        GuardianCard(contentPadding = 18.dp) {
            SectionLabel(
                if (state.phase == AnalysisPhase.SCANNING) "ANALYSING NOTIFICATION"
                else "ANALYSIS TRACE"
            )
            Spacer(Modifier.height(12.dp))
            if (state.phase == AnalysisPhase.SCANNING) {
                RadarScan()
                Spacer(Modifier.height(6.dp))
            }
            state.steps.forEachIndexed { index, step ->
                ScanStepRow(step, isLast = index == state.steps.lastIndex)
            }
        }
        Spacer(Modifier.height(18.dp))
    } else if (state.phase == AnalysisPhase.INCOMING) {
        GuardianCard(contentPadding = 18.dp) {
            RadarScan()
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Analysing notification...",
                style = MaterialTheme.typography.bodyLarge,
                color = GTextSecondary
            )
        }
    }

    AnimatedVisibility(
        visible = state.phase == AnalysisPhase.RESULT && assessment != null,
        enter = fadeIn(tween(420)) + slideInVertically(tween(460)) { it / 10 }
    ) {
        if (assessment != null) {
            Box(modifier = Modifier.glitch(assessment.severity == Severity.CRITICAL)) {
                VerdictBody(
                    assessment = assessment,
                    baselineScore = state.result?.baselineScore ?: assessment.score,
                    onBlock = onBlock,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun VerdictBody(
    assessment: RiskAssessment,
    baselineScore: Int,
    onBlock: () -> Unit,
    onDismiss: () -> Unit
) {
    val accent = assessment.severity.color()

    Column {
        // ---- Verdict header ----
        com.iqoo.guardian.ui.components.VerdictHeader(
            score = assessment.score,
            severity = assessment.severity,
            labelOverride = if (assessment.contradictions.isNotEmpty()) "CONTRADICTION DETECTED" else null
        )

        // ---- The contradiction, when there is one ----
        assessment.contradictions.firstOrNull()?.let { contradiction ->
            Spacer(Modifier.height(18.dp))
            ContradictionCard(contradiction = contradiction)
            Spacer(Modifier.height(14.dp))
            CorrelationSummary(
                explanation = contradiction.explanation,
                baselineScore = baselineScore,
                finalScore = assessment.score
            )
            
            // NEW: Context Correlation Graph
            Spacer(Modifier.height(22.dp))
            SectionLabel("CONTEXT CORRELATION GRAPH")
            Spacer(Modifier.height(12.dp))
            GuardianCard(contentPadding = 16.dp) {
                ContextCorrelationGraph()
            }
            
            // NEW: Counterfactuals / Temporal Timeline
            Spacer(Modifier.height(22.dp))
            SectionLabel("PREDICTIVE IMPACT")
            Spacer(Modifier.height(12.dp))
            GuardianCard(borderColor = GAccent.copy(alpha = 0.5f), contentPadding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(
                        Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = GAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Counterfactual Analysis", style = MaterialTheme.typography.labelSmall, color = GTextMuted)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "If you restrict FlashDeals now, you will regain +1h 25m of battery life.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GTextPrimary
                        )
                    }
                }
            }
        }

        // ---- Why ----
        Spacer(Modifier.height(22.dp))
        SectionLabel("WHY GUARDIAN FLAGGED THIS")
        Spacer(Modifier.height(12.dp))
        if (assessment.reasons.isEmpty()) {
            GuardianCard(borderColor = GSafe.copy(alpha = 0.24f)) {
                Text(
                    text = "No suspicious patterns detected.",
                    style = MaterialTheme.typography.titleLarge,
                    color = GSafe
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = assessment.explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GTextSecondary
                )
            }
        } else {
            StaggeredReasons(assessment, accent)
            Spacer(Modifier.height(16.dp))
            Text(
                text = assessment.explanation,
                style = MaterialTheme.typography.bodyLarge,
                color = GTextSecondary
            )
        }

        // ---- Action ----
        Spacer(Modifier.height(22.dp))
        RecommendationCard(
            text = assessment.recommendedAction,
            accent = if (assessment.isThreat) accent else GSafe
        )

        Spacer(Modifier.height(18.dp))
        ActionButtons(
            isThreat = assessment.isThreat,
            secondaryLabel = assessment.secondaryAction,
            onBlock = onBlock,
            onDismiss = onDismiss
        )
    }
}

/** Reasons arrive one after another so the case builds rather than dumping. */
@Composable
private fun StaggeredReasons(
    assessment: RiskAssessment,
    accent: androidx.compose.ui.graphics.Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        assessment.reasons.forEachIndexed { index, reason ->
            val shown = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(120L * index)
                shown.value = true
            }
            AnimatedVisibility(
                visible = shown.value,
                enter = fadeIn(tween(300)) + slideInVertically(tween(340)) { it / 3 }
            ) {
                ReasonChip(
                    label = reason.label,
                    evidence = reason.evidence,
                    accent = accent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isThreat: Boolean,
    secondaryLabel: String?,
    onBlock: () -> Unit,
    onDismiss: () -> Unit
) {
    Column {
        androidx.compose.material3.Button(
            onClick = if (isThreat) onBlock else onDismiss,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = GAccent,
                contentColor = GBackground
            )
        ) {
            Text(
                text = if (isThreat) "BLOCK & DISMISS" else "GOT IT",
                style = MaterialTheme.typography.labelMedium
            )
        }
        if (isThreat && secondaryLabel != null) {
            Spacer(Modifier.height(10.dp))
            androidx.compose.material3.TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = GTextSecondary
                )
            ) {
                Text(secondaryLabel, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun AnalysisPausedCard() {
    GuardianCard {
        SectionLabel("ANALYSIS PAUSED")
        Spacer(Modifier.height(10.dp))
        Text(
            text = "This signal class is switched off on the Privacy screen, so Guardian did not " +
                "read it.",
            style = MaterialTheme.typography.bodyLarge,
            color = GTextSecondary
        )
    }
}

/**
 * Success state. Auto-returns home so the presenter never has to hunt for a
 * button, but a manual button is there in case the timing needs stretching.
 */
@Composable
private fun BlockedState(
    severity: Severity,
    onDone: () -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2100)
        onDone()
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        com.iqoo.guardian.ui.components.SuccessCheck()
        Spacer(Modifier.height(26.dp))
        Text(
            text = "Threat handled.",
            style = MaterialTheme.typography.headlineMedium,
            color = GTextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "No further action is required.",
            style = MaterialTheme.typography.bodyLarge,
            color = GTextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        RiskBadge("${severity.label} THREAT BLOCKED", GSafe)
        Spacer(Modifier.height(34.dp))
        androidx.compose.material3.TextButton(
            onClick = onDone,
            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                contentColor = GTextMuted
            )
        ) {
            Text("RETURN TO HOME", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun RadarScan() {
    var phase by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while(true) {
            androidx.compose.runtime.withFrameNanos { time ->
                phase = (time / 10000000L % 360).toFloat()
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(140.dp)) {
            val radius = size.width / 2
            
            // Outer ring
            drawCircle(
                color = com.iqoo.guardian.ui.theme.GBorder,
                radius = radius,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
            
            // Radar sweep
            drawArc(
                brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color.Transparent,
                        com.iqoo.guardian.ui.theme.GAccent.copy(alpha = 0.6f),
                        com.iqoo.guardian.ui.theme.GAccent
                    )
                ),
                startAngle = phase - 90f,
                sweepAngle = 90f,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset.Zero,
                size = this.size
            )
            
            // Center blip
            drawCircle(
                color = com.iqoo.guardian.ui.theme.GAccent,
                radius = 4.dp.toPx()
            )
        }
    }
}

@Composable
fun ContextCorrelationGraph() {
    Column(modifier = Modifier.fillMaxWidth()) {
        GraphNode("APP ACTIVITY", "FlashDeals background usage up 400%", GAccent)
        GraphLine()
        GraphNode("THERMAL", "CPU Temp rose to 34°C", com.iqoo.guardian.ui.theme.GWarning)
        GraphLine()
        GraphNode("BATTERY", "Drain increased by 34%", com.iqoo.guardian.ui.theme.GWarning)
        GraphLine()
        GraphNode("RISK VERDICT", "Coordinated resource abuse", com.iqoo.guardian.ui.theme.GCritical)
    }
}

@Composable
private fun GraphNode(category: String, detail: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, androidx.compose.foundation.shape.CircleShape))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(category, style = MaterialTheme.typography.labelSmall, color = color)
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = GTextPrimary)
        }
    }
}

@Composable
private fun GraphLine() {
    Box(
        modifier = Modifier
            .padding(start = 5.dp, top = 4.dp, bottom = 4.dp)
            .width(2.dp)
            .height(20.dp)
            .background(com.iqoo.guardian.ui.theme.GBorder)
    )
}
