package com.iqoo.guardian.domain.model

/**
 * Live protection rows on Home. Status strings are derived from the current
 * snapshot and event list rather than hardcoded per-row.
 */
data class ProtectionStatus(
    val notifications: String = "Protected",
    val deviceState: String = "Normal",
    val appActivity: String = "Normal",
    val storage: String = "Healthy",
    val privacy: String = "Protected",
    val allNormal: Boolean = true
)

/**
 * User-facing privacy switches. Turning one off actually stops that signal
 * class from being analysed, so the screen never over-promises.
 */
data class PrivacySettings(
    val analyzeNotifications: Boolean = true,
    val analyzeDeviceHealth: Boolean = true,
    val analyzeAppActivity: Boolean = true,
    val saveHistory: Boolean = true
)
