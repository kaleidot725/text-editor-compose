package jp.kaleidot725.texteditor.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import jp.kaleidot725.texteditor.state.TextEditorState

@Composable
fun rememberTextEditorState(text: String) = remember {
    mutableStateOf(TextEditorState(text))
}
