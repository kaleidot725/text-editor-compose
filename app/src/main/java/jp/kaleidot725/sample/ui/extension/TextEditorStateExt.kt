package jp.kaleidot725.sample.ui.extension

import jp.kaleidot725.texteditor.state.TextEditorState

fun TextEditorState.createMultipleSelectionModeState(): TextEditorState {
    return copy(isMultipleSelectionMode = true)
}

fun TextEditorState.createCopiedState(): TextEditorState {
    return copy(
        fields = fields.map { it.copy(isSelected = false) },
        isMultipleSelectionMode = false,
        selectedIndices = emptyList()
    )
}

fun TextEditorState.createDeletedState(): TextEditorState {
    val deleteFields = fields.filterIndexed { index, _ ->
        selectedIndices.contains(index)
    }
    val newFields = fields.toMutableList().apply {
        removeAll(deleteFields)
    }
    return copy(
        fields = newFields,
        isMultipleSelectionMode = false
    )
}

fun TextEditorState.createCancelledState(): TextEditorState {
    return copy(
        fields = fields.map { it.copy(isSelected = false) },
        isMultipleSelectionMode = false,
        selectedIndices = emptyList()
    )
}