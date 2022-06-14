package jp.kaleidot725.texteditor

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

@Composable
fun TextEditor(modifier: Modifier = Modifier) {
    val linesState by rememberTextEditorState(DemoText)

    LazyColumn(modifier = modifier) {
        itemsIndexed(
            items = linesState.fields
        ) { index, textFieldState ->
            val focusRequester by remember { mutableStateOf(FocusRequester()) }

            LaunchedEffect(textFieldState.isSelected) {
                if (textFieldState.isSelected) {
                    focusRequester.requestFocus()
                }
            }

            TextLine(
                textFieldValue = textFieldState.value,
                onUpdateText = { newText ->
                    linesState.updateLine(targetIndex = index, textFieldValue = newText)
                },
                onAddNewLine = { newText ->
                    linesState.addNewLine(targetIndex = index, textFieldValue = newText)
                },
                onDeleteNewLine = {
                    linesState.deleteNewLine(targetIndex = index)
                },
                focusRequester = focusRequester
            )
        }
    }
}
