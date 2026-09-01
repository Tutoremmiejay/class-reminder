package com.emmiejay.classreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

const val REMINDER_LEAD_MINUTES = 15

object AlarmScheduler {

    private fun pendingIntent(context: Context, entry: ClassEntry): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("classId", entry.id)
            putExtra("studentName", entry.studentName)
            putExtra("dayOfWeek", entry.dayOfWeek)
            putExtra("hour", entry.hour)
            putExtra("minute", entry.minute)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, entry.id, intent, flags)
    }

    /** Computes the next reminder time (class time minus 15 min) that is in the future. */
    fun nextTriggerMillis(entry: ClassEntry): Long {
        val now = Calendar.getInstance()
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, entry.hour)
            set(Calendar.MINUTE, entry.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, -REMINDER_LEAD_MINUTES)

            val targetDay = entry.dayOfWeek
            while (get(Calendar.DAY_OF_WEEK) != targetDay) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        // If that time already passed this week, push to next week.
        if (trigger.timeInMillis <= now.timeInMillis) {
            trigger.add(Calendar.DAY_OF_YEAR, 7)
        }
        return trigger.timeInMillis
    }

    fun schedule(context: Context, entry: ClassEntry) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = nextTriggerMillis(entry)
        val pi = pendingIntent(context, entry)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                } else {
                    // Fall back to an inexact alarm if the user hasn't granted the
                    // "Alarms & reminders" permission yet.
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    fun cancel(context: Context, entry: ClassEntry) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context, entry))
    }

    fun rescheduleAll(context: Context, entries: List<ClassEntry>) {
        entries.forEach { schedule(context, it) }
    }
}
