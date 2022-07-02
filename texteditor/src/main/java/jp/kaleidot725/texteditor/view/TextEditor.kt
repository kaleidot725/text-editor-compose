package jp.kaleidot725.texteditor.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import jp.kaleidot725.texteditor.controller.EditableTextEditorController
import jp.kaleidot725.texteditor.controller.TextEditorController

typealias DecorationBoxComposable = @Composable (
    index: Int,
    isSelected: Boolean,
    innerTextField: @Composable (modifier: Modifier) -> Unit
) -> Unit

@Composable
fun TextEditor(
    textEditorController: TextEditorController,
    modifier: Modifier = Modifier,
    decorationBox: DecorationBoxComposable = { _, _, innerTextField -> innerTextField(Modifier) },
) {
    val isMultipleSelectionMode by textEditorController.isMultipleSelectionMode

    // workaround: prevent to hide ime when editor delete newline
    val focusManager = LocalFocusManager.current

    LazyColumn(modifier = modifier) {
        itemsIndexed(
            items = textEditorController.toEditable().fields,
            key = { _, item -> item.id }
        ) { index, textFieldState ->

            val focusRequester by remember { mutableStateOf(FocusRequester()) }

            LaunchedEffect(textFieldState.isSelected) {
                if (textFieldState.isSelected) focusRequester.requestFocus()
            }

            decorationBox(
                index = index,
                isSelected = textFieldState.isSelected,
                innerTextField = { modifier ->
                    TextField(
                        textFieldValue = textFieldState.value,
                        enabled = !isMultipleSelectionMode,
                        onUpdateText = { newText ->
                            textEditorController.toEditable()
                                .updateField(targetIndex = index, textFieldValue = newText)
                        },
                        onAddNewLine = { newText ->
                            textEditorController.toEditable()
                                .splitField(targetIndex = index, textFieldValue = newText)
                        },
                        onDeleteNewLine = {
                            textEditorController.toEditable().deleteField(targetIndex = index)

                            // workaround: prevent to hide ime when editor delete newline
                            focusManager.moveFocus(FocusDirection.Up)
                        },
                        focusRequester = focusRequester,
                        onFocus = {
                            textEditorController.toEditable().selectField(targetIndex = index)
                        },
                        modifier = modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isMultipleSelectionMode) return@clickable
                            textEditorController.toEditable().selectField(targetIndex = index)
                        }
                    )
                }
            )
        }
    }
}

internal fun TextEditorController.toEditable(): EditableTextEditorController {
    return this as EditableTextEditorController
}