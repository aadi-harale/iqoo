package com.iqoo.guardian.domain.engine

import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.domain.model.DeviceSnapshot
import com.iqoo.guardian.domain.model.RiskAssessment

/**
 * The pipeline, in one place:
 *
 *   signal -> content classification -> context correlation -> re-classification
 *
 * The first pass is content-only. The correlation engine then checks the signal's
 * factual claims against the device. If it finds a contradiction, the signal is
 * classified again *with* that finding as evidence - which is what produces the
 * jump from "alarming wording" to "provably false".
 *
 * [AnalysisResult.baseline] keeps the content-only score so the UI can show what
 * the correlation step was worth.
 */
class GuardianAnalyzer(
    private val classifier: RiskClassifier,
    private val correlationEngine: ContextCorrelationEngine
) {

    data class AnalysisResult(
        val assessment: RiskAssessment,
        /** Score before device correlation ran. Equal to [assessment].score when nothing contradicted. */
        val baselineScore: Int,
        val snapshot: DeviceSnapshot
    ) {
        val correlationDelta: Int get() = assessment.score - baselineScore
    }

    suspend fun analyze(signal: DeviceSignal, snapshot: DeviceSnapshot): AnalysisResult {
        val baseline = classifier.classify(signal, emptyList())

        val contradictions = correlationEngine.detect(signal, snapshot)
        if (contradictions.isEmpty()) {
            return AnalysisResult(baseline, baseline.score, snapshot)
        }

        val correlated = classifier.classify(signal, contradictions)
        return AnalysisResult(correlated, baseline.score, snapshot)
    }
}
