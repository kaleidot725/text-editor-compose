package jp.kaleidot725.texteditor

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import java.util.UUID

@Stable
data class TextLineState(
    val id: String = UUID.randomUUID().toString(),
    val value: TextFieldValue = TextFieldValue(),
    val isSelected: Boolean
)
