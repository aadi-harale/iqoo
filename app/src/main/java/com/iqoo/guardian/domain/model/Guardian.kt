package com.iqoo.guardian.domain.model

enum class EventCategory(val label: String) {
    SCAM("SCAMS"),
    DEVICE("DEVICE"),
    PRIVACY("PRIVACY"),
    INFO("INFO")
}

/**
 * A single entry in the Guardian timeline. Everything the user can open and
 * inspect is one of these.
 */
data class GuardianEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: EventCategory,
    val severity: Severity,
    val timestampLabel: String,
    val timestampMillis: Long,
    val assessment: RiskAssessment,
    val signal: DeviceSignal?,
    val deviceSnapshot: DeviceSnapshot?,
    val handled: Boolean = false
)

/**
 * The headline protection number on Home.
 */
data class GuardianScore(
    val value: Int = 87,
    val statusLabel: String = "PROTECTED",
    val threatsBlocked: Int = 3,
    val checksToday: Int = 147,
    val cloudUploads: Int = 0,
    val lastCheckLabel: String = "just now"
)

/**
 * A scenario the Demo Lab can inject. The scenario only supplies the *input*;
 * the score and reasons it produces come from the engine, not from here.
 */
data class DemoScenario(
    val id: String,
    val title: String,
    val subtitle: String,
    val accentSeverity: Severity,
    val signal: DeviceSignal,
    val eventCategory: EventCategory = EventCategory.SCAM
)
