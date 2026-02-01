package com.example.plantgrow.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WateringNotificationScheduler(private val context: Context) {

    companion object {
        private const val WORK_NAME_MORNING = "watering_notification_morning"
        private const val WORK_NAME_AFTERNOON = "watering_notification_afternoon"
    }

    fun scheduleDailyNotifications() {
        scheduleMorningNotification()
        scheduleAfternoonNotification()
    }

    fun cancelAllNotifications() {
        WorkManager.getInstance(context).apply {
            cancelUniqueWork(WORK_NAME_MORNING)
            cancelUniqueWork(WORK_NAME_AFTERNOON)
        }
    }

    fun rescheduleNotifications(morningHour: Int, afternoonHour: Int) {
        cancelAllNotifications()

        val morningCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, morningHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val afternoonCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, afternoonHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        scheduleNotification(WORK_NAME_MORNING, morningCalendar)
        scheduleNotification(WORK_NAME_AFTERNOON, afternoonCalendar)
    }

    private fun scheduleMorningNotification() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        scheduleNotification(WORK_NAME_MORNING, calendar)
    }

    private fun scheduleAfternoonNotification() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        scheduleNotification(WORK_NAME_AFTERNOON, calendar)
    }


    private fun scheduleNotification(workName: String, targetTime: Calendar) {
        val now = Calendar.getInstance()
        if (now.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }
        val delayMs = targetTime.timeInMillis - now.timeInMillis
        require(delayMs >= 0) { "delayMs should never be negative" }

        val periodicWork = PeriodicWorkRequestBuilder<WateringNotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag("watering_notification")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWork
        )
    }
}