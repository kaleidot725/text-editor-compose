package jp.kaleidot725.texteditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TextLine(
    number: Long,
    text: String,
    onChangedText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = number.toString().padStart(2, '0'),
            color = Color(0xFF014900),
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
        )
        
        Spacer(modifier = Modifier.width(4.dp))

        BasicTextField(
            value = text,
            onValueChange = onChangedText,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
private fun TextLine_Preview() {
    Surface(color = Color.White) {
        TextLine(number = 0, text = "TEST", onChangedText = {}, modifier = Modifier)
    }
}