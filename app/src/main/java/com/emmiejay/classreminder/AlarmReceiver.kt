package com.emmiejay.classreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val CHANNEL_ID = "class_reminders"

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val classId = intent.getIntExtra("classId", -1)
        val studentName = intent.getStringExtra("studentName") ?: "Class"
        val hour = intent.getIntExtra("hour", 0)
        val minute = intent.getIntExtra("minute", 0)

        showNotification(context, classId, studentName, hour, minute)

        // Reschedule this same class for next week.
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (classId != -1) {
                    val entry = ClassDatabase.getInstance(context).classDao().getById(classId)
                    if (entry != null) {
                        AlarmScheduler.schedule(context, entry)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, classId: Int, studentName: String, hour: Int, minute: Int) {
        createChannel(context)

        val timeText = formatTime(hour, minute)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Class Reminder by Emmiejay")
            .setContentText("$studentName's class starts in $REMINDER_LEAD_MINUTES minutes ($timeText)")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "$studentName's class starts in $REMINDER_LEAD_MINUTES minutes ($timeText)"
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notify(classId, notification)
            }
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Class reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts 15 minutes before each tutoring class"
                    enableVibration(true)
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                        android.media.AudioAttributes.Builder()
                            .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                }
                manager.createNotificationChannel(channel)
            }
        }
    }
}
