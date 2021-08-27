package dev.kelocen.loadstatus.button

/**
 * A sealed class for the [LoadingButton] state.
 */
sealed class ButtonState {
    object Reset : ButtonState()
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}