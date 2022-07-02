package jp.kaleidot725.texteditor.factory

import jp.kaleidot725.texteditor.controller.EditableTextEditorController
import jp.kaleidot725.texteditor.controller.TextEditorController

object TextEditorStateFactory {
    fun create(text: String): TextEditorController {
        return EditableTextEditorController(text.lines())
    }

    fun create(lines: List<String>): TextEditorController {
        return EditableTextEditorController(lines)
    }
}