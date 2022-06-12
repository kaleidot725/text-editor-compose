package jp.kaleidot725.texteditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextEditor(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        (0L..100L).forEach {
            TextLine(number = it, text = "text$it", onChangedText = {}, modifier = Modifier.height(22.dp))
        }
    }
}