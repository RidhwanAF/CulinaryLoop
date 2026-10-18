package com.raf.culinaryloop.domain.repository

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.domain.model.Cart
import com.raf.culinaryloop.domain.model.Order
import com.raf.culinaryloop.domain.model.OrderId

/**
 * Domain boundary handling order dispatch and simulated ESB transaction polling.
 */
interface OrderRepository {
    /**
     * Submits an order payload to the ESB API gateway.
     */
    suspend fun submitOrder(cart: Cart): AppResult<Order, DomainError>

    /**
     * Queries status updates for a submitted order by [orderId].
     */
    suspend fun pollOrderStatus(orderId: OrderId): AppResult<Order, DomainError>
}
