package com.raf.culinaryloop.domain.usecase

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.domain.model.Cart
import com.raf.culinaryloop.domain.model.Order
import com.raf.culinaryloop.domain.model.OrderId
import com.raf.culinaryloop.domain.repository.OrderRepository
import javax.inject.Inject

/**
 * UC-04: Submits simulated order to the ESB API backend.
 */
class SubmitOrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(cart: Cart): AppResult<Order, DomainError> =
        if (cart.isEmpty) {
            AppResult.Failure(DomainError.Validation("Cart is empty"))
        } else {
            repository.submitOrder(cart)
        }
}

/**
 * UC-04: Polls order transaction status from ESB gateway.
 */
class PollOrderStatusUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(orderId: OrderId): AppResult<Order, DomainError> =
        repository.pollOrderStatus(orderId)
}
