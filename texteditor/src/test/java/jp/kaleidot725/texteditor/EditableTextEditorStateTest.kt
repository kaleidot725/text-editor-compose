package jp.kaleidot725.texteditor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jp.kaleidot725.texteditor.state.EditableTextEditorState
import java.security.InvalidParameterException

class EditableTextEditorStateTest : StringSpec({
    "initialize_when_text_is_empty" {
        val state = EditableTextEditorState("".lines())

        state.fields.count() shouldBe 1
        state.fields[0].value shouldBe TextFieldValue(text = "")
        state.fields[0].isSelected shouldBe true
    }
    "initialize_when_text_is_not_empty" {
        val state = EditableTextEditorState("""
            one
            two
            three
        """.trimIndent().lines())

        state.fields.count() shouldBe 3
        state.fields[0].value shouldBe TextFieldValue(text = "one")
        state.fields[0].isSelected shouldBe true
        state.fields[1].value shouldBe TextFieldValue(text = "two")
        state.fields[1].isSelected shouldBe false
        state.fields[2].value shouldBe TextFieldValue(text = "three")
        state.fields[2].isSelected shouldBe false

        state.lines.count() shouldBe 3
        state.lines[0] shouldBe "one"
        state.lines[1] shouldBe "two"
        state.lines[2] shouldBe "three"
    }
    "split_field_when_field_is_empty" {
        val state = EditableTextEditorState("".lines())
        state.splitField(targetIndex = 0, TextFieldValue(text = "\n"))

        state.fields.count() shouldBe 2
        state.fields[0].value shouldBe TextFieldValue(text = "")
        state.fields[0].isSelected shouldBe  false
        state.fields[1].value shouldBe TextFieldValue(text = "")
        state.fields[1].isSelected shouldBe true

        state.lines.count() shouldBe 2
        state.lines[0] shouldBe ""
        state.lines[1] shouldBe ""
    }
    "split_field_when_field_is_not_empty" {
        val state = EditableTextEditorState("aaa".lines())
        state.splitField(targetIndex = 0, TextFieldValue(text = "aaa\n"))

        state.fields.count() shouldBe 2
        state.fields[0].value shouldBe TextFieldValue(text = "aaa")
        state.fields[0].isSelected shouldBe  false
        state.fields[1].value shouldBe TextFieldValue(text = "")
        state.fields[1].isSelected shouldBe true

        state.lines.count() shouldBe 2
        state.lines[0] shouldBe "aaa"
        state.lines[1] shouldBe ""
    }
    "split_field_in_the_middle_of_the_field" {
        val state = EditableTextEditorState("aaaa".lines())
        state.splitField(targetIndex = 0, TextFieldValue(text = "aa\naa"))

        state.fields.count() shouldBe 2
        state.fields[0].value shouldBe TextFieldValue(text = "aa")
        state.fields[0].isSelected shouldBe  false
        state.fields[1].value shouldBe TextFieldValue(text = "aa")
        state.fields[1].isSelected shouldBe true

        state.lines.count() shouldBe 2
        state.lines[0] shouldBe "aa"
        state.lines[1] shouldBe "aa"
    }
    "split_field_when_input_several_newlines" {
        val state = EditableTextEditorState("".lines())
        state.splitField(targetIndex = 0, TextFieldValue(text = "a\nb\nc\nd"))

        state.fields.count() shouldBe 4
        state.fields[0].value shouldBe TextFieldValue(text = "a")
        state.fields[0].isSelected shouldBe  false
        state.fields[1].value shouldBe TextFieldValue(text = "b")
        state.fields[1].isSelected shouldBe  false
        state.fields[2].value shouldBe TextFieldValue(text = "c")
        state.fields[2].isSelected shouldBe  false
        state.fields[3].value shouldBe TextFieldValue(text = "d")
        state.fields[3].isSelected shouldBe true

        state.lines.count() shouldBe 4
        state.lines[0] shouldBe "a"
        state.lines[1] shouldBe "b"
        state.lines[2] shouldBe "c"
        state.lines[3] shouldBe "d"
    }
    "split_field_when_input_invalid_target_index" {
        val state = EditableTextEditorState("abc\ndef".lines())
        shouldThrow<InvalidParameterException> {
            state.splitField(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            state.splitField(2, TextFieldValue("yyyy"))
        }
    }
    "split_field_when_not_contain_new_line" {
        val state = EditableTextEditorState("".lines())
        shouldThrow<InvalidParameterException> {
            state.splitField(0, TextFieldValue("xxxx"))
        }
    }
    "update_field_when_field_is_empty" {
        val state = EditableTextEditorState("".lines())
        val newFieldValue = TextFieldValue(text = "abc", selection = TextRange(3))
        state.updateField(targetIndex = 0, newFieldValue)

        state.fields.count() shouldBe 1
        state.fields[0].value shouldBe newFieldValue

        state.lines.count() shouldBe 1
        state.lines[0] shouldBe newFieldValue.text
    }
    "update_field_when_input_invalid_target_index" {
        val state = EditableTextEditorState("abc\ndef".lines())
        shouldThrow<InvalidParameterException> {
            state.updateField(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            state.updateField(2, TextFieldValue("yyyy"))
        }
    }
    "update_field_when_input_newline" {
        val state = EditableTextEditorState("abc\ndef".lines())
        shouldThrow<InvalidParameterException> {
            state.updateField(0, TextFieldValue("xxxx\nyyyy"))
        }
    }
    "delete_field_when_fields_are_zero" {
        val state = EditableTextEditorState("".lines())
        state.deleteField(0)
        state.fields.count() shouldBe 1
        state.fields[0].value shouldBe TextFieldValue(text = "")
        state.fields[0].isSelected shouldBe true

        state.lines.count() shouldBe 1
        state.lines[0] shouldBe ""
    }
    "delete_field_when_fields_are_not_zero" {
        val state = EditableTextEditorState("abc\n".lines())
        state.selectField(1)

        state.fields.count() shouldBe 2
        state.fields[0].value shouldBe TextFieldValue("abc")
        state.fields[0].isSelected shouldBe false
        state.fields[1].value shouldBe TextFieldValue("")
        state.fields[1].isSelected shouldBe true
        state.lines.count() shouldBe 2
        state.lines[0] shouldBe "abc"
        state.lines[1] shouldBe ""

        state.deleteField(1)

        state.fields.count() shouldBe 1
        state.fields[0].value shouldBe TextFieldValue(
            text ="abc", selection = TextRange("abc".count())
        )
        state.fields[0].isSelected shouldBe  true
        state.lines.count() shouldBe 1
        state.lines[0] shouldBe "abc"
    }
    "delete_field_in_the_middle_of_the_field" {
        val state = EditableTextEditorState("abc\ndef".lines())
        state.selectField(1)

        state.fields.count() shouldBe 2
        state.fields[0].value shouldBe TextFieldValue("abc")
        state.fields[0].isSelected shouldBe false
        state.fields[1].value shouldBe TextFieldValue("def")
        state.fields[1].isSelected shouldBe true
        state.lines.count() shouldBe 2
        state.lines[0] shouldBe "abc"
        state.lines[1] shouldBe "def"

        state.deleteField(1)

        state.fields.count() shouldBe 1
        state.fields[0].value shouldBe
                TextFieldValue(text ="abcdef", selection = TextRange("abc".count()))
        state.fields[0].isSelected shouldBe  true
        state.lines.count() shouldBe 1
        state.lines[0] shouldBe "abcdef"
    }
    "delete_field_when_input_invalid_target_index" {
        val state = EditableTextEditorState("abc\ndef".lines())
        shouldThrow<InvalidParameterException> {
            state.splitField(-1, TextFieldValue("xxxx"))
        }
        shouldThrow<InvalidParameterException> {
            state.splitField(2, TextFieldValue("yyyy"))
        }
    }
    "select_field" {
        val state = EditableTextEditorState("0\n1\n2".lines())

        state.selectField(1)
        state.fields[0].isSelected shouldBe false
        state.fields[1].isSelected shouldBe true
        state.fields[2].isSelected shouldBe false

        state.selectField(2)
        state.fields[0].isSelected shouldBe false
        state.fields[1].isSelected shouldBe false
        state.fields[2].isSelected shouldBe true

        state.selectField(0)
        state.fields[0].isSelected shouldBe true
        state.fields[1].isSelected shouldBe false
        state.fields[2].isSelected shouldBe false
    }
    "select_field_when_input_invalid_target_index" {
        val state = EditableTextEditorState("0\n1\n2".lines())
        shouldThrow<InvalidParameterException> {
            state.selectField(-1)
        }
        shouldThrow<InvalidParameterException> {
            state.selectField(3)
        }
    }
})
