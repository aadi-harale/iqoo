package com.iqoo.guardian.domain.model

import kotlin.math.exp
import kotlin.math.roundToInt

/**
 * How Guardian turns matched evidence into a 0..100 number.
 *
 * Weights are additive *evidence*, but the final score is a saturating function
 * of that evidence rather than a raw sum. Two reasons:
 *
 *  - Stacking many weak flags should not trivially reach 100. Five mild red
 *    flags are not five times as dangerous as one severe one.
 *  - It keeps the scale meaningful at the top end, so CRITICAL still means
 *    something after several patterns have already matched.
 *
 *   score = ceiling - (ceiling - floor) * e^(-evidence / K)
 *
 * [FLOOR_EXTERNAL] is a deliberate residual: Guardian never reports absolute
 * zero for content that arrived from outside the device and cannot be positively
 * verified. Device-originated observations have no such floor.
 */
object RiskScoring {

    /** Evidence decay constant. Larger = slower approach to 100. */
    const val K = 33.0

    private const val CEILING = 100.0

    /** Residual risk for unverifiable externally-authored content. */
    const val FLOOR_EXTERNAL = 4.0

    /** Observations Guardian made itself can be genuinely clean. */
    const val FLOOR_INTERNAL = 0.0

    fun score(evidence: Int, floor: Double): Int {
        val safeEvidence = evidence.coerceAtLeast(0).toDouble()
        val raw = CEILING - (CEILING - floor) * exp(-safeEvidence / K)
        return raw.roundToInt().coerceIn(0, 100)
    }

    fun floorFor(type: SignalType): Double = when (type) {
        SignalType.NOTIFICATION, SignalType.MESSAGE -> FLOOR_EXTERNAL
        SignalType.APP_BEHAVIOUR, SignalType.DEVICE_STATE -> FLOOR_INTERNAL
    }
}
