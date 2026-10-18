package com.raf.culinaryloop.domain.repository

import com.raf.culinaryloop.domain.model.AppLocale
import com.raf.culinaryloop.domain.model.ThemeMode
import com.raf.culinaryloop.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Domain boundary managing user application settings and preferences.
 */
interface SettingsRepository {
    /**
     * Observes real-time user settings changes.
     */
    fun observeSettings(): Flow<UserSettings>

    /**
     * Updates UI theme mode.
     */
    suspend fun setThemeMode(themeMode: ThemeMode)

    /**
     * Updates per-app language locale.
     */
    suspend fun setAppLocale(locale: AppLocale)

    /**
     * Toggles Material You dynamic wallpaper coloration.
     */
    suspend fun setDynamicColor(enabled: Boolean)

    /**
     * Toggles tactile sensory feedback.
     */
    suspend fun setHapticFeedback(enabled: Boolean)
}
