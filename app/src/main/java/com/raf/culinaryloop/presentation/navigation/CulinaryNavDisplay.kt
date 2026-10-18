package com.raf.culinaryloop.presentation.navigation

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.raf.culinaryloop.R
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback
import com.raf.culinaryloop.presentation.cart.CartScreen
import com.raf.culinaryloop.presentation.cart.CartViewModel
import com.raf.culinaryloop.presentation.detail.ProductDetailScreen
import com.raf.culinaryloop.presentation.detail.ProductDetailViewModel
import com.raf.culinaryloop.presentation.menu.MenuCatalogScreen
import com.raf.culinaryloop.presentation.menu.MenuCatalogViewModel
import com.raf.culinaryloop.presentation.settings.SettingsScreen
import com.raf.culinaryloop.presentation.settings.SettingsViewModel
import com.raf.culinaryloop.presentation.stores.StoreLocatorScreen
import com.raf.culinaryloop.presentation.stores.StoreLocatorViewModel

/**
 * Root Navigation host managing type-safe destinations, backstack restoration,
 * hierarchical back navigation to Outlets parent, SharedElement transitions,
 * and adaptive landscape navigation rail.
 *
 * @param modifier Composable modifier.
 */
@Composable
fun CulinaryNavDisplay(
    modifier: Modifier = Modifier,
    storeViewModel: StoreLocatorViewModel = hiltViewModel(),
    menuViewModel: MenuCatalogViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    detailViewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val haptics = LocalAppHapticFeedback.current
    val backStack = remember { mutableStateListOf<CulinaryNavKey>(CulinaryNavKey.Stores) }
    val currentDestination = backStack.lastOrNull() ?: CulinaryNavKey.Stores

    val storeUiState by storeViewModel.uiState.collectAsStateWithLifecycle()
    val menuUiState by menuViewModel.uiState.collectAsStateWithLifecycle()
    val cartUiState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isTopLevelDestination = currentDestination is CulinaryNavKey.Stores ||
            currentDestination is CulinaryNavKey.Menu ||
            currentDestination is CulinaryNavKey.Cart ||
            currentDestination is CulinaryNavKey.Settings

    fun navigateToTab(target: CulinaryNavKey) {
        haptics.performClick()
        if (currentDestination == target) return
        backStack.clear()
        backStack.add(CulinaryNavKey.Stores)
        if (target !is CulinaryNavKey.Stores) {
            backStack.add(target)
        }
    }

    // Hierarchical back handling: Outlets is root parent; pressing back from Menu, Cart, Settings returns to Outlets
    BackHandler(enabled = backStack.size == 1 && currentDestination !is CulinaryNavKey.Stores) {
        backStack.clear()
        backStack.add(CulinaryNavKey.Stores)
    }

    @Composable
    fun NavContent() {
        SharedTransitionLayout {
            NavDisplay(
                backStack = backStack,
                modifier = Modifier.fillMaxSize(),
                sharedTransitionScope = this,
                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.lastIndex)
                    } else if (backStack.lastOrNull() !is CulinaryNavKey.Stores) {
                        backStack.clear()
                        backStack.add(CulinaryNavKey.Stores)
                    }
                },
                entryProvider = entryProvider {
                    entry<CulinaryNavKey.Stores> {
                        StoreLocatorScreen(
                            uiState = storeUiState,
                            onSelectStore = storeViewModel::onSelectStore,
                            onSearchQueryChange = storeViewModel::onSearchQueryChange,
                            onNavigateToMenu = {
                                navigateToTab(CulinaryNavKey.Menu)
                            },
                            onUserLocationUpdated = storeViewModel::onUserLocationUpdated,
                            onLocationPermissionGranted = storeViewModel::onLocationPermissionGranted,
                            calculateDistance = storeViewModel::calculateDistanceKm
                        )
                    }
                    entry<CulinaryNavKey.Menu> {
                        val localNavAnimatedContentScope = LocalNavAnimatedContentScope.current
                        MenuCatalogScreen(
                            uiState = menuUiState,
                            onSelectCategory = menuViewModel::onCategorySelected,
                            onSearchQueryChange = menuViewModel::onSearchQueryChanged,
                            onNavigateToDetail = { itemId ->
                                detailViewModel.loadItem(itemId)
                                backStack.add(CulinaryNavKey.ProductDetail(itemId))
                            },
                            onNavigateToCart = {
                                navigateToTab(CulinaryNavKey.Cart)
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = localNavAnimatedContentScope
                        )
                    }
                    entry<CulinaryNavKey.ProductDetail> {
                        val localNavAnimatedContentScope = LocalNavAnimatedContentScope.current
                        ProductDetailScreen(
                            uiState = detailUiState,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.removeAt(backStack.lastIndex)
                                } else {
                                    backStack.clear()
                                    backStack.add(CulinaryNavKey.Menu)
                                }
                            },
                            onToggleModifier = detailViewModel::onToggleModifier,
                            onQuantityChange = detailViewModel::onQuantityChange,
                            onNotesChange = detailViewModel::onNotesChange,
                            onAddToCart = detailViewModel::onAddToCart,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = localNavAnimatedContentScope
                        )
                    }
                    entry<CulinaryNavKey.Cart> {
                        CartScreen(
                            uiState = cartUiState,
                            onQuantityChange = cartViewModel::onQuantityChange,
                            onRemoveItem = cartViewModel::onRemoveItem,
                            onChannelSelect = cartViewModel::onChannelSelected,
                            onTableNumberChange = cartViewModel::onTableNumberChange,
                            onSubmitOrder = cartViewModel::onSubmitOrder,
                            onDismissSuccess = cartViewModel::onDismissOrderSuccess,
                            onNavigateToMenu = {
                                navigateToTab(CulinaryNavKey.Menu)
                            }
                        )
                    }
                    entry<CulinaryNavKey.Settings> {
                        SettingsScreen(
                            uiState = settingsUiState,
                            onThemeModeChange = settingsViewModel::onThemeModeChange,
                            onLocaleChange = settingsViewModel::onAppLocaleChange,
                            onToggleDynamicColor = settingsViewModel::onToggleDynamicColor,
                            onToggleHaptics = settingsViewModel::onToggleHaptics
                        )
                    }
                }
            )
        }
    }

    if (isLandscape && isTopLevelDestination) {
        Row(modifier = modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                // Outlets Tab
                NavigationRailItem(
                    selected = currentDestination is CulinaryNavKey.Stores,
                    onClick = { navigateToTab(CulinaryNavKey.Stores) },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_stores)) }
                )

                // Menu Tab
                NavigationRailItem(
                    selected = currentDestination is CulinaryNavKey.Menu,
                    onClick = { navigateToTab(CulinaryNavKey.Menu) },
                    icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_menu)) }
                )

                // Cart Tab
                NavigationRailItem(
                    selected = currentDestination is CulinaryNavKey.Cart,
                    onClick = { navigateToTab(CulinaryNavKey.Cart) },
                    icon = {
                        if (cartUiState.cart.totalItemCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(cartUiState.cart.totalItemCount.toString())
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            }
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        }
                    },
                    label = { Text(stringResource(R.string.nav_cart)) }
                )

                // Settings Tab
                NavigationRailItem(
                    selected = currentDestination is CulinaryNavKey.Settings,
                    onClick = { navigateToTab(CulinaryNavKey.Settings) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_settings)) }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                NavContent()
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                if (isTopLevelDestination) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        // Outlets Tab
                        NavigationBarItem(
                            selected = currentDestination is CulinaryNavKey.Stores,
                            onClick = { navigateToTab(CulinaryNavKey.Stores) },
                            icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_stores)) }
                        )

                        // Menu Tab
                        NavigationBarItem(
                            selected = currentDestination is CulinaryNavKey.Menu,
                            onClick = { navigateToTab(CulinaryNavKey.Menu) },
                            icon = {
                                Icon(
                                    Icons.Default.RestaurantMenu,
                                    contentDescription = null
                                )
                            },
                            label = { Text(stringResource(R.string.nav_menu)) }
                        )

                        // Cart Tab
                        NavigationBarItem(
                            selected = currentDestination is CulinaryNavKey.Cart,
                            onClick = { navigateToTab(CulinaryNavKey.Cart) },
                            icon = {
                                if (cartUiState.cart.totalItemCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge {
                                                Text(cartUiState.cart.totalItemCount.toString())
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                    }
                                } else {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                }
                            },
                            label = { Text(stringResource(R.string.nav_cart)) }
                        )

                        // Settings Tab
                        NavigationBarItem(
                            selected = currentDestination is CulinaryNavKey.Settings,
                            onClick = { navigateToTab(CulinaryNavKey.Settings) },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_settings)) }
                        )
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (isTopLevelDestination) innerPadding.calculateBottomPadding() else 0.dp)
            ) {
                NavContent()
            }
        }
    }
}
