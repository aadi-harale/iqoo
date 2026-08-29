package com.iqoo.guardian.domain.engine

import com.iqoo.guardian.domain.model.ContextContradiction
import com.iqoo.guardian.domain.model.ContradictionType
import com.iqoo.guardian.domain.model.DeviceSignal
import com.iqoo.guardian.domain.model.DeviceSnapshot
import com.iqoo.guardian.domain.model.Severity

/**
 * Guardian's differentiator.
 *
 * A content classifier can only tell you a message *sounds* alarming. This engine
 * takes the factual claims a message makes about the device and checks them
 * against what the device actually reports. When the two disagree, the message is
 * not merely alarming - it is provably wrong about the device, which is far
 * stronger evidence than any amount of wording analysis.
 *
 * Findings are fed back into [RiskClassifier.classify] as extra evidence.
 */
class ContextCorrelationEngine {

    fun detect(signal: DeviceSignal, snapshot: DeviceSnapshot): List<ContextContradiction> =
        listOfNotNull(
            checkStorageClaim(signal, snapshot),
            checkBatteryClaim(signal, snapshot)
        )

    /**
     * Handles both shapes of storage claim:
     *  1. numeric      - "less than 1 GB remaining"
     *  2. qualitative  - "storage critically full"
     *
     * A numeric claim is contradicted when the device has materially more free
     * space than asserted. A qualitative claim is contradicted when a healthy
     * fraction of the disk is still free.
     */
    private fun checkStorageClaim(signal: DeviceSignal, snapshot: DeviceSnapshot): ContextContradiction? {
        val text = "${signal.title} ${signal.body}".lowercase()

        val claimedFreeGb = extractClaimedFreeGb(text)
        val qualitativeClaim = STORAGE_FULL_TERMS.firstOrNull { text.contains(it) }
        if (claimedFreeGb == null && qualitativeClaim == null) return null

        val actualFree = snapshot.freeStorageGb
        val freeFraction = snapshot.freeFraction

        val contradicted = when {
            claimedFreeGb != null ->
                actualFree >= claimedFreeGb * NUMERIC_CLAIM_FACTOR && actualFree >= MIN_FREE_GB_FOR_MISMATCH
            else -> freeFraction >= MARGINAL_FREE_FRACTION
        }
        if (!contradicted) return null

        val claimText = claimedFreeGb
            ?.let { "Less than $it GB remaining" }
            ?: qualitativeClaim!!.replaceFirstChar { c -> c.uppercase() }

        val severity = when {
            freeFraction >= HEALTHY_FREE_FRACTION -> Severity.HIGH
            freeFraction >= MARGINAL_FREE_FRACTION -> Severity.MEDIUM
            else -> Severity.LOW
        }

        val percentFree = (freeFraction * 100).toInt()

        return ContextContradiction(
            type = ContradictionType.STORAGE_CLAIM_MISMATCH,
            severity = severity,
            claim = claimText,
            actual = "$actualFree GB available",
            explanation = "Guardian read this device's own storage state and found $actualFree GB free " +
                "of ${snapshot.totalStorageGb} GB - about $percentFree% of the disk. The notification's " +
                "claim is not true of this device.",
            weight = weightFor(severity)
        )
    }

    /**
     * The same idea applied to battery: "your battery is critically low / damaged"
     * is a common hook for fake optimiser apps.
     */
    private fun checkBatteryClaim(signal: DeviceSignal, snapshot: DeviceSnapshot): ContextContradiction? {
        val text = "${signal.title} ${signal.body}".lowercase()
        val claim = BATTERY_ALARM_TERMS.firstOrNull { text.contains(it) } ?: return null
        if (snapshot.batteryPercent < HEALTHY_BATTERY_PERCENT) return null

        return ContextContradiction(
            type = ContradictionType.BATTERY_CLAIM_MISMATCH,
            severity = Severity.HIGH,
            claim = claim.replaceFirstChar { c -> c.uppercase() },
            actual = "${snapshot.batteryPercent}% charged",
            explanation = "Guardian read the battery level directly. The device is at " +
                "${snapshot.batteryPercent}%, which contradicts the message.",
            weight = weightFor(Severity.HIGH)
        )
    }

    /**
     * Pulls the asserted free-space figure out of phrasings like
     * "less than 1 GB remaining" or "only 500 MB left".
     */
    private fun extractClaimedFreeGb(text: String): Int? {
        GB_CLAIM_REGEX.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { return it }
        // Anything expressed in MB is, for our purposes, a claim of "less than 1 GB".
        if (MB_CLAIM_REGEX.containsMatchIn(text)) return 1
        return null
    }

    private fun weightFor(severity: Severity): Int = when (severity) {
        Severity.CRITICAL, Severity.HIGH -> W_CONTRADICTION_HIGH
        Severity.MEDIUM -> W_CONTRADICTION_MEDIUM
        else -> W_CONTRADICTION_LOW
    }

    companion object {
        /** A directly falsified device claim is the strongest single input in the model. */
        const val W_CONTRADICTION_HIGH = 38
        const val W_CONTRADICTION_MEDIUM = 22
        const val W_CONTRADICTION_LOW = 10

        /** Free space above this fraction makes "critically full" plainly false. */
        const val HEALTHY_FREE_FRACTION = 0.10f

        /** Below this, the claim is close enough to true that we stay quiet. */
        const val MARGINAL_FREE_FRACTION = 0.05f

        /** A numeric claim must be off by at least this multiple to count. */
        const val NUMERIC_CLAIM_FACTOR = 4

        const val MIN_FREE_GB_FOR_MISMATCH = 5
        const val HEALTHY_BATTERY_PERCENT = 30

        private val STORAGE_FULL_TERMS = listOf(
            "storage critically full", "storage is full", "storage almost full",
            "running out of space", "low on storage", "memory full", "out of space"
        )
        private val BATTERY_ALARM_TERMS = listOf(
            "battery is damaged", "battery critically low", "battery is dying",
            "battery health is critical"
        )
        private val GB_CLAIM_REGEX = Regex("""(?:less than|only|under)\s+([0-9]+)\s*gb""")
        private val MB_CLAIM_REGEX = Regex("""(?:less than|only|under)\s+[0-9]+\s*mb""")
    }
}
