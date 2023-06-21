package com.example.notes.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notes.ui.theme.BlackOlive

@Composable
fun CustomLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp),
            shape = RoundedCornerShape(32.dp),
            color = BlackOlive
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(12.dp), strokeWidth = 3.dp, color = MaterialTheme.colors.secondary
            )
        }
    }
}