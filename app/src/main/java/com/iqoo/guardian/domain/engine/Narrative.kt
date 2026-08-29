package com.iqoo.guardian.domain.engine

import com.iqoo.guardian.domain.model.ContextContradiction
import com.iqoo.guardian.domain.model.ContradictionType
import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.domain.model.RiskPattern
import com.iqoo.guardian.domain.model.RiskReason
import com.iqoo.guardian.domain.model.Severity

/**
 * Turns engine output into the words the user reads.
 *
 * Deliberately driven by *which patterns matched*, never by which demo scenario
 * was injected - the classifier has no scenario identity to key off, and neither
 * does this. Change the injected text and the narrative changes with it.
 */
internal object Narrative {

    data class Copy(
        val headline: String,
        val explanation: String,
        val recommendedAction: String,
        val secondaryAction: String?
    )

    fun build(
        signal: DeviceSignal,
        reasons: List<RiskReason>,
        contradictions: List<ContextContradiction>,
        severity: Severity
    ): Copy {
        val patterns = reasons.map { it.pattern }.toSet()

        val storageMismatch = contradictions.firstOrNull {
            it.type == ContradictionType.STORAGE_CLAIM_MISMATCH
        }
        val batteryMismatch = contradictions.firstOrNull {
            it.type == ContradictionType.BATTERY_CLAIM_MISMATCH
        }

        return when {
            storageMismatch != null -> Copy(
                headline = "Deceptive system warning detected",
                explanation = "This notification claims your device is critically low on storage, but " +
                    "Guardian checked and found ${storageMismatch.actual}. The message also pressures " +
                    "you to install an unknown application through an external link.",
                recommendedAction = "Do not install the suggested application. Nothing is wrong with " +
                    "your storage.",
                secondaryAction = "Dismiss notification"
            )

            batteryMismatch != null -> Copy(
                headline = "False battery warning detected",
                explanation = "This message claims your battery is failing, but Guardian read the " +
                    "battery directly and found ${batteryMismatch.actual}.",
                recommendedAction = "Do not install the suggested application.",
                secondaryAction = "Dismiss notification"
            )

            RiskPattern.CREDENTIAL_REQUEST in patterns && isPinRequest(reasons) -> Copy(
                headline = "UPI PIN request detected",
                explanation = "Entering a UPI PIN authorises money to leave your account - it is never " +
                    "required to receive a refund. Guardian matched this against the pattern used by " +
                    "collect-request fraud.",
                recommendedAction = "Do not enter your UPI PIN. If you are expecting a refund, check it " +
                    "inside your bank or payment app.",
                secondaryAction = "Dismiss notification"
            )

            RiskPattern.BANK_IMPERSONATION in patterns &&
                (RiskPattern.CREDENTIAL_REQUEST in patterns || RiskPattern.KYC_THREAT in patterns) -> Copy(
                headline = "Suspicious KYC notification detected",
                explanation = "Guardian detected multiple social-engineering patterns commonly " +
                    "associated with credential phishing: an account threat, manufactured urgency, and " +
                    "a link that does not belong to the bank it names.",
                recommendedAction = "Open your bank's official application directly instead of using " +
                    "this link.",
                secondaryAction = "Dismiss notification"
            )

            RiskPattern.REMOTE_ACCESS_REQUEST in patterns -> Copy(
                headline = "Remote access request detected",
                explanation = "Something is asking for screen capture or remote-control access. That " +
                    "level of access lets another party see everything you do, including banking apps.",
                recommendedAction = "Revoke this permission in Settings and remove the app if you did " +
                    "not install it deliberately.",
                secondaryAction = "Review app permissions"
            )

            RiskPattern.PRIZE_OR_REWARD in patterns -> Copy(
                headline = "Prize scam pattern detected",
                explanation = "This message offers a reward you did not enter for and pushes you to " +
                    "claim it through an external link within a deadline. That combination is the " +
                    "standard shape of a prize scam.",
                recommendedAction = "Do not open the link. Genuine prizes are never claimed through " +
                    "an unsolicited message.",
                secondaryAction = "Dismiss notification"
            )

            RiskPattern.PAYMENT_REQUEST in patterns && RiskPattern.SUSPICIOUS_LINK in patterns -> Copy(
                headline = "Payment request from an unverified sender",
                explanation = "This message asks for a payment through a link on a domain your device " +
                    "has no relationship with. Delivery and customs fees are not collected this way.",
                recommendedAction = "Check the order inside the retailer's own app before paying " +
                    "anything.",
                secondaryAction = "Dismiss notification"
            )

            RiskPattern.BACKGROUND_ANOMALY in patterns -> Copy(
                headline = "Unusual background activity",
                explanation = "An app is using resources well outside its normal pattern while you are " +
                    "not using it. That is not proof of anything malicious, but it is worth a look.",
                recommendedAction = "Restrict this app's background activity if you do not need it " +
                    "running.",
                secondaryAction = "Review app"
            )

            severity >= Severity.MEDIUM -> Copy(
                headline = "Suspicious message detected",
                explanation = "Guardian matched ${reasons.size} risk " +
                    "${if (reasons.size == 1) "pattern" else "patterns"} in this content from " +
                    "${signal.sourceLabel}.",
                recommendedAction = "Treat this message as untrusted and verify through an official " +
                    "app before acting on it.",
                secondaryAction = "Dismiss notification"
            )

            else -> Copy(
                headline = "Looks safe",
                explanation = "No suspicious patterns detected. Guardian checked this content for " +
                    "urgency pressure, credential requests, payment hooks and link reputation, and " +
                    "found none of them.",
                recommendedAction = "No action needed.",
                secondaryAction = null
            )
        }
    }

    private fun isPinRequest(reasons: List<RiskReason>): Boolean =
        reasons.any {
            it.pattern == RiskPattern.CREDENTIAL_REQUEST &&
                it.weight == RuleBasedRiskClassifier.W_PIN_REQUEST
        }
}
