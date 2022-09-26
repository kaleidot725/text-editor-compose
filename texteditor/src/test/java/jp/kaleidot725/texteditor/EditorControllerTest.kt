package jp.kaleidot725.texteditor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jp.kaleidot725.texteditor.controller.EditorController
import jp.kaleidot725.texteditor.state.TextEditorState
import java.security.InvalidParameterException

class EditorControllerTest : StringSpec({
    "set_multiple_selection_mode" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode shouldBe true

        controller.setMultipleSelectionMode(false)
        controller.isMultipleSelectionMode shouldBe false
    }
    "set_on_changed_text_listener" {
        val controller = EditorController(TextEditorState.create("000\n111\n222".lines()))
        var count = 0

        controller.setOnChangedTextListener { count++ }
        controller.splitAtCursor(0, TextFieldValue("000\n000"))
        controller.deleteField(1)
        controller.updateField(0, TextFieldValue("000"))
        controller.setMultipleSelectionMode(true)
        controller.selectField(1)
        controller.deleteSelectedLines()
        controller.deleteAllLine()
        controller.clearSelectedIndex(0)
        controller.clearSelectedIndices()
        count shouldBe 9
    }
    "delete_all_line" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))
        controller.deleteAllLine()
        controller.fields.count() shouldBe 1
        controller.fields[0].isSelected shouldBe true
        controller.fields[0].value.text shouldBe ""
    }
    "delete_selected_line" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.selectField(1)
        controller.fields.count() shouldBe 3
        controller.fields[0].isSelected shouldBe false
        controller.fields[0].value.text shouldBe "0"
        controller.fields[1].isSelected shouldBe true
        controller.fields[1].value.text shouldBe "1"
        controller.fields[2].isSelected shouldBe false
        controller.fields[2].value.text shouldBe "2"

        controller.deleteSelectedLines()

        controller.fields.count() shouldBe 2
        controller.fields[0].isSelected shouldBe false
        controller.fields[0].value.text shouldBe "0"
        controller.fields[1].isSelected shouldBe false
        controller.fields[1].value.text shouldBe "2"
        controller.selectedIndices.count() shouldBe 0
    }
    "delete_selected_line_on_multiple_selection" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode shouldBe true
        controller.selectField(1)
        controller.selectField(2)

        controller.fields.count() shouldBe 3
        controller.fields[0].isSelected shouldBe true
        controller.fields[0].value.text shouldBe "0"
        controller.fields[1].isSelected shouldBe true
        controller.fields[1].value.text shouldBe "1"
        controller.fields[2].isSelected shouldBe true
        controller.fields[2].value.text shouldBe "2"

        controller.deleteSelectedLines()
        controller.fields.count() shouldBe 0
        controller.selectedIndices.count() shouldBe 0
    }
    "initialize_when_text_is_empty" {
        val controller = EditorController(TextEditorState.create("".lines()))

        controller.isMultipleSelectionMode shouldBe false
        controller.fields.count() shouldBe 1
        controller.fields[0].value shouldBe TextFieldValue(text = "")
        controller.fields[0].isSelected shouldBe true
    }
    "initialize_when_text_is_not_empty" {
        val controller = EditorController(
            TextEditorState.create(
                """
            one
            two
            three
        """.trimIndent().lines()
            )
        )
        controller.isMultipleSelectionMode shouldBe false
        controller.fields.count() shouldBe 3
        controller.fields[0].value shouldBe TextFieldValue(text = "one")
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].value shouldBe TextFieldValue(text = "two")
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].value shouldBe TextFieldValue(text = "three")
        controller.fields[2].isSelected shouldBe false
    }
    "create_text" {
        val controller = EditorController(TextEditorState.create("a\nb\nc".lines()))
        controller.splitNewLine(2, TextFieldValue("c\n"))
        controller.updateField(3, TextFieldValue("d"))

        controller.fields[0].value.text shouldBe "a"
        controller.fields[1].value.text shouldBe "b"
        controller.fields[2].value.text shouldBe "c"
        controller.fields[3].value.text shouldBe "d"
        controller.fields.count() shouldBe 4
    }
    "split_at_cursor" {
        val controller = EditorController(TextEditorState.create("abcdef".lines()))
        controller.splitAtCursor(0, TextFieldValue("abcdef", TextRange(3)))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "abc", selection = TextRange(3))
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "def")
        controller.fields[1].isSelected shouldBe true
    }
    "split_at_cursor_zero" {
        val controller = EditorController(TextEditorState.create("".lines()))
        controller.splitAtCursor(0, TextFieldValue("", TextRange(0)))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "")
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "")
        controller.fields[1].isSelected shouldBe true
    }
    "split_at_cursor_last" {
        val controller = EditorController(TextEditorState.create("".lines()))
        controller.splitAtCursor(0, TextFieldValue("abcdef", TextRange(6)))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "abcdef", selection = TextRange(6))
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "")
        controller.fields[1].isSelected shouldBe true
    }
    "split_at_cursor_when_input_invalid_target_index" {
        val controller = EditorController(TextEditorState.create("abc\ndef".lines()))
        shouldThrow<InvalidParameterException> {
            controller.splitAtCursor(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            controller.splitAtCursor(2, TextFieldValue("yyyy"))
        }
    }
    "split_field_when_field_is_empty" {
        val controller = EditorController(TextEditorState.create("".lines()))
        controller.splitNewLine(targetIndex = 0, TextFieldValue(text = "\n"))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "")
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "")
        controller.fields[1].isSelected shouldBe true
    }
    "split_field_when_field_is_not_empty" {
        val controller = EditorController(TextEditorState.create("aaa".lines()))
        controller.splitNewLine(targetIndex = 0, TextFieldValue(text = "aaa\n"))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "aaa", selection = TextRange("aaa".count()))
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "")
        controller.fields[1].isSelected shouldBe true
    }
    "split_field_in_the_middle_of_the_field" {
        val controller = EditorController(TextEditorState.create("aaaa".lines()))
        controller.splitNewLine(targetIndex = 0, TextFieldValue(text = "aa\naa"))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "aa", selection = TextRange("aa".count()))
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "aa")
        controller.fields[1].isSelected shouldBe true
    }
    "split_field_when_input_several_newlines" {
        val controller = EditorController(TextEditorState.create("".lines()))
        controller.splitNewLine(targetIndex = 0, TextFieldValue(text = "a\nb\nc\nd"))

        controller.fields.count() shouldBe 4
        controller.fields[0].value shouldBe TextFieldValue(text = "a", selection = TextRange("a".count()))
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "b")
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].value shouldBe TextFieldValue(text = "c")
        controller.fields[2].isSelected shouldBe false
        controller.fields[3].value shouldBe TextFieldValue(text = "d")
        controller.fields[3].isSelected shouldBe true
    }
    "split_field_when_input_invalid_target_index" {
        val controller = EditorController(TextEditorState.create("abc\ndef".lines()))
        shouldThrow<InvalidParameterException> {
            controller.splitNewLine(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            controller.splitNewLine(2, TextFieldValue("yyyy"))
        }
    }
    "split_field_when_not_contain_new_line" {
        val controller = EditorController(TextEditorState.create("".lines()))
        shouldThrow<InvalidParameterException> {
            controller.splitNewLine(0, TextFieldValue("xxxx"))
        }
    }
    "update_field_when_field_is_empty" {
        val controller = EditorController(TextEditorState.create("".lines()))
        val newFieldValue = TextFieldValue(text = "abc", selection = TextRange(3))
        controller.updateField(targetIndex = 0, newFieldValue)

        controller.fields.count() shouldBe 1
        controller.fields[0].value shouldBe newFieldValue
    }
    "update_field_when_input_invalid_target_index" {
        val controller = EditorController(TextEditorState.create("abc\ndef".lines()))
        shouldThrow<InvalidParameterException> {
            controller.updateField(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            controller.updateField(2, TextFieldValue("yyyy"))
        }
    }
    "update_field_when_input_newline" {
        val controller = EditorController(TextEditorState.create("abc\ndef".lines()))
        shouldThrow<InvalidParameterException> {
            controller.updateField(0, TextFieldValue("xxxx\nyyyy"))
        }
    }
    "delete_field_when_fields_are_zero" {
        val controller = EditorController(TextEditorState.create("".lines()))
        controller.deleteField(0)
        controller.fields.count() shouldBe 1
        controller.fields[0].value shouldBe TextFieldValue(text = "")
        controller.fields[0].isSelected shouldBe true
    }
    "delete_field_when_fields_are_not_zero" {
        val controller = EditorController(TextEditorState.create("abc\n".lines()))

        controller.selectField(1)

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue("abc")
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue("")
        controller.fields[1].isSelected shouldBe true
        controller.deleteField(1)

        controller.fields.count() shouldBe 1
        controller.fields[0].value shouldBe TextFieldValue(
            text = "abc", selection = TextRange("abc".count())
        )
        controller.fields[0].isSelected shouldBe true
    }
    "delete_field_in_the_middle_of_the_field" {
        val controller = EditorController(TextEditorState.create("abc\ndef".lines()))
        controller.selectField(1)

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue("abc")
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue("def")
        controller.fields[1].isSelected shouldBe true

        controller.deleteField(1)

        controller.fields.count() shouldBe 1
        controller.fields[0].value shouldBe
                TextFieldValue(text = "abcdef", selection = TextRange("abc".count()))
        controller.fields[0].isSelected shouldBe true
    }
    "delete_field_when_input_invalid_target_index" {
        val controller = EditorController(TextEditorState.create("abc\ndef".lines()))
        shouldThrow<InvalidParameterException> {
            controller.splitNewLine(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            controller.splitNewLine(2, TextFieldValue("yyyy"))
        }
    }
    "select_field" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.isMultipleSelectionMode shouldBe false
        controller.selectField(1)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe true
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 1

        controller.selectField(2)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe true
        controller.selectedIndices.count() shouldBe 1

        controller.selectField(0)
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 1
    }
    "select_selected_field" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.isMultipleSelectionMode shouldBe false
        controller.selectField(1)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe true
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 1

        controller.selectField(1)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe true
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 1
    }
    "select_field_when_input_invalid_target_index" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))
        shouldThrow<InvalidParameterException> {
            controller.selectField(-1)
        }
        shouldThrow<InvalidParameterException> {
            controller.selectField(3)
        }
    }
    "select_field_on_multiple_mode" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode shouldBe true

        controller.selectField(1)
        controller.selectField(2)
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].isSelected shouldBe true
        controller.fields[2].isSelected shouldBe true
        controller.selectedIndices.count() shouldBe 3
    }
    "select_selected_field_on_multiple_mode" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode shouldBe true

        controller.selectField(1)
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].isSelected shouldBe true
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 2

        controller.selectField(1)
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 1
    }
    "select_field_when_input_invalid_target_index_on_multiple_mode" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.setMultipleSelectionMode(true)
        shouldThrow<InvalidParameterException> {
            controller.selectField(-1)
        }
        shouldThrow<InvalidParameterException> {
            controller.selectField(3)
        }
    }
    "clear_selected_index" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.clearSelectedIndex(0)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 0

        controller.setMultipleSelectionMode(true)
        controller.selectField(0)
        controller.selectField(1)
        controller.selectField(2)

        controller.clearSelectedIndex(0)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe true
        controller.fields[2].isSelected shouldBe true
        controller.selectedIndices.count() shouldBe 2

        controller.clearSelectedIndex(1)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe true
        controller.selectedIndices.count() shouldBe 1

        controller.clearSelectedIndex(2)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 0
    }
    "clear_selected_indices" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.selectField(1)
        controller.clearSelectedIndices()
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 0

        controller.setMultipleSelectionMode(true)
        controller.selectField(0)
        controller.selectField(1)
        controller.selectField(2)
        controller.clearSelectedIndices()

        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false
        controller.selectedIndices.count() shouldBe 0
    }
    "clear_selected_index_when_toggle_multiple_selection_mode" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode shouldBe true

        controller.selectField(1)
        controller.selectField(2)
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].isSelected shouldBe true
        controller.fields[2].isSelected shouldBe true

        controller.setMultipleSelectionMode(false)
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false

        controller.selectField(0)
        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode shouldBe true
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false
    }
    "select_next_field" {
        val controller = EditorController(TextEditorState.create("\n\n".lines()))

        controller.selectNextField()
        controller.fields.count() shouldBe 3
        controller.fields[0].isSelected shouldBe false
        controller.fields[0].value.text shouldBe ""
        controller.fields[1].isSelected shouldBe true
        controller.fields[1].value.text shouldBe ""
        controller.fields[2].isSelected shouldBe false
        controller.fields[2].value.text shouldBe ""
    }
    "select_next_field_on_max_lines" {
        val controller = EditorController(TextEditorState.create("\n\n".lines()))

        controller.selectField(2)
        controller.selectNextField()

        controller.fields.count() shouldBe 3
        controller.fields[0].isSelected shouldBe false
        controller.fields[0].value.text shouldBe ""
        controller.fields[1].isSelected shouldBe false
        controller.fields[1].value.text shouldBe ""
        controller.fields[2].isSelected shouldBe true
        controller.fields[2].value.text shouldBe ""
    }
    "select_next_field_on_multiple_selection" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))
        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode shouldBe true
        controller.selectField(1)
        controller.selectField(2)

        controller.selectNextField()
        controller.fields.count() shouldBe 3
        controller.fields[0].isSelected shouldBe true
        controller.fields[0].value.text shouldBe "0"
        controller.fields[1].isSelected shouldBe true
        controller.fields[1].value.text shouldBe "1"
        controller.fields[2].isSelected shouldBe true
        controller.fields[2].value.text shouldBe "2"
    }
    "select_previous_field" {
        val controller = EditorController(TextEditorState.create("\n\n".lines()))

        controller.selectField(2)
        controller.selectPreviousField()

        controller.fields.count() shouldBe 3
        controller.fields[0].isSelected shouldBe false
        controller.fields[0].value.text shouldBe ""
        controller.fields[1].isSelected shouldBe true
        controller.fields[1].value.text shouldBe ""
        controller.fields[2].isSelected shouldBe false
        controller.fields[2].value.text shouldBe ""
    }
    "select_previous_field_on_min_lines" {
        val controller = EditorController(TextEditorState.create("\n\n".lines()))

        controller.selectPreviousField()

        controller.fields.count() shouldBe 3
        controller.fields[0].isSelected shouldBe true
        controller.fields[0].value.text shouldBe ""
        controller.fields[1].isSelected shouldBe false
        controller.fields[1].value.text shouldBe ""
        controller.fields[2].isSelected shouldBe false
        controller.fields[2].value.text shouldBe ""
    }
    "select_previous_field_on_multiple_selection" {
        val controller = EditorController(TextEditorState.create("0\n1\n2".lines()))
        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode shouldBe true
        controller.selectField(1)
        controller.selectField(2)

        controller.selectPreviousField()
        controller.fields.count() shouldBe 3
        controller.fields[0].isSelected shouldBe true
        controller.fields[0].value.text shouldBe "0"
        controller.fields[1].isSelected shouldBe true
        controller.fields[1].value.text shouldBe "1"
        controller.fields[2].isSelected shouldBe true
        controller.fields[2].value.text shouldBe "2"
    }
})
