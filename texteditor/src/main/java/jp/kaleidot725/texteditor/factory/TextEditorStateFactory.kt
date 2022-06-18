package jp.kaleidot725.texteditor.factory

import jp.kaleidot725.texteditor.state.EditableTextEditorState
import jp.kaleidot725.texteditor.state.TextEditorState

object TextEditorStateFactory {
    fun create(text: String): TextEditorState {
        return EditableTextEditorState(text.lines())
    }

    fun create(lines: List<String>): TextEditorState {
        return EditableTextEditorState(lines)
    }
}