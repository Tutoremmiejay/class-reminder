package com.emmiejay.classreminder

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

/**
 * dayOfWeek uses java.util.Calendar constants:
 * Calendar.SUNDAY=1, MONDAY=2, TUESDAY=3, WEDNESDAY=4, THURSDAY=5, FRIDAY=6, SATURDAY=7
 *
 * hour/minute are the CLASS start time (24-hour). The reminder alarm itself
 * fires 15 minutes before this, computed in AlarmScheduler.
 */
@Entity(tableName = "classes")
data class ClassEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: Int,
    val hour: Int,
    val minute: Int,
    val studentName: String
)

object DayNames {
    // Index matches Calendar.DAY_OF_WEEK (1..7)
    val names = listOf("", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    fun name(dayOfWeek: Int): String = names.getOrElse(dayOfWeek) { "?" }

    // Ordered Monday -> Sunday for display, mapped to Calendar constants
    val orderedForDisplay = listOf(
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
        Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
    )
}

fun formatTime(hour: Int, minute: Int): String {
    val h12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val ampm = if (hour < 12) "AM" else "PM"
    return String.format("%d:%02d %s", h12, minute, ampm)
}
