package com.raf.culinaryloop.presentation.detail

import androidx.compose.runtime.Immutable
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.SelectedModifier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Immutable UI State for Product Detail screen.
 */
@Immutable
data class ProductDetailUiState(
    val item: MenuItem? = null,
    val quantity: Int = 1,
    val selectedModifiers: ImmutableList<SelectedModifier> = persistentListOf(),
    val notes: String = "",
    val unitPrice: Long = 0L,
    val totalPrice: Long = 0L,
    val isLoading: Boolean = false,
    val isAddedToCart: Boolean = false,
    val validationError: String? = null
)
