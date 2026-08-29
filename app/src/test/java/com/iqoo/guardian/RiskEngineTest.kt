package com.iqoo.guardian

import com.iqoo.guardian.data.demo.DemoDeviceStateProvider
import com.iqoo.guardian.data.demo.DemoSignalProvider
import com.iqoo.guardian.domain.engine.ContextCorrelationEngine
import com.iqoo.guardian.domain.engine.GuardianAnalyzer
import com.iqoo.guardian.domain.engine.RuleBasedRiskClassifier
import com.iqoo.guardian.domain.model.ContradictionType
import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.domain.model.DeviceSnapshot
import com.iqoo.guardian.domain.model.RiskPattern
import com.iqoo.guardian.domain.model.Severity
import com.iqoo.guardian.domain.model.SignalType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * The engine's behaviour is the product, so these assert exact scores. If a
 * weight changes and a demo number moves, this fails loudly rather than the
 * presenter finding out on stage.
 */
class RiskEngineTest {

    private val classifier = RuleBasedRiskClassifier()
    private val correlation = ContextCorrelationEngine()
    private val analyzer = GuardianAnalyzer(classifier, correlation)
    private val snapshot = DemoDeviceStateProvider.DEFAULT

    private suspend fun analyze(scenarioId: String) =
        analyzer.analyze(DemoSignalProvider.byId(scenarioId)!!.signal, snapshot)

    @Test
    fun `safe message scores in the safe band`() = runTest {
        val result = analyze(DemoSignalProvider.SAFE_MESSAGE)
        assertEquals(4, result.assessment.score)
        assertEquals(Severity.SAFE, result.assessment.severity)
        assertTrue(result.assessment.reasons.isEmpty())
        assertFalse(result.assessment.isThreat)
    }

    @Test
    fun `bank kyc scam scores high`() = runTest {
        val result = analyze(DemoSignalProvider.BANK_KYC)
        assertEquals(92, result.assessment.score)
        // 92 falls in the 85-100 band, so the engine reports CRITICAL rather than HIGH.
        assertEquals(Severity.CRITICAL, result.assessment.severity)

        val patterns = result.assessment.reasons.map { it.pattern }
        assertTrue(RiskPattern.URGENCY in patterns)
        assertTrue(RiskPattern.ACCOUNT_THREAT in patterns)
        assertTrue(RiskPattern.BANK_IMPERSONATION in patterns)
        assertTrue(RiskPattern.SUSPICIOUS_LINK in patterns)
        assertTrue(RiskPattern.CREDENTIAL_REQUEST in patterns)
    }

    @Test
    fun `upi pin request is critical`() = runTest {
        val result = analyze(DemoSignalProvider.UPI_REFUND)
        assertEquals(96, result.assessment.score)
        assertEquals(Severity.CRITICAL, result.assessment.severity)
        assertTrue(result.assessment.reasons.any { it.pattern == RiskPattern.CREDENTIAL_REQUEST })
        assertTrue(result.assessment.reasons.any { it.pattern == RiskPattern.PAYMENT_REQUEST })
    }

    @Test
    fun `fake storage warning is critical once the device contradicts it`() = runTest {
        val result = analyze(DemoSignalProvider.FAKE_STORAGE)
        assertEquals(94, result.assessment.score)
        assertEquals(Severity.CRITICAL, result.assessment.severity)
    }

    @Test
    fun `storage contradiction is detected with the right claim and actual values`() = runTest {
        val signal = DemoSignalProvider.byId(DemoSignalProvider.FAKE_STORAGE)!!.signal
        val contradictions = correlation.detect(signal, snapshot)

        assertEquals(1, contradictions.size)
        val finding = contradictions.first()
        assertEquals(ContradictionType.STORAGE_CLAIM_MISMATCH, finding.type)
        assertEquals(Severity.HIGH, finding.severity)
        assertEquals("Less than 1 GB remaining", finding.claim)
        assertEquals("42 GB available", finding.actual)
    }

    /**
     * The heart of the demo claim: the same text scores HIGH on wording alone and
     * only becomes CRITICAL because the device disagreed with it.
     */
    @Test
    fun `correlation is what escalates the storage warning`() = runTest {
        val result = analyze(DemoSignalProvider.FAKE_STORAGE)

        assertEquals(81, result.baselineScore)
        assertEquals(Severity.HIGH, Severity.forScore(result.baselineScore))
        assertEquals(94, result.assessment.score)
        assertEquals(13, result.correlationDelta)
    }

    /**
     * Same alarming notification, but on a phone that really is full. No
     * contradiction, so no escalation - the engine is checking, not pattern-matching
     * the word "storage".
     */
    @Test
    fun `identical warning on a genuinely full device is not escalated`() = runTest {
        val fullDevice = DeviceSnapshot(
            totalStorageGb = 256,
            usedStorageGb = 255,
            freeStorageGb = 1
        )
        val signal = DemoSignalProvider.byId(DemoSignalProvider.FAKE_STORAGE)!!.signal
        val result = analyzer.analyze(signal, fullDevice)

        assertTrue(result.assessment.contradictions.isEmpty())
        assertEquals(0, result.correlationDelta)
        assertTrue(
            "expected below CRITICAL, was ${result.assessment.score}",
            result.assessment.severity < Severity.CRITICAL
        )
    }

    @Test
    fun `a legitimate low-storage notice stays low risk`() = runTest {
        val signal = DeviceSignal(
            id = "legit_storage",
            type = SignalType.NOTIFICATION,
            sourceLabel = "Files",
            title = "Free up space",
            body = "You can free up 2.1 GB by reviewing old screenshots."
        )
        val result = analyzer.analyze(signal, snapshot)

        assertTrue(result.assessment.contradictions.isEmpty())
        assertTrue(
            "expected SAFE or LOW, was ${result.assessment.severity}",
            result.assessment.severity <= Severity.LOW
        )
    }

    @Test
    fun `reasons accumulate as evidence is added`() = runTest {
        val plain = DeviceSignal(
            id = "t1", type = SignalType.MESSAGE, sourceLabel = "WhatsApp",
            title = "Note", body = "Your parcel arrives tomorrow."
        )
        val withUrgency = plain.copy(
            id = "t2",
            body = "URGENT: your parcel is on hold, act now."
        )
        val withUrgencyAndLink = withUrgency.copy(
            id = "t3",
            body = "URGENT: your parcel is on hold. Pay a customs fee to release delivery.",
            url = "in-parcel-fee.example"
        )

        val a = classifier.classify(plain).score
        val b = classifier.classify(withUrgency).score
        val c = classifier.classify(withUrgencyAndLink).score

        assertTrue("$a should be < $b", a < b)
        assertTrue("$b should be < $c", b < c)
        assertTrue(classifier.classify(withUrgencyAndLink).reasons.size >= 3)
    }

    @Test
    fun `score is bounded even when every pattern matches`() = runTest {
        val kitchenSink = DeviceSignal(
            id = "max",
            type = SignalType.NOTIFICATION,
            sourceLabel = "SBI SECURITY system storage",
            title = "URGENT account suspended",
            body = "URGENT: your bank account will be suspended. Storage critically full, less " +
                "than 1 GB remaining. Enter your UPI PIN and share the OTP. Install Super Cleaner " +
                "and AnyDesk now. You have won a lucky draw of Rs 50,000. Complete KYC.",
            url = "sbi-verify-account.example"
        )
        val result = analyzer.analyze(kitchenSink, snapshot)

        assertTrue(result.assessment.score <= 100)
        assertTrue(result.assessment.score >= 85)
        assertEquals(Severity.CRITICAL, result.assessment.severity)
    }

    @Test
    fun `severity bands cover the whole scale without gaps`() {
        (0..100).forEach { score ->
            val severity = Severity.forScore(score)
            assertTrue("score $score fell outside ${severity.name}", score in severity.range)
        }
        assertEquals(Severity.SAFE, Severity.forScore(-10))
        assertEquals(Severity.CRITICAL, Severity.forScore(500))
    }

    @Test
    fun `every demo scenario produces a usable, explainable verdict`() = runTest {
        DemoSignalProvider.scenarios.forEach { scenario ->
            val result = analyzer.analyze(scenario.signal, snapshot)
            val assessment = result.assessment
            assertTrue(
                "${scenario.id} produced an out-of-range score",
                assessment.score in 0..100
            )
            assertTrue("${scenario.id} has no headline", assessment.headline.isNotBlank())
            assertTrue("${scenario.id} has no explanation", assessment.explanation.isNotBlank())
            assertTrue("${scenario.id} has no action", assessment.recommendedAction.isNotBlank())
            assertTrue("${scenario.id} listed no inputs", assessment.signalsUsed.isNotEmpty())
        }
    }
}
