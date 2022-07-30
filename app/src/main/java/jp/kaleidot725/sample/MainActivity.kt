package jp.kaleidot725.sample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import jp.kaleidot725.sample.ui.component.EditorMenus
import jp.kaleidot725.sample.ui.component.FieldIcon
import jp.kaleidot725.sample.ui.theme.DemoText
import jp.kaleidot725.sample.ui.theme.SampleTheme
import jp.kaleidot725.texteditor.state.TextEditorState
import jp.kaleidot725.texteditor.view.TextEditor

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SampleTheme {
                var textEditorState by remember { mutableStateOf(TextEditorState.create(DemoText)) }
                val context: Context = LocalContext.current
                val clipboardManager: ClipboardManager = LocalClipboardManager.current
                val keyboardController = LocalSoftwareKeyboardController.current

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    TextEditor(
                        textEditorState = textEditorState,
                        onChanged = { textEditorState = it },
                        contentPaddingValues = PaddingValues(
                            bottom = with(LocalDensity.current) {
                                WindowInsets.ime.getBottom(this).toDp()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (textEditorState.isMultipleSelectionMode) 100.dp else 0.dp)
                    ) { index, isSelected, innerTextField ->
                        val backgroundColor = if (isSelected) Color(0x806456A5) else Color.White
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(backgroundColor)
                        ) {
                            Text(
                                text = (index + 1).toString().padStart(3, '0'),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )

                            innerTextField(
                                modifier = Modifier
                                    .weight(0.9f, true)
                                    .align(Alignment.CenterVertically)
                            )

                            FieldIcon(
                                isMultipleSelection = textEditorState.isMultipleSelectionMode,
                                isSelected = isSelected,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        if (!textEditorState.isMultipleSelectionMode) {
                                            textEditorState = textEditorState.copy(isMultipleSelectionMode = true)
                                            keyboardController?.hide()
                                        }
                                    }
                            )
                        }
                    }

                    if (textEditorState.isMultipleSelectionMode) {
                        EditorMenus(
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(textEditorState.getSelectedText()))
                                Toast.makeText(context, "Copy text to clipboard", Toast.LENGTH_SHORT).show()
                                textEditorState = textEditorState.copy(
                                    fields = textEditorState.fields.map { it.copy(isSelected = false) },
                                    isMultipleSelectionMode = false,
                                    selectedIndices = emptyList()
                                )
                            },
                            onDelete = {
                                val deleteFields = textEditorState.fields.filterIndexed { index, _ ->
                                    textEditorState.selectedIndices.contains(index)
                                }
                                val newFields = textEditorState.fields.toMutableList().apply {
                                    removeAll(deleteFields)
                                }
                                textEditorState = textEditorState.copy(
                                    fields = newFields,
                                    isMultipleSelectionMode = false
                                )
                            },
                            onCancel = {
                                textEditorState = textEditorState.copy(
                                    fields = textEditorState.fields.map { it.copy(isSelected = false) },
                                    isMultipleSelectionMode = false,
                                    selectedIndices = emptyList()
                                )
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
        }
    }
}