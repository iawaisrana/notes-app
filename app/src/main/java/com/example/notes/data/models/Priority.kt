package com.example.notes.data.models

import androidx.compose.ui.graphics.Color
import com.example.notes.ui.theme.HighPriorityColor
import com.example.notes.ui.theme.LowPriorityColor
import com.example.notes.ui.theme.MediumPriorityColor
import com.example.notes.ui.theme.NonePriorityColor

enum class Priority(val color: Color) {
    HIGH(HighPriorityColor),
    MEDIUM(MediumPriorityColor),
    LOW(LowPriorityColor),
    NONE(NonePriorityColor)
}