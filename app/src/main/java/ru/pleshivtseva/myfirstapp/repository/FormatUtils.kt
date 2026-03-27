package ru.pleshivtseva.myfirstapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.floor

object FormatUtils {
    fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> {
                val millions = floor(count / 1_000_00.0) /10.0
                "$millions" + "M"
            }
            count >= 10_000 -> {
                "${count / 1000}K"
            }
            count >= 1_000 -> {
                val thousands = floor(count / 100.0) /10.0
                "$thousands" + "K"
            }
            else -> count.toString()
        }
    }
    fun formatDate(date: Date): String {
        val format = SimpleDateFormat("d MMM в HH:mm", Locale("ru"))
        return format.format(date)
    }
}