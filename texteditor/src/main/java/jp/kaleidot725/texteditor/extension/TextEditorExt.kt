package jp.kaleidot725.texteditor.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import jp.kaleidot725.texteditor.controller.EditorController
import jp.kaleidot725.texteditor.state.TextEditorState

@Composable
fun rememberTextEditorController(
    state: TextEditorState,
    onChanged: (editorState: TextEditorState) -> Unit
) = remember {
    mutableStateOf(
        EditorController(state).apply {
            setOnChangedTextListener { onChanged(it) }
        }
    )
}
