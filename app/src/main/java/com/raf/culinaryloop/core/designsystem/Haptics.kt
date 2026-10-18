package com.raf.culinaryloop.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Tactile Haptic Feedback contract supporting purpose-driven sensory responses.
 */
interface AppHapticFeedback {
    /**
     * Subtle tactile tick on button presses or menu clicks.
     */
    fun performClick()

    /**
     * Segment tick on quantity increment/decrement.
     */
    fun performQuantityChange()

    /**
     * Confirmation buzz on completing actions (Add to Cart, Order Placed).
     */
    fun performConfirm()

    /**
     * Rejection feedback on errors, validation failures, or out of stock items.
     */
    fun performReject()

    /**
     * Context click feedback when selecting map pins or branch items.
     */
    fun performPinSelect()

    /**
     * Toggle feedback when flipping switches or radio settings.
     */
    fun performToggle(isOn: Boolean)

    /**
     * Feedback when predictive back gesture crosses action threshold.
     */
    fun performThresholdPass()
}

/**
 * Implementation of [AppHapticFeedback] utilizing Compose's [HapticFeedback].
 */
class ComposeAppHapticFeedback(
    private val hapticFeedback: HapticFeedback,
    var isEnabled: Boolean = true
) : AppHapticFeedback {

    override fun performClick() {
        if (!isEnabled) return
        hapticFeedback.performHapticFeedback(HapticFeedbackType.VirtualKey)
    }

    override fun performQuantityChange() {
        if (!isEnabled) return
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
    }

    override fun performConfirm() {
        if (!isEnabled) return
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
    }

    override fun performReject() {
        if (!isEnabled) return
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
    }

    override fun performPinSelect() {
        if (!isEnabled) return
        hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
    }

    override fun performToggle(isOn: Boolean) {
        if (!isEnabled) return
        if (isOn) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
        } else {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
        }
    }

    override fun performThresholdPass() {
        if (!isEnabled) return
        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
    }
}

val LocalAppHapticFeedback = staticCompositionLocalOf<AppHapticFeedback> {
    object : AppHapticFeedback {
        override fun performClick() {}
        override fun performQuantityChange() {}
        override fun performConfirm() {}
        override fun performReject() {}
        override fun performPinSelect() {}
        override fun performToggle(isOn: Boolean) {}
        override fun performThresholdPass() {}
    }
}

/**
 * Composable helper returning the current active [AppHapticFeedback].
 */
@Composable
fun rememberAppHapticFeedback(enabled: Boolean = true): AppHapticFeedback {
    val composeHaptics = LocalHapticFeedback.current
    return ComposeAppHapticFeedback(composeHaptics, enabled)
}
