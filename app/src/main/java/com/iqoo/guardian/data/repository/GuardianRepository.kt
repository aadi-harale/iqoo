package com.iqoo.guardian.data.repository

import com.iqoo.guardian.data.demo.DemoDeviceStateProvider
import com.iqoo.guardian.data.demo.DemoSignalProvider
import com.iqoo.guardian.domain.engine.ContextCorrelationEngine
import com.iqoo.guardian.domain.engine.GuardianAnalyzer
import com.iqoo.guardian.domain.engine.RuleBasedRiskClassifier
import com.iqoo.guardian.domain.model.DemoScenario
import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.domain.model.DeviceSnapshot
import com.iqoo.guardian.domain.model.EventCategory
import com.iqoo.guardian.domain.model.GuardianEvent
import com.iqoo.guardian.domain.model.GuardianScore
import com.iqoo.guardian.domain.model.PrivacySettings
import com.iqoo.guardian.domain.model.ProtectionStatus
import com.iqoo.guardian.domain.model.Severity
import com.iqoo.guardian.domain.model.SignalType
import com.iqoo.guardian.domain.repository.DeviceStateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Single source of truth for the demo. Holds Guardian state as [StateFlow]s and
 * owns the one path through which any event can be created: [analyze].
 *
 * Nothing in the UI constructs a [GuardianEvent] by hand - including the seeded
 * history, which is produced by running real signals through the real pipeline at
 * startup.
 */
class GuardianRepository(
    private val deviceStateProvider: DeviceStateProvider = DemoDeviceStateProvider(),
    private val analyzer: GuardianAnalyzer = GuardianAnalyzer(
        RuleBasedRiskClassifier(),
        ContextCorrelationEngine()
    )
) {

    private val _score = MutableStateFlow(GuardianScore())
    val score: StateFlow<GuardianScore> = _score.asStateFlow()

    private val _events = MutableStateFlow<List<GuardianEvent>>(emptyList())
    val events: StateFlow<List<GuardianEvent>> = _events.asStateFlow()

    private val _snapshot = MutableStateFlow(deviceStateProvider.snapshot())
    val snapshot: StateFlow<DeviceSnapshot> = _snapshot.asStateFlow()

    private val _privacy = MutableStateFlow(PrivacySettings())
    val privacy: StateFlow<PrivacySettings> = _privacy.asStateFlow()

    private val _presentationMode = MutableStateFlow(true)
    val presentationMode: StateFlow<Boolean> = _presentationMode.asStateFlow()

    private val _protection = MutableStateFlow(ProtectionStatus())
    val protection: StateFlow<ProtectionStatus> = _protection.asStateFlow()

    /** Events created since app start that the user has not yet handled. */
    val openEvents: List<GuardianEvent> get() = _events.value.filter { !it.handled }

    fun eventById(id: String): GuardianEvent? = _events.value.firstOrNull { it.id == id }

    // ------------------------------------------------------------------
    // Analysis
    // ------------------------------------------------------------------

    /**
     * The only way an event enters the timeline.
     *
     * Respects the user's privacy switches: a signal class the user has turned
     * off is not analysed at all, so the Privacy screen's promises are literally
     * true of the running app.
     */
    suspend fun analyze(signal: DeviceSignal, category: EventCategory): GuardianAnalyzer.AnalysisResult? {
        if (!isAnalysisEnabled(signal.type)) return null

        val result = analyzer.analyze(signal, deviceStateProvider.snapshot())
        val event = GuardianEvent(
            id = "${signal.id}-${System.currentTimeMillis()}",
            title = result.assessment.headline,
            subtitle = signal.body,
            category = category,
            severity = result.assessment.severity,
            timestampLabel = nowLabel(),
            timestampMillis = System.currentTimeMillis(),
            assessment = result.assessment,
            signal = signal,
            deviceSnapshot = result.snapshot,
            handled = false
        )

        if (_privacy.value.saveHistory) {
            _events.update { listOf(event) + it }
        }
        _score.update { it.copy(checksToday = it.checksToday + 1, lastCheckLabel = "just now") }
        refreshProtection()
        return result.copy(assessment = result.assessment).also { latestEventId = event.id }
    }

    /** Id of the event produced by the most recent [analyze] call. */
    var latestEventId: String? = null
        private set

    private fun isAnalysisEnabled(type: SignalType): Boolean = when (type) {
        SignalType.NOTIFICATION, SignalType.MESSAGE -> _privacy.value.analyzeNotifications
        SignalType.APP_BEHAVIOUR -> _privacy.value.analyzeAppActivity
        SignalType.DEVICE_STATE -> _privacy.value.analyzeDeviceHealth
    }

    // ------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------

    /** Marks a threat handled and credits the score, capped at 99. */
    fun blockEvent(eventId: String) {
        val event = eventById(eventId) ?: return
        if (event.handled) return

        _events.update { list -> list.map { if (it.id == eventId) it.copy(handled = true) else it } }

        val credit = when (event.severity) {
            Severity.CRITICAL -> 2
            Severity.HIGH -> 2
            Severity.MEDIUM -> 1
            else -> 0
        }
        _score.update {
            it.copy(
                value = (it.value + credit).coerceAtMost(MAX_SCORE),
                threatsBlocked = if (event.assessment.isThreat) it.threatsBlocked + 1 else it.threatsBlocked,
                lastCheckLabel = "just now"
            )
        }
        refreshProtection()
    }

    fun dismissEvent(eventId: String) {
        _events.update { list -> list.map { if (it.id == eventId) it.copy(handled = true) else it } }
        refreshProtection()
    }

    fun setPrivacySetting(transform: (PrivacySettings) -> PrivacySettings) {
        _privacy.update(transform)
        refreshProtection()
    }

    fun setPresentationMode(enabled: Boolean) {
        _presentationMode.value = enabled
    }

    // ------------------------------------------------------------------
    // Demo lifecycle
    // ------------------------------------------------------------------

    fun clearAlerts() {
        _events.value = emptyList()
        refreshProtection()
    }

    /** Full reset to the state judges see at launch. Safe to call mid-demo. */
    suspend fun resetDemo() {
        deviceStateProvider.reset()
        _snapshot.value = deviceStateProvider.snapshot()
        _privacy.value = PrivacySettings()
        _score.value = GuardianScore()
        _events.value = emptyList()
        latestEventId = null
        seedHistory()
        refreshProtection()
    }

    /**
     * Builds the starting timeline by pushing real signals through the real
     * pipeline, then back-dating the labels. Nothing here is a hand-written
     * verdict - open any seeded alert and it explains itself the same way a live
     * one does.
     */
    suspend fun seedHistory() {
        val seeds = listOf(
            SeedSpec(
                scenario = DemoSignalProvider.byId(DemoSignalProvider.FAKE_STORAGE)!!,
                label = "Today, 6:42 PM", handled = true, hoursAgo = 4,
                title = "Deceptive storage warning blocked"
            ),
            SeedSpec(
                scenario = DemoSignalProvider.byId(DemoSignalProvider.BANK_KYC)!!,
                label = "Today, 4:18 PM", handled = true, hoursAgo = 6,
                title = "Suspicious KYC notification detected"
            ),
            SeedSpec(
                scenario = dormantAppsScenario(),
                label = "Today, 11:32 AM", handled = false, hoursAgo = 11,
                title = "2 apps haven't been used in 60+ days",
                subtitle = "Potentially unnecessary background activity"
            ),
            SeedSpec(
                scenario = DemoSignalProvider.byId(DemoSignalProvider.PRIZE_SCAM)!!,
                label = "Yesterday, 2:15 PM", handled = true, hoursAgo = 28,
                title = "Fake prize reward blocked"
            ),
            SeedSpec(
                scenario = DemoSignalProvider.byId(DemoSignalProvider.RISKY_APP)!!,
                label = "Yesterday, 9:05 AM", handled = true, hoursAgo = 32,
                title = "Sensitive permissions revoked"
            ),
            SeedSpec(
                scenario = storageGrowthScenario(),
                label = "Tuesday", handled = false, hoursAgo = 76,
                title = "Storage increased by 2.1 GB",
                subtitle = "Mostly video captured by the camera"
            ),
            SeedSpec(
                scenario = DemoSignalProvider.byId(DemoSignalProvider.SAFE_MESSAGE)!!,
                label = "Monday", handled = true, hoursAgo = 98,
                title = "Routine message scanned"
            )
        )

        val snapshot = deviceStateProvider.snapshot()
        val seeded = seeds.map { spec ->
            val result = analyzer.analyze(spec.scenario.signal, snapshot)
            GuardianEvent(
                id = "seed-${spec.scenario.id}",
                title = spec.title ?: result.assessment.headline,
                subtitle = spec.subtitle ?: spec.scenario.signal.body,
                category = spec.scenario.eventCategory,
                severity = result.assessment.severity,
                timestampLabel = spec.label,
                timestampMillis = System.currentTimeMillis() - spec.hoursAgo * 3_600_000L,
                assessment = result.assessment,
                signal = spec.scenario.signal,
                deviceSnapshot = snapshot,
                handled = spec.handled
            )
        }
        _events.value = seeded
    }

    /**
     * A seeded timeline entry. The verdict still comes from the engine - [title]
     * and [subtitle] only control how the row reads in the list.
     */
    private data class SeedSpec(
        val scenario: DemoScenario,
        val label: String,
        val handled: Boolean,
        val hoursAgo: Int,
        val title: String? = null,
        val subtitle: String? = null
    )

    /**
     * Two device-originated signals that are not offered in the Demo Lab but are
     * still real inputs: they go through the same classifier as everything else.
     */
    private fun dormantAppsScenario() = DemoScenario(
        id = "dormant_apps",
        title = "Dormant apps",
        subtitle = "",
        accentSeverity = Severity.LOW,
        signal = DeviceSignal(
            id = "dormant_apps",
            type = SignalType.APP_BEHAVIOUR,
            sourceLabel = "App activity",
            title = "Dormant apps",
            body = "2 apps have not been used in 60+ days and are still running background work."
        ),
        eventCategory = EventCategory.DEVICE
    )

    private fun storageGrowthScenario() = DemoScenario(
        id = "storage_growth",
        title = "Storage growth",
        subtitle = "",
        accentSeverity = Severity.SAFE,
        signal = DeviceSignal(
            id = "storage_growth",
            type = SignalType.DEVICE_STATE,
            sourceLabel = "Storage",
            title = "Storage increased by 2.1 GB",
            body = "Device storage grew by 2.1 GB yesterday, mostly video captured by the camera."
        ),
        eventCategory = EventCategory.INFO
    )

    private fun refreshProtection() {
        val open = openEvents
        val snapshot = deviceStateProvider.snapshot()
        val privacy = _privacy.value
        val unresolvedThreat = open.any { it.assessment.isThreat }

        _protection.value = ProtectionStatus(
            notifications = if (privacy.analyzeNotifications) {
                if (unresolvedThreat) "1 needs review" else "Protected"
            } else "Paused",
            deviceState = if (privacy.analyzeDeviceHealth) "Normal" else "Paused",
            appActivity = if (privacy.analyzeAppActivity) {
                if (open.any { it.category == EventCategory.DEVICE }) "Review suggested" else "Normal"
            } else "Paused",
            storage = if (snapshot.freeFraction >= 0.10f) "Healthy" else "Low",
            privacy = "Protected",
            allNormal = !unresolvedThreat
        )
    }

    private fun nowLabel(): String =
        "Today, " + LocalTime.now().format(TIME_FORMAT)

    companion object {
        const val MAX_SCORE = 99
        private val TIME_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
    }
}
