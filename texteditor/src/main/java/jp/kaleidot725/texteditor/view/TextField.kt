package jp.kaleidot725.texteditor.view

import android.view.KeyEvent.KEYCODE_DEL
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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

@Composable
internal fun TextField(
    textFieldValue: TextFieldValue,
    enabled: Boolean,
    onUpdateText: (TextFieldValue) -> Unit,
    onAddNewLine: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    onFocus: () -> Unit,
    onDeleteNewLine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTextField by rememberUpdatedState(newValue = textFieldValue)

    Box(modifier = modifier) {
        BasicTextField(
            value = currentTextField,
            enabled = enabled,
            onValueChange = {
                if (it.text.contains('\n')) onAddNewLine(it) else onUpdateText(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .focusRequester(focusRequester)
                .onFocusChanged { if (it.isFocused) onFocus() }
                .onPreviewKeyEvent { event ->
                    onPreviewDelKeyEvent(event, currentTextField.selection) {
                        onDeleteNewLine()
                    }
                }
        )
    }
}

private fun onPreviewDelKeyEvent(
    event: KeyEvent,
    selection: TextRange,
    invoke: () -> Unit
): Boolean {
    val isKeyUp = event.type == KeyEventType.KeyUp
    val isBackKey = event.nativeKeyEvent.keyCode == KEYCODE_DEL
    val isEmpty = selection == TextRange.Zero
    return if (isKeyUp && isBackKey && isEmpty) {
        invoke()
        true
    } else {
        false
    }
}