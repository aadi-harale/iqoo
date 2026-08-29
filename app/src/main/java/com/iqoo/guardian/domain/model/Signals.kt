package com.iqoo.guardian.domain.model

/**
 * The kind of on-device signal Guardian is reasoning about.
 *
 * In this prototype every signal is produced by [com.iqoo.guardian.data.demo.DemoSignalProvider].
 * The pipeline downstream of this type is real: swapping in a NotificationListenerService
 * or a StorageStatsManager-backed provider would not change anything below.
 */
enum class SignalType {
    NOTIFICATION,
    MESSAGE,
    APP_BEHAVIOUR,
    DEVICE_STATE
}

/**
 * A single structured observation handed to the analysis pipeline.
 */
data class DeviceSignal(
    val id: String,
    val type: SignalType,
    /** App / channel the signal presents itself as coming from, e.g. "SYSTEM STORAGE". */
    val sourceLabel: String,
    val title: String,
    val body: String,
    val url: String? = null,
    val packageName: String? = null,
    val timestampMillis: Long = System.currentTimeMillis(),
    /** True when the signal was injected from the Demo Lab rather than observed by the OS. */
    val simulated: Boolean = true
)

/**
 * Guardian's view of what the device is *actually* doing right now.
 *
 * Values are supplied by the active [com.iqoo.guardian.domain.repository.DeviceStateProvider].
 * The demo implementation returns fixed values; a production implementation would read
 * StatFs / BatteryManager / PowerManager.
 */
data class DeviceSnapshot(
    val totalStorageGb: Int = 256,
    val usedStorageGb: Int = 214,
    val freeStorageGb: Int = 42,
    val batteryPercent: Int = 68,
    val charging: Boolean = false,
    val thermalState: ThermalState = ThermalState.NORMAL,
    val networkState: NetworkState = NetworkState.CONNECTED,
    val backgroundActiveApps: Int = 3,
    /** True while values come from the demo provider rather than platform APIs. */
    val simulated: Boolean = true
) {
    val usedFraction: Float
        get() = if (totalStorageGb <= 0) 0f else usedStorageGb.toFloat() / totalStorageGb.toFloat()

    val freeFraction: Float
        get() = if (totalStorageGb <= 0) 0f else freeStorageGb.toFloat() / totalStorageGb.toFloat()
}

enum class ThermalState { NORMAL, WARM, HOT }

enum class NetworkState { CONNECTED, METERED, OFFLINE }
