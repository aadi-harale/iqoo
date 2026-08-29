package com.iqoo.guardian.domain.engine

import com.iqoo.guardian.domain.model.ContextContradiction
import com.iqoo.guardian.domain.model.ContradictionType
import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.domain.model.RiskAssessment
import com.iqoo.guardian.domain.model.RiskPattern
import com.iqoo.guardian.domain.model.RiskReason
import com.iqoo.guardian.domain.model.RiskScoring
import com.iqoo.guardian.domain.model.Severity
import com.iqoo.guardian.domain.model.SignalType

/**
 * Deterministic, fully local rule engine.
 *
 * Every weight below is evidence, not a score - [RiskScoring] combines them.
 * Nothing here is scenario-aware: the Demo Lab injects text, and this class has
 * no idea which scenario produced it.
 */
class RuleBasedRiskClassifier : RiskClassifier {

    override suspend fun classify(
        signal: DeviceSignal,
        contradictions: List<ContextContradiction>
    ): RiskAssessment {
        val text = "${signal.title} ${signal.body}".lowercase()
        val sender = signal.sourceLabel.lowercase()
        val host = signal.url?.let { hostOf(it) }

        val reasons = buildList {
            detectUrgency(text)?.let(::add)
            detectAccountThreat(text)?.let(::add)
            detectPaymentRequest(text)?.let(::add)
            detectCredentialRequest(text)?.let(::add)
            detectKycPressure(text, signal.url)?.let(::add)
            detectOtpRequest(text)?.let(::add)
            detectRemoteAccess(text)?.let(::add)
            detectPrize(text)?.let(::add)
            detectUnknownAppInstall(text)?.let(::add)
            detectBankImpersonation(text, sender)?.let(::add)
            detectSystemImpersonation(sender, text)?.let(::add)
            detectSuspiciousLink(host, text, sender)?.let(::add)
            detectUnknownSender(signal, sender)?.let(::add)
            detectBackgroundAnomaly(text, signal.type)?.let(::add)
            detectDeviceStateClaim(text)?.let(::add)
        }.sortedByDescending { it.weight }

        val evidence = reasons.sumOf { it.weight } + contradictions.sumOf { it.weight }
        val score = RiskScoring.score(evidence, RiskScoring.floorFor(signal.type))
        val severity = Severity.forScore(score)

        val narrative = Narrative.build(signal, reasons, contradictions, severity)

        return RiskAssessment(
            score = score,
            severity = severity,
            reasons = reasons,
            contradictions = contradictions,
            headline = narrative.headline,
            explanation = narrative.explanation,
            recommendedAction = narrative.recommendedAction,
            secondaryAction = narrative.secondaryAction,
            signalsUsed = signalsUsed(signal, contradictions)
        )
    }

    // ------------------------------------------------------------------
    // Detectors. Each returns at most one reason, carrying the exact text it
    // matched so the UI can show the engine's working.
    // ------------------------------------------------------------------

    private fun detectUrgency(text: String): RiskReason? {
        val hit = URGENCY_TERMS.firstOrNull { text.contains(it) } ?: return null
        return RiskReason(RiskPattern.URGENCY, W_URGENCY, quote(hit))
    }

    private fun detectAccountThreat(text: String): RiskReason? {
        val hit = ACCOUNT_THREAT_TERMS.firstOrNull { text.contains(it) } ?: return null
        return RiskReason(RiskPattern.ACCOUNT_THREAT, W_ACCOUNT_THREAT, quote(hit))
    }

    /**
     * An explicit payment phrase always counts. A bare currency amount only counts
     * when nothing else explains it - a prize scam quotes a figure too, and calling
     * that a "payment request" would be wrong.
     */
    private fun detectPaymentRequest(text: String): RiskReason? {
        PAYMENT_TERMS.firstOrNull { text.contains(it) }?.let {
            return RiskReason(RiskPattern.PAYMENT_REQUEST, W_PAYMENT, quote(it))
        }
        if (PRIZE_TERMS.any { text.contains(it) }) return null
        val amount = CURRENCY_REGEX.find(text)?.value ?: return null
        return RiskReason(RiskPattern.PAYMENT_REQUEST, W_PAYMENT, quote(amount.trim()))
    }

    /**
     * PIN / password / card requests. A PIN request is weighted far above every
     * other credential ask: entering a UPI PIN authorises money leaving the
     * account, so on its own it is close to conclusive.
     */
    private fun detectCredentialRequest(text: String): RiskReason? {
        PIN_TERMS.firstOrNull { text.contains(it) }?.let {
            return RiskReason(RiskPattern.CREDENTIAL_REQUEST, W_PIN_REQUEST, quote(it))
        }
        CREDENTIAL_TERMS.firstOrNull { text.contains(it) }?.let {
            return RiskReason(RiskPattern.CREDENTIAL_REQUEST, W_CREDENTIAL, quote(it))
        }
        return null
    }

    /**
     * A KYC demand carrying a link is full credential phishing and is already
     * reported by [detectCredentialRequest]. A KYC demand with no link is the
     * softer "call this number" variant, reported separately.
     */
    private fun detectKycPressure(text: String, url: String?): RiskReason? {
        if (url != null) return null
        val hit = KYC_TERMS.firstOrNull { text.contains(it) } ?: return null
        return RiskReason(RiskPattern.KYC_THREAT, W_KYC, quote(hit))
    }

    private fun detectOtpRequest(text: String): RiskReason? {
        val hit = OTP_TERMS.firstOrNull { text.contains(it) } ?: return null
        return RiskReason(RiskPattern.OTP_REQUEST, W_OTP, quote(hit))
    }

    private fun detectRemoteAccess(text: String): RiskReason? {
        val hit = REMOTE_ACCESS_TERMS.firstOrNull { text.contains(it) } ?: return null
        return RiskReason(RiskPattern.REMOTE_ACCESS_REQUEST, W_REMOTE_ACCESS, quote(hit))
    }

    private fun detectPrize(text: String): RiskReason? {
        val hit = PRIZE_TERMS.firstOrNull { text.contains(it) } ?: return null
        return RiskReason(RiskPattern.PRIZE_OR_REWARD, W_PRIZE, quote(hit))
    }

    private fun detectUnknownAppInstall(text: String): RiskReason? {
        val hit = INSTALL_TERMS.firstOrNull { text.contains(it) } ?: return null
        return RiskReason(RiskPattern.UNKNOWN_APP_INSTALL, W_UNKNOWN_APP, quote(hit))
    }

    private fun detectBankImpersonation(text: String, sender: String): RiskReason? {
        val brand = BRAND_TOKENS.firstOrNull { sender.contains(it) || text.contains(it) }
        val financial = FINANCIAL_TERMS.any { sender.contains(it) || text.contains(it) }
        return when {
            brand != null -> RiskReason(
                RiskPattern.BANK_IMPERSONATION, W_BANK_IMPERSONATION,
                "Sender presents as " + quote(brand)
            )
            financial -> RiskReason(
                RiskPattern.BANK_IMPERSONATION, W_FINANCIAL_IMPERSONATION,
                "Presents as a payment service"
            )
            else -> null
        }
    }

    private fun detectSystemImpersonation(sender: String, text: String): RiskReason? {
        val hit = SYSTEM_SENDER_TERMS.firstOrNull { sender.contains(it) } ?: return null
        // Only a concern when the "system" channel is also pushing an action.
        if (INSTALL_TERMS.none { text.contains(it) } && URGENCY_TERMS.none { text.contains(it) }) return null
        return RiskReason(RiskPattern.SYSTEM_IMPERSONATION, W_SYSTEM_IMPERSONATION, "Channel styled as " + quote(hit))
    }

    /**
     * Two distinct link findings, weighted differently because they are not
     * equally strong evidence:
     *  - a host that carries a brand name but is not that brand's domain
     *  - a host this device has no relationship with
     */
    private fun detectSuspiciousLink(host: String?, text: String, sender: String): RiskReason? {
        if (host == null) return null
        if (TRUSTED_HOSTS.any { host == it || host.endsWith(".$it") }) return null

        val brand = BRAND_TOKENS.firstOrNull { host.contains(it) }
        if (brand != null) {
            return RiskReason(
                RiskPattern.SUSPICIOUS_LINK, W_LOOKALIKE_LINK,
                host + " carries the " + quote(brand) + " name but is not its domain"
            )
        }
        val impersonating = BRAND_TOKENS.any { sender.contains(it) || text.contains(it) } ||
            SYSTEM_SENDER_TERMS.any { sender.contains(it) }
        return RiskReason(
            RiskPattern.SUSPICIOUS_LINK,
            if (impersonating) W_UNTRUSTED_LINK_IMPERSONATING else W_UNTRUSTED_LINK,
            host + " is not a domain this device has seen before"
        )
    }

    private fun detectUnknownSender(signal: DeviceSignal, sender: String): RiskReason? {
        if (signal.type != SignalType.MESSAGE) return null
        if (KNOWN_MESSAGING_SOURCES.any { sender.contains(it) }) return null
        return RiskReason(
            RiskPattern.UNKNOWN_SENDER_PATTERN, W_UNKNOWN_SENDER,
            "Sender " + quote(signal.sourceLabel) + " is not in contacts"
        )
    }

    /**
     * Two tiers, because they are not the same finding. An app burning battery
     * while the screen is off is an active anomaly; an app nobody has opened in
     * months is merely worth tidying up.
     */
    private fun detectBackgroundAnomaly(text: String, type: SignalType): RiskReason? {
        if (type != SignalType.APP_BEHAVIOUR) return null
        ANOMALY_TERMS.firstOrNull { text.contains(it) }?.let {
            return RiskReason(RiskPattern.BACKGROUND_ANOMALY, W_BACKGROUND_ANOMALY, quote(it))
        }
        DORMANCY_TERMS.firstOrNull { text.contains(it) }?.let {
            return RiskReason(RiskPattern.BACKGROUND_ANOMALY, W_DORMANT_APP, quote(it))
        }
        return null
    }

    /**
     * Flags that the message asserts something about the device that Guardian can
     * independently check. Weak on its own - it only becomes decisive once
     * [ContextCorrelationEngine] actually falsifies the claim.
     */
    private fun detectDeviceStateClaim(text: String): RiskReason? {
        val hit = DEVICE_CLAIM_TERMS.firstOrNull { text.contains(it) } ?: return null
        return RiskReason(RiskPattern.DEVICE_STATE_CLAIM, W_DEVICE_CLAIM, "Claim: " + quote(hit))
    }

    private fun signalsUsed(signal: DeviceSignal, contradictions: List<ContextContradiction>): List<String> =
        buildList {
            when (signal.type) {
                SignalType.NOTIFICATION -> add("Notification text")
                SignalType.MESSAGE -> add("Message text")
                SignalType.APP_BEHAVIOUR -> add("App activity record")
                SignalType.DEVICE_STATE -> add("Device state reading")
            }
            add("Sender context")
            if (signal.url != null) add("URL structure")
            contradictions.forEach {
                when (it.type) {
                    ContradictionType.STORAGE_CLAIM_MISMATCH -> add("Device storage")
                    ContradictionType.BATTERY_CLAIM_MISMATCH -> add("Battery state")
                    ContradictionType.NETWORK_CLAIM_MISMATCH -> add("Network state")
                }
            }
        }.distinct()

    private fun hostOf(url: String): String {
        val withoutScheme = url.substringAfter("://", url)
        return withoutScheme.substringBefore('/').substringBefore('?').lowercase().removePrefix("www.")
    }

    private fun quote(value: String): String = "“" + value + "”"

    companion object {
        // ---- Evidence weights ----
        const val W_URGENCY = 15
        const val W_ACCOUNT_THREAT = 20
        const val W_PAYMENT = 15
        const val W_PIN_REQUEST = 64
        const val W_CREDENTIAL = 12
        const val W_KYC = 14
        const val W_OTP = 30
        const val W_REMOTE_ACCESS = 30
        const val W_PRIZE = 18
        const val W_UNKNOWN_APP = 12
        const val W_BANK_IMPERSONATION = 15
        const val W_FINANCIAL_IMPERSONATION = 20
        const val W_SYSTEM_IMPERSONATION = 12
        const val W_LOOKALIKE_LINK = 20
        const val W_UNTRUSTED_LINK_IMPERSONATING = 10
        const val W_UNTRUSTED_LINK = 8
        const val W_UNKNOWN_SENDER = 6
        const val W_BACKGROUND_ANOMALY = 22
        const val W_DORMANT_APP = 8
        const val W_DEVICE_CLAIM = 5

        // ---- Lexicons ----
        private val URGENCY_TERMS = listOf(
            "immediately", "urgent", "right now", "within 2 hours", "within 24 hours",
            "act now", "last chance", "expires today", "will be suspended today",
            "prevent data loss", "is on hold"
        )
        private val ACCOUNT_THREAT_TERMS = listOf(
            "account will be suspended", "account will be blocked", "account has been locked",
            "account will be closed", "will be deactivated", "account suspended",
            "service will be stopped"
        )
        private val PAYMENT_TERMS = listOf(
            "refund pending", "customs fee", "processing fee", "outstanding amount",
            "release delivery", "payment of"
        )
        private val CURRENCY_REGEX = Regex("""(?:₹|rs\.?\s?)\s?[0-9][0-9,]*""")
        private val PIN_TERMS = listOf(
            "upi pin", "atm pin", "card pin", "enter your pin", "share your pin", "mpin"
        )
        private val CREDENTIAL_TERMS = listOf(
            "kyc verification", "complete kyc", "verify your account", "confirm your identity",
            "update your details", "net banking password", "card number", "cvv", "login credentials"
        )
        private val KYC_TERMS = listOf("kyc", "re-kyc", "document verification")
        private val OTP_TERMS = listOf("otp", "one time password", "one-time password", "verification code")
        private val REMOTE_ACCESS_TERMS = listOf(
            "anydesk", "teamviewer", "screen share", "screen capture", "remote access",
            "accessibility access", "quick support"
        )
        private val PRIZE_TERMS = listOf(
            "you have won", "lucky draw", "claim your prize", "you are a winner", "reward of"
        )
        private val INSTALL_TERMS = listOf(
            "install super cleaner", "install now", "download the app", "install the app",
            "install cleaner", "download apk"
        )
        private val BRAND_TOKENS = listOf(
            "sbi", "state bank", "hdfc", "icici", "axis bank", "kotak", "paytm", "phonepe", "gpay"
        )
        private val FINANCIAL_TERMS = listOf("upi", "npci", "wallet", "payments", "bank")
        private val SYSTEM_SENDER_TERMS = listOf(
            "system storage", "android system", "system update", "system security", "device care"
        )
        private val DEVICE_CLAIM_TERMS = listOf(
            "storage critically full", "storage is full", "gb remaining", "running out of space",
            "low on storage", "memory full", "battery is damaged", "battery critically low"
        )
        private val ANOMALY_TERMS = listOf(
            "battery in the background", "while the screen was off", "in the background",
            "background activity", "without being opened"
        )
        private val TRUSTED_HOSTS = listOf(
            "play.google.com", "google.com", "android.com", "iqoo.com", "vivo.com", "whatsapp.com"
        )
        private val DORMANCY_TERMS = listOf(
            "have not been used", "haven't been used", "not been opened", "unused for"
        )
        private val KNOWN_MESSAGING_SOURCES = listOf("whatsapp", "messages", "signal", "telegram")
    }
}
