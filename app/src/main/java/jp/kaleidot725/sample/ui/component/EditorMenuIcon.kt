package jp.kaleidot725.sample.ui.component

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MenuIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.Menu,
        contentDescription = "Menu",
        modifier = modifier
    )
}

@Composable
fun CheckCircleIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "CheckCircle",
        modifier = modifier
    )
}

@Composable
fun CopyIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.Create,
        contentDescription = "Copy",
        modifier = modifier
    )
}

@Composable
fun TrashIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Trash",
        modifier = modifier
    )
}

@Composable
fun CancelIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Close",
        modifier = modifier
    )
}