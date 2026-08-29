package com.iqoo.guardian.domain.model

/**
 * Behavioural patterns the classifier can recognise in a signal.
 * Each carries the display copy Guardian shows the user, so the UI never
 * re-describes a pattern the engine detected.
 */
enum class RiskPattern(val label: String, val detail: String) {
    URGENCY(
        "ARTIFICIAL URGENCY",
        "The message pressures you to act immediately, a common way to stop people from checking."
    ),
    ACCOUNT_THREAT(
        "ACCOUNT THREAT",
        "It threatens that an account will be suspended, blocked or lost."
    ),
    PAYMENT_REQUEST(
        "PAYMENT REQUEST",
        "The message is built around moving money."
    ),
    CREDENTIAL_REQUEST(
        "CREDENTIAL PHISHING PATTERN",
        "It asks for details that should never be shared through a message."
    ),
    SUSPICIOUS_LINK(
        "SUSPICIOUS LINK",
        "The included link does not belong to the organisation the message claims to be."
    ),
    UNKNOWN_SENDER_PATTERN(
        "UNKNOWN SENDER",
        "The sender is not a contact and not a channel this device has seen before."
    ),
    PRIZE_OR_REWARD(
        "PRIZE OR REWARD BAIT",
        "It offers a prize or reward you did not enter for."
    ),
    KYC_THREAT(
        "KYC PRESSURE",
        "It demands a KYC or verification step outside the bank's own app."
    ),
    BANK_IMPERSONATION(
        "BANK IMPERSONATION",
        "The sender presents itself as a bank without arriving through a verified channel."
    ),
    OTP_REQUEST(
        "OTP REQUEST",
        "It asks for a one-time password. Genuine services never ask you to share one."
    ),
    REMOTE_ACCESS_REQUEST(
        "REMOTE ACCESS REQUEST",
        "It asks you to install screen-sharing or remote-control software."
    ),
    SYSTEM_IMPERSONATION(
        "SYSTEM IMPERSONATION",
        "It is styled to look like a message from Android or the device manufacturer."
    ),
    DEVICE_STATE_CLAIM(
        "DEVICE CLAIM CONTRADICTED",
        "It makes a claim about your device that Guardian was able to check directly."
    ),
    UNKNOWN_APP_INSTALL(
        "UNKNOWN CLEANER APP",
        "It pushes you to install an application from outside the app store."
    ),
    BACKGROUND_ANOMALY(
        "ABNORMAL BACKGROUND ACTIVITY",
        "An app is consuming resources well outside its normal pattern."
    )
}

/**
 * One pattern the classifier actually matched, with the weight it contributed.
 */
data class RiskReason(
    val pattern: RiskPattern,
    val weight: Int,
    /** The specific text or value that triggered the match — shown for transparency. */
    val evidence: String
) {
    val label: String get() = pattern.label
    val detail: String get() = pattern.detail
}

enum class Severity(val label: String, val range: IntRange) {
    SAFE("SAFE", 0..19),
    LOW("LOW", 20..39),
    MEDIUM("MEDIUM", 40..59),
    HIGH("HIGH", 60..84),
    CRITICAL("CRITICAL", 85..100);

    companion object {
        fun forScore(score: Int): Severity {
            val clamped = score.coerceIn(0, 100)
            return entries.first { clamped in it.range }
        }
    }
}

enum class ContradictionType(val label: String) {
    STORAGE_CLAIM_MISMATCH("Storage claim mismatch"),
    BATTERY_CLAIM_MISMATCH("Battery claim mismatch"),
    NETWORK_CLAIM_MISMATCH("Network claim mismatch")
}

/**
 * Produced when a claim inside a signal disagrees with the measured device state.
 * This is the cross-signal reasoning Guardian exists to do.
 */
data class ContextContradiction(
    val type: ContradictionType,
    val severity: Severity,
    /** What the message asserted, in the message's own words. */
    val claim: String,
    /** What the device actually reports. */
    val actual: String,
    val explanation: String,
    val weight: Int
)

/**
 * The full, explainable output of the pipeline for one signal.
 */
data class RiskAssessment(
    val score: Int,
    val severity: Severity,
    val reasons: List<RiskReason>,
    val contradictions: List<ContextContradiction> = emptyList(),
    val headline: String,
    val explanation: String,
    val recommendedAction: String,
    val secondaryAction: String? = null,
    /** Which inputs the decision was based on — drives the "Signals used" section. */
    val signalsUsed: List<String> = emptyList()
) {
    val isThreat: Boolean get() = severity >= Severity.MEDIUM
}
