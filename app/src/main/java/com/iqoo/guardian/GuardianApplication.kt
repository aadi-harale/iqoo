package com.iqoo.guardian

import android.app.Application
import com.iqoo.guardian.data.repository.GuardianRepository

/**
 * Deliberately tiny service locator instead of a DI framework. One repository,
 * one lifetime, no graph to explain to a judge in two minutes.
 */
class GuardianApplication : Application() {

    val repository: GuardianRepository by lazy { GuardianRepository() }
}
