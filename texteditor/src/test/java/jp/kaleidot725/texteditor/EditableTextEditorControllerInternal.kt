package jp.kaleidot725.texteditor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jp.kaleidot725.texteditor.controller.EditableTextEditorController
import java.security.InvalidParameterException

class EditableTextEditorControllerInternalTest : StringSpec({
    "initialize_when_text_is_empty" {
        val controller = EditableTextEditorController("".lines())

        controller.isMultipleSelectionMode.value shouldBe false
        controller.fields.count() shouldBe 1
        controller.fields[0].value shouldBe TextFieldValue(text = "")
        controller.fields[0].isSelected shouldBe true
    }
    "initialize_when_text_is_not_empty" {
        val controller = EditableTextEditorController(
            """
            one
            two
            three
        """.trimIndent().lines()
        )

        controller.isMultipleSelectionMode.value shouldBe false
        controller.fields.count() shouldBe 3
        controller.fields[0].value shouldBe TextFieldValue(text = "one")
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].value shouldBe TextFieldValue(text = "two")
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].value shouldBe TextFieldValue(text = "three")
        controller.fields[2].isSelected shouldBe false
    }
    "create_text" {
        val controller = EditableTextEditorController("a\nb\nc".lines())
        controller.splitField(2, TextFieldValue("c\n"))
        controller.updateField(3, TextFieldValue("d"))
        val actual = controller.getAllText()
        actual shouldBe "a\nb\nc\nd"
    }
    "split_field_when_field_is_empty" {
        val controller = EditableTextEditorController("".lines())
        controller.splitField(targetIndex = 0, TextFieldValue(text = "\n"))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "")
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "")
        controller.fields[1].isSelected shouldBe true
    }
    "split_field_when_field_is_not_empty" {
        val controller = EditableTextEditorController("aaa".lines())
        controller.splitField(targetIndex = 0, TextFieldValue(text = "aaa\n"))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "aaa")
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "")
        controller.fields[1].isSelected shouldBe true
    }
    "split_field_in_the_middle_of_the_field" {
        val controller = EditableTextEditorController("aaaa".lines())
        controller.splitField(targetIndex = 0, TextFieldValue(text = "aa\naa"))

        controller.fields.count() shouldBe 2
        controller.fields[0].value shouldBe TextFieldValue(text = "aa")
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "aa")
        controller.fields[1].isSelected shouldBe true
    }
    "split_field_when_input_several_newlines" {
        val controller = EditableTextEditorController("".lines())
        controller.splitField(targetIndex = 0, TextFieldValue(text = "a\nb\nc\nd"))

        controller.fields.count() shouldBe 4
        controller.fields[0].value shouldBe TextFieldValue(text = "a")
        controller.fields[0].isSelected shouldBe false
        controller.fields[1].value shouldBe TextFieldValue(text = "b")
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].value shouldBe TextFieldValue(text = "c")
        controller.fields[2].isSelected shouldBe false
        controller.fields[3].value shouldBe TextFieldValue(text = "d")
        controller.fields[3].isSelected shouldBe true
    }
    "split_field_when_input_invalid_target_index" {
        val controller = EditableTextEditorController("abc\ndef".lines())
        shouldThrow<InvalidParameterException> {
            controller.splitField(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            controller.splitField(2, TextFieldValue("yyyy"))
        }
    }
    "split_field_when_not_contain_new_line" {
        val controller = EditableTextEditorController("".lines())
        shouldThrow<InvalidParameterException> {
            controller.splitField(0, TextFieldValue("xxxx"))
        }
    }
    "update_field_when_field_is_empty" {
        val controller = EditableTextEditorController("".lines())
        val newFieldValue = TextFieldValue(text = "abc", selection = TextRange(3))
        controller.updateField(targetIndex = 0, newFieldValue)

        controller.fields.count() shouldBe 1
        controller.fields[0].value shouldBe newFieldValue
    }
    "update_field_when_input_invalid_target_index" {
        val controller = EditableTextEditorController("abc\ndef".lines())
        shouldThrow<InvalidParameterException> {
            controller.updateField(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            controller.updateField(2, TextFieldValue("yyyy"))
        }
    }
    "update_field_when_input_newline" {
        val controller = EditableTextEditorController("abc\ndef".lines())
        shouldThrow<InvalidParameterException> {
            controller.updateField(0, TextFieldValue("xxxx\nyyyy"))
        }
    }
    "delete_field_when_fields_are_zero" {
        val controller = EditableTextEditorController("".lines())
        controller.deleteField(0)
        controller.fields.count() shouldBe 1
        controller.fields[0].value shouldBe TextFieldValue(text = "")
        controller.fields[0].isSelected shouldBe true
    }
    "delete_field_when_fields_are_not_zero" {
        val controller = EditableTextEditorController("abc\n".lines())

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
        val controller = EditableTextEditorController("abc\ndef".lines())
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
        val controller = EditableTextEditorController("abc\ndef".lines())
        shouldThrow<InvalidParameterException> {
            controller.splitField(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            controller.splitField(2, TextFieldValue("yyyy"))
        }
    }
    "select_field" {
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.isMultipleSelectionMode.value shouldBe false
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
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.isMultipleSelectionMode.value shouldBe false
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
        val controller = EditableTextEditorController("0\n1\n2".lines())
        shouldThrow<InvalidParameterException> {
            controller.selectField(-1)
        }
        shouldThrow<InvalidParameterException> {
            controller.selectField(3)
        }
    }
    "select_field_on_multiple_mode" {
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode.value shouldBe true

        controller.selectField(1)
        controller.selectField(2)
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].isSelected shouldBe true
        controller.fields[2].isSelected shouldBe true
        controller.selectedIndices.count() shouldBe 3
    }
    "select_selected_field_on_multiple_mode" {
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode.value shouldBe true

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
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.setMultipleSelectionMode(true)
        shouldThrow<InvalidParameterException> {
            controller.selectField(-1)
        }
        shouldThrow<InvalidParameterException> {
            controller.selectField(3)
        }
    }
    "clear_selected_index_when_toggle_multiple_selection_mode" {
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode.value shouldBe true

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
        controller.isMultipleSelectionMode.value shouldBe true
        controller.fields[0].isSelected shouldBe true
        controller.fields[1].isSelected shouldBe false
        controller.fields[2].isSelected shouldBe false
    }
    "select_next_field" {
        val controller = EditableTextEditorController("\n\n".lines())

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
        val controller = EditableTextEditorController("\n\n".lines())

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
        val controller = EditableTextEditorController("0\n1\n2".lines())
        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode.value shouldBe true
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
        val controller = EditableTextEditorController("\n\n".lines())

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
        val controller = EditableTextEditorController("\n\n".lines())

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
        val controller = EditableTextEditorController("0\n1\n2".lines())
        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode.value shouldBe true
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

class EditableTextEditorControllerExternalTest : StringSpec({
    "set_multiple_selection_mode" {
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode.value shouldBe  true

        controller.setMultipleSelectionMode(false)
        controller.isMultipleSelectionMode.value shouldBe  false
    }
    "set_on_changed_text_listener" {
        val controller = EditableTextEditorController("000\n111\n222".lines())
        var count = 0

        controller.setOnChangedTextListener { count++ }
        controller.splitField(0, TextFieldValue("000\n000"))
        controller.deleteField(1)
        controller.updateField(0, TextFieldValue("000"))
        controller.setMultipleSelectionMode(true)
        controller.selectField(1)
        controller.deleteSelectedLines()
        controller.deleteAllLine()
        count shouldBe 7
    }

    "get_all_test" {
        val controller = EditableTextEditorController("0\n1\n2".lines())
        val actual = controller.getAllText()
        actual shouldBe  "0\n1\n2"
    }
    "get_selected_text" {
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.selectField(1)
        val actual = controller.getSelectedText()
        actual shouldBe "1"
    }
    "get_selected_text_on_multiple_selection" {
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode.value shouldBe true

        controller.selectField(1)
        controller.selectField(2)
        val actual = controller.getSelectedText()
        actual shouldBe "0\n1\n2"
    }
    "delete_all_line" {
        val controller = EditableTextEditorController("0\n1\n2".lines())
        controller.deleteAllLine()
        controller.fields.count() shouldBe  1
        controller.fields[0].isSelected shouldBe true
        controller.fields[0].value.text shouldBe ""
    }
    "delete_selected_line" {
        val controller = EditableTextEditorController("0\n1\n2".lines())

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
        val controller = EditableTextEditorController("0\n1\n2".lines())

        controller.setMultipleSelectionMode(true)
        controller.isMultipleSelectionMode.value shouldBe true
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
})
