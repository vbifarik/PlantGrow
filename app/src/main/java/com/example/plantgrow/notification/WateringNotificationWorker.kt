package com.example.plantgrow.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.plantgrow.MainActivity
import com.example.plantgrow.R

class WateringNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        const val CHANNEL_ID = "watering_channel"
        const val NOTIFICATION_ID = 2001
    }

    override fun doWork(): Result {
        return try {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val timeOfDay = if (hour < 12) "утренний" else "дневной"

            showNotification("$timeOfDay полив растений")

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(message: String) {
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_potato)
            .setContentTitle("Напоминание о поливе")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message. Не забудьте полить ваши растения! " +
                            "Регулярный полив обеспечит здоровый рост растений.")
            )
            .build()

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        )
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Уведомления о поливе",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Напоминания о времени полива растений"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }

            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}