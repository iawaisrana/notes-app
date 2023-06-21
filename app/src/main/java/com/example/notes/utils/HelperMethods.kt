package com.example.notes.utils

import java.text.SimpleDateFormat
import java.util.*

fun dateToString(date: Date?): String? {
    val pattern = "h:mm a, MMM dd yyyy";
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.ENGLISH);
    return date?.let { simpleDateFormat.format(it) }
}

fun stringToDate(str: String): Date? {
    val simpleDateFormat = SimpleDateFormat("h:mm a, MMM dd yyyy", Locale.ENGLISH)
    return simpleDateFormat.parse(str)
}