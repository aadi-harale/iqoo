package com.iqoo.guardian.ui.theme

import androidx.compose.ui.graphics.Color
import com.iqoo.guardian.domain.model.EventCategory
import com.iqoo.guardian.domain.model.Severity

/** One place that maps engine severity to colour, so no screen invents its own. */
fun Severity.color(): Color = when (this) {
    Severity.SAFE -> GSafe
    Severity.LOW -> GTextSecondary
    Severity.MEDIUM -> GWarning
    Severity.HIGH -> GDanger
    Severity.CRITICAL -> GCritical
}

/** Info-category events read as "INFO" rather than "SAFE" in the timeline. */
fun badgeLabel(severity: Severity, category: EventCategory): String =
    if (category == EventCategory.INFO && severity == Severity.SAFE) "INFO" else severity.label

fun badgeColor(severity: Severity, category: EventCategory): Color =
    if (category == EventCategory.INFO && severity == Severity.SAFE) GTextSecondary else severity.color()
