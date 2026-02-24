package me.thankgodr.fintechchallegeapp.presentation.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

fun String.toTitleCase(): String =
    lowercase().split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }

fun String.toReadableDate(): String {
    return try {
        val months = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
        )

        val dateTimeParts = this.replace("Z", "").split("T")
        val datePart = dateTimeParts[0]
        val timePart = dateTimeParts.getOrNull(1)

        val dateParts = datePart.split("-")
        if (dateParts.size < 3) return this

        val year = dateParts[0].toIntOrNull() ?: return this
        val month = dateParts[1].toIntOrNull() ?: return this
        val day = dateParts[2].toIntOrNull() ?: return this
        val monthName = months.getOrNull(month - 1) ?: return this

        // Format time part (12-hour with AM/PM)
        val formattedTime = if (timePart != null && timePart.isNotEmpty()) {
            val timeComponents = timePart.split(":")
            val hour24 = timeComponents[0].toIntOrNull() ?: 0
            val minute = timeComponents.getOrNull(1) ?: "00"
            val amPm = if (hour24 < 12) "AM" else "PM"
            val hour12 = when {
                hour24 == 0 -> 12
                hour24 > 12 -> hour24 - 12
                else -> hour24
            }
            "$hour12:$minute $amPm"
        } else {
            null
        }

        // Compare with today/yesterday
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val todayYear = today.year
        val todayMonth = today.monthNumber
        val todayDay = today.dayOfMonth

        val isToday = year == todayYear && month == todayMonth && day == todayDay
        val isYesterday = run {
            val yesterdayEpoch = today.toEpochDays() - 1
            val inputEpoch = kotlinx.datetime.LocalDate(year, month, day).toEpochDays()
            inputEpoch == yesterdayEpoch
        }

        val dateLabel = when {
            isToday -> "Today"
            isYesterday -> "Yesterday"
            else -> "$monthName $day, $year"
        }

        if (formattedTime != null) "$dateLabel · $formattedTime" else dateLabel
    } catch (_: Exception) {
        this
    }
}
