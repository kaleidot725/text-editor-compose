package jp.kaleidot725.texteditor.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State

@Stable
interface TextEditorState {
    val lines: List<String>
    val selectedIndices: List<Int>
    val isMultipleSelectionMode: State<Boolean>

    fun createText(): String
    fun enableMultipleSelectionMode(value: Boolean)
}
