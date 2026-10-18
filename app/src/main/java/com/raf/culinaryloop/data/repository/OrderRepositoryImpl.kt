package com.raf.culinaryloop.data.repository

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DispatcherProvider
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.core.common.map
import com.raf.culinaryloop.data.datasource.EsbRemoteDataSource
import com.raf.culinaryloop.data.model.CreateOrderRequestDto
import com.raf.culinaryloop.data.model.OrderItemModifierRequest
import com.raf.culinaryloop.data.model.OrderItemRequest
import com.raf.culinaryloop.data.model.toDomain
import com.raf.culinaryloop.domain.model.Cart
import com.raf.culinaryloop.domain.model.Order
import com.raf.culinaryloop.domain.model.OrderId
import com.raf.culinaryloop.domain.repository.OrderRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [OrderRepository] handling ESB checkout payloads and status polling.
 */
@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val remoteDataSource: EsbRemoteDataSource,
    private val dispatchers: DispatcherProvider
) : OrderRepository {

    override suspend fun submitOrder(cart: Cart): AppResult<Order, DomainError> = withContext(dispatchers.io) {
        val branchId = cart.store?.id?.value ?: "esb_branch_001"
        val request = CreateOrderRequestDto(
            branchId = branchId,
            orderChannel = cart.channel.key,
            tableNumber = cart.tableNumber,
            items = cart.items.map { item ->
                OrderItemRequest(
                    itemId = item.menuItem.id.value,
                    itemName = item.menuItem.name,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    modifiers = item.selectedModifiers.map { mod ->
                        OrderItemModifierRequest(
                            groupId = mod.groupId,
                            groupTitle = mod.groupTitle,
                            optionId = mod.optionId,
                            optionName = mod.optionName,
                            priceDelta = mod.priceDelta
                        )
                    },
                    notes = item.notes
                )
            },
            subtotal = cart.subtotal,
            taxAmount = cart.tax,
            serviceChargeAmount = cart.serviceCharge,
            grandTotal = cart.grandTotal
        )

        remoteDataSource.submitOrder(request).map { responseDto ->
            responseDto.toDomain(cart.items)
        }
    }

    override suspend fun pollOrderStatus(orderId: OrderId): AppResult<Order, DomainError> =
        withContext(dispatchers.io) {
            remoteDataSource.getOrderStatus(orderId.value).map { responseDto ->
                responseDto.toDomain(emptyList())
            }
        }
}
