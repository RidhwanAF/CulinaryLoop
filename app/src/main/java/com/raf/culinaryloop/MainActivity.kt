package com.raf.culinaryloop

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raf.culinaryloop.core.datastore.UserPreferencesDataSource
import com.raf.culinaryloop.core.designsystem.CulinaryLoopTheme
import com.raf.culinaryloop.domain.model.UserSettings
import com.raf.culinaryloop.presentation.navigation.CulinaryNavDisplay
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main application host activity enabling edge-to-edge layout, dynamic theme switching,
 * and bridging AppCompat per-app locale selection.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferencesDataSource: UserPreferencesDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userSettings by userPreferencesDataSource.userSettingsFlow
                .collectAsStateWithLifecycle(initialValue = UserSettings())

            CulinaryLoopTheme(
                themeMode = userSettings.themeMode,
                dynamicColor = userSettings.isDynamicColor,
                isHapticEnabled = userSettings.isHapticEnabled
            ) {
                CulinaryNavDisplay()
            }
        }
    }
}