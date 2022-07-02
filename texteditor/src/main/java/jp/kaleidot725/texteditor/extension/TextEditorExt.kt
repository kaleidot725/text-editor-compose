package jp.kaleidot725.texteditor.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import jp.kaleidot725.texteditor.factory.TextEditorStateFactory

@Composable
fun rememberTextEditorController(lines: List<String>, onChanged: () -> Unit) = remember {
    mutableStateOf(
        TextEditorStateFactory.create(lines).apply {
            setOnChangedTextListener(onChanged)
        }
    )
}
