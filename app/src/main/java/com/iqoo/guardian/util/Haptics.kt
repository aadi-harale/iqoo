package com.iqoo.guardian.util

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Thin wrappers so haptics stay optional. Compose exposes only two constants, so
 * emphasis is expressed by repetition rather than by inventing platform calls.
 */
object Haptics {

    fun tap(haptic: HapticFeedback) {
        runCatching { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    }

    fun confirm(haptic: HapticFeedback) {
        runCatching { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
    }

    fun alert(haptic: HapticFeedback) {
        runCatching {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}
