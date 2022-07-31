package jp.kaleidot725.sample.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EditorMenus(
    onCopy: () -> Unit = {},
    onDelete: () -> Unit = {},
    onCancel: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            EditorMenu(
                icon = { CopyIcon() },
                label = { Text(text = "Copy") },
                modifier = Modifier
                        .weight(0.2f)
                        .align(Alignment.CenterVertically)
                        .clickable { onCopy() }
            )
            EditorMenu(
                icon = { TrashIcon() },
                label = { Text(text = "Delete") },
                modifier = Modifier
                        .weight(0.2f)
                        .align(Alignment.CenterVertically)
                        .clickable { onDelete() }
            )
            EditorMenu(
                icon = { CancelIcon() },
                label = { Text(text = "Cancel") },
                modifier = Modifier
                        .weight(0.2f)
                        .align(Alignment.CenterVertically)
                        .clickable { onCancel() }
            )
        }
    }
}

@Preview
@Composable
private fun EditorMenus_Preview() {
    EditorMenus()
}
