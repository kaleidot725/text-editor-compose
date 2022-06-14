package jp.kaleidot725.texteditor

import androidx.compose.ui.text.input.TextFieldValue

data class TextLineState(
    val value: TextFieldValue,
    val isSelected: Boolean
)
