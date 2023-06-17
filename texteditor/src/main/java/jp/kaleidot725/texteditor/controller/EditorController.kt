package jp.kaleidot725.texteditor.controller

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import jp.kaleidot725.texteditor.state.TextEditorState
import jp.kaleidot725.texteditor.state.TextFieldState
import java.security.InvalidParameterException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class EditorController(
    textEditorState: TextEditorState
) {
    private var onChanged: (TextEditorState) -> Unit = {}

    private var _isMultipleSelectionMode = textEditorState.isMultipleSelectionMode
    val isMultipleSelectionMode get() = _isMultipleSelectionMode

    private val _fields = (textEditorState.fields).toMutableList()
    val fields get() = _fields.toList()

    private val _selectedIndices = (textEditorState.selectedIndices).toMutableList()
    val selectedIndices get() = _selectedIndices.toList()

    private val state: TextEditorState
        get() = TextEditorState(fields, selectedIndices, isMultipleSelectionMode)

    private val lock = ReentrantLock()

    init {
        selectFieldInternal(0)
    }

    fun syncState(state: TextEditorState) {
        _isMultipleSelectionMode = state.isMultipleSelectionMode
        _selectedIndices.clear()
        _selectedIndices.addAll(state.selectedIndices)
        _fields.clear()
        _fields.addAll(state.fields)
    }

    fun splitNewLine(targetIndex: Int, textFieldValue: TextFieldValue) {
        lock.withLock {
            if (targetIndex < 0 || fields.count() <= targetIndex) {
                throw InvalidParameterException("targetIndex out of range($targetIndex)")
            }

            if (!textFieldValue.text.contains('\n')) {
                throw InvalidParameterException("textFieldValue doesn't contains newline")
            }

            val splitFieldValues = textFieldValue.splitTextsByNL()
            val firstSplitFieldValue = splitFieldValues.first()
            _fields[targetIndex] =
                _fields[targetIndex].copy(value = firstSplitFieldValue, isSelected = false)

            val newSplitFieldValues = splitFieldValues.subList(1, splitFieldValues.count())
            val newSplitFieldStates =
                newSplitFieldValues.map { TextFieldState(value = it, isSelected = false) }
            _fields.addAll(targetIndex + 1, newSplitFieldStates)

            val lastNewSplitFieldIndex = targetIndex + splitFieldValues.lastIndex
            selectFieldInternal(lastNewSplitFieldIndex)
            onChanged(state)
        }
    }

    fun splitAtCursor(targetIndex: Int, textFieldValue: TextFieldValue) {
        lock.withLock {
            if (targetIndex < 0 || fields.count() <= targetIndex) {
                throw InvalidParameterException("targetIndex out of range($targetIndex)")
            }

            val splitPosition = textFieldValue.selection.start
            if (splitPosition < 0 || textFieldValue.text.length < splitPosition) {
                throw InvalidParameterException("splitPosition out of range($splitPosition)")
            }

            val firstStart = 0
            val firstEnd = if (splitPosition == 0) 0 else splitPosition
            val first = textFieldValue.text.substring(firstStart, firstEnd)

            val secondStart = if (splitPosition == 0) 0 else splitPosition
            val secondEnd = textFieldValue.text.count()
            val second = textFieldValue.text.substring(secondStart, secondEnd)

            val firstValue = textFieldValue.copy(first)
            val firstState = _fields[targetIndex].copy(value = firstValue, isSelected = false)
            _fields[targetIndex] = firstState

            val secondValue = TextFieldValue(second, TextRange.Zero)
            val secondState = TextFieldState(value = secondValue, isSelected = false)
            _fields.add(targetIndex + 1, secondState)

            selectFieldInternal(targetIndex + 1)
            onChanged(state)
        }
    }

    fun updateField(targetIndex: Int, textFieldValue: TextFieldValue) {
        lock.withLock {
            if (targetIndex < 0 || fields.count() <= targetIndex) {
                throw InvalidParameterException("targetIndex out of range($targetIndex)")
            }

            if (textFieldValue.text.contains('\n')) {
                throw InvalidParameterException("textFieldValue contains newline")
            }

            _fields[targetIndex] = _fields[targetIndex].copy(value = textFieldValue)
            onChanged(state)
        }
    }

    fun deleteField(targetIndex: Int) {
        lock.withLock {
            if (targetIndex < 0 || fields.count() <= targetIndex) {
                throw InvalidParameterException("targetIndex out of range($targetIndex)")
            }

            if (targetIndex == 0) {
                return
            }

            val toText = _fields[targetIndex - 1].value.text
            val fromText = _fields[targetIndex].value.text

            val concatText = toText + fromText
            val concatSelection = TextRange(toText.count())
            val concatTextFieldValue =
                TextFieldValue(text = concatText, selection = concatSelection)
            val toTextFieldState =
                _fields[targetIndex - 1].copy(value = concatTextFieldValue, isSelected = false)

            _fields[targetIndex - 1] = toTextFieldState

            _fields.removeAt(targetIndex)

            selectFieldInternal(targetIndex - 1)
            onChanged(state)
        }
    }

    fun selectField(targetIndex: Int) {
        lock.withLock {
            selectFieldInternal(targetIndex)
            onChanged(state)
        }
    }

    fun selectPreviousField() {
        lock.withLock {
            if (isMultipleSelectionMode) return
            val selectedIndex = selectedIndices.firstOrNull() ?: return
            if (selectedIndex == 0) return

            val previousIndex = selectedIndex - 1
            selectFieldInternal(previousIndex, SelectionOption.LAST_POSITION)
            onChanged(state)
        }
    }

    fun selectNextField() {
        lock.withLock {
            if (isMultipleSelectionMode) return
            val selectedIndex = selectedIndices.firstOrNull() ?: return
            if (selectedIndex == fields.lastIndex) return

            val nextIndex = selectedIndex + 1
            selectFieldInternal(nextIndex, SelectionOption.FIRST_POSITION)
            onChanged(state)
        }
    }

    fun clearSelectedIndex(targetIndex: Int) {
        lock.withLock {
            if (targetIndex < 0 || fields.count() <= targetIndex) {
                return@withLock
            }

            _fields[targetIndex] = _fields[targetIndex].copy(isSelected = false)
            _selectedIndices.remove(targetIndex)
            onChanged(state)
        }
    }

    fun clearSelectedIndices() {
        lock.withLock {
            this.clearSelectedIndicesInternal()
            onChanged(state)
        }
    }

    fun setMultipleSelectionMode(value: Boolean) {
        lock.withLock {
            if (isMultipleSelectionMode && !value) {
                this.clearSelectedIndicesInternal()
            }
            _isMultipleSelectionMode = value
            onChanged(state)
        }
    }

    fun setOnChangedTextListener(onChanged: (TextEditorState) -> Unit) {
        this.onChanged = onChanged
    }

    fun deleteAllLine() {
        lock.withLock {
            _fields.clear()
            _fields.addAll(emptyList<String>().createInitTextFieldStates())
            _selectedIndices.clear()
            selectFieldInternal(0)
            onChanged(state)
        }
    }

    fun deleteSelectedLines() {
        lock.withLock {
            val targets = selectedIndices.mapNotNull { _fields.getOrNull(it) }
            _fields.removeAll(targets)
            _selectedIndices.clear()
            onChanged(state)
        }
    }

    private fun clearSelectedIndicesInternal() {
        val copyFields = _fields.toList().map { it.copy(isSelected = false) }
        _fields.clear()
        _fields.addAll(copyFields)
        _selectedIndices.clear()
    }

    private fun selectFieldInternal(
        targetIndex: Int,
        option: SelectionOption = SelectionOption.NONE
    ) {
        if (targetIndex < 0 || fields.count() <= targetIndex) {
            throw InvalidParameterException("targetIndex out of range($targetIndex)")
        }

        val target = _fields[targetIndex]
        val selection = when (option) {
            SelectionOption.NONE -> target.value.selection
            SelectionOption.FIRST_POSITION -> TextRange.Zero
            SelectionOption.LAST_POSITION -> {
                if (target.value.text.lastIndex != -1) {
                    TextRange(target.value.text.lastIndex)
                } else {
                    TextRange.Zero
                }
            }
        }

        if (isMultipleSelectionMode) {
            val isSelected = !_fields[targetIndex].isSelected
            val copyTarget = target.copy(
                isSelected = isSelected,
                value = target.value.copy(selection = selection)
            )
            _fields[targetIndex] = copyTarget
            if (isSelected) _selectedIndices.add(targetIndex) else _selectedIndices.remove(
                targetIndex
            )
        } else {
            val copyTarget = target.copy(
                isSelected = true,
                value = target.value.copy(selection = selection)
            )
            this.clearSelectedIndicesInternal()
            _fields[targetIndex] = copyTarget
            _selectedIndices.add(targetIndex)
        }
    }

    private fun TextFieldValue.splitTextsByNL(): List<TextFieldValue> {
        var position = 0
        val splitTexts = this.text.split("\n").map {
            position += it.count()
            it to position
        }

        return splitTexts.mapIndexed { index, pair ->
            if (index == 0) {
                TextFieldValue(pair.first, TextRange(pair.second))
            } else {
                TextFieldValue(pair.first, TextRange.Zero)
            }
        }
    }

    private enum class SelectionOption {
        FIRST_POSITION,
        LAST_POSITION,
        NONE
    }

    companion object {
        fun List<String>.createInitTextFieldStates(): List<TextFieldState> {
            if (this.isEmpty()) return listOf(TextFieldState(isSelected = false))
            return this.mapIndexed { _, s ->
                TextFieldState(
                    value = TextFieldValue(s, TextRange.Zero),
                    isSelected = false
                )
            }
        }
    }
}

@Composable
internal fun rememberTextEditorController(
    state: TextEditorState,
    onChanged: (editorState: TextEditorState) -> Unit
) = remember {
    mutableStateOf(
        EditorController(state).apply {
            setOnChangedTextListener { onChanged(it) }
        }
    )
}
