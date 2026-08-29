package com.iqoo.guardian.domain.repository

import com.iqoo.guardian.domain.model.DeviceSnapshot

/**
 * Where Guardian's picture of the device comes from.
 *
 * The prototype ships [com.iqoo.guardian.data.demo.DemoDeviceStateProvider], which
 * returns fixed values. A production implementation would read StatFs for storage,
 * BatteryManager for charge, and PowerManager for thermal status - the rest of the
 * app would not change, because nothing above this interface knows the difference.
 */
interface DeviceStateProvider {
    fun snapshot(): DeviceSnapshot
    /** Applies a storage delta, so blocking a threat can visibly change device state later. */
    fun update(transform: (DeviceSnapshot) -> DeviceSnapshot)
    fun reset()
}
