package jp.kaleidot725.texteditor.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    contentPaddingValues: PaddingValues = PaddingValues(),
    decorationBox: DecorationBoxComposable = { _, _, innerTextField -> innerTextField(Modifier) },
) {
    val editableController by rememberUpdatedState(textEditorController as EditableTextEditorController)
    val isMultipleSelectionMode by textEditorController.isMultipleSelectionMode

    val listState = rememberLazyListState()
    var lastEvent by remember { mutableStateOf(null as Event?) }

    LaunchedEffect(lastEvent) {
        when (val event = lastEvent) {
            is Event.AddNewLine -> {
                listState.animateScrollToItem(event.index)
            }
            is Event.DeleteNewLine -> {
                listState.animateScrollToItem(event.index)
            }
            is Event.Down -> {
                listState.animateScrollToItem(event.index)
            }
            is Event.Up -> {
                listState.animateScrollToItem(event.index)
            }
            else -> {}
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = contentPaddingValues
    ) {
        itemsIndexed(
            items = editableController.fields,
            key = { _, item -> item.id }
        ) { index, textFieldState ->
            decorationBox(
                index = index,
                isSelected = textFieldState.isSelected,
                innerTextField = { modifier ->
                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isMultipleSelectionMode) return@clickable
                                editableController.selectField(targetIndex = index)
                            }
                    ) {
                        DisposableEffect(Unit) {
                            onDispose { editableController.clearSelectedIndex(index) }
                        }

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
                                lastEvent = Event.AddNewLine(index + 1)
                            },
                            onAddNewLine = { newText ->
                                editableController.splitAtCursor(
                                    targetIndex = index, textFieldValue = newText
                                )
                                lastEvent = Event.AddNewLine(index + 1)
                            },
                            onDeleteNewLine = {
                                editableController.deleteField(targetIndex = index)
                                if (index != 0) lastEvent = Event.DeleteNewLine(index - 1)
                            },
                            onFocus = {
                                editableController.selectField(index)
                            },
                            onUpFocus = {
                                editableController.selectPreviousField()
                                if (index != 0) lastEvent = Event.DeleteNewLine(index - 1)
                            },
                            onDownFocus = {
                                editableController.selectNextField()
                                lastEvent = Event.AddNewLine(index + 1)
                            },
                            modifier = modifier
                        )
                    }
                }
            )
        }
    }
}

sealed class Event(val time: Long, val index: Int = -1) {
    class Up(index: Int) : Event(System.currentTimeMillis(), index)
    class Down(index: Int) : Event(System.currentTimeMillis(), index)
    class AddNewLine(index: Int) : Event(System.currentTimeMillis(), index)
    class DeleteNewLine(index: Int) : Event(System.currentTimeMillis(), index)
}
