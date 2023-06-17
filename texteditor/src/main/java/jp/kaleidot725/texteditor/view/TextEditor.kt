package jp.kaleidot725.texteditor.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import jp.kaleidot725.texteditor.controller.rememberTextEditorController
import jp.kaleidot725.texteditor.state.TextEditorState
import java.util.Date

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
    val textEditorState by rememberUpdatedState(newValue = textEditorState)
    val editableController by rememberTextEditorController(textEditorState, onChanged = { onChanged(it) })
    var lastScrollEvent by remember { mutableStateOf(null as ScrollEvent?) }
    val lazyColumnState = rememberLazyListState()
    val focusRequesters by remember { mutableStateOf(mutableMapOf<Int, FocusRequester>()) }
    editableController.syncState(textEditorState)

    LaunchedEffect(lastScrollEvent) {
        lastScrollEvent?.index?.let { index ->
            val first = lazyColumnState.layoutInfo.visibleItemsInfo.minBy { it.index }.index
            val end = lazyColumnState.layoutInfo.visibleItemsInfo.maxBy { it.index }.index
            if (index < first || end < index) {
                lazyColumnState.animateScrollToItem(index)
            }
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
            val focusRequester by remember { mutableStateOf(FocusRequester()) }

            DisposableEffect(Unit) {
                focusRequesters[index] = focusRequester
                onDispose {
                    focusRequesters.remove(index)
                }
            }

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
                                if (!textEditorState.isMultipleSelectionMode) {
                                    editableController.clearSelectedIndex(index)
                                }
                            }
                        }

                        TextField(
                            textFieldState = textFieldState,
                            enabled = !textEditorState.isMultipleSelectionMode,
                            focusRequester = focusRequester,
                            onUpdateText = { newText ->
                                editableController.updateField(targetIndex = index, textFieldValue = newText)
                            },
                            onContainNewLine = { newText ->
                                if (lastScrollEvent != null && lastScrollEvent?.isConsumed != true) return@TextField
                                editableController.splitNewLine(targetIndex = index, textFieldValue = newText)
                                lastScrollEvent = ScrollEvent(index + 1)
                            },
                            onAddNewLine = { newText ->
                                if (lastScrollEvent != null && lastScrollEvent?.isConsumed != true) return@TextField
                                editableController.splitAtCursor(targetIndex = index, textFieldValue = newText)
                                lastScrollEvent = ScrollEvent(index + 1)
                            },
                            onDeleteNewLine = {
                                if (lastScrollEvent != null && lastScrollEvent?.isConsumed != true) return@TextField
                                editableController.deleteField(targetIndex = index)
                                if (index != 0) lastScrollEvent = ScrollEvent(index - 1)
                            },
                            onFocus = {
                                editableController.selectField(index)
                            },
                            onUpFocus = {
                                if (lastScrollEvent != null && lastScrollEvent?.isConsumed != true) return@TextField
                                editableController.selectPreviousField()
                                if (index != 0) lastScrollEvent = ScrollEvent(index - 1)
                            },
                            onDownFocus = {
                                if (lastScrollEvent != null && lastScrollEvent?.isConsumed != true) return@TextField
                                editableController.selectNextField()
                                if (index != textEditorState.fields.lastIndex) lastScrollEvent = ScrollEvent(index + 1)
                            },
                            scrollEvent = if (lastScrollEvent?.index == index) lastScrollEvent else null
                        )
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

data class ScrollEvent(val index: Int = -1, val time: Long = Date().time) {
    var isConsumed: Boolean = false
        private set

    fun consume() {
        isConsumed = true
    }
}
