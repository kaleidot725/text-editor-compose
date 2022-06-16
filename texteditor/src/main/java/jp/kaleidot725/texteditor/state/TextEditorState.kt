package jp.kaleidot725.texteditor.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.security.InvalidParameterException

@Composable
fun rememberTextEditorState(text: String) = remember {
    mutableStateOf(TextEditorState(text))
}

@Stable
data class TextEditorState(private val text: String) {
    private val selectedIndexMarks: MutableList<Int> = mutableListOf(0)

    private val _fields = text.createInitTextFieldStates().toMutableStateList()
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

        val newSplitFieldValues = splitFieldValues.subList(1, splitFieldValues.count())
        val newSplitFieldStates = newSplitFieldValues.map { TextFieldState(value = it, isSelected = false) }
        _fields.addAll(targetIndex + 1, newSplitFieldStates)

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
        _fields.removeAt(targetIndex)
        selectField(targetIndex - 1)
    }

    fun selectField(targetIndex: Int) {
        if (targetIndex < 0 || fields.count() <= targetIndex) {
            throw InvalidParameterException("targetIndex out of range($targetIndex)")
        }

        selectedIndexMarks
            .filter { _fields.getOrNull(it) != null }
            .forEach { index -> _fields[index] = _fields[index].copy(isSelected = false) }
        selectedIndexMarks.clear()

        _fields[targetIndex] = _fields[targetIndex].copy(isSelected = true)
        selectedIndexMarks.add(targetIndex)
    }

    private fun String.createInitTextFieldStates(): List<TextFieldState> {
        if (this.lines().isEmpty()) return listOf(TextFieldState(isSelected = false))
        return this.lines().mapIndexed { index, s ->
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
