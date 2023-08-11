package jp.kaleidot725.texteditor.view

import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.KeyEvent.KEYCODE_TAB
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import jp.kaleidot725.texteditor.state.TextFieldState

@Composable
internal fun TextField(
    textFieldState: TextFieldState,
    enabled: Boolean,
    focusRequester: FocusRequester,
    onUpdateText: (TextFieldValue) -> Unit,
    onContainNewLine: (TextFieldValue) -> Unit,
    onAddNewLine: (TextFieldValue) -> Unit,
    onDeleteNewLine: () -> Unit,
    onFocus: () -> Unit,
    onUpFocus: () -> Unit,
    onDownFocus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentTextField by rememberUpdatedState(newValue = textFieldState.value)

    LaunchedEffect(textFieldState.isSelected) {
        if (textFieldState.isSelected) {
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        value = textFieldState.value,
        enabled = enabled,
        onValueChange = {
            if (currentTextField == it) return@BasicTextField
            if (it.text.contains('\n')) onContainNewLine(it) else onUpdateText(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .focusTarget()
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) onFocus() }
            .onPreviewKeyEvent { event ->
                val value = textFieldState.value
                val selection = currentTextField.selection

                val b1 = onPreviewDelKeyEvent(event, selection) { onDeleteNewLine() }
                if (b1) return@onPreviewKeyEvent true

                val b2 = onPreviewDownKeyEvent(event, value) { onDownFocus() }
                if (b2) return@onPreviewKeyEvent true

                val b3 = onPreviewUpKeyEvent(event, selection) { onUpFocus() }
                if (b3) return@onPreviewKeyEvent true

                val b4 = onPreviewEnterKeyEvent(event) { onAddNewLine(currentTextField) }
                if (b4) return@onPreviewKeyEvent true

                val b5 = onPreviewTabKeyEvent(event) { onDownFocus() }
                if (b5) return@onPreviewKeyEvent true

                false
            }
    )
}

private fun onPreviewDelKeyEvent(
    event: KeyEvent,
    selection: TextRange,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    if (!isKeyDown) return false

    val isDelKey = event.nativeKeyEvent.keyCode == KEYCODE_DEL
    if (!isDelKey) return false

    val isEmpty = selection == TextRange.Zero
    if (!isEmpty) return false

    invoke()
    return true
}

private fun onPreviewUpKeyEvent(
    event: KeyEvent,
    selection: TextRange,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    if (!isKeyDown) return false

    val isUpKey = event.nativeKeyEvent.keyCode == KEYCODE_DPAD_UP
    if (!isUpKey) return false

    val isEmpty = selection == TextRange.Zero
    if (!isEmpty) return false

    invoke()
    return true
}

private fun onPreviewDownKeyEvent(
    event: KeyEvent,
    value: TextFieldValue,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    if (!isKeyDown) return false

    val isDownKey = event.nativeKeyEvent.keyCode == KEYCODE_DPAD_DOWN
    if (!isDownKey) return false

    val isEmpty = value.selection == TextRange(value.text.count())
    if (!isEmpty) return false

    invoke()
    return true
}

private fun onPreviewTabKeyEvent(
    event: KeyEvent,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    if (!isKeyDown) return false

    val isTabKey = event.nativeKeyEvent.keyCode == KEYCODE_TAB
    if (!isTabKey) return false

    invoke()
    return true
}

private fun onPreviewEnterKeyEvent(
    event: KeyEvent,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    if (!isKeyDown) return false

    val isEnterKey = event.nativeKeyEvent.keyCode == KEYCODE_ENTER
    if (!isEnterKey) return false

    invoke()
    return true
}
