package com.iqoo.guardian.data.demo

import com.iqoo.guardian.domain.model.DemoScenario
import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.domain.model.EventCategory
import com.iqoo.guardian.domain.model.Severity
import com.iqoo.guardian.domain.model.SignalType

/**
 * The scenario catalogue for the Demo Lab.
 *
 * Everything here is an *input*: sender, title, body, link. No scenario carries a
 * score, a severity verdict or a list of reasons - those are produced by the
 * engine when the signal is analysed. [DemoScenario.accentSeverity] is used only
 * to colour the card in the lab, and is not consulted during analysis.
 *
 * All URLs use the reserved `.example` TLD so nothing here can resolve anywhere.
 */
object DemoSignalProvider {

    const val FAKE_STORAGE = "fake_storage"
    const val BANK_KYC = "bank_kyc"
    const val UPI_REFUND = "upi_refund"
    const val FAKE_DELIVERY = "fake_delivery"
    const val PRIZE_SCAM = "prize_scam"
    const val BATTERY_ANOMALY = "battery_anomaly"
    const val RISKY_APP = "risky_app"
    const val SAFE_MESSAGE = "safe_message"

    val scenarios: List<DemoScenario> = listOf(
        DemoScenario(
            id = BANK_KYC,
            title = "BANK KYC SCAM",
            subtitle = "Account-suspension phishing",
            accentSeverity = Severity.HIGH,
            signal = DeviceSignal(
                id = BANK_KYC,
                type = SignalType.NOTIFICATION,
                sourceLabel = "SBI SECURITY",
                title = "Action required on your account",
                body = "URGENT: Your bank account will be suspended today. Complete KYC " +
                    "verification immediately.",
                url = "sbi-verify-account.example"
            )
        ),
        DemoScenario(
            id = FAKE_STORAGE,
            title = "FAKE STORAGE WARNING",
            subtitle = "Claims about your device Guardian can check",
            accentSeverity = Severity.CRITICAL,
            signal = DeviceSignal(
                id = FAKE_STORAGE,
                type = SignalType.NOTIFICATION,
                sourceLabel = "SYSTEM STORAGE",
                title = "Storage critically full",
                body = "Your phone has less than 1 GB remaining. Install Super Cleaner now to " +
                    "prevent data loss.",
                url = "clean-now.example"
            ),
            eventCategory = EventCategory.DEVICE
        ),
        DemoScenario(
            id = UPI_REFUND,
            title = "UPI REFUND SCAM",
            subtitle = "PIN request disguised as a refund",
            accentSeverity = Severity.CRITICAL,
            signal = DeviceSignal(
                id = UPI_REFUND,
                type = SignalType.MESSAGE,
                sourceLabel = "UPI-REFUND",
                title = "Refund pending",
                body = "₹4,999 refund pending. Enter your UPI PIN to receive your refund.",
                url = null
            )
        ),
        DemoScenario(
            id = FAKE_DELIVERY,
            title = "FAKE DELIVERY MESSAGE",
            subtitle = "Customs fee on a parcel you never sent for",
            accentSeverity = Severity.HIGH,
            signal = DeviceSignal(
                id = FAKE_DELIVERY,
                type = SignalType.MESSAGE,
                sourceLabel = "IN-PARCEL",
                title = "Delivery on hold",
                body = "Your parcel is on hold at the depot. Pay a customs fee of ₹35 to release " +
                    "delivery today.",
                url = "in-parcel-fee.example"
            )
        ),
        DemoScenario(
            id = PRIZE_SCAM,
            title = "PRIZE / REWARD SCAM",
            subtitle = "A draw you never entered",
            accentSeverity = Severity.HIGH,
            signal = DeviceSignal(
                id = PRIZE_SCAM,
                type = SignalType.MESSAGE,
                sourceLabel = "WIN-ALERT",
                title = "You have won",
                body = "Congratulations, you have won ₹50,000 in the lucky draw. Claim within 2 " +
                    "hours before it expires.",
                url = "prize-claim-now.example"
            )
        ),
        DemoScenario(
            id = BATTERY_ANOMALY,
            title = "BATTERY ANOMALY",
            subtitle = "Device signal, not a message",
            accentSeverity = Severity.MEDIUM,
            signal = DeviceSignal(
                id = BATTERY_ANOMALY,
                type = SignalType.APP_BEHAVIOUR,
                sourceLabel = "Photo Widgets",
                title = "High background drain",
                body = "Photo Widgets used 22% battery in the background over four hours while the " +
                    "screen was off.",
                packageName = "com.example.photowidgets"
            ),
            eventCategory = EventCategory.DEVICE
        ),
        DemoScenario(
            id = RISKY_APP,
            title = "RISKY APP ACTIVITY",
            subtitle = "Accessibility and screen capture",
            accentSeverity = Severity.HIGH,
            signal = DeviceSignal(
                id = RISKY_APP,
                type = SignalType.APP_BEHAVIOUR,
                sourceLabel = "Photo Widgets",
                title = "Sensitive access requested",
                body = "Photo Widgets requested accessibility access and screen capture in the " +
                    "background.",
                packageName = "com.example.photowidgets"
            ),
            eventCategory = EventCategory.PRIVACY
        ),
        DemoScenario(
            id = SAFE_MESSAGE,
            title = "SAFE MESSAGE",
            subtitle = "Proof Guardian does not flag everything",
            accentSeverity = Severity.SAFE,
            signal = DeviceSignal(
                id = SAFE_MESSAGE,
                type = SignalType.MESSAGE,
                sourceLabel = "WhatsApp",
                title = "Aarav",
                body = "Hey, I'll reach around 7. Can you send me the location?",
                url = null
            ),
            eventCategory = EventCategory.INFO
        )
    )

    fun byId(id: String): DemoScenario? = scenarios.firstOrNull { it.id == id }
}
