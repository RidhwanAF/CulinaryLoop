package com.raf.culinaryloop.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.culinaryloop.domain.model.AppLocale
import com.raf.culinaryloop.domain.model.ThemeMode
import com.raf.culinaryloop.domain.usecase.ObserveUserSettingsUseCase
import com.raf.culinaryloop.domain.usecase.ToggleDynamicColorUseCase
import com.raf.culinaryloop.domain.usecase.ToggleHapticsUseCase
import com.raf.culinaryloop.domain.usecase.UpdateAppLocaleUseCase
import com.raf.culinaryloop.domain.usecase.UpdateThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel orchestrating runtime theme mode switches, per-app language localization, and haptic preferences.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val updateThemeModeUseCase: UpdateThemeModeUseCase,
    private val updateAppLocaleUseCase: UpdateAppLocaleUseCase,
    private val toggleDynamicColorUseCase: ToggleDynamicColorUseCase,
    private val toggleHapticsUseCase: ToggleHapticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeUserSettingsUseCase().collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun onThemeModeChange(themeMode: ThemeMode) {
        viewModelScope.launch {
            applyNightMode(themeMode)
            updateThemeModeUseCase(themeMode)
        }
    }

    private fun applyNightMode(themeMode: ThemeMode) {
        val mode = when (themeMode) {
            ThemeMode.FollowSystem -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            ThemeMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
        }
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    fun onAppLocaleChange(appLocale: AppLocale) {
        viewModelScope.launch {
            updateAppLocaleUseCase(appLocale)
            _uiState.update { it.copy(settings = it.settings.copy(appLocale = appLocale)) }
        }
    }

    fun onToggleDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            toggleDynamicColorUseCase(enabled)
        }
    }

    fun onToggleHaptics(enabled: Boolean) {
        viewModelScope.launch {
            toggleHapticsUseCase(enabled)
        }
    }
}
