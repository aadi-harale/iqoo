package com.iqoo.guardian.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.iqoo.guardian.data.repository.GuardianRepository
import com.iqoo.guardian.domain.model.PrivacySettings
import kotlinx.coroutines.launch

/**
 * App-wide state holder. Screens read flows from here and never touch the
 * repository directly, so there is exactly one place demo state can change.
 */
class GuardianViewModel(private val repository: GuardianRepository) : ViewModel() {

    val score = repository.score
    val events = repository.events
    val snapshot = repository.snapshot
    val privacy = repository.privacy
    val protection = repository.protection
    val presentationMode = repository.presentationMode

    init {
        viewModelScope.launch { repository.seedHistory() }
    }

    fun eventById(id: String) = repository.eventById(id)

    fun blockEvent(eventId: String) = repository.blockEvent(eventId)

    fun dismissEvent(eventId: String) = repository.dismissEvent(eventId)

    fun setPrivacy(transform: (PrivacySettings) -> PrivacySettings) =
        repository.setPrivacySetting(transform)

    fun setPresentationMode(enabled: Boolean) = repository.setPresentationMode(enabled)

    fun clearAlerts() = repository.clearAlerts()

    fun resetDemo() {
        viewModelScope.launch { repository.resetDemo() }
    }

    class Factory(private val repository: GuardianRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GuardianViewModel(repository) as T
    }
}
