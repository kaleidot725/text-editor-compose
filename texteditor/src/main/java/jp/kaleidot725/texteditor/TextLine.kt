package jp.kaleidot725.texteditor

import android.util.Log
import android.view.KeyEvent.KEYCODE_DEL
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TextLine(
    textFieldValue: TextFieldValue,
    onUpdateText: (TextFieldValue) -> Unit,
    onAddNewLine: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester?,
    onDeleteNewLine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTextField by rememberUpdatedState(newValue = textFieldValue)

    Row(modifier) {
        BasicTextField(
            value = currentTextField,
            onValueChange = {
                if (it.text.contains('\n')) onAddNewLine(it) else onUpdateText(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Top)
                .focusRequester(focusRequester?: FocusRequester())
                .onPreviewKeyEvent { event ->
                    val isKeyUp = event.type == KeyEventType.KeyUp
                    val isBackKey = event.nativeKeyEvent.keyCode == KEYCODE_DEL
                    val isEmpty = currentTextField.selection == TextRange.Zero
                    if (isKeyUp && isBackKey  && isEmpty) {
                        onDeleteNewLine()
                        true
                    } else {
                        false
                    }
                }
        )
    }
}