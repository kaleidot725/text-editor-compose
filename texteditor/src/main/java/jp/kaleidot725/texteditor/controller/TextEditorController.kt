package jp.kaleidot725.texteditor.controller

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State

@Stable
interface TextEditorController {
    val isMultipleSelectionMode: State<Boolean>

    // Config
    fun setMultipleSelectionMode(value: Boolean)
    fun setOnChangedTextListener(onChanged: () -> Unit)

    // Get
    fun getAllText(): String
    fun getSelectedText(): String

    // Delete
    fun deleteAllLine()
    fun deleteSelectedLines()
}