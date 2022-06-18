package jp.kaleidot725.texteditor.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.security.InvalidParameterException

@Stable
internal class EditableTextEditorState(lines: List<String>) : TextEditorState {
    private val _lines = lines.toMutableStateList()
    override val lines get() = _lines.toList()

    private val _selectedIndices = mutableStateListOf(0)
    override val selectedIndices = _selectedIndices

    private val _fields = lines.createInitTextFieldStates().toMutableStateList()
    val fields get() = _fields.toList()

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

        _selectedIndices
            .filter { _fields.getOrNull(it) != null }
            .forEach { index -> _fields[index] = _fields[index].copy(isSelected = false) }
        _selectedIndices.clear()

        _fields[targetIndex] = _fields[targetIndex].copy(isSelected = true)
        _selectedIndices.add(targetIndex)
    }

    private fun List<String>.createInitTextFieldStates(): List<TextFieldState> {
        if (this.isEmpty()) return listOf(TextFieldState(isSelected = false))
        return this.mapIndexed { index, s ->
            TextFieldState(
                value = TextFieldValue(s, TextRange.Zero),
                isSelected = index == 0
            )
        }
    }

    private fun TextFieldValue.splitTextsByNL(): List<TextFieldValue> {
        return this.text.split("\n").map { TextFieldValue(it, TextRange.Zero) }
    }
}
