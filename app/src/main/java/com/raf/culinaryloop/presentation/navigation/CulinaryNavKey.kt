package com.raf.culinaryloop.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation key destinations for Navigation 3.
 */
sealed interface CulinaryNavKey {
    @Serializable
    data object Stores : CulinaryNavKey

    @Serializable
    data object Menu : CulinaryNavKey

    @Serializable
    data class ProductDetail(val itemId: String) : CulinaryNavKey

    @Serializable
    data object Cart : CulinaryNavKey

    @Serializable
    data object Settings : CulinaryNavKey
}
