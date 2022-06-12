package com.sdercolin.vlabeler.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.sdercolin.vlabeler.ui.string.Strings
import com.sdercolin.vlabeler.ui.string.string

@Composable
fun SetResolutionDialog(
    current: Int,
    min: Int,
    max: Int,
    submit: (Int) -> Unit,
    dismiss: () -> Unit
) {
    Dialog(
        title = string(Strings.SetResolutionDialogTitle),
        onCloseRequest = { dismiss() },
        state = DialogState(width = 700.dp, height = 280.dp)
    ) {
        Content(current, min, max, submit)
    }
}

@Composable
private fun Content(
    current: Int,
    min: Int,
    max: Int,
    submit: (Int) -> Unit
) {
    var input by remember { mutableStateOf(current.toString()) }
    var value by remember { mutableStateOf<Int?>(current) }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.padding(horizontal = 50.dp, vertical = 30.dp)
                .align(Alignment.Center)
        ) {
            Text(string(Strings.SetResolutionDialogDescription, min, max))
            Spacer(Modifier.height(15.dp))
            OutlinedTextField(
                value = input,
                singleLine = true,
                onValueChange = {
                    input = it
                    val intValue = it.toIntOrNull()
                    value = if (intValue != null && intValue in min..max) {
                        intValue
                    } else {
                        null
                    }
                }
            )
            Spacer(Modifier.height(40.dp))
            Button(
                modifier = Modifier.align(Alignment.End),
                enabled = value != null,
                onClick = { input.toIntOrNull()?.let { submit(it) } }
            ) {
                Text(string(Strings.CommonDialogConfirmButton))
            }
        }
    }
}

@Composable
@Preview
private fun Preview() = Content(current = 100, min = 10, max = 1000, submit = {})