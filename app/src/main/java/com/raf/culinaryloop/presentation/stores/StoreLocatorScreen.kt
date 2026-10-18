package com.raf.culinaryloop.presentation.stores

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TakeoutDining
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.raf.culinaryloop.R
import com.raf.culinaryloop.core.designsystem.CulinaryColors
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback
import com.raf.culinaryloop.core.designsystem.LocalThemeIsDark
import com.raf.culinaryloop.core.designsystem.components.CulinaryButton
import com.raf.culinaryloop.core.designsystem.components.MapMarkerCallout
import com.raf.culinaryloop.domain.model.GeoCoordinate
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.model.Store
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.material3.Material3
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.overlay.include
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

/**
 * Screen presenting an interactive MapLibre map and bottom sheet for discovering ESB outlets.
 * Features edge-to-edge chips scrolling, responsive landscape adaptation, and runtime location permissions.
 *
 * @param uiState State of the store locator.
 * @param onSelectStore Invoked when an outlet is chosen.
 * @param onSearchQueryChange Invoked on query adjustment.
 * @param onNavigateToMenu Navigates to the menu catalog of the chosen store.
 * @param onUserLocationUpdated Updates the user's geocoordinates upon location retrieval.
 * @param onLocationPermissionGranted Updates permission state in the ViewModel.
 * @param calculateDistance Callback returning distance in km.
 * @param modifier Composable modifier.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreLocatorScreen(
    uiState: StoreLocatorUiState,
    onSelectStore: (Store) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToMenu: () -> Unit,
    onUserLocationUpdated: (Double, Double) -> Unit,
    onLocationPermissionGranted: (Boolean) -> Unit,
    calculateDistance: (Store) -> Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = LocalAppHapticFeedback.current
    val isDark = LocalThemeIsDark.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val mapStyleUrl = if (isDark) {
        "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
    } else {
        "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"
    }

    val selectedTarget = uiState.selectedStore?.coordinates ?: GeoCoordinate(-6.2297, 106.8112)

    val mapState = key(mapStyleUrl) {
        rememberMapState(
            baseStyle = BaseStyle.Uri(mapStyleUrl),
            initialCameraPosition = CameraPosition(
                center = Position(longitude = selectedTarget.longitude, latitude = selectedTarget.latitude),
                zoom = 13.5
            )
        )
    }

    // Check on startup if location permission was already granted and retrieve coordinates
    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            onLocationPermissionGranted(true)
            retrieveDeviceLocation(context) { lat, lng ->
                onUserLocationUpdated(lat, lng)
            }
        }
    }

    // Smoothly animate camera when a different store is selected
    LaunchedEffect(uiState.selectedStore) {
        uiState.selectedStore?.let { store ->
            mapState.animateCamera(
                org.maplibre.compose.camera.CameraUpdate(
                    center = Position(longitude = store.coordinates.longitude, latitude = store.coordinates.latitude),
                    zoom = 14.5
                )
            )
        }
    }

    val insets = WindowInsets.safeDrawing.asPaddingValues()
    val sheetPeekHeight = if (isLandscape) 100.dp else 210.dp

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    // Runtime location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            onLocationPermissionGranted(true)
            retrieveDeviceLocation(context) { lat, lng ->
                onUserLocationUpdated(lat, lng)
                scope.launch {
                    mapState.animateCamera(
                        org.maplibre.compose.camera.CameraUpdate(
                            center = Position(longitude = lng, latitude = lat),
                            zoom = 14.5
                        )
                    )
                }
            }
        }
    }

    fun requestAndLocateUser() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            onLocationPermissionGranted(true)
            retrieveDeviceLocation(context) { lat, lng ->
                onUserLocationUpdated(lat, lng)
                scope.launch {
                    mapState.animateCamera(
                        org.maplibre.compose.camera.CameraUpdate(
                            center = Position(longitude = lng, latitude = lat),
                            zoom = 14.5
                        )
                    )
                }
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = sheetPeekHeight,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetShadowElevation = 16.dp,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        sheetContent = {
            StoreBottomSheetContent(
                selectedStore = uiState.selectedStore,
                stores = uiState.filteredStores,
                isLandscape = isLandscape,
                calculateDistance = calculateDistance,
                onSelectStore = { store ->
                    haptics.performPinSelect()
                    onSelectStore(store)
                },
                onViewMenu = {
                    haptics.performConfirm()
                    onNavigateToMenu()
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // MapLibre Interactive Vector Map with correct viewportInsets and overlays
            MaplibreMap(
                state = mapState,
                viewportInsets = PaddingValues(
                    top = insets.calculateTopPadding() + 116.dp,
                    bottom = if (isLandscape) 100.dp else 226.dp
                ),
                modifier = Modifier.fillMaxSize(),
                overlay = {
                    include(MapOverlay.Material3)

                    // Render Store Outlets Pins & Callouts
                    uiState.filteredStores.forEach { store ->
                        val isSelected = uiState.selectedStore?.id == store.id
                        MapMarkerCallout(
                            storeName = store.name.substringAfter("CulinaryLoop "),
                            isOpen = store.isOpen,
                            isSelected = isSelected,
                            onClick = { onSelectStore(store) },
                            modifier = Modifier.placedAt(
                                position = Position(
                                    longitude = store.coordinates.longitude,
                                    latitude = store.coordinates.latitude
                                ),
                                alignment = Alignment.BottomCenter
                            )
                        )
                    }

                    // Render Current User Location Marker on the map
                    if (uiState.hasLocationPermission) {
                        UserLocationMarker(
                            modifier = Modifier.placedAt(
                                position = Position(
                                    longitude = uiState.userLongitude,
                                    latitude = uiState.userLatitude
                                ),
                                alignment = Alignment.Center
                            )
                        )
                    }
                }
            )

            // Top Search & Outlet Quick-Selector Header (Edge-to-Edge chips)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(CulinaryDimensions.RadiusMedium),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 6.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.hint_search_stores),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.cd_clear_search)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Quick horizontal outlet pills (Edge-to-Edge scroll without clipping)
                if (uiState.filteredStores.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredStores, key = { it.id.value }) { store ->
                            val isSelected = uiState.selectedStore?.id == store.id
                            Surface(
                                shape = RoundedCornerShape(CulinaryDimensions.RadiusPill),
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                                shadowElevation = 4.dp,
                                modifier = Modifier.clickable {
                                    haptics.performPinSelect()
                                    onSelectStore(store)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (store.isOpen) CulinaryColors.StatusOpen
                                                else CulinaryColors.StatusClosed
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = store.name.substringAfter("CulinaryLoop "),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating "Locate Me" Action Button with runtime permission request
            FloatingActionButton(
                onClick = {
                    haptics.performClick()
                    requestAndLocateUser()
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = if (isLandscape) 116.dp else 226.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.btn_locate_me)
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Animated Google/Apple Maps-style current location marker with pulsing radar halo and center core dot.
 *
 * @param modifier Composable modifier positioned via [org.maplibre.compose.overlay.MapOverlayScope.placedAt].
 */
@Composable
fun UserLocationMarker(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    val currentLocDesc = stringResource(R.string.cd_current_location)

    Box(
        modifier = modifier
            .size(48.dp)
            .semantics { contentDescription = currentLocDesc },
        contentAlignment = Alignment.Center
    ) {
        // Pulsing radar ripple ring
        Box(
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                    alpha = pulseAlpha
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        // Accuracy radius halo
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        )
        // Solid center core dot with crisp white border and drop shadow
        Box(
            modifier = Modifier
                .size(16.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(2.5.dp, Color.White, CircleShape)
        )
    }
}

@SuppressLint("MissingPermission")
private fun retrieveDeviceLocation(
    context: Context,
    onLocation: (Double, Double) -> Unit
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
    val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )
    for (provider in providers) {
        if (locationManager.isProviderEnabled(provider)) {
            val loc = locationManager.getLastKnownLocation(provider)
            if (loc != null) {
                onLocation(loc.latitude, loc.longitude)
                return
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> LocationManager.PASSIVE_PROVIDER
        }
        locationManager.getCurrentLocation(
            provider,
            null,
            ContextCompat.getMainExecutor(context)
        ) { loc ->
            if (loc != null) {
                onLocation(loc.latitude, loc.longitude)
            }
        }
    }
}

@Composable
private fun StoreBottomSheetContent(
    selectedStore: Store?,
    stores: List<Store>,
    isLandscape: Boolean,
    calculateDistance: (Store) -> Double,
    onSelectStore: (Store) -> Unit,
    onViewMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        if (selectedStore != null) {
            val distanceKm = calculateDistance(selectedStore)

            if (isLandscape) {
                // Compact row representation for landscape mode to prevent covering the map
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedStore.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(CulinaryDimensions.RadiusSmall),
                                color = if (selectedStore.isOpen) CulinaryColors.StatusOpen.copy(alpha = 0.15f)
                                else CulinaryColors.StatusClosed.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (selectedStore.isOpen) stringResource(R.string.label_status_open)
                                    else stringResource(R.string.label_status_closed),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedStore.isOpen) CulinaryColors.StatusOpen
                                    else CulinaryColors.StatusClosed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${stringResource(R.string.label_distance_km, distanceKm)} • ${selectedStore.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    CulinaryButton(
                        text = stringResource(R.string.btn_view_menu),
                        onClick = onViewMenu,
                        modifier = Modifier.width(140.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedStore.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // Open/Closed badge
                    Surface(
                        shape = RoundedCornerShape(CulinaryDimensions.RadiusSmall),
                        color = if (selectedStore.isOpen) CulinaryColors.StatusOpen.copy(alpha = 0.15f)
                        else CulinaryColors.StatusClosed.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (selectedStore.isOpen) stringResource(R.string.label_status_open)
                            else stringResource(R.string.label_status_closed),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedStore.isOpen) CulinaryColors.StatusOpen
                            else CulinaryColors.StatusClosed,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Address and distance
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${stringResource(R.string.label_distance_km, distanceKm)} • ${selectedStore.address}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Operating Hours
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.label_operating_hours, selectedStore.operatingHours),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Active Channels
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedStore.supportedChannels.forEach { channel ->
                        val (labelRes, icon) = when (channel) {
                            OrderChannel.DineIn -> R.string.label_channel_dine_in to Icons.Default.Restaurant
                            OrderChannel.Takeaway -> R.string.label_channel_takeaway to Icons.Default.TakeoutDining
                            OrderChannel.Delivery -> R.string.label_channel_delivery to Icons.Default.DeliveryDining
                        }
                        Surface(
                            shape = RoundedCornerShape(CulinaryDimensions.RadiusPill),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(labelRes),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Button to browse catalog
                CulinaryButton(
                    text = stringResource(R.string.btn_view_menu),
                    onClick = onViewMenu,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
