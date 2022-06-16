package jp.kaleidot725.texteditor.view

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import jp.kaleidot725.texteditor.state.TextEditorState

@Composable
fun TextEditor(
    textEditorState: TextEditorState,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(
            items = textEditorState.fields,
            key = { _, item -> item.id }
        ) { index, textFieldState ->
            val focusRequester by remember { mutableStateOf(FocusRequester()) }

            LaunchedEffect(textFieldState.isSelected) {
                if (textFieldState.isSelected) focusRequester.requestFocus()
            }

            val bgColor = if (textFieldState.isSelected) Color(0x80eaffea) else Color.White
            TextField(
                number = (index + 1).toString().padStart(3, '0'),
                textFieldValue = textFieldState.value,
                onUpdateText = { newText ->
                    textEditorState.updateField(targetIndex = index, textFieldValue = newText)
                },
                onAddNewLine = { newText ->
                    textEditorState.splitField(targetIndex = index, textFieldValue = newText)
                },
                onDeleteNewLine = {
                    textEditorState.deleteField(targetIndex = index)
                },
                focusRequester = focusRequester,
                onFocus = {
                    textEditorState.selectField(targetIndex = index)
                },
                modifier = Modifier.background(bgColor)
            )
        }
    }
}
