package jp.kaleidot725.texteditor.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import jp.kaleidot725.texteditor.state.EditableTextEditorState
import jp.kaleidot725.texteditor.state.TextEditorState

@Composable
fun TextEditor(
    textEditorState: TextEditorState,
    onUpdatedState: () -> Unit,
    modifier: Modifier = Modifier,
    decorationBox: @Composable (
        index: Int,
        isSelected: Boolean, innerTextField: @Composable (index: Int, isSelected: Boolean, modifier: Modifier) -> Unit
    ) -> Unit = { index, isSelected, innerTextField ->  innerTextField(index, isSelected, Modifier) },
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(
            items = textEditorState.toEditable().fields,
            key = { _, item -> item.id }
        ) { index, textFieldState ->
            decorationBox(index, textFieldState.isSelected) { index, isSelected, modifier ->
                val focusRequester by remember { mutableStateOf(FocusRequester()) }

                LaunchedEffect(isSelected) {
                    if (isSelected) focusRequester.requestFocus()
                }

                TextField(
                    textFieldValue = textFieldState.value,
                    onUpdateText = { newText ->
                        textEditorState.toEditable()
                            .updateField(targetIndex = index, textFieldValue = newText)
                        onUpdatedState()
                    },
                    onAddNewLine = { newText ->
                        textEditorState.toEditable()
                            .splitField(targetIndex = index, textFieldValue = newText)
                        onUpdatedState()
                    },
                    onDeleteNewLine = {
                        textEditorState.toEditable().deleteField(targetIndex = index)
                        onUpdatedState()
                    },
                    focusRequester = focusRequester,
                    onFocus = {
                        if (textEditorState.selectedIndices.contains(index)) return@TextField
                        textEditorState.toEditable().selectField(targetIndex = index)
                        onUpdatedState()
                    },
                    modifier = modifier
                )
            }
        }
    }
}

internal fun TextEditorState.toEditable(): EditableTextEditorState {
    return this as EditableTextEditorState
}