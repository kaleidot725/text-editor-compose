package jp.kaleidot725.texteditor

import android.util.Log
import android.view.KeyEvent.KEYCODE_DEL
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextLine(
    number: String,
    textFieldValue: TextFieldValue,
    onUpdateText: (TextFieldValue) -> Unit,
    onAddNewLine: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    onFocus: () -> Unit,
    onDeleteNewLine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTextField by rememberUpdatedState(newValue = textFieldValue)

    Row(modifier = modifier) {
        Text(
            text = number,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Top)
        )

        Spacer(modifier = Modifier.size(4.dp))

        BasicTextField(
            value = currentTextField,
            onValueChange = {
                if (it.text.contains('\n')) onAddNewLine(it) else onUpdateText(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Top)
                .focusRequester(focusRequester)
                .onFocusChanged { if (it.isFocused) onFocus() }
                .onPreviewKeyEvent { event ->
                    val isKeyUp = event.type == KeyEventType.KeyUp
                    val isBackKey = event.nativeKeyEvent.keyCode == KEYCODE_DEL
                    val isEmpty = currentTextField.selection == TextRange.Zero
                    if (isKeyUp && isBackKey && isEmpty) {
                        onDeleteNewLine()
                        true
                    } else {
                        false
                    }
                }
        )
    }


}