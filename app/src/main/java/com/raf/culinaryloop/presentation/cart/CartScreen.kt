package com.raf.culinaryloop.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.TakeoutDining
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.raf.culinaryloop.R
import com.raf.culinaryloop.core.designsystem.CulinaryColors
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback
import com.raf.culinaryloop.core.designsystem.components.CulinaryButton
import com.raf.culinaryloop.core.designsystem.components.CulinaryCard
import com.raf.culinaryloop.core.designsystem.components.CulinaryTopBar
import com.raf.culinaryloop.core.designsystem.components.QuantityStepper
import com.raf.culinaryloop.domain.model.CartItem
import com.raf.culinaryloop.domain.model.CartItemId
import com.raf.culinaryloop.domain.model.Order
import com.raf.culinaryloop.domain.model.OrderChannel

/**
 * Screen presenting user shopping cart, ordering channels, cost breakdowns, and ESB simulation checkout.
 *
 * @param uiState State of the cart.
 * @param onQuantityChange Invoked on quantity adjustment.
 * @param onRemoveItem Invoked on line item deletion.
 * @param onChannelSelect Invoked on channel toggle.
 * @param onTableNumberChange Invoked on table input.
 * @param onSubmitOrder Commits simulated order to ESB backend.
 * @param onDismissSuccess Dialog dismiss callback.
 * @param onNavigateToMenu Navigates back to the catalog if empty.
 * @param modifier Composable modifier.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    uiState: CartUiState,
    onQuantityChange: (CartItemId, Int) -> Unit,
    onRemoveItem: (CartItemId) -> Unit,
    onChannelSelect: (OrderChannel) -> Unit,
    onTableNumberChange: (String) -> Unit,
    onSubmitOrder: () -> Unit,
    onDismissSuccess: () -> Unit,
    onNavigateToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalAppHapticFeedback.current
    val cart = uiState.cart
    val scrollBehavior = androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CulinaryTopBar(
                title = stringResource(R.string.title_cart),
                scrollBehavior = scrollBehavior,
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            if (!cart.isEmpty) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        CulinaryButton(
                            text = stringResource(R.string.btn_simulate_checkout),
                            onClick = {
                                haptics.performConfirm()
                                onSubmitOrder()
                            },
                            isLoading = uiState.isSubmitting,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (cart.isEmpty) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveShoppingCart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.title_empty_cart),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text(
                        text = stringResource(R.string.msg_empty_cart),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CulinaryButton(
                        text = stringResource(R.string.btn_browse_catalog),
                        onClick = {
                            haptics.performClick()
                            onNavigateToMenu()
                        },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Channel Selector
                item(key = "channel_selector") {
                    ChannelSelectorCard(
                        selectedChannel = cart.channel,
                        onChannelSelect = {
                            haptics.performToggle(true)
                            onChannelSelect(it)
                        }
                    )
                }

                // Table Number (for Dine-in)
                if (cart.channel == OrderChannel.DineIn) {
                    item(key = "table_number_input") {
                        OutlinedTextField(
                            value = cart.tableNumber,
                            onValueChange = onTableNumberChange,
                            label = { Text(stringResource(R.string.label_table_number)) },
                            placeholder = { Text(stringResource(R.string.hint_table_number)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.TableRestaurant, contentDescription = null)
                            },
                            shape = RoundedCornerShape(CulinaryDimensions.RadiusMedium),
                            modifier = Modifier
                                .fillMaxWidth()
                                .imePadding()
                        )
                    }
                }

                // Cart Line Items
                items(cart.items, key = { it.id.value }) { item ->
                    CartItemRow(
                        cartItem = item,
                        onQuantityChange = { qty -> onQuantityChange(item.id, qty) },
                        onRemove = {
                            haptics.performReject()
                            onRemoveItem(item.id)
                        }
                    )
                }

                // Order Breakdown
                item(key = "order_summary") {
                    OrderBreakdownCard(
                        subtotal = cart.subtotal,
                        tax = cart.tax,
                        serviceCharge = cart.serviceCharge,
                        total = cart.grandTotal
                    )
                }
            }
        }

        // Placed Order Success Modal
        if (uiState.placedOrder != null) {
            OrderSuccessDialog(
                order = uiState.placedOrder,
                onDismiss = onDismissSuccess
            )
        }
    }
}

@Composable
private fun ChannelSelectorCard(
    selectedChannel: OrderChannel,
    onChannelSelect: (OrderChannel) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(CulinaryDimensions.RadiusMedium),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ChannelTab(
                label = stringResource(R.string.label_channel_dine_in),
                icon = Icons.Default.Restaurant,
                isSelected = selectedChannel == OrderChannel.DineIn,
                onClick = { onChannelSelect(OrderChannel.DineIn) },
                modifier = Modifier.weight(1f)
            )
            ChannelTab(
                label = stringResource(R.string.label_channel_takeaway),
                icon = Icons.Default.TakeoutDining,
                isSelected = selectedChannel == OrderChannel.Takeaway,
                onClick = { onChannelSelect(OrderChannel.Takeaway) },
                modifier = Modifier.weight(1f)
            )
            ChannelTab(
                label = stringResource(R.string.label_channel_delivery),
                icon = Icons.Default.DeliveryDining,
                isSelected = selectedChannel == OrderChannel.Delivery,
                onClick = { onChannelSelect(OrderChannel.Delivery) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ChannelTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(CulinaryDimensions.RadiusSmall),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    CulinaryCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cartItem.menuItem.imageUrl,
                contentDescription = cartItem.menuItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(CulinaryDimensions.RadiusSmall))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (cartItem.selectedModifiers.isNotEmpty()) {
                    Text(
                        text = cartItem.selectedModifiers.joinToString(", ") { it.optionName },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (cartItem.notes.isNotBlank()) {
                    Text(
                        text = "\"${cartItem.notes}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = stringResource(
                        R.string.label_currency_format,
                        "%,d".format(cartItem.totalPrice).replace(',', '.')
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.cd_remove_item),
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                QuantityStepper(
                    quantity = cartItem.quantity,
                    onQuantityChange = onQuantityChange,
                    minQuantity = 0
                )
            }
        }
    }
}

@Composable
private fun OrderBreakdownCard(
    subtotal: Long,
    tax: Long,
    serviceCharge: Long,
    total: Long
) {
    Surface(
        shape = RoundedCornerShape(CulinaryDimensions.RadiusMedium),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.title_order_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(
                label = stringResource(R.string.label_subtotal),
                amount = subtotal
            )

            SummaryRow(
                label = stringResource(R.string.label_tax),
                amount = tax
            )

            SummaryRow(
                label = stringResource(R.string.label_service_charge),
                amount = serviceCharge
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        R.string.label_currency_format,
                        "%,d".format(total).replace(',', '.')
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(
                R.string.label_currency_format,
                "%,d".format(amount).replace(',', '.')
            ),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun OrderSuccessDialog(
    order: Order,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = CulinaryColors.StatusOpen,
                modifier = Modifier.size(54.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.title_order_success),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_order_id, order.referenceNumber),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.label_order_status, order.status.displayKey),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CulinaryColors.StatusOpen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = stringResource(
                        R.string.label_currency_format,
                        "%,d".format(order.total).replace(',', '.')
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        },
        confirmButton = {
            CulinaryButton(
                text = stringResource(R.string.btn_order_done),
                onClick = onDismiss
            )
        }
    )
}
