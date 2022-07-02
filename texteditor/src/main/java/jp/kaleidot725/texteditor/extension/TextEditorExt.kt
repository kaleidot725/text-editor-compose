package jp.kaleidot725.texteditor.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import jp.kaleidot725.texteditor.controller.TextEditorController
import jp.kaleidot725.texteditor.factory.TextEditorStateFactory

@Composable
fun rememberTextEditorController(text: String, onChanged: (controller: TextEditorController) -> Unit) = remember {
    mutableStateOf(
        TextEditorStateFactory.create(text).apply {
            setOnChangedTextListener { onChanged(this) }
        }
    )
}

@Composable
fun rememberTextEditorController(lines: List<String>, onChanged: (controller: TextEditorController) -> Unit) = remember {
    mutableStateOf(
        TextEditorStateFactory.create(lines).apply {
            setOnChangedTextListener { onChanged(this) }
        }
    )
}
