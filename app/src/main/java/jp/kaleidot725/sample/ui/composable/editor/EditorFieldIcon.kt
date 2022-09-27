package jp.kaleidot725.sample.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kaleidot725.sample.ui.composable.editor.CheckCircleIcon
import jp.kaleidot725.sample.ui.composable.editor.MenuIcon

@Composable
fun FieldIcon(
    isMultipleSelection: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when {
            isMultipleSelection && isSelected -> {
                CheckCircleIcon(modifier = Modifier.align(Alignment.Center))
            }
            isSelected -> {
                MenuIcon(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview
@Composable
private fun FieldIcon_Preview() {
    MaterialTheme {
        Column {
            FieldIcon(isMultipleSelection = false, isSelected = false, modifier = Modifier.size(32.dp))
            FieldIcon(isMultipleSelection = false, isSelected = true, modifier = Modifier.size(32.dp))
            FieldIcon(isMultipleSelection = true, isSelected = true, modifier = Modifier.size(32.dp))
        }
    }
}
