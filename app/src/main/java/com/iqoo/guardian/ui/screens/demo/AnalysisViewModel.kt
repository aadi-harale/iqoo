package com.iqoo.guardian.ui.screens.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.iqoo.guardian.data.demo.DemoSignalProvider
import com.iqoo.guardian.data.repository.GuardianRepository
import com.iqoo.guardian.domain.engine.GuardianAnalyzer
import com.iqoo.guardian.domain.model.DemoScenario
import com.iqoo.guardian.domain.model.RiskPattern
import com.iqoo.guardian.domain.model.SignalType
import com.iqoo.guardian.ui.components.ScanStep
import com.iqoo.guardian.ui.components.ScanStepState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AnalysisPhase { INCOMING, SCANNING, RESULT, BLOCKED }

data class AnalysisUiState(
    val scenario: DemoScenario? = null,
    val phase: AnalysisPhase = AnalysisPhase.INCOMING,
    val steps: List<ScanStep> = emptyList(),
    val result: GuardianAnalyzer.AnalysisResult? = null,
    val eventId: String? = null,
    val contradictionFound: Boolean = false,
    val analysisSkipped: Boolean = false
)

/**
 * Drives one run of the demo flow: show the signal, narrate the analysis, reveal
 * the verdict.
 *
 * The analysis itself runs once, up front. The trace that plays afterwards is
 * built from what the engine actually did - the steps are a narration of a
 * completed decision, not a script that decides anything.
 */
class AnalysisViewModel(
    private val repository: GuardianRepository,
    private val scenarioId: String
) : ViewModel() {

    private val _state = MutableStateFlow(AnalysisUiState())
    val state: StateFlow<AnalysisUiState> = _state.asStateFlow()

    init {
        start()
    }

    fun start() {
        val scenario = DemoSignalProvider.byId(scenarioId) ?: return
        _state.value = AnalysisUiState(scenario = scenario, phase = AnalysisPhase.INCOMING)

        viewModelScope.launch {
            val paced = repository.presentationMode.value

            delay(if (paced) 1100 else 500)

            val result = repository.analyze(scenario.signal, scenario.eventCategory)
            if (result == null) {
                // The user has switched this signal class off on the Privacy screen.
                _state.update { it.copy(phase = AnalysisPhase.RESULT, analysisSkipped = true) }
                return@launch
            }
            val eventId = repository.latestEventId

            val plan = buildPlan(scenario, result)
            _state.update {
                it.copy(
                    phase = AnalysisPhase.SCANNING,
                    steps = plan.map { step -> step.copy(state = ScanStepState.PENDING) },
                    result = result,
                    eventId = eventId,
                    contradictionFound = result.assessment.contradictions.isNotEmpty()
                )
            }
            playTrace(plan, paced)
            _state.update { it.copy(phase = AnalysisPhase.RESULT) }
        }
    }

    /**
     * Builds the trace from the completed assessment. Steps only appear for work
     * the engine genuinely did: no URL step without a URL, no device-state step
     * unless the signal actually made a checkable claim about the device.
     */
    private fun buildPlan(
        scenario: DemoScenario,
        result: GuardianAnalyzer.AnalysisResult
    ): List<ScanStep> {
        val signal = scenario.signal
        val assessment = result.assessment
        val madeDeviceClaim = assessment.reasons.any { it.pattern == RiskPattern.DEVICE_STATE_CLAIM }
        val contradicted = assessment.contradictions.isNotEmpty()

        return buildList {
            add(
                ScanStep(
                    when (signal.type) {
                        SignalType.NOTIFICATION -> "Reading notification context"
                        SignalType.MESSAGE -> "Reading message context"
                        else -> "Reading app activity context"
                    },
                    ScanStepState.PENDING
                )
            )
            add(ScanStep("Analyzing language patterns", ScanStepState.PENDING))
            if (signal.url != null) {
                add(ScanStep("Checking suspicious URL", ScanStepState.PENDING))
            }
            if (madeDeviceClaim || contradicted) {
                add(ScanStep("Comparing device state", ScanStepState.PENDING))
            }
            if (contradicted) {
                add(ScanStep("Correlating signals", ScanStepState.PENDING))
                add(ScanStep("CONTRADICTION FOUND", ScanStepState.PENDING))
            } else {
                add(ScanStep("Correlating signals", ScanStepState.PENDING))
            }
            add(ScanStep("Threat intelligence generated", ScanStepState.PENDING))
        }
    }

    private suspend fun playTrace(plan: List<ScanStep>, paced: Boolean) {
        // Target: ~1.5 - 2.5s total animation time
        val stepDuration = if (paced) 400L else 200L
        val verifyDuration = if (paced) 600L else 300L
        val alertHold = if (paced) 800L else 300L

        plan.forEachIndexed { index, step ->
            val isVerify = step.label.startsWith("Comparing") || step.label.startsWith("Correlating")
            val isAlert = step.label == "CONTRADICTION FOUND"

            setStepState(index, if (isAlert) ScanStepState.ALERT else ScanStepState.RUNNING)
            delay(
                when {
                    isAlert -> alertHold
                    isVerify -> verifyDuration
                    else -> stepDuration
                }
            )
            if (!isAlert) setStepState(index, ScanStepState.DONE)
        }
        delay(if (paced) 200L else 100L)
    }

    private fun setStepState(index: Int, state: ScanStepState) {
        _state.update { current ->
            current.copy(
                steps = current.steps.mapIndexed { i, step ->
                    if (i == index) step.copy(state = state) else step
                }
            )
        }
    }

    fun block() {
        val eventId = _state.value.eventId ?: return
        repository.blockEvent(eventId)
        _state.update { it.copy(phase = AnalysisPhase.BLOCKED) }
    }

    fun dismiss() {
        val eventId = _state.value.eventId ?: return
        repository.dismissEvent(eventId)
    }

    class Factory(
        private val repository: GuardianRepository,
        private val scenarioId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AnalysisViewModel(repository, scenarioId) as T
    }
}
