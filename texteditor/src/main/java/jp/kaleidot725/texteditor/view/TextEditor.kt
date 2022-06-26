package jp.kaleidot725.texteditor.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import jp.kaleidot725.texteditor.state.EditableTextEditorState
import jp.kaleidot725.texteditor.state.TextEditorState

typealias DecorationBoxComposable = @Composable (
    index: Int,
    isSelected: Boolean,
    innerTextField: @Composable (modifier: Modifier) -> Unit
) -> Unit

@Composable
fun TextEditor(
    textEditorState: TextEditorState,
    onUpdatedState: () -> Unit,
    modifier: Modifier = Modifier,
    decorationBox: DecorationBoxComposable = { _, _, innerTextField -> innerTextField(Modifier) },
) {
    val isMultipleSelectionMode by textEditorState.isMultipleSelectionMode

    LazyColumn(modifier = modifier) {
        itemsIndexed(
            items = textEditorState.toEditable().fields,
            key = { _, item -> item.id }
        ) { index, textFieldState ->
            // workaround: prevent to hide ime when editor delete newline
            val focusManager = LocalFocusManager.current
            val focusRequester by remember { mutableStateOf(FocusRequester()) }

            LaunchedEffect(textFieldState.isSelected) {
                if (textFieldState.isSelected) focusRequester.requestFocus()
            }

            decorationBox(index, textFieldState.isSelected) {
                TextField(
                    textFieldValue = textFieldState.value,
                    enabled = !isMultipleSelectionMode,
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

                        // workaround: prevent to hide ime when editor delete newline
                        focusManager.moveFocus(FocusDirection.Up)
                    },
                    focusRequester = focusRequester,
                    onFocus = {
                        textEditorState.toEditable().selectField(targetIndex = index)
                        onUpdatedState()
                    },
                    modifier = Modifier.clickable {
                        if (!isMultipleSelectionMode) return@clickable
                        textEditorState.toEditable().selectField(targetIndex = index)
                        onUpdatedState()
                    }
                )
            }
        }
    }
}

internal fun TextEditorState.toEditable(): EditableTextEditorState {
    return this as EditableTextEditorState
}