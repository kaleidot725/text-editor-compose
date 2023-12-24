package jp.kaleidot725.texteditor.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import jp.kaleidot725.texteditor.controller.EditorController.Companion.createInitTextFieldStates

@Immutable
data class TextEditorState(
    val fields: List<TextFieldState>,
    val selectedIndices: List<Int>,
    val isMultipleSelectionMode: Boolean,
    val textStyle: TextStyle,
    val textSelectedStyle: TextStyle
) {
    fun getAllText(): String {
        return fields.map { it.value.text }.foldIndexed("") { index, acc, s ->
            if (index == 0) acc + s else acc + "\n" + s
        }
    }

    fun getSelectedText(): String {
        val lines = fields.map { it.value.text }
        val targets = selectedIndices.sortedBy { it }.mapNotNull { lines.getOrNull(it) }
        return targets.foldIndexed("") { index, acc, s ->
            if (index == 0) acc + s else acc + "\n" + s
        }
    }

    companion object {
        fun create(text: String, textStyle: TextStyle = TextStyle(), textSelectedStyle: TextStyle = TextStyle()): TextEditorState {
            return TextEditorState(
                fields = text.lines().createInitTextFieldStates(textStyle, textSelectedStyle),
                selectedIndices = listOf(-1),
                isMultipleSelectionMode = false,
                textStyle = textStyle,
                textSelectedStyle = textSelectedStyle
            )
        }

        fun create(lines: List<String>, textStyle: TextStyle = TextStyle(), textSelectedStyle: TextStyle = TextStyle()): TextEditorState {
            return TextEditorState(
                fields = lines.createInitTextFieldStates(textStyle, textSelectedStyle),
                selectedIndices = listOf(-1),
                isMultipleSelectionMode = false,
                textStyle = textStyle,
                textSelectedStyle = textSelectedStyle
            )
        }
    }
}
