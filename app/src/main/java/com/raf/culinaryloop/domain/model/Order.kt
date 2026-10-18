package com.raf.culinaryloop.domain.model

/**
 * Strongly-typed order identifier.
 */
@JvmInline
value class OrderId(val value: String)

/**
 * Lifecycle states of an ESB F&B order.
 */
sealed interface OrderStatus {
    val displayKey: String

    data object Pending : OrderStatus {
        override val displayKey: String = "PENDING"
    }

    data object Confirmed : OrderStatus {
        override val displayKey: String = "CONFIRMED"
    }

    data object Preparing : OrderStatus {
        override val displayKey: String = "PREPARING"
    }

    data object Ready : OrderStatus {
        override val displayKey: String = "READY"
    }

    data object Completed : OrderStatus {
        override val displayKey: String = "COMPLETED"
    }

    data object Cancelled : OrderStatus {
        override val displayKey: String = "CANCELLED"
    }
}

/**
 * Business entity representing a submitted ESB order.
 */
data class Order(
    val id: OrderId,
    val referenceNumber: String,
    val storeId: StoreId,
    val storeName: String,
    val items: List<CartItem>,
    val subtotal: Long,
    val tax: Long,
    val serviceCharge: Long,
    val total: Long,
    val status: OrderStatus,
    val channel: OrderChannel,
    val tableNumber: String,
    val createdAtEpochMs: Long
)
