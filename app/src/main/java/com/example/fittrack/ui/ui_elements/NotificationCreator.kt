package com.example.fittrack.ui.ui_elements

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.fittrack.entity.NotificationDataEntity
import com.example.fittrack.service.NotificationService
import java.time.LocalDateTime
import java.time.ZoneId

object NotificationCreator {

    private const val CHANNEL_ID = "notes_notifications"
    private const val CHANNEL_NAME = "Recordatorios de Notas"
    private const val CHANNEL_DESCRIPTION = "Notificaciones para recordatorios de notas"

    /**
     * Programar una notificación para una fecha y hora específica
     */
    fun scheduleNotification(
        context: Context,
        notificationId: Int,
        title: String,
        content: String,
        dateTime: LocalDateTime
    ): Boolean {
        try {
            createNotificationChannel(context)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationService::class.java).apply {
                putExtra("note_id", notificationId)
                putExtra("note_title", title)
                putExtra("note_content", content)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,

                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

            )

            val triggerTime = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // Verificar que la fecha no sea en el pasado
            if (triggerTime <= System.currentTimeMillis()) {
                Log.w("NotificationCreator", "Cannot schedule notification for past time")
                return false
            }

            // Verificar permisos de alarma exacta para Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    // Fallback a alarma inexacta
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    Log.w("NotificationCreator", "Using inexact alarm due to missing permission")
                }
            } else {
                try {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } catch (e: SecurityException) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    Log.w("NotificationCreator", "Using regular alarm due to security exception")
                }
            }

            Log.d("NotificationCreator", "Notification scheduled for $dateTime with ID $notificationId")
            return true

        } catch (e: Exception) {
            Log.e("NotificationCreator", "Error scheduling notification: ${e.message}")
            return false
        }
    }



    /**
     * Cancelar una notificación programada
     */
    fun cancelScheduledNotification(context: Context, notificationId: Int) {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationService::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,

                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

            )
            alarmManager.cancel(pendingIntent)

    }

    /**
     * Cancelar una notificación que ya se está mostrando
     */
    fun cancelDisplayedNotification(context: Context, notificationId: Int) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(notificationId)
            Log.d("NotificationCreator", "Cancelled displayed notification with ID $notificationId")
        } catch (e: Exception) {
            Log.e("NotificationCreator", "Error cancelling displayed notification: ${e.message}")
        }
    }

    /**
     * Verificar si la aplicación tiene permisos de notificación
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Verificar si se pueden programar alarmas exactas (Android 12+)
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * Crear el canal de notificaciones (necesario para Android 8.0+)
     */
    private fun createNotificationChannel(context: Context) {

            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

    }

    /**
     * Programar múltiples notificaciones de una vez
     */
    fun scheduleMultipleNotifications(
        context: Context,
        notifications: List<NotificationDataEntity>
    ): List<Boolean> {
        return notifications.map { notification ->
            scheduleNotification(
                context = context,
                notificationId = notification.id,
                title = notification.title,
                content = notification.content,
                dateTime = notification.dateTime
            )
        }
    }

    /**
     * Cancelar múltiples notificaciones
     */
    fun cancelMultipleNotifications(context: Context, notificationIds: List<Int>) {
        notificationIds.forEach { id ->
            cancelScheduledNotification(context, id)
            cancelDisplayedNotification(context, id)
        }
    }
}