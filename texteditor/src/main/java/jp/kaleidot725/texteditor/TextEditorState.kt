package jp.kaleidot725.texteditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun rememberTextEditorState(text: String) = remember {
    mutableStateOf(TextEditorState(text))
}

@Stable
data class TextEditorState(private val text: String) {
    private val selectedIndexMarks: MutableList<Int> = mutableListOf(0)

    private val _fields = text.createInitTextFieldStates().toMutableStateList()
    val fields get() = _fields.toList()

    fun addNewLine(targetIndex: Int, textFieldValue: TextFieldValue) {
        val newFieldValues = textFieldValue.splitTextsByNL()
        _fields[targetIndex] = _fields[targetIndex].copy(value = newFieldValues.first(), isSelected = false)

        val tests = newFieldValues.subList(1, newFieldValues.count()).map { TextLineState(value = it, isSelected = false) }
        _fields.addAll(targetIndex + 1, tests)

        selectLine(targetIndex + 1)
    }

    fun updateLine(targetIndex: Int, textFieldValue: TextFieldValue) {
        _fields[targetIndex] = _fields[targetIndex].copy(value = textFieldValue)
    }

    fun deleteNewLine(targetIndex: Int) {
        if (targetIndex == 0) return

        val toText = _fields[targetIndex - 1].value.text
        val fromText = _fields[targetIndex].value.text
        val newText = toText + fromText
        val newFieldValue = TextFieldValue(newText, TextRange(toText.count()))
        val newFieldState = _fields[targetIndex - 1].copy(value = newFieldValue, isSelected = true)

        selectedIndexMarks.add(targetIndex - 1)
        _fields[targetIndex - 1] = newFieldState
        _fields.removeAt(targetIndex)
    }

    fun selectLine(targetIndex: Int) {
        selectedIndexMarks.forEach() { mark ->
            val old = _fields.getOrNull(mark)?.copy(isSelected = false) ?: return@forEach
            _fields[mark] = old
        }
        selectedIndexMarks.clear()

        val new = _fields.getOrNull(targetIndex)?.copy(isSelected = true)
        new?.let { _fields[targetIndex] = it }

        selectedIndexMarks.add(targetIndex)
    }

    private fun String.createInitTextFieldStates(): List<TextLineState> {
        if (this.lines().isEmpty()) return listOf(TextLineState(isSelected = false))
        return this.lines().mapIndexed { index, s ->
            TextLineState(
                value = TextFieldValue(s, TextRange.Zero),
                isSelected = index == 0
            )
        }
    }

    private fun TextFieldValue.splitTextsByNL(): List<TextFieldValue> {
        return this.text.split("\n").map { TextFieldValue(it, TextRange.Zero) }
    }
}
