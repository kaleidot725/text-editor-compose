package jp.kaleidot725.texteditor

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.TextRange

@Composable
fun TextEditor(modifier: Modifier = Modifier) {
    val linesState by rememberTextLinesState(DemoText)
    var selectedIndex by remember { mutableStateOf(0) }
    val selection by remember { mutableStateOf(TextRange.Zero) }

    LazyColumn(modifier = modifier) {
        itemsIndexed(items = linesState.lines) { index, textFieldValue ->
            val focusRequester by remember { mutableStateOf(FocusRequester()) }
            val isSelected by remember(selectedIndex) { derivedStateOf { selectedIndex == index } }

            LaunchedEffect(isSelected) {
                if (isSelected) focusRequester.requestFocus()
            }

            Log.v("TEST", "${textFieldValue} ${selection}")

            TextLine(
                number = index + 1,
                textFieldValue = textFieldValue,
                isSelected = isSelected,
                focusRequester = focusRequester,
                onChangedText = { newText ->
                    linesState.updateLine(index, newText)
                },
                onFocus = {
                    selectedIndex = index
                },
                onInputBackKey = {
                    linesState.inputBackKey(
                        targetIndex = index,
                        onRemovedLine = { selectedIndex -= 1 }
                    )
                },
            )
        }
    }
}
