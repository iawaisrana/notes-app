package com.example.notes.utils

import androidx.compose.ui.graphics.Color
import com.example.notes.ui.theme.*

fun noteItemColor(index: Int): Color {
    val listOfColors = listOf<Color>(
        RichBrilliantLavender, LightSalmonPink, LightGreen, PastelYellow, Waterspout, PaleViolet
    )

    val colorIndex = index % listOfColors.size

    return listOfColors[colorIndex]
}