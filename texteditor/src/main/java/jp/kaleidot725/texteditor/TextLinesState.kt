package jp.kaleidot725.texteditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

data class TextEditorState(private val _text: String) {
    private val _lines: MutableList<TextFieldValue> =
        _text.lines().map { TextFieldValue(it, TextRange.Zero) }.toMutableStateList()
    val lines get() = _lines.toList()

    private val _canDeleteBackKeyList : MutableList<Int> = mutableListOf()


    fun updateLine(targetIndex: Int, line: TextFieldValue) : Int {
        val lines =  if(line.containNewLine()) line.splitTextsByNewLine() else listOf(line)
        _lines.removeAt(targetIndex)
        _lines.addAll(targetIndex, lines)
        return targetIndex + lines.count() - 1
    }

    fun inputBackKey(targetIndex: Int, onRemovedLine: () -> Unit) {
        if(lines[targetIndex].selection != TextRange.Zero){
            return
        }

        if (!_canDeleteBackKeyList.contains(targetIndex)) {
            _canDeleteBackKeyList.add(targetIndex)
            return
        }

        val newValue = _lines[targetIndex - 1].text + _lines[targetIndex].text
        _lines.removeAt(targetIndex -1)
        _lines.add(targetIndex - 1, TextFieldValue(newValue, TextRange(newValue.count())))
        _lines.removeAt(targetIndex)
        _canDeleteBackKeyList.remove(targetIndex)

        onRemovedLine()
    }

    private fun TextFieldValue.containNewLine(): Boolean {
        return text.contains("\n")
    }

    private fun TextFieldValue.splitTextsByNewLine() : List<TextFieldValue>{
        return text.split("\n").map { TextFieldValue(it, TextRange.Zero) }
    }
}

@Composable
fun rememberTextLinesState(text: String) = remember {
    mutableStateOf(TextEditorState(text))
}
