package jp.kaleidot725.texteditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextLine(
    number: Int,
    text: String,
    isSelected: Boolean,
    onChangedText: (String) -> Unit,
    onInputNewLine: () -> Unit,
    onInputBackKey: () -> Unit,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSelected) {
        if (isSelected) focusRequester.requestFocus()
    }

    Row(modifier = modifier.background(if (isSelected) Color.Gray else Color.White)) {
        Text(
            text = number.toString().padStart(2, '0'),
            color = Color(0xFF014900),
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.Top)
        )

        Spacer(modifier = Modifier.width(4.dp))

        BasicTextField(
            value = text,
            onValueChange = {
                when {
                    it.contains("\n") -> {
                        onInputNewLine()
                    }
                    else -> {
                        onChangedText(it)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Top)
                .focusRequester(focusRequester)
                .onFocusChanged { if (it.isFocused) onFocus() }
                .onKeyEvent { event: KeyEvent ->
                    val isKeyUp = event.type == KeyEventType.KeyUp
                    val isBackKey = (event.key == Key.Backspace) || (event.key == Key.Delete)
                    if (isKeyUp && isBackKey) {
                        onInputBackKey()
                        true
                    } else {
                        false
                    }
                }
                .clearFocusOnKeyboardDismiss()
        )
    }
}
