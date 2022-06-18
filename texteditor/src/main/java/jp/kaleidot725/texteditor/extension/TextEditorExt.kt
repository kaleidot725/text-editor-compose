package jp.kaleidot725.texteditor.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import jp.kaleidot725.texteditor.state.EditableTextEditorState
import jp.kaleidot725.texteditor.state.TextEditorState

@Composable
fun rememberTextEditorState(lines: List<String>) = remember {
    mutableStateOf<TextEditorState>(EditableTextEditorState(lines))
}

internal fun TextEditorState.toEditable(): EditableTextEditorState {
    return this as EditableTextEditorState
}
