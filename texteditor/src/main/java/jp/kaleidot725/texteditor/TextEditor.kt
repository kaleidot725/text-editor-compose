package jp.kaleidot725.texteditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

@Composable
fun TextEditor(modifier: Modifier = Modifier) {
    val linesState by rememberTextLinesState(DemoText)
    var selectedIndex by remember { mutableStateOf(0) }

    LazyColumn(modifier = modifier) {
        itemsIndexed(items = linesState.value) { index, text ->
            val focusRequester by remember { mutableStateOf(FocusRequester()) }
            val isSelected by remember(selectedIndex) { derivedStateOf { selectedIndex == index } }

            LaunchedEffect(isSelected) {
                if (isSelected) focusRequester.requestFocus()
            }

            TextLine(
                number = index + 1,
                text = text,
                isSelected = isSelected,
                focusRequester = focusRequester,
                onChangedText = { newText ->
                    linesState.updateLineText(index, newText)
                },
                onInputNewLine = {
                    selectedIndex += 1
                    linesState.inputNewLineKey(index = index)
                },
                onInputBackKey = {
                    linesState.inputBackKey(
                        index = index,
                        onRemovedLine = { selectedIndex -= 1 }
                    )
                },
                onFocus = {
                    selectedIndex = index
                },
            )
        }
    }
}
