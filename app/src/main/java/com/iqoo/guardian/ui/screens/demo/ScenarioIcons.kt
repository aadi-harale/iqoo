package com.iqoo.guardian.ui.screens.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.BatteryAlert
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.ScreenShare
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.ui.graphics.vector.ImageVector
import com.iqoo.guardian.data.demo.DemoSignalProvider

/**
 * Icon per scenario. Lives in the UI layer so [DemoSignalProvider] stays free of
 * Compose types and remains a plain description of the signals.
 */
fun scenarioIcon(scenarioId: String): ImageVector = when (scenarioId) {
    DemoSignalProvider.BANK_KYC -> Icons.Rounded.AccountBalance
    DemoSignalProvider.FAKE_STORAGE -> Icons.Rounded.Storage
    DemoSignalProvider.UPI_REFUND -> Icons.Rounded.CurrencyRupee
    DemoSignalProvider.FAKE_DELIVERY -> Icons.Rounded.LocalShipping
    DemoSignalProvider.PRIZE_SCAM -> Icons.Rounded.CardGiftcard
    DemoSignalProvider.BATTERY_ANOMALY -> Icons.Rounded.BatteryAlert
    DemoSignalProvider.RISKY_APP -> Icons.Rounded.ScreenShare
    DemoSignalProvider.SAFE_MESSAGE -> Icons.Rounded.CheckCircle
    else -> Icons.Rounded.Shield
}
