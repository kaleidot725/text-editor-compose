package jp.kaleidot725.texteditor.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.security.InvalidParameterException

@Stable
internal class EditableTextEditorState(
    lines: List<String>,
    selectedIndices: List<Int>? = null,
    fields: List<TextFieldState>? = null,
) : TextEditorState {
    private val _lines = lines.toMutableStateList()
    override val lines get() = _lines.toList()

    private val _selectedIndices = (selectedIndices ?: listOf(-1)).toMutableStateList()
    override val selectedIndices get() = _selectedIndices.toList()

    private var _isMultipleSelectionMode = mutableStateOf(false)
    override val isMultipleSelectionMode get() = _isMultipleSelectionMode

    private val _fields = (fields ?: lines.createInitTextFieldStates()).toMutableStateList()
    val fields get() = _fields.toList()

    init {
        selectField(0)
    }

    fun splitField(targetIndex: Int, textFieldValue: TextFieldValue) {
        if (targetIndex < 0 || fields.count() <= targetIndex) {
            throw InvalidParameterException("targetIndex out of range($targetIndex)")
        }

        if (!textFieldValue.text.contains('\n')) {
            throw InvalidParameterException("textFieldValue doesn't contains newline")
        }

        val splitFieldValues = textFieldValue.splitTextsByNL()
        val firstSplitFieldValue = splitFieldValues.first()
        _fields[targetIndex] = _fields[targetIndex].copy(value = firstSplitFieldValue, isSelected = false)
        _lines[targetIndex] = firstSplitFieldValue.text

        val newSplitFieldValues = splitFieldValues.subList(1, splitFieldValues.count())
        val newSplitFieldStates = newSplitFieldValues.map { TextFieldState(value = it, isSelected = false) }
        _fields.addAll(targetIndex + 1, newSplitFieldStates)
        _lines.addAll(targetIndex + 1, newSplitFieldStates.map { it.value.text })

        val lastNewSplitFieldIndex = targetIndex + splitFieldValues.lastIndex
        selectField(lastNewSplitFieldIndex)
    }

    fun updateField(targetIndex: Int, textFieldValue: TextFieldValue) {
        if (targetIndex < 0 || fields.count() <= targetIndex) {
            throw InvalidParameterException("targetIndex out of range($targetIndex)")
        }

        if (textFieldValue.text.contains('\n')) {
            throw InvalidParameterException("textFieldValue contains newline")
        }

        _fields[targetIndex] = _fields[targetIndex].copy(value = textFieldValue)
        _lines[targetIndex] = textFieldValue.text
    }

    fun deleteField(targetIndex: Int) {
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
        val toTextFieldState = _fields[targetIndex - 1].copy(value = concatTextFieldValue, isSelected = false)

        _fields[targetIndex - 1] = toTextFieldState
        _lines[targetIndex - 1] = toTextFieldState.value.text

        _fields.removeAt(targetIndex)
        _lines.removeAt(targetIndex)

        selectField(targetIndex - 1)
    }

    fun selectField(targetIndex: Int) {
        if (targetIndex < 0 || fields.count() <= targetIndex) {
            throw InvalidParameterException("targetIndex out of range($targetIndex)")
        }

        if (!isMultipleSelectionMode.value) clearSelectedIndices()

        val isSelected = if (isMultipleSelectionMode.value) !_fields[targetIndex].isSelected else true
        _fields[targetIndex] = _fields[targetIndex].copy(isSelected = isSelected)
        _selectedIndices.add(targetIndex)
    }

    override fun enableMultipleSelectionMode(value: Boolean) {
        if (isMultipleSelectionMode.value && !value) {
            clearSelectedIndices()
        }
        _isMultipleSelectionMode.value = value
    }

    override fun getAllText(): String {
        return lines.foldIndexed("") { index, acc, s ->
            if (index == 0) acc + s else acc + "\n" + s
        }
    }

    override fun getSelectedText(): String {
        val targets = selectedIndices.mapNotNull { lines.getOrNull(it) }
        return targets.foldIndexed("") { index, acc, s ->
            if (index == 0) acc + s else acc + "\n" + s
        }
    }

    override fun deleteSelectedLines() {
        val targets = selectedIndices.mapNotNull { _fields.getOrNull(it) }
        _fields.removeAll(targets)
        _selectedIndices.clear()
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

    private fun TextFieldValue.splitTextsByNL(): List<TextFieldValue> {
        return this.text.split("\n").map { TextFieldValue(it, TextRange.Zero) }
    }
}
