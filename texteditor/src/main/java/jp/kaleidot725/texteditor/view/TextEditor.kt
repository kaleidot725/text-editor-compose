package jp.kaleidot725.texteditor.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import jp.kaleidot725.texteditor.controller.EditableTextEditorController
import jp.kaleidot725.texteditor.controller.TextEditorController
import kotlinx.coroutines.launch

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
    val editableController by rememberUpdatedState(
        textEditorController as EditableTextEditorController
    )
    val isMultipleSelectionMode by textEditorController.isMultipleSelectionMode

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        editableController.fields.forEachIndexed { index, textFieldState ->
            decorationBox(
                index = index,
                isSelected = textFieldState.isSelected,
                innerTextField = { modifier ->
                    Box(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isMultipleSelectionMode) return@clickable
                            editableController.selectField(targetIndex = index)
                        }
                    ) {
                        TextField(
                            textFieldState = textFieldState,
                            enabled = !isMultipleSelectionMode,
                            onUpdateText = { newText ->
                                editableController.updateField(
                                    targetIndex = index,
                                    textFieldValue = newText
                                )
                            },
                            onContainNewLine = { newText ->
                                editableController.splitNewLine(
                                    targetIndex = index, textFieldValue = newText
                                )
                            },
                            onAddNewLine = { newText ->
                                editableController.splitAtCursor(
                                    targetIndex = index, textFieldValue = newText
                                )
                            },
                            onDeleteNewLine = {
                                editableController.deleteField(targetIndex = index)
                            },
                            onFocus = {
                                editableController.selectField(index)
                            },
                            onUpFocus = {
                                editableController.selectPreviousField()
                            },
                            onDownFocus = {
                                editableController.selectNextField()
                            },
                            modifier = modifier
                        )
                    }
                }
            )
        }
    }
}