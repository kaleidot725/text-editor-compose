package jp.kaleidot725.sample.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kaleidot725.sample.ui.extension.createCancelledState
import jp.kaleidot725.sample.ui.extension.createCopiedState
import jp.kaleidot725.sample.ui.extension.createDeletedState
import jp.kaleidot725.sample.ui.extension.createMultipleSelectionModeState
import jp.kaleidot725.sample.ui.theme.DemoText
import jp.kaleidot725.texteditor.state.TextEditorState
import jp.kaleidot725.texteditor.view.TextEditor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Demo(text: String) {
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var textEditorState by remember { mutableStateOf(TextEditorState.create(text)) }
    val bottomPadding = if (textEditorState.isMultipleSelectionMode) 100.dp else 0.dp
    val contentBottomPaddingValue = with(LocalDensity.current) { WindowInsets.ime.getBottom(this).toDp() }
    val contentPaddingValues = PaddingValues(bottom = contentBottomPaddingValue)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        TextEditor(
            textEditorState = textEditorState,
            onChanged = { textEditorState = it },
            contentPaddingValues = contentPaddingValues,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) { index, isSelected, innerTextField ->
            // TextLine
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(getBackgroundColor(isSelected))
            ) {
                // TextLine Number
                Text(
                    text = getLineNumber(index),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                // TextField
                innerTextField(
                    modifier = Modifier
                        .weight(0.9f, true)
                        .align(Alignment.CenterVertically)
                )

                // TextFieldMenu
                FieldIcon(
                    isMultipleSelection = textEditorState.isMultipleSelectionMode,
                    isSelected = isSelected,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp)
                        .align(Alignment.CenterVertically)
                        .focusable(false)
                        .clickable {
                            // Start multiple selection mode
                            if (textEditorState.isMultipleSelectionMode) return@clickable
                            textEditorState = textEditorState.createMultipleSelectionModeState()
                            keyboardController?.hide()
                        }
                )
            }
        }

        // Multiple Selection Menu
        if (textEditorState.isMultipleSelectionMode) {
            EditorMenus(
                onCopy = {
                    clipboardManager.setText(AnnotatedString(textEditorState.getSelectedText()))
                    textEditorState = textEditorState.createCopiedState()
                },
                onDelete = {
                    textEditorState = textEditorState.createDeletedState()
                },
                onCancel = {
                    textEditorState = textEditorState.createCancelledState()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .height(80.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

private fun getLineNumber(index: Int): String {
    return (index + 1).toString().padStart(3, '0')
}

private fun getBackgroundColor(isSelected: Boolean): Color {
    return if (isSelected) Color(0x806456A5) else Color.White
}

@Preview
@Composable
private fun Demo_Preview() {
    Demo(DemoText)
}
