package jp.kaleidot725.texteditor.view

import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
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
    modifier: Modifier = Modifier
) {
    val currentTextField by rememberUpdatedState(newValue = textFieldState.value)

    LaunchedEffect(textFieldState.isSelected) {
        if (textFieldState.isSelected) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
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

                val b4 = onPreviewEnterKeyEvent(event, selection) { onAddNewLine(currentTextField) }
                if (b4) return@onPreviewKeyEvent true

                false
            }
    ) {
        BasicTextField(
            value = textFieldState.value,
            enabled = enabled,
            onValueChange = {
                if (currentTextField == it) return@BasicTextField
                if (it.text.contains('\n')) onContainNewLine(it) else onUpdateText(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }
}

private fun onPreviewDelKeyEvent(
    event: KeyEvent,
    selection: TextRange,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    val isDelKey = event.nativeKeyEvent.keyCode == KEYCODE_DEL
    val isEmpty = selection == TextRange.Zero
    return if (isKeyDown && isDelKey && isEmpty) {
        invoke()
        true
    } else {
        false
    }
}

private fun onPreviewUpKeyEvent(
    event: KeyEvent,
    selection: TextRange,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    val isUpKey = event.nativeKeyEvent.keyCode == KEYCODE_DPAD_UP
    val isEmpty = selection == TextRange.Zero
    return if (isKeyDown && isUpKey && isEmpty) {
        invoke()
        true
    } else {
        false
    }
}

private fun onPreviewDownKeyEvent(
    event: KeyEvent,
    value: TextFieldValue,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    val isDownKey = event.nativeKeyEvent.keyCode == KEYCODE_DPAD_DOWN
    val isEmpty = value.selection == TextRange(value.text.count())
    return if (isKeyDown && isDownKey && isEmpty) {
        invoke()
        true
    } else {
        false
    }
}

private fun onPreviewEnterKeyEvent(
    event: KeyEvent,
    selection: TextRange,
    invoke: () -> Unit
): Boolean {
    val isKeyDown = event.type == KeyEventType.KeyDown
    val isEnterKey = event.nativeKeyEvent.keyCode == KEYCODE_ENTER
    val isEmpty = selection == TextRange.Zero
    return if (isKeyDown && isEnterKey && isEmpty) {
        invoke()
        true
    } else {
        false
    }
}
