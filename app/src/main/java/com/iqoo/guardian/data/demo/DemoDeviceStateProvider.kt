package com.iqoo.guardian.data.demo

import com.iqoo.guardian.domain.model.DeviceSnapshot
import com.iqoo.guardian.domain.repository.DeviceStateProvider
import java.util.concurrent.atomic.AtomicReference

/**
 * Simulated device state for the demo.
 *
 * These are fixed values, not measurements - the UI labels them as simulated
 * wherever a user could mistake them for a real reading. Replacing this class
 * with a StatFs / BatteryManager implementation is the only change needed to make
 * the whole pipeline operate on real device state.
 */
class DemoDeviceStateProvider : DeviceStateProvider {

    private val state = AtomicReference(DEFAULT)

    override fun snapshot(): DeviceSnapshot = state.get()

    override fun update(transform: (DeviceSnapshot) -> DeviceSnapshot) {
        state.updateAndGet(transform)
    }

    override fun reset() {
        state.set(DEFAULT)
    }

    companion object {
        val DEFAULT = DeviceSnapshot(
            totalStorageGb = 256,
            usedStorageGb = 214,
            freeStorageGb = 42,
            batteryPercent = 68,
            charging = false,
            backgroundActiveApps = 3,
            simulated = true
        )

        /** Storage breakdown shown on the Device screen. Demo data, sums to used space. */
        val STORAGE_BREAKDOWN = listOf(
            StorageCategory("Apps", 86),
            StorageCategory("Photos & Video", 61),
            StorageCategory("System", 31),
            StorageCategory("Other", 36)
        )
    }
}

data class StorageCategory(val label: String, val sizeGb: Int)
