package com.sdercolin.vlabeler.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sdercolin.vlabeler.ui.common.plainClickable
import com.sdercolin.vlabeler.ui.theme.Black50

sealed interface EmbeddedDialogArgs {
    val customMargin: Boolean get() = false
    val cancellableOnClickOutside: Boolean get() = false
}

sealed interface EmbeddedDialogResult

@Composable
fun EmbeddedDialog(args: EmbeddedDialogArgs, submit: (EmbeddedDialogResult?) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Black50)
            .plainClickable { if (args.cancellableOnClickOutside) submit(null) },
        contentAlignment = Alignment.Center
    ) {
        Surface {
            Box(
                modifier = Modifier
                    .run { if (!args.customMargin) padding(horizontal = 50.dp, vertical = 20.dp) else this }
            ) {
                @Suppress("REDUNDANT_ELSE_IN_WHEN")
                when (args) {
                    is SetResolutionDialogArgs -> SetResolutionDialog(args, submit)
                    is AskIfSaveDialogPurpose -> AskIfSaveDialog(args, submit)
                    is JumpToEntryDialogArgs -> JumpToEntryDialog(args, submit)
                    is EditEntryNameDialogArgs -> EditEntryNameDialog(args, submit)
                    is CommonConfirmationDialogAction -> CommonConfirmationDialog(args, submit)
                    is ErrorDialogContent -> ErrorDialog(args, submit)
                    else -> TODO("Dialog args handler is not implemented")
                }
            }
        }
    }
}
