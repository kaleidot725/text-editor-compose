package jp.kaleidot725.texteditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList

data class TextEditorState(
    private val _text: String,
    private val _lines: MutableList<String> = _text.lines().toMutableStateList(),
    private var _selectedIndex: MutableState<Int> = mutableStateOf(0)
) {
    val lines get() = _lines.toList()
    val text get() = lines.fold("") { text, line -> text + "$line\n" }
    val selectedIndex: State<Int> get() = _selectedIndex

    fun addNewline() {
        if (_selectedIndex.value == lines.lastIndex) _lines.add("")
        _selectedIndex.value = _selectedIndex.value + 1
    }

    fun removeLine(index: Int) {
        if(lines.lastIndex == 0) return
        _lines.removeAt(index)
        _selectedIndex.value = _selectedIndex.value - 1
    }

    fun updateLineText(index: Int, lineText: String) {
        _lines.removeAt(index)
        _lines.add(index, lineText)
    }

    fun selectLine(index: Int) {
        _selectedIndex.value = index
    }
}

@Composable
fun rememberTextEditorState(text: String) = remember {
    mutableStateOf(TextEditorState(text))
}
