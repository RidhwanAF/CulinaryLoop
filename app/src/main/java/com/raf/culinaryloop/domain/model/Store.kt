package com.raf.culinaryloop.domain.model

/**
 * Strongly-typed store identifier.
 */
@JvmInline
value class StoreId(val value: String)

/**
 * Geographic coordinate pair.
 */
data class GeoCoordinate(
    val latitude: Double,
    val longitude: Double
)

/**
 * Supported F&B fulfillment channels for an outlet.
 */
sealed interface OrderChannel {
    val key: String

    data object DineIn : OrderChannel {
        override val key: String = "dine_in"
    }

    data object Takeaway : OrderChannel {
        override val key: String = "takeaway"
    }

    data object Delivery : OrderChannel {
        override val key: String = "delivery"
    }
}

/**
 * Business entity representing an ESB restaurant outlet branch.
 */
data class Store(
    val id: StoreId,
    val name: String,
    val brand: String,
    val address: String,
    val city: String,
    val coordinates: GeoCoordinate,
    val operatingHours: String,
    val isOpen: Boolean,
    val supportedChannels: List<OrderChannel>,
    val phone: String,
    val bannerUrl: String
)
