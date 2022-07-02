package jp.kaleidot725.texteditor.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
        newValue = textEditorController as EditableTextEditorController
    )
    val isMultipleSelectionMode by textEditorController.isMultipleSelectionMode

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        textEditorController.toEditable().fields.forEachIndexed { index, textFieldState ->
            val focusRequester by remember { mutableStateOf(FocusRequester()) }

            LaunchedEffect(textFieldState.isSelected) {
                if (textFieldState.isSelected) {
                    focusRequester.requestFocus()
                }
            }

            decorationBox(
                index = index,
                isSelected = textFieldState.isSelected,
                innerTextField = { modifier ->
                    TextField(
                        textFieldValue = textFieldState.value,
                        enabled = !isMultipleSelectionMode,
                        focusRequester = focusRequester,
                        onUpdateText = { newText ->
                            editableController.updateField(
                                targetIndex = index,
                                textFieldValue = newText
                            )
                        },
                        onAddNewLine = { newText ->
                            editableController.splitField(
                                targetIndex = index,
                                textFieldValue = newText
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
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isMultipleSelectionMode) return@clickable
                                editableController.selectField(targetIndex = index)
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