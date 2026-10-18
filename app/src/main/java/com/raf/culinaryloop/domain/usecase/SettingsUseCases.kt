package com.raf.culinaryloop.domain.usecase

import com.raf.culinaryloop.domain.model.AppLocale
import com.raf.culinaryloop.domain.model.ThemeMode
import com.raf.culinaryloop.domain.model.UserSettings
import com.raf.culinaryloop.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UC-05: Observes real-time user preferences.
 */
class ObserveUserSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> = repository.observeSettings()
}

/**
 * UC-05: Updates UI theme selection.
 */
class UpdateThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(themeMode: ThemeMode) = repository.setThemeMode(themeMode)
}

/**
 * UC-05: Updates per-app language selection.
 */
class UpdateAppLocaleUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(locale: AppLocale) = repository.setAppLocale(locale)
}

/**
 * UC-05: Toggles dynamic wallpaper coloring.
 */
class ToggleDynamicColorUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDynamicColor(enabled)
}

/**
 * UC-05: Toggles tactile haptic feedback.
 */
class ToggleHapticsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setHapticFeedback(enabled)
}
