package com.iqoo.guardian

import com.iqoo.guardian.data.demo.DemoDeviceStateProvider
import com.iqoo.guardian.data.demo.DemoSignalProvider
import com.iqoo.guardian.domain.engine.ContextCorrelationEngine
import com.iqoo.guardian.domain.engine.GuardianAnalyzer
import com.iqoo.guardian.domain.engine.RuleBasedRiskClassifier
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** Not an assertion - prints the demo table so the presenter knows every number. */
class EngineProbe {
    @Test
    fun `print the demo scorecard`() = runTest {
        val analyzer = GuardianAnalyzer(RuleBasedRiskClassifier(), ContextCorrelationEngine())
        val snap = DemoDeviceStateProvider.DEFAULT
        println("== GUARDIAN DEMO SCORECARD ==")
        DemoSignalProvider.scenarios.forEach { s ->
            val r = analyzer.analyze(s.signal, snap)
            val a = r.assessment
            println("${s.title.padEnd(24)} score=${a.score.toString().padStart(3)} " +
                "${a.severity.label.padEnd(8)} base=${r.baselineScore} delta=${r.correlationDelta}")
            println("   headline: ${a.headline}")
            a.reasons.forEach { println("   - [${it.weight}] ${it.label}  ${it.evidence}") }
            a.contradictions.forEach { println("   ! ${it.claim}  vs  ${it.actual}") }
        }
    }
}
