package com.raf.culinaryloop.presentation.settings

import androidx.compose.runtime.Immutable
import com.raf.culinaryloop.domain.model.UserSettings

/**
 * Immutable UI State representation for Settings and Preferences.
 */
@Immutable
data class SettingsUiState(
    val settings: UserSettings = UserSettings()
)
