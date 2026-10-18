package com.raf.culinaryloop.data.model

import com.raf.culinaryloop.domain.model.GeoCoordinate
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.model.Store
import com.raf.culinaryloop.domain.model.StoreId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ESB API network transfer representation for a restaurant branch outlet.
 */
@Serializable
data class StoreDto(
    @SerialName("branch_id") val branchId: String,
    @SerialName("branch_name") val branchName: String,
    @SerialName("brand_name") val brandName: String,
    @SerialName("address") val address: String,
    @SerialName("city") val city: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("operating_hours") val operatingHours: String,
    @SerialName("is_open") val isOpen: Boolean,
    @SerialName("supported_channels") val supportedChannels: List<String>,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("banner_image_url") val bannerImageUrl: String
)

/**
 * Extension mapper transforming [StoreDto] into domain [Store].
 */
fun StoreDto.toDomain(): Store = Store(
    id = StoreId(branchId),
    name = branchName,
    brand = brandName,
    address = address,
    city = city,
    coordinates = GeoCoordinate(latitude, longitude),
    operatingHours = operatingHours,
    isOpen = isOpen,
    supportedChannels = supportedChannels.map { channelKey ->
        when (channelKey) {
            "takeaway" -> OrderChannel.Takeaway
            "delivery" -> OrderChannel.Delivery
            else -> OrderChannel.DineIn
        }
    },
    phone = phoneNumber,
    bannerUrl = bannerImageUrl
)
