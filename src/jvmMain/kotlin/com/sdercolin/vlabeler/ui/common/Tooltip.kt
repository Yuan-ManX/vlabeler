package com.sdercolin.vlabeler.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun Tooltip(text: String) {
    Box(
        Modifier.background(
            color = MaterialTheme.colors.background,
            shape = RoundedCornerShape(5.dp),
        )
            .padding(10.dp)
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(5.dp)),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption,
        )
    }
}
