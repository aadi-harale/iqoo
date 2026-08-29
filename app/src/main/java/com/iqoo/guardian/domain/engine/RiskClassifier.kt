package com.iqoo.guardian.domain.engine

import com.iqoo.guardian.domain.model.ContextContradiction
import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.domain.model.RiskAssessment

/**
 * Turns a structured signal into an explainable risk assessment.
 *
 * The implementation shipped in this prototype is a deterministic rule engine
 * ([RuleBasedRiskClassifier]) — there is no model inference happening. The
 * interface exists so an on-device classifier (TFLite / NNAPI) can be dropped in
 * later without any UI or repository changes.
 */
interface RiskClassifier {

    /**
     * @param contradictions cross-signal findings from [ContextCorrelationEngine].
     *        Passing an empty list yields the content-only assessment, which is
     *        what makes the "before vs after correlation" difference measurable.
     */
    suspend fun classify(
        signal: DeviceSignal,
        contradictions: List<ContextContradiction> = emptyList()
    ): RiskAssessment
}
