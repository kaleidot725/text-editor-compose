package jp.kaleidot725.texteditor

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jp.kaleidot725.texteditor.state.TextEditorState

class TextEditorStateTest : StringSpec({
    "get_all_test" {
        val state = TextEditorState.create("0\n1\n2")
        val actual = state.getAllText()
        actual shouldBe "0\n1\n2"
    }
    "get_selected_text" {
        val state = TextEditorState.create("0\n1\n2").copy(selectedIndices = listOf(0))
        val actual = state.getSelectedText()
        actual shouldBe "0"
    }
    "get_selected_text_on_multiple_selection" {
        val state = TextEditorState.create("0\n1\n2").copy(selectedIndices = listOf(0, 1, 2))
        val actual = state.getSelectedText()
        actual shouldBe "0\n1\n2"
    }
})
