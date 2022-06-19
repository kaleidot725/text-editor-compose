package jp.kaleidot725.texteditor.state

import androidx.compose.runtime.Stable

@Stable
interface TextEditorState {
    val lines: List<String>
    val selectedIndices: List<Int>
    fun createText(): String
}
