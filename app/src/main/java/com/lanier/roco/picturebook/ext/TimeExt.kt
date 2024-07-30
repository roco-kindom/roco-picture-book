package com.lanier.roco.picturebook.ext

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedString(
    format: String = "yyyy年MM月dd日"
): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val date = Date(this)
    return sdf.format(date)
}
