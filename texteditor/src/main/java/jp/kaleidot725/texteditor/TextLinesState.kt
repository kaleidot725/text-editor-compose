package jp.kaleidot725.texteditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList

data class TextEditorState(
    private val _text: String,
    private val _lines: MutableList<String> = _text.lines().toMutableStateList(),
) {
    val value get() = _lines.toList()
    val text get() = value.fold("") { text, line -> text + "$line\n" }

    fun updateLineText(index: Int, lineText: String) {
        _lines.removeAt(index)
        _lines.add(index, lineText)
    }

    fun inputNewLineKey(index: Int) {
        if (value.lastIndex == index) _lines.add("")
    }

    fun inputBackKey(index: Int, onRemovedLine: () -> Unit) {
        if(value.lastIndex == 0) return

        if (value[index].isNotEmpty()) {
            val newValue = _lines[index - 1] + _lines[index]
            _lines.removeAt(index -1)
            _lines.add(index - 1, newValue)
        }

        onRemovedLine()
        _lines.removeAt(index)
    }
}

@Composable
fun rememberTextLinesState(text: String) = remember {
    mutableStateOf(TextEditorState(text))
}
