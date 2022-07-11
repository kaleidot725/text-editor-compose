package jp.kaleidot725.texteditor.view

import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import jp.kaleidot725.texteditor.state.TextFieldState
import org.w3c.dom.Text

@Composable
internal fun TextField(
    textFieldState: TextFieldState,
    enabled: Boolean,
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
    val focusRequester by remember { mutableStateOf(FocusRequester()) }

    LaunchedEffect(textFieldState.isSelected) {
        if (textFieldState.isSelected) focusRequester.requestFocus()
    }

    Box(modifier = modifier
        .focusRequester(focusRequester)
        .onFocusChanged {
            if (it.isFocused) onFocus()
        }
        .onPreviewKeyEvent { event ->
            val value = textFieldState.value
            val selection = currentTextField.selection
            val b1 = onPreviewDelKeyEvent(event, selection) { onDeleteNewLine() }
            val b2 = onPreviewDownKeyEvent(event, value) { onDownFocus() }
            val b3 = onPreviewUpKeyEvent(event, selection) { onUpFocus() }
            val b4 = onPreviewEnterKeyEvent(event, selection) { onAddNewLine(currentTextField) }
            b1 || b2 || b3 || b4
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
    val isKeyUp = event.type == KeyEventType.KeyDown
    val isBackKey = event.nativeKeyEvent.keyCode == KEYCODE_DEL
    val isEmpty = selection == TextRange.Zero
    return if (isKeyUp && isBackKey && isEmpty) {
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
    val isKeyUp = event.type == KeyEventType.KeyDown
    val isBackKey = event.nativeKeyEvent.keyCode == KEYCODE_DPAD_UP
    val isEmpty = selection == TextRange.Zero
    return if (isKeyUp && isBackKey && isEmpty) {
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
    val isKeyUp = event.type == KeyEventType.KeyDown
    val isBackKey = event.nativeKeyEvent.keyCode == KEYCODE_DPAD_DOWN
    val isEmpty = value.selection == TextRange(value.text.count())
    return if (isKeyUp && isBackKey && isEmpty) {
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
    val isKeyUp = event.type == KeyEventType.KeyDown
    val isBackKey = event.nativeKeyEvent.keyCode == KEYCODE_ENTER
    val isEmpty = selection == TextRange.Zero
    return if (isKeyUp && isBackKey && isEmpty) {
        invoke()
        true
    } else {
        false
    }
}