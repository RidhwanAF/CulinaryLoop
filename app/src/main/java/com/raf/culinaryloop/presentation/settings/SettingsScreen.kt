package com.raf.culinaryloop.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.raf.culinaryloop.R
import com.raf.culinaryloop.core.common.AppLocaleHelper
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback
import com.raf.culinaryloop.core.designsystem.components.CulinaryCard
import com.raf.culinaryloop.core.designsystem.components.CulinaryTopBar
import com.raf.culinaryloop.domain.model.AppLocale
import com.raf.culinaryloop.domain.model.ThemeMode

/**
 * Screen presenting user preference controls for Theme mode, per-app language, dynamic color, and haptics.
 *
 * @param uiState State of settings.
 * @param onThemeModeChange Invoked on theme toggle.
 * @param onLocaleChange Invoked on language toggle.
 * @param onToggleDynamicColor Invoked on dynamic color toggle.
 * @param onToggleHaptics Invoked on haptic toggle.
 * @param modifier Composable modifier.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit,
    onLocaleChange: (AppLocale) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalAppHapticFeedback.current
    val settings = uiState.settings
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val activeLocale = remember(configuration) {
        AppLocaleHelper.getCurrentLocale(context)
    }
    val scrollBehavior = androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CulinaryTopBar(
                title = stringResource(R.string.title_settings),
                scrollBehavior = scrollBehavior,
                modifier = Modifier.statusBarsPadding()
            )
        },
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Appearance & Theme
            item(key = "appearance_section") {
                SectionHeader(
                    title = stringResource(R.string.header_appearance),
                    icon = Icons.Default.Palette
                )
                Spacer(modifier = Modifier.height(8.dp))
                CulinaryCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.label_theme_mode),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RadioSettingItem(
                            label = stringResource(R.string.theme_follow_system),
                            isSelected = settings.themeMode == ThemeMode.FollowSystem,
                            onClick = {
                                haptics.performToggle(true)
                                onThemeModeChange(ThemeMode.FollowSystem)
                            }
                        )

                        RadioSettingItem(
                            label = stringResource(R.string.theme_light),
                            isSelected = settings.themeMode == ThemeMode.Light,
                            onClick = {
                                haptics.performToggle(true)
                                onThemeModeChange(ThemeMode.Light)
                            }
                        )

                        RadioSettingItem(
                            label = stringResource(R.string.theme_dark),
                            isSelected = settings.themeMode == ThemeMode.Dark,
                            onClick = {
                                haptics.performToggle(true)
                                onThemeModeChange(ThemeMode.Dark)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic Color Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.label_dynamic_color),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.desc_dynamic_color),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settings.isDynamicColor,
                                onCheckedChange = {
                                    haptics.performToggle(it)
                                    onToggleDynamicColor(it)
                                }
                            )
                        }
                    }
                }
            }

            // Section 2: Localization (Per-App Language)
            item(key = "localization_section") {
                SectionHeader(
                    title = stringResource(R.string.header_localization),
                    icon = Icons.Default.Language
                )
                Spacer(modifier = Modifier.height(8.dp))
                CulinaryCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.label_language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RadioSettingItem(
                            label = stringResource(R.string.lang_system),
                            isSelected = activeLocale == AppLocale.SystemDefault,
                            onClick = {
                                haptics.performToggle(true)
                                onLocaleChange(AppLocale.SystemDefault)
                            }
                        )

                        RadioSettingItem(
                            label = stringResource(R.string.lang_english),
                            isSelected = activeLocale == AppLocale.English,
                            onClick = {
                                haptics.performToggle(true)
                                onLocaleChange(AppLocale.English)
                            }
                        )

                        RadioSettingItem(
                            label = stringResource(R.string.lang_indonesian),
                            isSelected = activeLocale == AppLocale.Indonesian,
                            onClick = {
                                haptics.performToggle(true)
                                onLocaleChange(AppLocale.Indonesian)
                            }
                        )
                    }
                }
            }

            // Section 3: Tactile Sensory Haptics
            item(key = "haptics_section") {
                SectionHeader(
                    title = stringResource(R.string.header_haptics),
                    icon = Icons.Default.Vibration
                )
                Spacer(modifier = Modifier.height(8.dp))
                CulinaryCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.label_enable_haptics),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.desc_enable_haptics),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.isHapticEnabled,
                            onCheckedChange = {
                                haptics.performToggle(it)
                                onToggleHaptics(it)
                            }
                        )
                    }
                }
            }

            // Section 4: ESB Platform Info
            item(key = "platform_section") {
                SectionHeader(
                    title = stringResource(R.string.header_platform),
                    icon = Icons.Default.Info
                )
                Spacer(modifier = Modifier.height(8.dp))
                CulinaryCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.label_esb_environment),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.desc_esb_environment),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.label_app_version),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun RadioSettingItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CulinaryDimensions.RadiusSmall))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
