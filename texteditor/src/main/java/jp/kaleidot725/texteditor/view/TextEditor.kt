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
import jp.kaleidot725.texteditor.extension.rememberTextEditorController
import jp.kaleidot725.texteditor.state.TextEditorState

typealias DecorationBoxComposable = @Composable (
    index: Int,
    isSelected: Boolean,
    innerTextField: @Composable (modifier: Modifier) -> Unit
) -> Unit

@Composable
fun TextEditor(
    textEditorState: TextEditorState,
    onChanged: (TextEditorState) -> Unit,
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues = PaddingValues(),
    decorationBox: DecorationBoxComposable = { _, _, innerTextField -> innerTextField(Modifier) },
) {
    val editableController by rememberTextEditorController(textEditorState, onChanged = { onChanged(it) })
    val lazyColumnState = rememberLazyListState()
    var lastEvent by remember { mutableStateOf(null as Event?) }
    val isMultipleSelectionMode by rememberUpdatedState(newValue = textEditorState.isMultipleSelectionMode)

    editableController.syncState(textEditorState)

    LaunchedEffect(lastEvent) {
        when (val event = lastEvent) {
            is Event.AddNewLine -> {
                lazyColumnState.animateScrollToItem(event.index)
            }
            is Event.DeleteNewLine -> {
                lazyColumnState.animateScrollToItem(event.index)
            }
            is Event.Down -> {
                lazyColumnState.animateScrollToItem(event.index)
            }
            is Event.Up -> {
                lazyColumnState.animateScrollToItem(event.index)
            }
            else -> {}
        }
    }

    LazyColumn(
        state = lazyColumnState,
        modifier = modifier,
        contentPadding = contentPaddingValues
    ) {
        itemsIndexed(
            items = textEditorState.fields,
            key = { _, item -> item.id }
        ) { index, textFieldState ->
            decorationBox(
                index = index,
                isSelected = textFieldState.isSelected,
                innerTextField = { modifier ->
                    Box(
                        modifier = modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!textEditorState.isMultipleSelectionMode) return@clickable
                                editableController.selectField(targetIndex = index)
                            }
                    ) {
                        DisposableEffect(Unit) {
                            onDispose {
                                if (!isMultipleSelectionMode) editableController.clearSelectedIndex(index)
                            }
                        }

                        TextField(
                            textFieldState = textFieldState,
                            enabled = !textEditorState.isMultipleSelectionMode,
                            onUpdateText = { newText ->
                                editableController.updateField(targetIndex = index, textFieldValue = newText)
                            },
                            onContainNewLine = { newText ->
                                editableController.splitNewLine(targetIndex = index, textFieldValue = newText)
                                lastEvent = Event.AddNewLine(index + 1)
                            },
                            onAddNewLine = { newText ->
                                editableController.splitAtCursor(targetIndex = index, textFieldValue = newText)
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
                            }
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
