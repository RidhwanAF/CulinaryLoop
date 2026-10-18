package com.raf.culinaryloop.data.model

import com.raf.culinaryloop.domain.model.CartItem
import com.raf.culinaryloop.domain.model.Order
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.model.OrderId
import com.raf.culinaryloop.domain.model.OrderStatus
import com.raf.culinaryloop.domain.model.StoreId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request payload for creating an order with the ESB gateway.
 */
@Serializable
data class OrderItemModifierRequest(
    @SerialName("group_id") val groupId: String,
    @SerialName("group_title") val groupTitle: String,
    @SerialName("option_id") val optionId: String,
    @SerialName("option_name") val optionName: String,
    @SerialName("price_delta") val priceDelta: Long
)

@Serializable
data class OrderItemRequest(
    @SerialName("item_id") val itemId: String,
    @SerialName("item_name") val itemName: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("unit_price") val unitPrice: Long,
    @SerialName("modifiers") val modifiers: List<OrderItemModifierRequest>,
    @SerialName("notes") val notes: String
)

@Serializable
data class CreateOrderRequestDto(
    @SerialName("branch_id") val branchId: String,
    @SerialName("order_channel") val orderChannel: String,
    @SerialName("table_number") val tableNumber: String,
    @SerialName("items") val items: List<OrderItemRequest>,
    @SerialName("subtotal") val subtotal: Long,
    @SerialName("tax_amount") val taxAmount: Long,
    @SerialName("service_charge_amount") val serviceChargeAmount: Long,
    @SerialName("grand_total") val grandTotal: Long
)

/**
 * Response returned from the ESB Order creation / status API.
 */
@Serializable
data class OrderResponseDto(
    @SerialName("order_id") val orderId: String,
    @SerialName("reference_number") val referenceNumber: String,
    @SerialName("branch_id") val branchId: String,
    @SerialName("branch_name") val branchName: String,
    @SerialName("status") val status: String,
    @SerialName("channel") val channel: String,
    @SerialName("table_number") val tableNumber: String,
    @SerialName("subtotal") val subtotal: Long,
    @SerialName("tax") val tax: Long,
    @SerialName("service_charge") val serviceCharge: Long,
    @SerialName("total") val total: Long,
    @SerialName("created_at_epoch_ms") val createdAtEpochMs: Long
)

fun OrderResponseDto.toDomain(items: List<CartItem>): Order = Order(
    id = OrderId(orderId),
    referenceNumber = referenceNumber,
    storeId = StoreId(branchId),
    storeName = branchName,
    items = items,
    subtotal = subtotal,
    tax = tax,
    serviceCharge = serviceCharge,
    total = total,
    status = when (status.uppercase()) {
        "CONFIRMED" -> OrderStatus.Confirmed
        "PREPARING" -> OrderStatus.Preparing
        "READY" -> OrderStatus.Ready
        "COMPLETED" -> OrderStatus.Completed
        "CANCELLED" -> OrderStatus.Cancelled
        else -> OrderStatus.Pending
    },
    channel = when (channel) {
        "takeaway" -> OrderChannel.Takeaway
        "delivery" -> OrderChannel.Delivery
        else -> OrderChannel.DineIn
    },
    tableNumber = tableNumber,
    createdAtEpochMs = createdAtEpochMs
)
