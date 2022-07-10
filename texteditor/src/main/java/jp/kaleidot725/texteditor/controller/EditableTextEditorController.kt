package jp.kaleidot725.texteditor.controller

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import jp.kaleidot725.texteditor.state.TextFieldState
import java.security.InvalidParameterException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Stable
internal class EditableTextEditorController(
    lines: List<String>,
    selectedIndices: List<Int>? = null,
    fields: List<TextFieldState>? = null,
) : TextEditorController {
    private var onChanged: () -> Unit = {}

    private var _isMultipleSelectionMode = mutableStateOf(false)
    override val isMultipleSelectionMode get() = _isMultipleSelectionMode

    private val _selectedIndices = (selectedIndices ?: listOf(-1)).toMutableStateList()
    val selectedIndices get() = _selectedIndices.toList()

    private val _fields = (fields ?: lines.createInitTextFieldStates()).toMutableStateList()
    val fields get() = _fields.toList()

    private val lock = ReentrantLock()

    init {
        selectFieldInternal(0)
    }

    fun splitField(targetIndex: Int, textFieldValue: TextFieldValue) {
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
            onChanged()
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
            onChanged()
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
            val concatTextFieldValue = TextFieldValue(text = concatText, selection = concatSelection)
            val toTextFieldState =
                _fields[targetIndex - 1].copy(value = concatTextFieldValue, isSelected = false)

            _fields[targetIndex - 1] = toTextFieldState

            _fields.removeAt(targetIndex)

            selectFieldInternal(targetIndex - 1)
            onChanged()
        }
    }

    fun selectField(targetIndex: Int) {
        lock.withLock {
            selectFieldInternal(targetIndex)
            onChanged()
        }
    }

    fun selectPreviousField() {
        lock.withLock {
            if (isMultipleSelectionMode.value) return
            val selectedIndex = selectedIndices.firstOrNull() ?: return
            if (selectedIndex == 0) return

            val previousIndex = selectedIndex - 1
            selectFieldInternal(previousIndex, SelectionOption.LAST_POSITION)
        }
    }

    fun selectNextField() {
        lock.withLock {
            if (isMultipleSelectionMode.value) return
            val selectedIndex = selectedIndices.firstOrNull() ?: return
            if (selectedIndex == fields.lastIndex) return

            val nextIndex = selectedIndex + 1
            selectFieldInternal(nextIndex, SelectionOption.FIRST_POSITION)
        }
    }

    override fun setMultipleSelectionMode(value: Boolean) {
        lock.withLock {
            if (isMultipleSelectionMode.value && !value) {
                clearSelectedIndices()
            }
            _isMultipleSelectionMode.value = value
            onChanged()
        }
    }

    override fun setOnChangedTextListener(onChanged: () -> Unit) {
        this.onChanged = onChanged
    }

    override fun getAllText(): String {
        return fields.map { it.value.text }.foldIndexed("") { index, acc, s ->
            if (index == 0) acc + s else acc + "\n" + s
        }
    }

    override fun getSelectedText(): String {
        val lines = fields.map { it.value.text }
        val targets = selectedIndices.sortedBy { it }.mapNotNull { lines.getOrNull(it) }
        return targets.foldIndexed("") { index, acc, s ->
            if (index == 0) acc + s else acc + "\n" + s
        }
    }

    override fun deleteAllLine() {
        lock.withLock {
            _fields.clear()
            _fields.addAll(emptyList<String>().createInitTextFieldStates())
            _selectedIndices.clear()
            selectFieldInternal(0)
            onChanged()
        }
    }

    override fun deleteSelectedLines() {
        lock.withLock {
            val targets = selectedIndices.mapNotNull { _fields.getOrNull(it) }
            _fields.removeAll(targets)
            _selectedIndices.clear()
            onChanged()
        }
    }

    private fun clearSelectedIndices() {
        _selectedIndices
            .filter { _fields.getOrNull(it) != null }
            .forEach { index -> _fields[index] = _fields[index].copy(isSelected = false) }
        _selectedIndices.clear()
    }

    private fun List<String>.createInitTextFieldStates(): List<TextFieldState> {
        if (this.isEmpty()) return listOf(TextFieldState(isSelected = false))
        return this.mapIndexed { _, s ->
            TextFieldState(
                value = TextFieldValue(s, TextRange.Zero),
                isSelected = false
            )
        }
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

        if (isMultipleSelectionMode.value) {
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
            val isSelected = true
            val copyTarget = target.copy(
                isSelected = isSelected,
                value = target.value.copy(selection = selection)
            )
            clearSelectedIndices()
            _fields[targetIndex] = copyTarget
            _selectedIndices.add(targetIndex)
        }
    }

    private fun TextFieldValue.splitTextsByNL(): List<TextFieldValue> {
        return this.text.split("\n").map { TextFieldValue(it, TextRange.Zero) }
    }

    private enum class SelectionOption {
        FIRST_POSITION,
        LAST_POSITION,
        NONE
    }
}
