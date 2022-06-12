package jp.kaleidot725.texteditor

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun TextEditor(modifier: Modifier = Modifier) {
    val textEditorState by rememberTextEditorState("")

    Column(modifier = modifier) {
        textEditorState.lines.forEachIndexed { index, text ->
            TextLine(
                number = index + 1,
                text = text,
                isSelected = textEditorState.selectedIndex.value == index,
                onChangedText = { newText -> textEditorState.updateLineText(index, newText) },
                onNextLine = { textEditorState.addNewline() },
                onRemovedLine = { textEditorState.removeLine(index) },
                onFocus = { textEditorState.selectLine(index) }
            )
        }
    }
}