package com.example.notes.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notes.R
import com.example.notes.data.models.Priority
import com.example.notes.ui.theme.BlackOlive

@Composable
fun PriorityDropDown(priority: Priority, onPriorityChange: (Priority) -> Unit) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val angle: Float by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Canvas(
            modifier = Modifier
                .size(16.dp)
                .weight(1.5f)
        ) {
            drawCircle(color = priority.color)
        }

        CustomText(
            modifier = Modifier.weight(8f),
            text = priority.name,
            color = MaterialTheme.colors.secondary,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp
        )

        IconButton(modifier = Modifier
            .alpha(ContentAlpha.medium)
            .rotate(degrees = angle)
            .weight(1.5f),
            onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = stringResource(id = R.string.down_arrow),
                tint = MaterialTheme.colors.secondary
            )
        }

        DropdownMenu(
            modifier = Modifier
                .background(BlackOlive)
                .fillMaxWidth(fraction = 0.9f),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            Priority.values().slice(0..2).forEach { priority ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onPriorityChange(priority)
                }) {
                    PriorityItem(priority = priority)
                }
            }
        }
    }
}

@Composable
@Preview
fun PriorityDropDownPreview() {
    PriorityDropDown(priority = Priority.HIGH, onPriorityChange = {})
}