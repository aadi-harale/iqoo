package com.iqoo.guardian.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Every route in the app, in one place. Routes with arguments expose a builder so
 * no screen constructs a route string by hand.
 */
object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val ALERTS = "alerts"
    const val DEVICE = "device"
    const val PRIVACY = "privacy"
    const val DEMO_LAB = "demo_lab"
    const val CHAT = "chat"
    const val ABOUT = "about"
    const val BATTERY = "battery"
    const val THERMAL = "thermal"
    const val STORAGE = "storage"
    const val PERFORMANCE = "performance"
    const val APP_USAGE = "app_usage"
    const val MEMORY = "memory"
    const val CAMERA = "camera"
    const val NETWORK = "network"
    const val SENSORS = "sensors"
    const val HEALTH = "health"

    const val ANALYSIS_ARG = "scenarioId"
    const val ANALYSIS = "analysis/{$ANALYSIS_ARG}"
    fun analysis(scenarioId: String) = "analysis/$scenarioId"

    const val INSIGHT_ARG = "eventId"
    const val INSIGHT = "insight/{$INSIGHT_ARG}"
    fun insight(eventId: String) = "insight/$eventId"
}

enum class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME(Routes.HOME, "HOME", Icons.Rounded.Shield),
    ALERTS(Routes.ALERTS, "ALERTS", Icons.Rounded.Notifications),
    CHAT(Routes.CHAT, "ASK AI", Icons.Rounded.SmartToy),
    DEVICE(Routes.DEVICE, "DEVICE", Icons.Rounded.PhoneAndroid),
    PRIVACY(Routes.PRIVACY, "PRIVACY", Icons.Rounded.VisibilityOff)
}
