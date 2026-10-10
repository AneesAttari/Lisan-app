package com.example.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_ID = "lisan_daily_reminder_channel"
    private const val NOTIFICATION_ID = 1001

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val dailyChannel = NotificationChannel(
                CHANNEL_ID,
                "Daily English Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to practice English at your fixed scheduled time"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(dailyChannel)
        }
    }

    fun showDailyReminderNotification(context: Context, style: String = "Motivational") {
        createChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (shortText, bigText) = when (style) {
            "Gentle" -> Pair(
                "العلم نور، والاستقامة طريق النجاح.",
                "العلم نور، والاستقامة طريق النجاح. روزانہ کی باقاعدگی اور علمی محنت ہی کامیابی کا حقیقی راستہ ہے۔"
            )
            "Minimal" -> Pair(
                "الاستقامة فوق الكرامة • العلم نور",
                "الاستقامة فوق الكرامة • العلم نور، والتعلم المستمر طريق النجاح."
            )
            else -> Pair(
                "العلم نور، والاستقامة طريق النجاح.",
                "العلم نور، والاستقامة طريق النجاح. اپنے روزانہ کے سبق اور مشق کو باقاعدگی سے مکمل کریں۔"
            )
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Lisani")
            .setSubText("Al-Istiqamatu fawqa al-karama")
            .setContentText(shortText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
