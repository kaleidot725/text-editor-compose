package jp.kaleidot725.texteditor

import androidx.compose.ui.text.input.TextFieldValue
import java.util.UUID

data class TextLineState(
    val id: String = UUID.randomUUID().toString(),
    val value: TextFieldValue,
    val isSelected: Boolean
)
