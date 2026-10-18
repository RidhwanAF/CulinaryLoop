package com.raf.culinaryloop.domain.model

/**
 * Strongly-typed cart line item identifier.
 */
@JvmInline
value class CartItemId(val value: String)

/**
 * Selected modifier option linked to a cart item.
 */
data class SelectedModifier(
    val groupId: String,
    val groupTitle: String,
    val optionId: String,
    val optionName: String,
    val priceDelta: Long
)

/**
 * Line item in user's active shopping cart.
 */
data class CartItem(
    val id: CartItemId,
    val menuItem: MenuItem,
    val quantity: Int,
    val selectedModifiers: List<SelectedModifier> = emptyList(),
    val notes: String = ""
) {
    val unitPrice: Long get() = menuItem.basePrice + selectedModifiers.sumOf { it.priceDelta }
    val totalPrice: Long get() = unitPrice * quantity
}

/**
 * Aggregated cart containing current outlet and order items.
 */
data class Cart(
    val store: Store? = null,
    val items: List<CartItem> = emptyList(),
    val channel: OrderChannel = OrderChannel.DineIn,
    val tableNumber: String = ""
) {
    val subtotal: Long get() = items.sumOf { it.totalPrice }
    val tax: Long get() = (subtotal * 10) / 100 // 10% PB1 Restaurant Tax
    val serviceCharge: Long get() = (subtotal * 5) / 100 // 5% Service Charge
    val grandTotal: Long get() = subtotal + tax + serviceCharge
    val totalItemCount: Int get() = items.sumOf { it.quantity }
    val isEmpty: Boolean get() = items.isEmpty()
}
