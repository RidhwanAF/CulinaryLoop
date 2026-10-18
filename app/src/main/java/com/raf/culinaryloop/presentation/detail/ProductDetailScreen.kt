package com.raf.culinaryloop.presentation.detail

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.raf.culinaryloop.R
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback
import com.raf.culinaryloop.core.designsystem.components.CulinaryButton
import com.raf.culinaryloop.core.designsystem.components.ModifierChip
import com.raf.culinaryloop.core.designsystem.components.QuantityStepper
import com.raf.culinaryloop.domain.model.ModifierGroup
import com.raf.culinaryloop.domain.model.ModifierOption
import kotlinx.coroutines.CancellationException

/**
 * Screen presenting product detail hero image, custom modifier options, and tactile checkout commitment.
 * Integrates predictive back gesture scaling, corner radius morphing, and Compose SharedElement Transition.
 *
 * @param uiState State of the detail screen.
 * @param onBack Navigates back to the catalog.
 * @param onToggleModifier Invoked when a modifier option is checked.
 * @param onQuantityChange Invoked when line item quantity changes.
 * @param onNotesChange Invoked when special instructions are typed.
 * @param onAddToCart Invoked when adding product to shopping cart.
 * @param sharedTransitionScope Scope for shared element animation.
 * @param animatedVisibilityScope Visibility scope for shared transitions.
 * @param modifier Composable modifier.
 */
@Composable
fun ProductDetailScreen(
    uiState: ProductDetailUiState,
    onBack: () -> Unit,
    onToggleModifier: (ModifierGroup, ModifierOption) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onNotesChange: (String) -> Unit,
    onAddToCart: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val haptics = LocalAppHapticFeedback.current

    // Predictive Back Gesture Progress and Transformations
    var backProgress by remember { mutableFloatStateOf(0f) }
    var hasPassedThreshold by remember { mutableStateOf(false) }

    PredictiveBackHandler { progressFlow ->
        try {
            progressFlow.collect { backEvent ->
                backProgress = backEvent.progress
                if (backProgress > 0.45f && !hasPassedThreshold) {
                    hasPassedThreshold = true
                    haptics.performThresholdPass()
                } else if (backProgress <= 0.45f && hasPassedThreshold) {
                    hasPassedThreshold = false
                }
            }
            // Gesture completed
            haptics.performClick()
            onBack()
        } catch (_: CancellationException) {
            // Gesture cancelled
            backProgress = 0f
            hasPassedThreshold = false
        }
    }

    val predictiveScale = 1.0f - (backProgress * 0.08f)
    val predictiveRadius = (backProgress * 24.dp.value).dp
    val predictiveAlpha = 1.0f - (backProgress * 0.15f)

    val item = uiState.item
    val scrollState = rememberScrollState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(predictiveRadius),
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = predictiveScale
                scaleY = predictiveScale
                alpha = predictiveAlpha
                clip = true
                shape = RoundedCornerShape(predictiveRadius)
            }
    ) {
        if (uiState.isLoading || item == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Scaffold(
                bottomBar = {
                    // Sticky bottom action bar
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        shadowElevation = 16.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            QuantityStepper(
                                quantity = uiState.quantity,
                                onQuantityChange = onQuantityChange
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            val formattedPrice = stringResource(
                                R.string.label_currency_format,
                                "%,d".format(uiState.totalPrice).replace(',', '.')
                            )

                            CulinaryButton(
                                text = stringResource(R.string.btn_add_to_cart, formattedPrice),
                                onClick = {
                                    haptics.performConfirm()
                                    onAddToCart {
                                        onBack()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    // Hero Image with Overlaid Floating Back Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(CulinaryDimensions.HeroImageHeight)
                    ) {
                        val imageModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                            with(sharedTransitionScope) {
                                Modifier
                                    .fillMaxSize()
                                    .sharedElement(
                                        rememberSharedContentState(key = "image_${item.id.value}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                            }
                        } else {
                            Modifier.fillMaxSize()
                        }

                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = imageModifier
                        )

                        // Floating Circular Back Button
                        IconButton(
                            onClick = {
                                haptics.performClick()
                                onBack()
                            },
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(16.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_back),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Product Details Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        val titleModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                            with(sharedTransitionScope) {
                                Modifier.sharedBounds(
                                    rememberSharedContentState(key = "title_${item.id.value}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            }
                        } else {
                            Modifier
                        }

                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = titleModifier
                        )

                        Text(
                            text = stringResource(
                                R.string.label_currency_format,
                                "%,d".format(item.basePrice).replace(',', '.')
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        // Modifier Groups
                        item.modifierGroups.forEach { group ->
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = group.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                group.options.forEach { option ->
                                    val isSelected = uiState.selectedModifiers.any {
                                        it.groupId == group.id && it.optionId == option.id
                                    }
                                    ModifierChip(
                                        label = option.name,
                                        isSelected = isSelected,
                                        priceDelta = option.priceDelta,
                                        onClick = {
                                            onToggleModifier(group, option)
                                        }
                                    )
                                }
                            }
                        }

                        // Special Instructions
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.label_special_notes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = uiState.notes,
                            onValueChange = onNotesChange,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.hint_special_notes),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            shape = RoundedCornerShape(CulinaryDimensions.RadiusMedium),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .imePadding()
                        )
                    }
                }
            }
        }
    }
}
